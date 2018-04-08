package nl.menio.moneybunqer.ui.viewholders

import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.databinding.ItemSelectAccountBinding
import java.io.Serializable

class SelectAccountViewHolder(
        val binding: ItemSelectAccountBinding,
        val listener: OnAccountSelectionChangedListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SelectAccountItem) {
        binding.name.text = item.monetaryAccount.monetaryAccountBank.description
        binding.selected.isChecked = item.selected
        binding.avatar.setAttachmentPublicUuid(item.monetaryAccount.monetaryAccountBank.avatar.image.first().attachmentPublicUuid)

        binding.selected.setOnCheckedChangeListener { _, checked ->
            item.selected = checked
            listener?.onAccountSelectionChanged(item.monetaryAccount, checked)
        }
    }
}

data class SelectAccountItem(
        val monetaryAccount: MonetaryAccount,
        var selected: Boolean
) : Serializable

interface OnAccountSelectionChangedListener {
    fun onAccountSelectionChanged(monetaryAccount: MonetaryAccount, selected: Boolean)
}