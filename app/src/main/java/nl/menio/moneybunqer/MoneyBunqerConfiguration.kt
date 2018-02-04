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

    /**
     * The number of payments to show on the dashboard, from ALL the accounts.s
     */
    const val DASHBOARD_PAYMENTS_COUNT = 100

    /**
     * The size in bytes of disk cache
     */
    const val CACHE_SIZE_BYTES: Long = 256 * 1024 * 1024
    const val CACHE_BITMAP_EXPIRY_HOURS: Long = 24 * 3

}