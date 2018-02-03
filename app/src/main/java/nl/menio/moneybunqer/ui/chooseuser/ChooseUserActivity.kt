package nl.menio.moneybunqer.ui.chooseuser

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.bunq.sdk.model.generated.endpoint.User
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ActivityChooseUserBinding
import nl.menio.moneybunqer.ui.BaseActivity

class ChooseUserActivity : BaseActivity(), ChooseUserViewModel.Listener {

    var binding: ActivityChooseUserBinding? = null
    var viewModel: ChooseUserViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(ChooseUserViewModel::class.java)
        viewModel?.setListener(this)
        viewModel?.init()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_user)
        binding?.viewModel = viewModel
        binding?.items?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.items?.itemAnimator = DefaultItemAnimator()
        binding?.items?.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding?.items?.adapter = viewModel?.getAdapter()

        viewModel?.showUsers()
    }

    override fun onUserSelected(user: User) {
        Toast.makeText(this, "User selected: ${user.userPerson.displayName}", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG = ChooseUserActivity::class.java.simpleName

        fun startActivity(context: Context) {
            val intent = Intent(context, ChooseUserActivity::class.java)
            context.startActivity(intent)
        }
    }
}