package io.github.supermonster003.autojs6.plugin.fileinspector

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestAlgorithm
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestValue
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReport
import io.github.supermonster003.autojs6.plugin.fileinspector.databinding.ItemDigestBinding

internal data class DigestRow(
    val algorithm: DigestAlgorithm,
    val value: DigestValue,
)

internal class DigestAdapter(
    private val onCopy: (DigestRow) -> Unit,
) : RecyclerView.Adapter<DigestAdapter.ViewHolder>() {

    private var rows: List<DigestRow> = emptyList()

    fun submit(report: InspectionReport) {
        val previousSize = rows.size
        rows = DigestAlgorithm.entries.map { algorithm -> DigestRow(algorithm, report[algorithm]) }
        if (previousSize == 0) {
            notifyItemRangeInserted(0, rows.size)
        } else {
            notifyItemRangeChanged(0, rows.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDigestBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(rows[position])
    }

    override fun getItemCount(): Int = rows.size

    inner class ViewHolder(
        private val binding: ItemDigestBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(row: DigestRow) = with(binding) {
            algorithm.text = row.algorithm.label(root.context)
            digest.text = row.value.hex
            legacy.visibility = if (row.algorithm.isLegacy()) View.VISIBLE else View.GONE
            copy.setOnClickListener { onCopy(row) }
        }
    }
}
