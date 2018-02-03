package nl.menio.moneybunqer

import android.app.Application
import nl.menio.moneybunqer.network.BunqConnector

class MoneyBunqerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BunqPreferences.init(this)
        BunqConnector.init()
    }
}