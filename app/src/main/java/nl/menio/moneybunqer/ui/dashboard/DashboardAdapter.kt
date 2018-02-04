package nl.menio.moneybunqer.ui.dashboard

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemPaymentBinding
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            ViewType.PAYMENT.ordinal -> onCreateViewHolderPayment(parent)
            else -> null
        }
    }

    private fun onCreateViewHolderPayment(parent: ViewGroup) : PaymentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPaymentBinding = DataBindingUtil.inflate(inflater, R.layout.item_payment, parent, false)
        return PaymentViewHolder(binding, onPaymentClickedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val item = data[position]
        when (item) {
            is PaymentViewHolder.MonetaryAccountPayment -> (holder as PaymentViewHolder).bind(item)
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

    private enum class ViewType {
        PAYMENT
    }
}