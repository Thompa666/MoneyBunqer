package nl.menio.moneybunqer.ui.selectaccounts

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ActivitySelectAccountsBinding
import nl.menio.moneybunqer.ui.ActivityData
import nl.menio.moneybunqer.ui.ActivityRequest
import nl.menio.moneybunqer.ui.BaseActivity

class SelectAccountsActivity : BaseActivity(), SelectAccountsViewModel.Listener {

    private var binding: ActivitySelectAccountsBinding? = null
    private var viewModel: SelectAccountsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedMonetaryAccountIds: IntArray = if (intent != null) {
            intent.getIntArrayExtra(ActivityData.MONETARY_ACCOUNT_IDS) ?: IntArray(0)
        } else if (savedInstanceState != null) {
            savedInstanceState.getIntArray(ActivityData.MONETARY_ACCOUNT_IDS) ?: IntArray(0)
        } else {
            IntArray(0)
        }

        viewModel = ViewModelProviders.of(this).get(SelectAccountsViewModel::class.java)
        viewModel?.setListener(this)
        viewModel?.init()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_accounts)
        binding?.viewModel = viewModel
        binding?.items?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.items?.itemAnimator = DefaultItemAnimator()
        binding?.items?.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        binding?.items?.adapter = viewModel?.getAdapter()

        viewModel?.showAccounts(selectedMonetaryAccountIds)
    }

    override fun onListMonetaryAccountsSuccess() {
        // Do nothing here
    }

    override fun onListMonetaryAccountsError() {
        Toast.makeText(this, "Could not load accounts.", Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onAccountsSelected(monetaryAccountsIds: List<Int>) {
        val result = Intent()
        result.putExtra(ActivityData.MONETARY_ACCOUNT_IDS, monetaryAccountsIds.toIntArray())
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    companion object {
        val TAG: String = SelectAccountsActivity::class.java.simpleName

        fun startActivityForResult(activity: AppCompatActivity, selectedMonetaryAccountIds: IntArray?) {
            val intent = Intent(activity, SelectAccountsActivity::class.java)
            selectedMonetaryAccountIds?.let { intent.putExtra(ActivityData.MONETARY_ACCOUNT_IDS, selectedMonetaryAccountIds) }
            activity.startActivityForResult(intent, ActivityRequest.CHOOSE_ACCOUNT)
        }
    }
}