package nl.menio.moneybunqer.ui.selectaccounts

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.ui.viewholders.OnAccountSelectionChangedListener
import nl.menio.moneybunqer.ui.viewholders.SelectAccountItem
import nl.menio.moneybunqer.utils.MonetaryAccountUtils

class SelectAccountsViewModel : ViewModel() {

    val canSave = ObservableBoolean()

    private val monetaryAccountRepository = MonetaryAccountRepository.getInstance()
    private val adapter = SelectAccountAdapter()

    private val accountSelectionChangedListener = object : OnAccountSelectionChangedListener {
        override fun onAccountSelectionChanged(monetaryAccount: MonetaryAccount, selected: Boolean) {
            updateControls()
        }
    }

    private var listener: Listener? = null

    fun init() {
        adapter.setOnAccountSelectionChangedListener(accountSelectionChangedListener)
        updateControls()
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getAdapter() : SelectAccountAdapter = adapter

    fun showAccounts(selectedMonetaryAccounts: IntArray) {
        monetaryAccountRepository.getMonetaryAccounts(false, object : BunqConnector.OnListMonetaryAccountsListener {
            override fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>) {
                listener?.onListMonetaryAccountsSuccess()
                showAccounts(monetaryAccounts, selectedMonetaryAccounts)
            }

            override fun onListMonetaryAccountsError() {
                listener?.onListMonetaryAccountsError()
            }
        })
    }

    fun showAccounts(monetaryAccounts: List<MonetaryAccount>, selectedMonetaryAccounts: IntArray) {
        val items = ArrayList<SelectAccountItem>()
        monetaryAccounts.mapTo(items) { SelectAccountItem(it, false) }
        adapter.setAccounts(items)
        updateControls()
    }

    fun onDoneClicked() {
        val selectedMonetaryAccounts = adapter.getSelectedAccounts()
        val selectedMonetaryAccountIds = MonetaryAccountUtils.getMonetaryAccountIds(selectedMonetaryAccounts)
        listener?.onAccountsSelected(selectedMonetaryAccountIds)
    }

    fun updateControls() {
        canSave.set(adapter.getSelectedAccounts().isNotEmpty())
    }

    interface Listener {
        fun onListMonetaryAccountsSuccess()
        fun onListMonetaryAccountsError()
        fun onAccountsSelected(monetaryAccountsIds: List<Int>)
    }
}