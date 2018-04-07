package nl.menio.moneybunqer.ui.viewholders

import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.databinding.ItemDashboardTotalBalanceBinding
import nl.menio.moneybunqer.utils.FormattingUtils
import java.io.Serializable

class DashboardTotalBalanceViewHolder(val binding: ItemDashboardTotalBalanceBinding,
                                      val clickedListener: OnDashboardTotalBalanceActionListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DashboardTotalBalanceItem) {
        val totalBalance = item.getTotalBalance()
        val totalBalanceString = FormattingUtils.getFormattedAmount(totalBalance, true)
        binding.totalBalance = totalBalanceString
        clickedListener?.let { binding.filter.setOnClickListener { clickedListener.onFilterTotalBalanceClicked() } }
    }
}

data class DashboardTotalBalanceItem(
        private val monetaryAccounts: List<MonetaryAccount>
) : Serializable {

    fun getTotalBalance() : Amount {
        val total = monetaryAccounts.sumByDouble { it.monetaryAccountBank.balance.value.toDouble() }
        val currency = monetaryAccounts.first().monetaryAccountBank.currency
        return Amount(total.toString(), currency)
    }
}

interface OnDashboardTotalBalanceActionListener {
    fun onFilterTotalBalanceClicked()
}