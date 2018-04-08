package nl.menio.moneybunqer.ui.selectaccounts

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemSelectAccountBinding
import nl.menio.moneybunqer.ui.viewholders.OnAccountSelectionChangedListener
import nl.menio.moneybunqer.ui.viewholders.SelectAccountItem
import nl.menio.moneybunqer.ui.viewholders.SelectAccountViewHolder

class SelectAccountAdapter : RecyclerView.Adapter<SelectAccountViewHolder>() {

    private val data = ArrayList<SelectAccountItem>()

    private var accountSelectionChangedListener: OnAccountSelectionChangedListener? = null

    fun setOnAccountSelectionChangedListener(accountSelectionChangedListener: OnAccountSelectionChangedListener) {
        this.accountSelectionChangedListener = accountSelectionChangedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAccountViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemSelectAccountBinding = DataBindingUtil.inflate(inflater, R.layout.item_select_account, parent, false)
        return SelectAccountViewHolder(binding, accountSelectionChangedListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SelectAccountViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun setAccounts(accounts: List<SelectAccountItem>) {
        data.clear()
        data.addAll(accounts)
        notifyDataSetChanged()
    }

    fun getSelectedAccounts() : List<MonetaryAccount> {
        val selectedAccounts = ArrayList<MonetaryAccount>()
        data.filter { it.selected }.mapTo(selectedAccounts) { it.monetaryAccount }
        return selectedAccounts
    }
}