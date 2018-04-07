package nl.menio.moneybunqer.utils

import com.bunq.sdk.model.generated.endpoint.MonetaryAccount

class MonetaryAccountUtils {

    companion object {
        val TAG: String = MonetaryAccountUtils::class.java.simpleName

        fun getMonetaryAccountIds(monetaryAccounts: List<MonetaryAccount>) : List<Int> {
            val monetaryAccountIds = ArrayList<Int>()
            monetaryAccounts.mapTo(monetaryAccountIds) { it.monetaryAccountBank.id }
            return monetaryAccountIds
        }
    }
}