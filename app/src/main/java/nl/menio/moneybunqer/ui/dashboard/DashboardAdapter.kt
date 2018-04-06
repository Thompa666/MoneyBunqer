package nl.menio.moneybunqer.ui.dashboard

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemPaymentBinding
import nl.menio.moneybunqer.databinding.ItemPaymentSimpleBinding
import nl.menio.moneybunqer.network.BunqConnector
import nl.menio.moneybunqer.ui.viewholders.DefaultViewHolder
import nl.menio.moneybunqer.ui.viewholders.PaymentSimpleViewHolder
import nl.menio.moneybunqer.ui.viewholders.PaymentViewHolder

class DashboardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = ArrayList<Any>()

    private var onPaymentClickedListener: PaymentViewHolder.OnPaymentClickedListener? = null

    init {
        setHasStableIds(true)
    }

    fun setItems(items: List<Any>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnPaymentClickedListener(listener: PaymentViewHolder.OnPaymentClickedListener) {
        onPaymentClickedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.PAYMENT.ordinal -> onCreateViewHolderPayment(parent)
            else -> DefaultViewHolder(parent.context)
        }
    }

    private fun onCreateViewHolderPayment(parent: ViewGroup) : PaymentSimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPaymentSimpleBinding = DataBindingUtil.inflate(inflater, R.layout.item_payment_simple, parent, false)
        return PaymentSimpleViewHolder(binding, onPaymentClickedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (item) {
            is PaymentViewHolder.MonetaryAccountPayment -> (holder as PaymentSimpleViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is PaymentViewHolder.MonetaryAccountPayment -> ViewType.PAYMENT.ordinal
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemId(position: Int): Long {
        val item = data[position]
        return when (item) {
            is PaymentViewHolder.MonetaryAccountPayment -> 0x100000000 + item.payment.id
            else -> super.getItemId(position)
        }
    }

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

    private enum class ViewType {
        PAYMENT
    }
}