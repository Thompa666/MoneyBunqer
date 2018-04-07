package nl.menio.moneybunqer

import android.app.Application
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.data.PaymentRepository
import nl.menio.moneybunqer.data.UserRepository
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.utils.AttachmentManager
import nl.menio.moneybunqer.utils.Localizer

class MoneyBunqerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Needed for all other dependencies
        Localizer.init(this)
        BunqPreferences.init(this)
        BunqConnector.init()

        // Initialize the repositories
        UserRepository.init()
        MonetaryAccountRepository.init()
        PaymentRepository.init()
        AttachmentManager.init(this)
    }
}