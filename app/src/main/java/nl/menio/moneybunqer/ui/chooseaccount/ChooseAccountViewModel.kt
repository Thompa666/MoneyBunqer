package nl.menio.moneybunqer.ui.chooseaccount

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.ui.viewholders.AccountViewHolder

class ChooseAccountViewModel : ViewModel(), BunqConnector.OnListMonetaryAccountsListener, AccountViewHolder.OnAccountClickedListener {

    private val monetaryAccountRepository = MonetaryAccountRepository.getInstance()
    private val adapter = AccountAdapter()

    private var listener: Listener? = null

    fun init() {
        adapter.setOnAccountClickedListener(this)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getAdapter() : AccountAdapter = adapter

    fun showAccounts() {
        monetaryAccountRepository.getMonetaryAccounts(false, this)
        monetaryAccountRepository.getMonetaryAccounts(true, this)
    }

    override fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>) {
        adapter.setItems(monetaryAccounts)
    }

    override fun onAccountClicked(account: MonetaryAccount) {
        Log.d(TAG, "Account clicked: $account")
    }

    override fun onListMonetaryAccountsError() {
        listener?.onError("Error while loading accounts.")
    }

    companion object {
        val TAG: String = ChooseAccountViewModel::class.java.simpleName
    }

    interface Listener {
        fun onAccountSelected(monetaryAccount: MonetaryAccount)
        fun onError(message: String)
    }
}