package nl.menio.moneybunqer.ui.chooseuser

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ItemUserBinding
import nl.menio.moneybunqer.ui.viewholders.UserViewHolder

class UserAdapter : RecyclerView.Adapter<UserViewHolder>() {

    private val data = ArrayList<User>()

    private var onUserClickedListener: UserViewHolder.OnUserClickedListener? = null

    init {
        setHasStableIds(true)
    }

    fun setOnUserClickedListener(listener: UserViewHolder.OnUserClickedListener) {
        onUserClickedListener = listener
    }

    fun setData(users: List<User>) {
        data.clear()
        data.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemUserBinding = DataBindingUtil.inflate(inflater, R.layout.item_user, parent, false)
        return UserViewHolder(binding, onUserClickedListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long {
        val user = data[position]
        return user.userPerson.id.toLong()
    }
}