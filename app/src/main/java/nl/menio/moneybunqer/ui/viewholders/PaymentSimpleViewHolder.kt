package nl.menio.moneybunqer.ui.viewholders

import android.support.annotation.ColorRes
import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.`object`.Amount
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemPaymentSimpleBinding
import nl.menio.moneybunqer.utils.FormattingUtils

class PaymentSimpleViewHolder(val binding: ItemPaymentSimpleBinding, val listener: PaymentViewHolder.OnPaymentClickedListener?) : RecyclerView.ViewHolder(binding.root) {

    fun bind(monetaryAccountPayment: PaymentViewHolder.MonetaryAccountPayment) {
        val payment = monetaryAccountPayment.payment

        binding.contact.text = payment.counterpartyAlias.labelMonetaryAccount.displayName

        val amountColorResId = getAmountColor(payment.amount)
        binding.amount.text = FormattingUtils.getFormattedAmount(payment.amount, showSign = true)
        binding.amount.setTextColor(binding.root.context.resources.getColor(amountColorResId))

        if (listener != null) {
            binding.root.setOnClickListener { _ -> listener.onPaymentClicked(payment) }
        }
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
}