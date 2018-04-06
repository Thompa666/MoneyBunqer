package nl.menio.moneybunqer.ui.viewholders

import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.Payment
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.databinding.ItemPaymentBinding
import nl.menio.moneybunqer.utils.FormattingUtils
import java.io.Serializable

class PaymentViewHolder(val binding: ItemPaymentBinding, val listener: OnPaymentClickedListener?) : RecyclerView.ViewHolder(binding.root) {

    val monetaryAccountRepository = MonetaryAccountRepository.getInstance()

    fun bind(monetaryAccountPayment: MonetaryAccountPayment) {
        val payment = monetaryAccountPayment.payment
        val monetaryAccount = monetaryAccountPayment.monetaryAccount

        binding.contact.text = payment.counterpartyAlias.labelMonetaryAccount.displayName

        binding.description.text = payment.description
        binding.description.visibility = if (TextUtils.isEmpty(payment.description)) View.GONE else View.VISIBLE

        val amountColorResId = getAmountColor(payment.amount)
        binding.amount.text = FormattingUtils.getFormattedAmount(payment.amount, showSign = true)
        binding.amount.setTextColor(binding.root.context.resources.getColor(amountColorResId))

        binding.account.text = monetaryAccount.monetaryAccountBank.description

        val accountAvatarUuid: String? = monetaryAccount.monetaryAccountBank?.avatar?.image?.first()?.attachmentPublicUuid
        accountAvatarUuid?.let { binding.avatarAccount.setAttachmentPublicUuid(accountAvatarUuid) }
        val contactAvatarUuid: String? = payment.counterpartyAlias?.labelMonetaryAccount?.avatar?.image?.first()?.attachmentPublicUuid
        contactAvatarUuid?.let { binding.avatarContact.setAttachmentPublicUuid(contactAvatarUuid) }

        if (listener != null) {
            binding.root.setOnClickListener { _ -> listener.onPaymentClicked(payment) }
        }
    }

    interface OnPaymentClickedListener {
        fun onPaymentClicked(payment: Payment)
    }

    companion object {
        val TAG: String = PaymentViewHolder::class.java.simpleName

        @ColorRes
        fun getAmountColor(amount: Amount) : Int {
            val amountDouble = amount.value.toDouble()
            return when {
                amountDouble > 0 -> R.color.bunq_green_dark
                amountDouble < 0 -> R.color.bunq_orange
                else -> R.color.gray_light
            }
        }
    }

    data class MonetaryAccountPayment(
            val monetaryAccount: MonetaryAccount,
            val payment: Payment)
        : Serializable
}