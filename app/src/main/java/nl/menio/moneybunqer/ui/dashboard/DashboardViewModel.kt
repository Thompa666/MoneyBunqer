package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.util.Log
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.utils.ApiUtils

class DashboardViewModel : ViewModel() {

    val totalBalance = ObservableField<String>()

    private val bunqConnector = BunqConnector.getInstance()
    private val bunqPreferences = BunqPreferences.getInstance()

    private var listener: Listener? = null

    fun init() {
        // Do something here
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun showDashBoard() {
        val apiContext = ApiUtils.getApiContext()
        val userId = bunqPreferences.getDefaultUserId()

        // Check if API key is configured first
        if (apiContext == null) {
            Log.w(TAG, "API not configured!")
            listener?.onAPIKeyNotSet()
            return
        } else if (userId == null) {
            Log.w(TAG, "Default user not set!")
            listener?.onUserNotSet()
            return
        }

        // Load the default users monetary accounts
        bunqConnector.listMonetaryAccounts(object : BunqConnector.OnListMonetaryAccountsListener {
            override fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>) {
                for (monetaryAccount in monetaryAccounts) {
                    Log.d(TAG, "Got monetary account: $monetaryAccount")
                }
                calculateTotalBalance(monetaryAccounts)
            }

            override fun onListMonetaryAccountsError() {
                listener?.onError("Error while loading bank accounts.")
            }
        })
    }

    private fun calculateTotalBalance(monetaryAccounts: List<MonetaryAccount>) {
        val total = monetaryAccounts.sumByDouble { it.monetaryAccountBank.balance.value.toDouble() }
        totalBalance.set("$total")
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    public interface Listener {
        fun onAPIKeyNotSet()
        fun onUserNotSet()
        fun onError(message: String)
    }
}