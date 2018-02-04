package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.icu.util.Currency
import android.text.Spannable
import android.util.Log
import android.widget.Toast
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.Payment
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.BunqPreferences
import nl.menio.moneybunqer.MoneyBunqerConfiguration.DASHBOARD_PAYMENTS_COUNT
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.data.PaymentRepository
import nl.menio.moneybunqer.data.UserRepository
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.ui.viewholders.PaymentViewHolder
import nl.menio.moneybunqer.utils.ApiUtils
import nl.menio.moneybunqer.utils.FormattingUtils
import nl.menio.moneybunqer.utils.FormattingUtils.Companion.EMPTY_AMOUNT_STRING

class DashboardViewModel : ViewModel(), PaymentViewHolder.OnPaymentClickedListener {

    val totalBalance = ObservableField<Spannable>()

    private val bunqPreferences = BunqPreferences.getInstance()
    private val monetaryAccountsRepository = MonetaryAccountRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    private val paymentsRepository = PaymentRepository.getInstance()
    private val adapter = DashboardAdapter()

    private var listener: Listener? = null

    fun init() {
        adapter.setOnPaymentClickedListener(this)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getAdapter(): DashboardAdapter = adapter

    fun showDashBoard() {
        val apiContext = ApiUtils.getApiContext()
        val userId = bunqPreferences.getDefaultUserId()

        // Check if API key is configured first
        if (apiContext == null) {
            Log.w(TAG, "API not configured!")
            listener?.onAPIKeyNotSet()
            return
        } else if (userId == null) {
            Log.w(TAG, "Default user not set!")
            listener?.onUserNotSet()
            return
        }

        // Get the user information
        userRepository.getUser(false, object : BunqConnector.OnGetUserListener {
            override fun onGetUserSuccess(user: User) {
                updateUserInformation(user)
            }

            override fun onGetUserError() {
                listener?.onError("User could not be loaded.")
            }
        })

        // Load the default users monetary accounts
        monetaryAccountsRepository.getMonetaryAccounts(false, object : BunqConnector.OnListMonetaryAccountsListener {
            override fun onListMonetaryAccountsSuccess(monetaryAccounts: List<MonetaryAccount>) {
                calculateTotalBalance(monetaryAccounts)
                loadTransactions(monetaryAccounts)
            }

            override fun onListMonetaryAccountsError() {
                listener?.onError("Error while loading bank accounts.")
            }
        })
    }

    private fun updateUserInformation(user: User) {
        listener?.onLoadAvatar(user.userPerson.avatar.image.first().attachmentPublicUuid)
    }

    private fun calculateTotalBalance(monetaryAccounts: List<MonetaryAccount>) {
        if (monetaryAccounts.isNotEmpty()) {
            val total = monetaryAccounts.sumByDouble { it.monetaryAccountBank.balance.value.toDouble() }
            val currency = monetaryAccounts.first().monetaryAccountBank.currency
            val amount = Amount(total.toString(), currency)
            totalBalance.set(FormattingUtils.getFormattedAmount(amount, true))
        } else {
            totalBalance.set(null)
        }
    }

    private fun loadTransactions(monetaryAccounts: List<MonetaryAccount>) {
        val allPayments = ArrayList<PaymentViewHolder.MonetaryAccountPayment>()
        for (monetaryAccount in monetaryAccounts) {
            val monetaryAccountId = monetaryAccount.monetaryAccountBank.id
            paymentsRepository.getPayments(false, monetaryAccountId, object : BunqConnector.OnListPaymentsListener {
                override fun onListPaymentsSuccess(payments: List<Payment>) {
                    processPayments(allPayments, payments, monetaryAccount)
                }

                override fun onListPaymentsError() {
                    listener?.onError("Could not load payments from ${monetaryAccount.monetaryAccountBank.description}")
                }
            })
        }
    }

    private fun processPayments(allPayments: MutableList<PaymentViewHolder.MonetaryAccountPayment>, newPayments: List<Payment>, monetaryAccount: MonetaryAccount) {
        val newMonetaryAccountPayments = ArrayList<PaymentViewHolder.MonetaryAccountPayment>()
        newPayments.mapTo(newMonetaryAccountPayments) { PaymentViewHolder.MonetaryAccountPayment(monetaryAccount, it) }
        allPayments.addAll(newMonetaryAccountPayments)
        allPayments.sortByDescending { it.payment.id }
        val subPayments = ArrayList<PaymentViewHolder.MonetaryAccountPayment>()
        for (i in 0 until Math.min(DASHBOARD_PAYMENTS_COUNT, allPayments.size)) {
            subPayments.add(allPayments[i])
        }
        adapter.setItems(subPayments)
    }

    override fun onPaymentClicked(payment: Payment) {
        // TODO:
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    public interface Listener {
        fun onAPIKeyNotSet()
        fun onUserNotSet()
        fun onLoadAvatar(uuid: String)
        fun onError(message: String)
    }
}