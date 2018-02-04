package nl.menio.moneybunqer.network

import android.os.AsyncTask
import com.bunq.sdk.exception.TooManyRequestsException
import com.bunq.sdk.model.generated.endpoint.*
import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.utils.ApiUtils

class BunqConnector {

    private val bunqPreferences = BunqPreferences.getInstance()

    companion object {
        val TAG: String = BunqConnector::class.java.simpleName

        private var singleton: BunqConnector? = null

        fun init() {
            singleton = BunqConnector()
        }

        fun getInstance() : BunqConnector {
            return singleton ?: throw RuntimeException("Not initialized")
        }
    }

    fun listUsers(listener: OnListUsersListener) {
        ListUsersTask(listener).execute()
    }

    fun getUser(listener: OnGetUserListener) {
        GetUSerTask(listener).execute()
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

    private class ListUsersTask(val listener: OnListUsersListener)
        : AsyncTask<Void, Void, List<User>>() {
        override fun doInBackground(vararg params: Void?): List<User> {
            val apiContext = ApiUtils.getApiContext()
            val response = User.list(apiContext)
            return response.value
        }

        override fun onPostExecute(result: List<User>?) {
            if (result != null) {
                listener.onListUsersSuccess(result)
            } else {
                listener.onListUsersError()
            }
        }
    }

    private class GetUSerTask(val listener: OnGetUserListener)
        : AsyncTask<Void, Void, User>() {
        override fun doInBackground(vararg params: Void?): User {
            val userId = BunqPreferences.getInstance().getDefaultUserId()
            val response = User.get(ApiUtils.getApiContext(), userId)
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
            val apiContext = ApiUtils.getApiContext()
            val userId = BunqPreferences.getInstance().getDefaultUserId()
            val response = MonetaryAccount.list(apiContext, userId)
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
            val apiContext = ApiUtils.getApiContext()
            val response = Avatar.get(apiContext, uuid)
            return response.value
        }
    }

    private class GetAttachmentPublicContentTask(val attachmentPublicUuid: String, val listener: OnGetAttachmentPublicContentListener)
        : AsyncTask<Void, Void, ByteArray>() {
        override fun doInBackground(vararg params: Void?): ByteArray? {
            try {
                val apiContext = ApiUtils.getApiContext()
                val response = AttachmentPublicContent.list(apiContext, attachmentPublicUuid)
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
            val apiContext = ApiUtils.getApiContext()
            val userId = BunqPreferences.getInstance().getDefaultUserId()
            val response = Payment.list(apiContext, userId, monetaryAccountId)
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

    interface OnListUsersListener {
        fun onListUsersSuccess(users: List<User>)
        fun onListUsersError()
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
}
