package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModel
import android.util.Log
import nl.menio.moneybunqer.utils.ApiUtils

class DashboardViewModel : ViewModel() {

    private var listener: Listener? = null

    fun init() {
        // Do something here
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun showDashBoard() {

        // Check if API key is configured first
        val apiContext = ApiUtils.getApiContext()
        if (apiContext == null) {
            Log.w(TAG, "API not configured!")
            listener?.onAPIKeyNotSet()
            return
        }

        // Show the total amount saved
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    public interface Listener {
        fun onAPIKeyNotSet()
    }
}