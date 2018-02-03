package nl.menio.moneybunqer.ui.chooseuser

import android.arch.lifecycle.ViewModel
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.network.BunqConnector

class ChooseUserViewModel : ViewModel(), BunqConnector.OnListUsersListener, UserViewHolder.OnUserClickedListener {

    private val adapter = UserAdapter()
    private val bunqConnector = BunqConnector.getInstance()
    private val bunqPreferences = BunqPreferences.getInstance()

    private var listener: Listener? = null

    fun init() {
        adapter.setOnUserClickedListener(this)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getAdapter() : UserAdapter = adapter

    fun showUsers() {
        bunqConnector.listUsers(this)
    }

    override fun onListUsersSuccess(users: List<User>) {
        adapter.setData(users)
    }

    override fun onListUsersError() {
        listener?.onError("Could not load users.")
    }

    override fun onUserClicked(user: User) {
        val userId = user.userPerson.id
        bunqPreferences.setDefaultUserId(userId)
        listener?.onUserSelected(user)
    }

    interface Listener {
        fun onUserSelected(user: User)
        fun onError(message: String)
    }
}