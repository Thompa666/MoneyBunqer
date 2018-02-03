package nl.menio.moneybunqer.ui.onboarding

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.Toast
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.ActivityOnboardingBinding
import nl.menio.moneybunqer.ui.BaseActivity

class OnboardingActivity : BaseActivity(), OnboardingViewModel.Listener {

    private var binding: ActivityOnboardingBinding? = null
    private var viewModel: OnboardingViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)
        viewModel?.setListener(this)
        viewModel?.init()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        binding?.viewModel = viewModel
    }

    override fun onSave() {
        finish()
    }

    override fun onError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG: String = OnboardingActivity::class.java.simpleName

        fun startActivity(context: Context) {
            val intent = Intent(context, OnboardingActivity::class.java)
            context.startActivity(intent)
        }
    }
}