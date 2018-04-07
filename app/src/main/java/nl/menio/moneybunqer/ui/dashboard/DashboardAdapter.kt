package nl.menio.moneybunqer.ui.dashboard

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemDashboardScrapsBinding
import nl.menio.moneybunqer.databinding.ItemDashboardTotalBalanceBinding
import nl.menio.moneybunqer.databinding.ItemPaymentSimpleBinding
import nl.menio.moneybunqer.ui.viewholders.*

class DashboardAdapter(
        private val totalBalanceActionListener: OnDashboardTotalBalanceActionListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    fun getItems() : ArrayList<Any> = data

    fun setOnPaymentClickedListener(listener: PaymentViewHolder.OnPaymentClickedListener) {
        onPaymentClickedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.TOTAL_BALANCE.ordinal -> onCreateViewHolderTotalBalance(parent)
            ViewType.SCRAPS.ordinal -> onCreateViewHolderScraps(parent)
            ViewType.PAYMENT.ordinal -> onCreateViewHolderPayment(parent)
            else -> DefaultViewHolder(parent.context)
        }
    }

    private fun onCreateViewHolderTotalBalance(parent: ViewGroup) : DashboardTotalBalanceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemDashboardTotalBalanceBinding = DataBindingUtil.inflate(inflater, R.layout.item_dashboard_total_balance, parent, false)
        return DashboardTotalBalanceViewHolder(binding, totalBalanceActionListener)
    }

    private fun onCreateViewHolderScraps(parent: ViewGroup) : DashboardScrapsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemDashboardScrapsBinding = DataBindingUtil.inflate(inflater, R.layout.item_dashboard_scraps, parent, false)
        return DashboardScrapsViewHolder(binding)
    }

    private fun onCreateViewHolderPayment(parent: ViewGroup) : PaymentSimpleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPaymentSimpleBinding = DataBindingUtil.inflate(inflater, R.layout.item_payment_simple, parent, false)
        return PaymentSimpleViewHolder(binding, onPaymentClickedListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        when (item) {
            is DashboardTotalBalanceItem -> (holder as DashboardTotalBalanceViewHolder).bind(item)
            is DashboardScrapsItem -> (holder as DashboardScrapsViewHolder).bind(item)
            is PaymentViewHolder.MonetaryAccountPayment -> (holder as PaymentSimpleViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is DashboardTotalBalanceItem -> ViewType.TOTAL_BALANCE.ordinal
            is DashboardScrapsItem -> ViewType.SCRAPS.ordinal
            is PaymentViewHolder.MonetaryAccountPayment -> ViewType.PAYMENT.ordinal
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemId(position: Int): Long {
        val item = data[position]
        return when (item) {
            is DashboardTotalBalanceItem -> 0x000000000
            is DashboardScrapsItem -> 0x100000000
            is PaymentViewHolder.MonetaryAccountPayment -> 0xF00000000 + item.payment.id
            else -> super.getItemId(position)
        }
    }

    private enum class ViewType {
        TOTAL_BALANCE,
        SCRAPS,
        PAYMENT
    }
}