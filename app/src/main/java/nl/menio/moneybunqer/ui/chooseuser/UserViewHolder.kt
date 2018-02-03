package nl.menio.moneybunqer.ui.chooseuser

import android.support.v7.widget.RecyclerView
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.databinding.ItemUserBinding

class UserViewHolder(val binding: ItemUserBinding, val listener: OnUserClickedListener?) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.displayName = user.userPerson.displayName
        binding.legalName = user.userPerson.legalName
        binding.dateOfBirth = "${user.userPerson.dateOfBirth} (${user.userPerson.countryOfBirth})"

        if (listener != null) {
            binding.root.setOnClickListener { _ -> listener.onUserClicked(user)}
        }
    }

    interface OnUserClickedListener {
        fun onUserClicked(user: User)
    }
}