package nl.menio.moneybunqer

object MoneyBunqerConfiguration {

    /**
     * The base API URL to use.
     */
    const val API_URL = "https://sandbox.public.api.bunq.com/v1/"

    /**
     * Clears data on start of app like the API key. Should be used in combination with BuildConfig.DEBUG
     * to make sure no data is cleared in production.
     */
    const val CLEAR_DATA_ON_START = true

}