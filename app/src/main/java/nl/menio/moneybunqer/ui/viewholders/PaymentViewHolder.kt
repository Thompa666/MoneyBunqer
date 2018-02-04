package nl.menio.moneybunqer.ui.viewholders

import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.Payment
import nl.menio.moneybunqer.databinding.ItemPaymentBinding
import nl.menio.moneybunqer.utils.FormattingUtils
import java.io.Serializable

class PaymentViewHolder(val binding: ItemPaymentBinding, val listener: OnPaymentClickedListener?) : RecyclerView.ViewHolder(binding.root) {

    fun bind(monetaryAccountPayment: MonetaryAccountPayment) {
        val payment = monetaryAccountPayment.payment
        val monetaryAccount = monetaryAccountPayment.monetaryAccount

        binding.contact.text = payment.counterpartyAlias.labelMonetaryAccount.displayName
        binding.description.text = payment.description
        binding.amount.text = FormattingUtils.getFormattedAmount(payment.amount)

        binding.avatarAccount.setAttachmentPublicUuid(monetaryAccount.monetaryAccountBank.avatar.image.first().attachmentPublicUuid)
        //binding.avatarContact.setAttachmentPublicUuid(payment.merchantReference)

        if (listener != null) {
            binding.root.setOnClickListener { _ -> listener.onPaymentClicked(payment) }
        }
    }

    interface OnPaymentClickedListener {
        fun onPaymentClicked(payment: Payment)
    }

    data class MonetaryAccountPayment(
            val monetaryAccount: MonetaryAccount,
            val payment: Payment)
        : Serializable
}