package nl.menio.moneybunqer.data

import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.network.BunqConnector

class MonetaryAccountRepository : Repository() {

    private var cachedMonetaryAccounts: List<MonetaryAccount>? = null

    fun getMonetaryAccounts(refresh: Boolean, listener: BunqConnector.OnListMonetaryAccountsListener) {
        val monetaryAccounts = cachedMonetaryAccounts
        if (monetaryAccounts != null && !refresh) {
            listener.onListMonetaryAccountsSuccess(monetaryAccounts)
        } else {
            bunqConnector.listMonetaryAccounts(object : BunqConnector.OnListMonetaryAccountsListener {
                override fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>) {
                    cachedMonetaryAccounts = monetaryAccounts
                    listener.onListMonetaryAccountsSuccess(monetaryAccounts)
                }

                override fun onListMonetaryAccountsError() {
                    listener.onListMonetaryAccountsError()
                }
            })
        }
    }

    companion object {
        val TAG: String = MonetaryAccountRepository::class.java.simpleName

        private var singleton: MonetaryAccountRepository? = null

        fun init() {
            singleton = MonetaryAccountRepository()
        }

        fun getInstance() : MonetaryAccountRepository {
            return singleton ?: throw RuntimeException("Not initialized!")
        }
    }
}