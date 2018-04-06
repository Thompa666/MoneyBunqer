package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.text.Spannable
import android.util.Log
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import com.bunq.sdk.model.generated.endpoint.Payment
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.data.MonetaryAccountRepository
import nl.menio.moneybunqer.data.PaymentRepository
import nl.menio.moneybunqer.data.UserRepository
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.ui.viewholders.PaymentViewHolder
import nl.menio.moneybunqer.utils.DateUtils
import nl.menio.moneybunqer.utils.FormattingUtils
import java.util.*
import kotlin.math.absoluteValue
import com.bunq.sdk.model.generated.`object`.Pointer

class DashboardViewModel : ViewModel(), PaymentViewHolder.OnPaymentClickedListener {

    val totalBalance = ObservableField<Spannable>()
    val amountToSave = ObservableField<Spannable>()

    private val monetaryAccountsRepository = MonetaryAccountRepository.getInstance()
    private val userRepository = UserRepository.getInstance()
    private val paymentsRepository = PaymentRepository.getInstance()
    private val adapter = DashboardAdapter()

    private var totalAmountToSave: Double = 0.0

    private var listener: Listener? = null

    fun init() {
        adapter.setOnPaymentClickedListener(this)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun getAdapter(): DashboardAdapter = adapter

    fun showDashBoard() {

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
        val now = Calendar.getInstance()

        // Get the allowed start date
        val start = Calendar.getInstance()
        start.time = now.time
        start.add(Calendar.DAY_OF_MONTH, -1)
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        // Get the allowed end date
        val end = Calendar.getInstance()
        end.time = start.time
        end.add(Calendar.DAY_OF_MONTH, 1)
        end.add(Calendar.MILLISECOND, -1)

        // Get payments from all accounts
        val allPayments = ArrayList<PaymentViewHolder.MonetaryAccountPayment>()
        for (monetaryAccount in monetaryAccounts) {
            val monetaryAccountId = monetaryAccount.monetaryAccountBank.id
            Log.d(TAG, "Monetary account ID = " + monetaryAccountId)
            paymentsRepository.getPayments(false, monetaryAccountId, object : BunqConnector.OnListPaymentsListener {
                override fun onListPaymentsSuccess(payments: List<Payment>) {
                    processPayments(allPayments, payments, monetaryAccount, start.time, end.time)
                }

                override fun onListPaymentsError() {
                    listener?.onError("Could not load payments from ${monetaryAccount.monetaryAccountBank.description}")
                }
            })
        }
    }

    private fun processPayments(allPayments: MutableList<PaymentViewHolder.MonetaryAccountPayment>,
                                newPayments: List<Payment>,
                                monetaryAccount: MonetaryAccount,
                                start: Date, // Should be start of yesterday
                                end: Date) { // Should be end of yesterday

        // Get the filtered payments from yesterday
        for (payment in newPayments) {
            val paymentDate = DateUtils.parse(payment.created)

            // Make sure the payment is from yesterday
            val isYesterday = ((paymentDate.equals(start) || paymentDate.after(start))
                    && (paymentDate.equals(end) || paymentDate.before(end)))

            // Make sure the payment is a withdrawal, does not matter what kind
            val isWithdrawal = payment.amount.value.toDouble() < 0.0

            // If is from correct date and withdrawal, we can process the amount that can be saved
            if (isYesterday && isWithdrawal) {
                allPayments.add(PaymentViewHolder.MonetaryAccountPayment(monetaryAccount, payment))
            }
        }

        // Calculate the amount that can be saved
        totalAmountToSave = allPayments.map { calculateRemainingFraction(it.payment.amount.value.toDouble()) }.sum()
        val amountSaved = Amount(totalAmountToSave.toString(), "EUR")
        amountToSave.set(FormattingUtils.getFormattedAmount(amountSaved, true, showSign = false))

        // Set the payments
        adapter.setItems(allPayments)
    }

    fun calculateRemainingFraction(amount: Double) : Double {
        val nonFractionalAmount = amount.toInt().toDouble()
        return (amount - nonFractionalAmount).absoluteValue
    }

    fun onSaveClicked() {

        // Create payment map
        val paymentMap = HashMap<String, Any>()
        val amount = Amount(totalAmountToSave.toString(), "EUR")
        paymentMap[Payment.FIELD_AMOUNT] = amount
        val pointerCounterparty = Pointer("", "IBAN")
        paymentMap[Payment.FIELD_COUNTERPARTY_ALIAS] = pointerCounterparty
        paymentMap[Payment.FIELD_DESCRIPTION] = "MoneyBunq"

        // Create payment
        //val payment = Payment.create(ApiUtils.getApiContext(), paymentMap, )

    }

    override fun onPaymentClicked(payment: Payment) {
        // Don't do anything here
    }

    companion object {
        val TAG: String = DashboardViewModel::class.java.simpleName
    }

    public interface Listener {
        fun onAPIKeyNotSet()
        fun onLoadAvatar(uuid: String)
        fun onError(message: String)
    }
}