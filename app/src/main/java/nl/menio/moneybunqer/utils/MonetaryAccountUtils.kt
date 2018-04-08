package nl.menio.moneybunqer.utils

import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.data.MonetaryAccountRepository

class MonetaryAccountUtils {

    companion object {
        val TAG: String = MonetaryAccountUtils::class.java.simpleName

        fun getMonetaryAccountIds(monetaryAccounts: List<MonetaryAccount>) : List<Int> {
            val monetaryAccountIds = ArrayList<Int>()
            monetaryAccounts.mapTo(monetaryAccountIds) { it.monetaryAccountBank.id }
            return monetaryAccountIds
        }

        fun getMonetaryAccounts(monetaryAccountIds: List<Int>) : List<MonetaryAccount> {
            return getMonetaryAccounts(monetaryAccountIds.toIntArray())
        }

        fun getMonetaryAccounts(monetaryAccountIds: IntArray) : List<MonetaryAccount> {
            val monetaryAccountRepository = MonetaryAccountRepository.getInstance()
            val monetaryAccounts = monetaryAccountRepository.getMonetaryAccounts()
            val result = ArrayList<MonetaryAccount>()
            for (monetaryAccountId in monetaryAccountIds) {
                monetaryAccounts.filterTo(result) { it.monetaryAccountBank.id == monetaryAccountId }
            }
            return monetaryAccounts
        }
    }
}