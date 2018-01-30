package nl.menio.moneybunqer

import android.app.Application
import nl.menio.moneybunqer.data.BunqConfiguration
import nl.menio.moneybunqer.network.SecurityUtils

class MoneyBunqerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize settings
        BunqConfiguration.init(this)
        if (MoneyBunqerConfiguration.CLEAR_DATA_ON_START && BuildConfig.DEBUG) {
            BunqConfiguration.getInstance().clear()
        }

        // Initialize networking
        SecurityUtils.init()
    }
}