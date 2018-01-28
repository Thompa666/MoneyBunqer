package nl.menio.moneybunqer.data

import android.content.Context
import android.content.SharedPreferences
import android.support.annotation.Nullable
import com.securepreferences.SecurePreferences

class BunqConfiguration(context: Context) {

    private val context: Context
    private val prefs: SharedPreferences

    init {
        this.context = context.applicationContext
        prefs = SecurePreferences(context)
    }

    fun setAPIKey(apiKey: String) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    fun clearAPIKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }

    @Nullable
    fun getAPIKey() : String? {
        return prefs.getString(KEY_API_KEY, null)
    }

    fun clear() {
        clearAPIKey()
    }

    companion object {
        val TAG: String = BunqConfiguration::class.java.simpleName

        private const val KEY_API_KEY = "bunqConfiguration.apiKey"

        private var singleton: BunqConfiguration? = null

        fun init(context: Context) {
            singleton = BunqConfiguration(context)
        }

        fun getInstance() : BunqConfiguration {
            return singleton ?: throw RuntimeException("Not initialized!")
        }
    }
}