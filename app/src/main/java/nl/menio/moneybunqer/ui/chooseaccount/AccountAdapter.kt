package nl.menio.moneybunqer.ui.chooseaccount

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemAccountBinding
import nl.menio.moneybunqer.ui.viewholders.AccountViewHolder

class AccountAdapter : RecyclerView.Adapter<AccountViewHolder>() {

    private val data = ArrayList<MonetaryAccount>()

    private var onAccountClickedListener: AccountViewHolder.OnAccountClickedListener? = null

    init {
        setHasStableIds(true)
    }

    fun setOnAccountClickedListener(listener: AccountViewHolder.OnAccountClickedListener?) {
        onAccountClickedListener = listener
    }

    fun setItems(items: List<MonetaryAccount>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemAccountBinding = DataBindingUtil.inflate(inflater, R.layout.item_account, parent, false)
        return AccountViewHolder(binding, onAccountClickedListener)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long {
        return data[position].monetaryAccountBank.id.hashCode().toLong()
    }
}