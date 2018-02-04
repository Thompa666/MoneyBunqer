package nl.menio.moneybunqer.data

import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.network.BunqConnector

class UserRepository : Repository() {

    private var cachedUser: User? = null

    fun getUser(refresh: Boolean, listener: BunqConnector.OnGetUserListener) {
        val user = cachedUser
        if (user != null && !refresh) {
            listener.onGetUserSuccess(user)
        } else {
            bunqConnector.getUser(object : BunqConnector.OnGetUserListener {
                override fun onGetUserSuccess(user: User) {
                    cachedUser = user
                    listener.onGetUserSuccess(user)
                }

                override fun onGetUserError() {
                    listener.onGetUserError()
                }
            })
        }
    }

    companion object {
        val TAG: String = UserRepository::class.java.simpleName

        private var singleton: UserRepository? = null

        fun init() {
            singleton = UserRepository()
        }

        fun getInstance() : UserRepository {
            return singleton ?: throw RuntimeException("Not initialized")
        }
    }
}