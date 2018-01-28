package nl.menio.moneybunqer

import android.app.Application
import nl.menio.moneybunqer.data.BunqConfiguration

class MoneyBunqerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize settings
        BunqConfiguration.init(this)
    }
}