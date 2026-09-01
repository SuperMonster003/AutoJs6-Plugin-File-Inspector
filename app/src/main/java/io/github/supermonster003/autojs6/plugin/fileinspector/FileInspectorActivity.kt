package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestInputError
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestInputNormalizer
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestParseResult
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestVerifier
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionRate
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReport
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReportFormat
import io.github.supermonster003.autojs6.plugin.fileinspector.core.VerificationStatus
import io.github.supermonster003.autojs6.plugin.fileinspector.core.ZipContainerInspector
import io.github.supermonster003.autojs6.plugin.fileinspector.databinding.ActivityFileInspectorBinding
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class FileInspectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileInspectorBinding
    private lateinit var request: FileInspectionRequest
    private val viewModel by viewModels<FileInspectorViewModel>()
    private val digestAdapter = DigestAdapter(::copyDigest)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val validatedRequest = FileInspectorIntentPolicy.resolve(intent)
        if (validatedRequest == null) {
            Toast.makeText(this, R.string.error_invalid_request, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        request = validatedRequest
        binding = ActivityFileInspectorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarContrast()
        bindStaticContent()
        bindActions()
        observeState()
        viewModel.inspect(request)
    }

    private fun bindStaticContent() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        fileName.text = request.displayName
        mimeType.text = getString(R.string.file_mime_type, request.mimeType)
        extension.text = getString(
            R.string.file_extension,
            request.displayName.fileExtensionOrNull() ?: getString(R.string.no_extension),
        )
        declaredSize.text = getString(
            R.string.declared_size,
            Formatter.formatFileSize(this@FileInspectorActivity, request.declaredSize),
        )
        digestList.layoutManager = LinearLayoutManager(this@FileInspectorActivity)
        digestList.adapter = digestAdapter
        listOf(
            fileName,
            fileInformationTitle,
            reportFormatLabel,
            integrityCheckTitle,
            fileHeaderTitle,
        ).forEach { heading -> ViewCompat.setAccessibilityHeading(heading, true) }
    }

    private fun bindActions() = with(binding) {
        cancel.setOnClickListener { viewModel.cancel() }
        retry.setOnClickListener { viewModel.retry(request) }
        copyReport.setOnClickListener {
            viewModel.completedReport()?.let { report ->
                val format = selectedReportFormat()
                copyText(
                    getString(R.string.app_name),
                    buildInspectionReport(request, report, format),
                )
                Toast.makeText(this@FileInspectorActivity, R.string.report_copied, Toast.LENGTH_SHORT).show()
            }
        }
        shareReport.setOnClickListener {
            viewModel.completedReport()?.let { report ->
                val format = selectedReportFormat()
                shareReport(buildInspectionReport(request, report, format), format)
            }
        }
        copyHeader.setOnClickListener {
            viewModel.completedReport()?.let { report ->
                val header = report.header.bytes.formatHeader().ifEmpty { getString(R.string.header_empty) }
                copyText(getString(R.string.file_header), header)
                Toast.makeText(this@FileInspectorActivity, R.string.header_copied, Toast.LENGTH_SHORT).show()
            }
        }
        verify.setOnClickListener { verifyExpectedDigest() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: FileInspectorUiState) = with(binding) {
        when (state) {
            FileInspectorUiState.Idle -> renderRunning(bytesRead = 0L, rate = null)
            is FileInspectorUiState.Running -> renderRunning(state.bytesRead, state.rate)
            is FileInspectorUiState.Complete -> renderComplete(state.report)
            FileInspectorUiState.Canceled -> renderStopped(getString(R.string.inspect_canceled))
            is FileInspectorUiState.Failed -> renderStopped(errorMessage(state.error))
        }
    }

    private fun renderRunning(
        bytesRead: Long,
        rate: InspectionRate?,
    ) = with(binding) {
        resultsContainer.isVisible = false
        progress.isVisible = true
        cancel.isVisible = true
        retry.isVisible = false
        status.accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_NONE
        if (request.declaredSize > 0L) {
            progress.isIndeterminate = false
            progress.progress = ((bytesRead * 100.0 / request.declaredSize).toInt()).coerceIn(0, 100)
            val bytesReadDisplay = Formatter.formatFileSize(this@FileInspectorActivity, bytesRead)
            val totalBytesDisplay = Formatter.formatFileSize(
                this@FileInspectorActivity,
                request.declaredSize,
            )
            status.text = if (rate?.remainingMillis != null) {
                getString(
                    R.string.inspect_progress_with_estimate,
                    bytesReadDisplay,
                    totalBytesDisplay,
                    progress.progress,
                    Formatter.formatFileSize(this@FileInspectorActivity, rate.bytesPerSecond),
                    DateUtils.formatElapsedTime(
                        (
                            rate.remainingMillis / MILLIS_PER_SECOND +
                                if (rate.remainingMillis % MILLIS_PER_SECOND == 0L) 0L else 1L
                        )
                            .coerceAtLeast(1L),
                    ),
                )
            } else {
                getString(
                    R.string.inspect_progress,
                    bytesReadDisplay,
                    totalBytesDisplay,
                    progress.progress,
                )
            }
        } else {
            progress.isIndeterminate = true
            status.setText(R.string.inspect_starting)
        }
    }

    private fun renderComplete(report: InspectionReport) = with(binding) {
        progress.isVisible = false
        cancel.isVisible = false
        retry.isVisible = false
        status.accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        status.setText(R.string.inspect_complete)
        resultsContainer.isVisible = true
        actualSize.isVisible = true
        actualSize.text = getString(
            R.string.file_size,
            Formatter.formatFileSize(this@FileInspectorActivity, report.bytesRead),
        )
        detectedFormat.isVisible = true
        detectedFormat.text = getString(
            R.string.detected_format,
            report.header.signatures.formatSignatures(this@FileInspectorActivity),
        )
        val zipHint = ZipContainerInspector.inspect(request.displayName, report.header.signatures)
        containerHint.isVisible = zipHint != null
        containerHint.text = zipHint?.formatZipContainerHint(this@FileInspectorActivity)
        contentAnalysis.isVisible = true
        contentAnalysis.text = report.header.content.formatContentAnalysis(this@FileInspectorActivity)
        textEncoding.isVisible = true
        textEncoding.text = getString(
            R.string.text_encoding,
            report.header.bom.formatBom(this@FileInspectorActivity),
        )
        headerHex.text = report.header.bytes.formatHeader().ifEmpty { getString(R.string.header_empty) }
        digestAdapter.submit(report)
    }

    private fun renderStopped(message: String) = with(binding) {
        resultsContainer.isVisible = false
        progress.isVisible = false
        cancel.isVisible = false
        retry.isVisible = true
        status.accessibilityLiveRegion = View.ACCESSIBILITY_LIVE_REGION_POLITE
        status.text = message
    }

    private fun verifyExpectedDigest() {
        val report = viewModel.completedReport() ?: return
        binding.expectedDigestLayout.error = null
        binding.verificationFileNameWarning.isVisible = false
        when (val parsed = DigestInputNormalizer.parse(binding.expectedDigest.text?.toString().orEmpty())) {
            is DigestParseResult.Invalid -> {
                binding.expectedDigestLayout.error = getString(parsed.reason.errorResource())
                binding.verificationResult.isVisible = false
            }
            is DigestParseResult.Valid -> {
                val algorithm = parsed.value.algorithm
                val matches = DigestVerifier.verify(report, parsed.value) == VerificationStatus.MATCH
                binding.verificationResult.apply {
                    isVisible = true
                    text = getString(
                        if (matches) R.string.verification_match else R.string.verification_mismatch,
                        algorithm.label(this@FileInspectorActivity),
                    )
                    setTextColor(
                        ContextCompat.getColor(
                            this@FileInspectorActivity,
                            if (matches) R.color.verification_match else R.color.verification_mismatch,
                        ),
                    )
                }
                parsed.value.sourceFileName?.takeIf {
                    parsed.value.hasSourceFileNameMismatch(request.displayName)
                }?.let { sourceFileName ->
                    binding.verificationFileNameWarning.apply {
                        isVisible = true
                        text = getString(
                            R.string.verification_file_name_mismatch,
                            sourceFileName,
                            request.displayName,
                        )
                    }
                }
            }
        }
    }

    private fun copyDigest(row: DigestRow) {
        copyText(row.algorithm.label(this), row.value.hex)
        Toast.makeText(
            this,
            getString(R.string.checksum_copied, row.algorithm.label(this)),
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun copyText(label: String, value: String) {
        getSystemService(ClipboardManager::class.java)
            .setPrimaryClip(ClipData.newPlainText(label, value))
    }

    private fun shareReport(report: String, format: InspectionReportFormat) {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = when (format) {
                        InspectionReportFormat.MARKDOWN -> "text/markdown"
                        InspectionReportFormat.JSON -> "application/json"
                    }
                    putExtra(Intent.EXTRA_SUBJECT, request.displayName)
                    putExtra(Intent.EXTRA_TEXT, report)
                },
                getString(R.string.report_share_title),
            ),
        )
    }

    private fun selectedReportFormat(): InspectionReportFormat =
        if (binding.reportFormatJson.isChecked) {
            InspectionReportFormat.JSON
        } else {
            InspectionReportFormat.MARKDOWN
        }

    @Suppress("DEPRECATION")
    private fun applySystemBarContrast() {
        val primary = MaterialColors.getColor(this, androidx.appcompat.R.attr.colorPrimary, 0)
        val surface = MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorSurface,
            0,
        )
        window.statusBarColor = primary
        window.navigationBarColor = surface
        WindowInsetsControllerCompat(window, binding.root).apply {
            isAppearanceLightStatusBars = MaterialColors.isColorLight(primary)
            isAppearanceLightNavigationBars = MaterialColors.isColorLight(surface)
        }
    }

    private fun errorMessage(error: Throwable): String = when (error) {
        is SecurityException, is FileNotFoundException -> getString(R.string.error_permission_missing)
        is io.github.supermonster003.autojs6.plugin.fileinspector.core.DeclaredSizeLimitExceededException,
        is io.github.supermonster003.autojs6.plugin.fileinspector.core.StreamLimitExceededException,
        -> getString(R.string.error_file_too_large)
        is io.github.supermonster003.autojs6.plugin.fileinspector.core.DeclaredSizeMismatchException ->
            getString(R.string.error_file_changed)
        else -> getString(R.string.error_read_failed)
    }

    private fun DigestInputError.errorResource(): Int = when (this) {
        DigestInputError.EMPTY -> R.string.error_digest_empty
        DigestInputError.TOO_LONG -> R.string.error_digest_too_long
        DigestInputError.ALGORITHM_CONFLICT -> R.string.error_digest_algorithm_conflict
        else -> R.string.error_digest_invalid
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1_000L
    }
}
