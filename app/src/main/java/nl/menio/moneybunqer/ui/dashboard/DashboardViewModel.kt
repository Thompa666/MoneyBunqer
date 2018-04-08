package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.util.Log
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.Payment
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.data.PaymentRepository
import nl.menio.moneybunqer.data.UserRepository
import nl.menio.moneybunqer.model.totalbalance.TotalBalanceConfiguration
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.ui.viewholders.DashboardScrapsItem
import nl.menio.moneybunqer.ui.viewholders.PaymentViewHolder
import nl.menio.moneybunqer.ui.viewholders.DashboardTotalBalanceItem
import nl.menio.moneybunqer.ui.viewholders.OnDashboardTotalBalanceActionListener
import nl.menio.moneybunqer.utils.Localizer
import nl.menio.moneybunqer.utils.MonetaryAccountUtils
import nl.menio.moneybunqer.utils.PaymentUtils
import kotlin.collections.ArrayList

class DashboardViewModel : ViewModel(), PaymentViewHolder.OnPaymentClickedListener {

    val userName = ObservableField<String>()

    private val totalBalanceActionListener = object: OnDashboardTotalBalanceActionListener {
        override fun onFilterTotalBalanceClicked(configuration: TotalBalanceConfiguration) {
            listener?.onSelectTotalBalanceAccounts(configuration)
        }
    }

    private val monetaryAccountsRepository = MonetaryAccountRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    private val paymentsRepository = PaymentRepository.getInstance()
    private val adapter = DashboardAdapter(
            totalBalanceActionListener = totalBalanceActionListener
    )

    private var listener: Listener? = null
    private var totalBalanceItem: DashboardTotalBalanceItem? = null
    private var scrapsItem: DashboardScrapsItem? = null

    fun init() {
        adapter.setOnPaymentClickedListener(this)
        userName.set(Localizer.getString(R.string.dashboard_default_username))
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getAdapter(): DashboardAdapter = adapter

    fun loadDashboard() {

        // Get the user information
        userRepository.getUser(false, object : BunqConnector.OnGetUserListener {
            override fun onGetUserSuccess(user: User) {
                updateUser(user)
            }

            override fun onGetUserError() {
                listener?.onError("User could not be loaded.")
            }
        })

        // Load the default users monetary accounts
        monetaryAccountsRepository.getMonetaryAccounts(false, object : BunqConnector.OnListMonetaryAccountsListener {
            override fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>) {
                updateMonetaryAccounts(monetaryAccounts)

                // Load payments and then update data for that
                val monetaryAccountIds = MonetaryAccountUtils.getMonetaryAccountIds(monetaryAccounts)
                paymentsRepository.getPayments(false, monetaryAccountIds, object : BunqConnector.OnListPaymentsListener {
                    override fun onListPaymentsSuccess(payments: List<Payment>) {
                        updatePayments(payments)
                    }

                    override fun onListPaymentsError() {
                        listener?.onError("Payments could not be retrieved")
                    }
                })
            }

            override fun onListMonetaryAccountsError() {
                listener?.onError("Error while loading bank accounts.")
            }
        })
    }

    fun showDashBoard() {
        val items = ArrayList<Any>()

        // Show the total balance
        val totalBalanceItem = this.totalBalanceItem
        totalBalanceItem?.let { items.add(totalBalanceItem) }

        // Show the scraps
        val scrapsItem = this.scrapsItem
        scrapsItem?.let { items.add(scrapsItem) }

        // Update the table
        adapter.setItems(items)
    }

    private fun updateUser(user: User) {
        userName.set(user.userPerson.publicNickName)
        listener?.onLoadAvatar(user.userPerson.avatar.image.first().attachmentPublicUuid)
    }

    private fun updateMonetaryAccounts(monetaryAccounts: List<MonetaryAccount>) {
        for (monetaryAccount in monetaryAccounts) {
            Log.d(TAG, "Got monetary account with ID: ${monetaryAccount.monetaryAccountBank.id}")
        }
        updateTotalBalance(monetaryAccounts)
    }

    private fun updateTotalBalance(monetaryAccounts: List<MonetaryAccount>) {
        val configuration = TotalBalanceConfiguration("Total balance", MonetaryAccountUtils.getMonetaryAccountIds(monetaryAccounts))
        val totalBalanceItem = DashboardTotalBalanceItem(configuration)
        this.totalBalanceItem = totalBalanceItem
        showDashBoard()
    }

    private fun updatePayments(payments: List<Payment>) {
        Log.d(TAG, "Will process ${payments.size} payments")
        updateScraps(payments)
    }

    private fun updateScraps(payment: List<Payment>) {
        val yesterdaysPayments = PaymentUtils.filterPaymentsForYesterday(payment)
        val yesterdaysWithdrawals = PaymentUtils.filterPaymentsWithdrawals(yesterdaysPayments)
        val scrapsItem = DashboardScrapsItem(yesterdaysWithdrawals)
        this.scrapsItem = scrapsItem
        showDashBoard()
    }

    override fun onPaymentClicked(payment: Payment) {
        // Don't do anything here
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    interface Listener {
        fun onAPIKeyNotSet()
        fun onLoadAvatar(uuid: String)
        fun onError(message: String)
        fun onSelectTotalBalanceAccounts(configuration: TotalBalanceConfiguration)
    }
}