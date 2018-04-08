package nl.menio.moneybunqer

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.bunq.sdk.context.ApiContext
import com.securepreferences.SecurePreferences

class BunqPreferences(private val context: Context) {

    private val prefs = SecurePreferences(context)

    fun getPreferences() : SharedPreferences {
        return prefs
    }

    fun saveApiContext(apiContext: ApiContext) {
        prefs.edit().putString(KEY_API_CONTEXT, apiContext.toJson()).apply()
    }

    fun clearApiContext() {
        prefs.edit().remove(KEY_API_CONTEXT).apply()
    }

    fun getApiContext() : ApiContext? {
        val apiContextJson = prefs.getString(KEY_API_CONTEXT, null)
        return if (apiContextJson != null) {
            ApiContext.fromJson(apiContextJson)
        } else {
            null
        }
    }

    fun setDefaultUserId(userId: Int) {
        prefs.edit().putInt(KEY_DEFAULT_USER_ID, userId).apply()
    }

    fun clearDefaultUserId() {
        prefs.edit().remove(KEY_DEFAULT_USER_ID).apply()
    }

    fun getDefaultUserId() : Int? {
        val userId = prefs.getInt(KEY_DEFAULT_USER_ID, Int.MIN_VALUE)
        return if (userId != Int.MIN_VALUE) userId else null
    }

    fun setShowAccountBalances(showAccountBalances: Boolean) {
        prefs.edit().putBoolean(KEY_SHOW_ACCOUNT_BALANCES, showAccountBalances).apply()
    }

    fun getShowAccountBalances() : Boolean {
        return prefs.getBoolean(KEY_SHOW_ACCOUNT_BALANCES, SHOW_ACCOUNT_BALANCES_DEFAULT)
    }

    companion object {
        val TAG = BunqPreferences::class.java.simpleName

        const val KEY_API_CONTEXT = "bunq.apiContext"
        const val KEY_DEFAULT_USER_ID = "bunq.defaultUserId"
        const val KEY_SHOW_ACCOUNT_BALANCES = "moneyBunq.showAccountBalances"

        const val SHOW_ACCOUNT_BALANCES_DEFAULT = true

        @SuppressLint("StaticFieldLeak")
        private var singleton: BunqPreferences? = null

        fun init(context: Context) {
            singleton = BunqPreferences(context)
        }

        fun getInstance() : BunqPreferences {
            return singleton ?: throw RuntimeException("Not initialized!")
        }
    }
}