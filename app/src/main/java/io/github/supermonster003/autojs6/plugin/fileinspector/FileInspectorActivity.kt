package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestInputError
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestInputNormalizer
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestParseResult
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestVerifier
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReport
import io.github.supermonster003.autojs6.plugin.fileinspector.core.VerificationStatus
import io.github.supermonster003.autojs6.plugin.fileinspector.databinding.ActivityFileInspectorBinding
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.util.Locale

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
        bindStaticContent()
        bindActions()
        observeState()
        viewModel.inspect(request)
    }

    private fun bindStaticContent() = with(binding) {
        toolbar.setNavigationOnClickListener { finish() }
        fileName.text = request.displayName
        mimeType.text = getString(R.string.file_mime_type, request.mimeType)
        extension.text = getString(R.string.file_extension, request.displayName.fileExtension())
        declaredSize.text = getString(
            R.string.declared_size,
            Formatter.formatFileSize(this@FileInspectorActivity, request.declaredSize),
        )
        digestList.layoutManager = LinearLayoutManager(this@FileInspectorActivity)
        digestList.adapter = digestAdapter
    }

    private fun bindActions() = with(binding) {
        cancel.setOnClickListener { viewModel.cancel() }
        retry.setOnClickListener { viewModel.retry(request) }
        copyReport.setOnClickListener {
            viewModel.completedReport()?.let { report ->
                copyText(getString(R.string.app_name), buildInspectionReport(request, report))
                Toast.makeText(this@FileInspectorActivity, R.string.report_copied, Toast.LENGTH_SHORT).show()
            }
        }
        shareReport.setOnClickListener {
            viewModel.completedReport()?.let { report -> shareReport(buildInspectionReport(request, report)) }
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
            FileInspectorUiState.Idle -> renderRunning(bytesRead = 0L)
            is FileInspectorUiState.Running -> renderRunning(state.bytesRead)
            is FileInspectorUiState.Complete -> renderComplete(state.report)
            FileInspectorUiState.Canceled -> renderStopped(getString(R.string.inspect_canceled))
            is FileInspectorUiState.Failed -> renderStopped(errorMessage(state.error))
        }
    }

    private fun renderRunning(bytesRead: Long) = with(binding) {
        resultsContainer.isVisible = false
        progress.isVisible = true
        cancel.isVisible = true
        retry.isVisible = false
        if (request.declaredSize > 0L) {
            progress.isIndeterminate = false
            progress.progress = ((bytesRead * 100.0 / request.declaredSize).toInt()).coerceIn(0, 100)
            status.text = getString(
                R.string.inspect_progress,
                Formatter.formatFileSize(this@FileInspectorActivity, bytesRead),
                Formatter.formatFileSize(this@FileInspectorActivity, request.declaredSize),
                progress.progress,
            )
        } else {
            progress.isIndeterminate = true
            status.setText(R.string.inspect_starting)
        }
    }

    private fun renderComplete(report: InspectionReport) = with(binding) {
        progress.isVisible = false
        cancel.isVisible = false
        retry.isVisible = false
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
        status.text = message
    }

    private fun verifyExpectedDigest() {
        val report = viewModel.completedReport() ?: return
        binding.expectedDigestLayout.error = null
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

    private fun shareReport(report: String) {
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, request.displayName)
                    putExtra(Intent.EXTRA_TEXT, report)
                },
                getString(R.string.report_share_title),
            ),
        )
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

    private fun String.fileExtension(): String {
        val delimiter = lastIndexOf('.')
        return if (delimiter <= 0 || delimiter == lastIndex) {
            getString(R.string.no_extension)
        } else {
            substring(delimiter + 1).lowercase(Locale.ROOT)
        }
    }
}
