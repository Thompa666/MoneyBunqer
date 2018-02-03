package nl.menio.moneybunqer.network

import android.os.AsyncTask
import com.bunq.sdk.model.generated.endpoint.AttachmentPublicContent
import com.bunq.sdk.model.generated.endpoint.Avatar
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.utils.ApiUtils

class BunqConnector {

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

    fun listMonetaryAccounts(listener: OnListMonetaryAccountsListener) {
        ListMonetaryAccountsTask(listener).execute()
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
            val response = Avatar.get(ApiUtils.getApiContext(), uuid)
            return response.value
        }
    }

    private class GetAttachmentPublicContentTask(val attachmentPublicUuid: String, val listener: OnGetAttachmentPublicContentListener)
        : AsyncTask<Void, Void, ByteArray>() {
        override fun doInBackground(vararg params: Void?): ByteArray {
            val response = AttachmentPublicContent.list(ApiUtils.getApiContext(), attachmentPublicUuid)
            return response.value
        }

        override fun onPostExecute(result: ByteArray?) {
            if (result != null) {
                listener.onGetAttachmentPublicContentSuccess(result)
            } else {
                listener.onGetAttachmentPublicContentError()
            }
        }
    }


    interface OnListUsersListener {
        fun onListUsersSuccess(users: List<User>)
        fun onListUsersError()
    }

    interface OnListMonetaryAccountsListener {
        fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>)
        fun onListMonetaryAccountsError()
    }

    interface OnGetAttachmentPublicContentListener {
        fun onGetAttachmentPublicContentSuccess(bytes: ByteArray)
        fun onGetAttachmentPublicContentError()
    }
}
