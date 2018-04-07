package nl.menio.moneybunqer.data

import com.bunq.sdk.model.generated.endpoint.Payment
import nl.menio.moneybunqer.network.BunqConnector

class PaymentRepository : Repository() {

    private val cachedPayments = HashMap<Int, List<Payment>>()

    fun getPayments(refresh: Boolean, monetaryAccountId: Int, listener: BunqConnector.OnListPaymentsListener) {
        val cachedPaymentsForMonetaryAccount = cachedPayments[monetaryAccountId]
        if (cachedPaymentsForMonetaryAccount != null && !refresh) {
            listener.onListPaymentsSuccess(cachedPaymentsForMonetaryAccount)
        } else {
            bunqConnector.listPayments(monetaryAccountId, object : BunqConnector.OnListPaymentsListener {
                override fun onListPaymentsSuccess(payments: List<Payment>) {
                    cachedPayments[monetaryAccountId] = payments
                    listener.onListPaymentsSuccess(payments)
                }

                override fun onListPaymentsError() {
                    listener.onListPaymentsError()
                }
            })
        }
    }

    fun getPayments(refresh: Boolean, monetaryAccountIds: List<Int>, listener: BunqConnector.OnListPaymentsListener) {
        // FIXME: Cache is ignored for now
        bunqConnector.listAllPayments(monetaryAccountIds, object : BunqConnector.OnListPaymentsListener {
            override fun onListPaymentsSuccess(payments: List<Payment>) {
                listener.onListPaymentsSuccess(payments)
            }

            override fun onListPaymentsError() {
                listener.onListPaymentsError()
            }
        })
    }

    companion object {
        val TAG: String = PaymentRepository::class.java.simpleName

        private var singleton: PaymentRepository? = null

        fun init() {
            singleton = PaymentRepository()
        }

        fun getInstance() : PaymentRepository {
            return singleton ?: throw RuntimeException("Not initialized!")
        }
    }

}