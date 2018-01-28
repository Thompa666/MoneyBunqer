package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModel
import android.util.Log
import nl.menio.moneybunqer.data.BunqConfiguration

class DashboardViewModel : ViewModel() {

    private val bunqConfiguration = BunqConfiguration.getInstance()

    private var listener: Listener? = null

    fun init() {
        // Do something here
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun showDashBoard() {

        // Check if API key is configured first
        if (!isApiKeyConfigured()) {
            Log.w(TAG, "API key not configured!")
            listener?.onAPIKeyNotSet()
            return
        }

        // Show the total amount saved
    }

    fun isApiKeyConfigured() : Boolean {
        val apiKey = bunqConfiguration.getAPIKey()
        return apiKey != null
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    public interface Listener {
        fun onAPIKeyNotSet()
    }
}