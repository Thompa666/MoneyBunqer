package nl.menio.moneybunqer.ui.dashboard

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ActivityDashboardBinding
import nl.menio.moneybunqer.ui.BaseActivity
import nl.menio.moneybunqer.ui.onboarding.OnboardingActivity

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

        setContentView(R.layout.activity_dashboard)
    }

    override fun onResume() {
        super.onResume()
        viewModel?.showDashBoard()
    }

    override fun onAPIKeyNotSet() {
        OnboardingActivity.startActivity(this)
    }
}
