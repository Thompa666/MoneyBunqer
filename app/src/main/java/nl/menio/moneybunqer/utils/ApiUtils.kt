package nl.menio.moneybunqer.utils

import android.os.AsyncTask
import android.os.Build
import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import nl.menio.moneybunqer.BunqPreferences

class ApiUtils {

    companion object {
        val TAG = ApiUtils::class.java.simpleName

        fun create(apiKey: String, listener: ApiContextCreateListener) {
            CreateApiContextTask(apiKey, listener).execute()
        }

        fun getApiContext() : ApiContext? {
            return BunqPreferences.getInstance().getApiContext()
        }
    }

    private class CreateApiContextTask(val apiKey: String, val listener: ApiContextCreateListener) : AsyncTask<Void, Void, ApiContext?>() {

        override fun doInBackground(vararg p0: Void?): ApiContext? {
            val deviceDescription = "MoneyBunq/${Build.DEVICE}"
            val apiContext = ApiContext.create(ApiEnvironmentType.PRODUCTION, apiKey, deviceDescription)
            BunqPreferences.getInstance().saveApiContext(apiContext)
            return apiContext
        }

        override fun onPostExecute(result: ApiContext?) {
            if (result != null) {
                listener.onApiContextCreateSuccess(result)
            } else {
                listener.onApiContextCreateError()
            }
        }
    }

    interface ApiContextCreateListener {
        fun onApiContextCreateSuccess(apiContext: ApiContext)
        fun onApiContextCreateError()
    }
}