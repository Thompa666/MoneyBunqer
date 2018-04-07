package nl.menio.moneybunqer.ui.viewholders

import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.`object`.Amount
import com.bunq.sdk.model.generated.endpoint.Payment
import nl.menio.moneybunqer.databinding.ItemDashboardScrapsBinding
import nl.menio.moneybunqer.utils.FormattingUtils
import nl.menio.moneybunqer.utils.ScrapsUtils
import java.io.Serializable

class DashboardScrapsViewHolder(val binding: ItemDashboardScrapsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DashboardScrapsItem) {
        val totalScraps = item.getScrapsTotal()
        binding.amountToSave = FormattingUtils.getFormattedAmount(totalScraps, true)
    }
}

data class DashboardScrapsItem(
        val payments: List<Payment>
) : Serializable {

    fun getScrapsTotal() : Amount {
        return ScrapsUtils.calculateScraps(payments)
    }
}