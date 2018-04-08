package nl.menio.moneybunqer.ui.dashboard

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ActivityDashboardBinding
import nl.menio.moneybunqer.model.totalbalance.TotalBalanceConfiguration
import nl.menio.moneybunqer.ui.ActivityRequest
import nl.menio.moneybunqer.ui.BaseActivity
import nl.menio.moneybunqer.ui.chooseaccount.ChooseAccountDialog
import nl.menio.moneybunqer.ui.onboarding.OnboardingActivity
import nl.menio.moneybunqer.ui.selectaccounts.SelectAccountsActivity

class DashboardActivity : BaseActivity(), DashboardViewModel.Listener {

    private var binding: ActivityDashboardBinding? = null
    private var viewModel: DashboardViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        viewModel?.setListener(this)
        viewModel?.init()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding?.viewModel = viewModel
        setSupportActionBar(binding?.toolbar)

        binding?.items?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.items?.itemAnimator = DefaultItemAnimator()
        binding?.items?.adapter = viewModel?.getAdapter()

        viewModel?.loadDashboard()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ActivityRequest.CHOOSE_ACCOUNT -> {
                if (resultCode == Activity.RESULT_OK) {

                    return
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAPIKeyNotSet() {
        OnboardingActivity.startActivity(this)
    }

    override fun onLoadAvatar(uuid: String) {
        binding?.avatar?.setAttachmentPublicUuid(uuid)
    }

    override fun onSelectTotalBalanceAccounts(configuration: TotalBalanceConfiguration) {
        val selectedMonetaryAccountIds = configuration.monetaryAccountIds
        SelectAccountsActivity.startActivityForResult(this, selectedMonetaryAccountIds.toIntArray())
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
