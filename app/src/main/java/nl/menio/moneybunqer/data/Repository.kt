package nl.menio.moneybunqer.data

import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.network.BunqConnector

abstract class Repository {

    protected val bunqPreferences = BunqPreferences.getInstance()
    protected val bunqConnector = BunqConnector.getInstance()

    companion object {
        val TAG: String = Repository::class.java.simpleName
    }
}