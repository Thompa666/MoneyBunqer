package nl.menio.moneybunqer.network

import android.os.AsyncTask
import android.os.Build
import com.bunq.sdk.context.ApiContext
import com.bunq.sdk.context.ApiEnvironmentType
import com.bunq.sdk.context.BunqContext
import com.bunq.sdk.exception.TooManyRequestsException
import com.bunq.sdk.exception.UnauthorizedException
import com.bunq.sdk.model.generated.endpoint.*
import nl.menio.moneybunqer.BunqPreferences

class BunqConnector {

    fun create(apiKey: String, listener: ApiContextCreateListener) {
        CreateApiContextTask(apiKey, listener).execute()
    }

    fun getUser(listener: OnGetUserListener) {
        GetUserTask(listener).execute()
    }

    fun listMonetaryAccounts(listener: OnListMonetaryAccountsListener) {
        ListMonetaryAccountsTask(listener).execute()
    }

    fun getAttachmentPublicContent(uuid: String, listener: OnGetAttachmentPublicContentListener) {
        GetAttachmentPublicContentTask(uuid, listener).execute()
    }

    fun listPayments(monetaryAccountId: Int, listener: OnListPaymentsListener) {
        ListPaymentsTask(monetaryAccountId, listener).execute()
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

    private class GetUserTask(val listener: OnGetUserListener)
        : AsyncTask<Void, Void, User>() {
        override fun doInBackground(vararg params: Void?): User {
            ensureSession()
            val response = User.get()
            return response.value
        }

        override fun onPostExecute(result: User?) {
            if (result != null) {
                listener.onGetUserSuccess(result)
            } else {
                listener.onGetUserError()
            }
        }
    }

    private class ListMonetaryAccountsTask(val listener: OnListMonetaryAccountsListener)
        : AsyncTask<Void, Void, List<MonetaryAccount>>() {
        override fun doInBackground(vararg p0: Void?): List<MonetaryAccount> {
            ensureSession()
            val response = MonetaryAccount.list()
            return response.value
        }

        override fun onPostExecute(result: List<MonetaryAccount>?) {
            if (result != null) {
                listener.onListMonetaryAccountsSuccess(result)
            } else {
                listener.onListMonetaryAccountsError()
            }
        }
    }

    private class GetAvatarTask(val uuid: String) : AsyncTask<Void, Void, Avatar>() {
        override fun doInBackground(vararg params: Void?): Avatar {
            ensureSession()
            val response = Avatar.get()
            return response.value
        }
    }

    private class GetAttachmentPublicContentTask(val attachmentPublicUuid: String, val listener: OnGetAttachmentPublicContentListener)
        : AsyncTask<Void, Void, ByteArray>() {
        override fun doInBackground(vararg params: Void?): ByteArray? {
            ensureSession()
            try {
                val response = AttachmentPublicContent.list(attachmentPublicUuid)
                return response.value
            } catch (e: TooManyRequestsException) {
                listener.onGetAttachmentPublicContentTooManyRequestsError(attachmentPublicUuid)
            }
            return null
        }

        override fun onPostExecute(result: ByteArray?) {
            if (result != null) {
                listener.onGetAttachmentPublicContentSuccess(result)
            } else {
                listener.onGetAttachmentPublicContentError()
            }
        }
    }

    private class ListPaymentsTask(val monetaryAccountId: Int, val listener: OnListPaymentsListener) : AsyncTask<Void, Void, List<Payment>>() {
        override fun doInBackground(vararg params: Void?): List<Payment> {
            ensureSession()
            val response = Payment.list(monetaryAccountId)
            return response.value
        }

        override fun onPostExecute(result: List<Payment>?) {
            if (result != null) {
                listener.onListPaymentsSuccess(result)
            } else {
                listener.onListPaymentsError()
            }
        }
    }

    companion object {
        val TAG: String = BunqConnector::class.java.simpleName

        private var singleton: BunqConnector? = null
        private var apiContext: ApiContext? = null

        fun init() {
            singleton = BunqConnector()
        }

        fun getInstance() : BunqConnector {
            return singleton ?: throw RuntimeException("Not initialized")
        }

        fun ensureSession() {
            val loadedApiContext = apiContext
            if (loadedApiContext == null) {
                val savedApiContext = BunqPreferences.getInstance().getApiContext()
                if (savedApiContext != null) {
                    apiContext = savedApiContext
                    try {
                        BunqContext.loadApiContext(savedApiContext)
                    } catch (e: UnauthorizedException) {
                        savedApiContext.resetSession()
                        BunqContext.loadApiContext(savedApiContext)
                    }
                }
            } else {
                loadedApiContext.ensureSessionActive()
            }
        }
    }

    interface OnGetUserListener {
        fun onGetUserSuccess(user: User)
        fun onGetUserError()
    }

    interface OnListMonetaryAccountsListener {
        fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>)
        fun onListMonetaryAccountsError()
    }

    interface OnGetAttachmentPublicContentListener {
        fun onGetAttachmentPublicContentSuccess(bytes: ByteArray)
        fun onGetAttachmentPublicContentTooManyRequestsError(attachmentPublicUuid: String)
        fun onGetAttachmentPublicContentError()
    }

    interface OnListPaymentsListener {
        fun onListPaymentsSuccess(payments: List<Payment>)
        fun onListPaymentsError()
    }

    interface ApiContextCreateListener {
        fun onApiContextCreateSuccess(apiContext: ApiContext)
        fun onApiContextCreateError()
    }
}
