package nl.menio.moneybunqer.model.totalbalance

import com.google.gson.Gson
import nl.menio.moneybunqer.BunqPreferences
import java.io.Serializable

data class TotalBalanceConfigurations(
        val configurations: ArrayList<TotalBalanceConfiguration>
) : Serializable {

    companion object {
        val TAG: String = TotalBalanceConfigurations::class.java.simpleName
        val KEY_CONFIGURATIONS = "configurations.totalbalance"

        fun getConfigurations() : List<TotalBalanceConfiguration> {
            val prefs = BunqPreferences.getInstance().getPreferences()
            val json = prefs.getString(KEY_CONFIGURATIONS, "")
            val configurations = Gson().fromJson(json, TotalBalanceConfigurations::class.java)
            return configurations.configurations
        }

        fun setConfigurations(items: List<TotalBalanceConfiguration>) {
            val prefs = BunqPreferences.getInstance().getPreferences()
            val configurations = TotalBalanceConfigurations(ArrayList(items))
            val json = Gson().toJson(configurations)
            prefs.edit().putString(KEY_CONFIGURATIONS, json).apply()
        }
    }
}