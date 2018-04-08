package nl.menio.moneybunqer.ui.viewholders

import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.databinding.ItemDashboardTotalBalanceBinding
import nl.menio.moneybunqer.model.totalbalance.TotalBalanceConfiguration
import nl.menio.moneybunqer.utils.FormattingUtils
import nl.menio.moneybunqer.utils.MonetaryAccountUtils
import java.io.Serializable

class DashboardTotalBalanceViewHolder(private val binding: ItemDashboardTotalBalanceBinding,
                                      private val clickedListener: OnDashboardTotalBalanceActionListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DashboardTotalBalanceItem) {
        binding.title = item.configuration.title
        val totalBalance = item.getTotalBalance()
        val totalBalanceString = FormattingUtils.getFormattedAmount(totalBalance, true)
        binding.totalBalance = totalBalanceString

        clickedListener?.let { binding.filter.setOnClickListener {
            clickedListener.onFilterTotalBalanceClicked(item.configuration)
        } }
    }
}

data class DashboardTotalBalanceItem(
        val configuration: TotalBalanceConfiguration
) : Serializable {

    fun getTotalBalance() : Amount {
        val monetaryAccounts = MonetaryAccountUtils.getMonetaryAccounts(configuration.monetaryAccountIds)
        val total = monetaryAccounts.sumByDouble { it.monetaryAccountBank.balance.value.toDouble() }
        val currency = monetaryAccounts.first().monetaryAccountBank.currency
        return Amount(total.toString(), currency)
    }
}

interface OnDashboardTotalBalanceActionListener {
    fun onFilterTotalBalanceClicked(configuration: TotalBalanceConfiguration)
}