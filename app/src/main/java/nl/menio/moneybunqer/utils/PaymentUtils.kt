package nl.menio.moneybunqer.utils

import com.bunq.sdk.model.generated.endpoint.Payment
import java.util.*
import kotlin.collections.ArrayList

class PaymentUtils {

    companion object {
        val TAG: String = PaymentUtils::class.java.simpleName

        fun filterPaymentsForToday(payments: List<Payment>) : List<Payment> {
            val period = DateUtils.today()
            return filterPaymentsForPeriod(payments, period.start, period.end)
        }

        fun filterPaymentsForYesterday(payments: List<Payment>) : List<Payment> {
            val period = DateUtils.yesterday()
            return filterPaymentsForPeriod(payments, period.start, period.end)
        }

        fun filterPaymentsForPeriod(payments: List<Payment>, start: Date, end: Date) : List<Payment> {
            val result = ArrayList<Payment>()
            for (payment in payments) {
                val paymentDate = DateUtils.parse(payment.created)
                if ((paymentDate == start || paymentDate.after(start))
                        && (paymentDate == end || paymentDate.before(end))) {
                    result.add(payment)
                }
            }
            return result
        }

        fun filterPaymentsWithdrawals(payments: List<Payment>) : List<Payment> {
            val result = ArrayList<Payment>()
            payments.filterTo(result) { it.amount.value.toDouble() < 0.0 }
            return result
        }
    }
}