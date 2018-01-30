package nl.menio.moneybunqer.utils

import android.os.Build
import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType

class ApiUtils {

    companion object {

        val TAG = ApiUtils::class.java.simpleName

        fun create(apiKey: String) {
            val deviceDescription = "MoneyBunq/${Build.DEVICE}"
            val apiContext = ApiContext.create(ApiEnvironmentType.SANDBOX, apiKey, deviceDescription)
            apiContext.save()
        }

        fun getApiContext() : ApiContext? {
            return try {
                ApiContext.restore()
            } catch (e: Exception) {
                null
            }
        }
    }
}