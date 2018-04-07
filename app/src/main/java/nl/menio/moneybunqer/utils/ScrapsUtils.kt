package nl.menio.moneybunqer.utils

import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.Payment
import kotlin.math.absoluteValue

class ScrapsUtils {

    companion object {
        val TAG: String = ScrapsUtils::class.java.simpleName

        fun calculateScraps(payments: List<Payment>) : Amount {
            val withdrawalPayments = PaymentUtils.filterPaymentsWithdrawals(payments)
            val totalAmountToSave = withdrawalPayments.map { calculateRemainingFraction(it.amount.value.toDouble()) }.sum()
            return Amount(totalAmountToSave.toString(), "EUR")
        }

        fun calculateRemainingFraction(amount: Double) : Double {
            val nonFractionalAmount = amount.toInt().toDouble()
            return (amount - nonFractionalAmount).absoluteValue
        }
    }
}