package nl.menio.moneybunqer.ui.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.databinding.ItemAccountBinding
import nl.menio.moneybunqer.utils.FormattingUtils

class AccountViewHolder(val binding: ItemAccountBinding, val listener: OnAccountClickedListener?) : RecyclerView.ViewHolder(binding.root) {

    private val bunqPreferences = BunqPreferences.getInstance()

    fun bind(monetaryAccount: MonetaryAccount) {
        binding.name.text = monetaryAccount.monetaryAccountBank.description
        binding.balance.text = FormattingUtils.getFormattedAmount(monetaryAccount.monetaryAccountBank.balance)
        binding.balance.visibility = if (bunqPreferences.getShowAccountBalances()) View.VISIBLE else View.GONE
        binding.avatar.setAttachmentPublicUuid(monetaryAccount.monetaryAccountBank.avatar.image.first().attachmentPublicUuid)

        for (alias in monetaryAccount.monetaryAccountBank.alias) {
            if (alias.type == "IBAN") {
                binding.iban.text = alias.value
                break
            }
        }

        if (listener != null) {
            binding.root.setOnClickListener({ _ -> listener.onAccountClicked(monetaryAccount)})
        }
    }

    interface OnAccountClickedListener {
        fun onAccountClicked(account: MonetaryAccount)
    }
}