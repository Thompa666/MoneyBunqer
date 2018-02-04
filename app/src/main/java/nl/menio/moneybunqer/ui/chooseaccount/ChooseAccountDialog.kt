package nl.menio.moneybunqer.ui.chooseaccount

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.bunq.sdk.model.generated.endpoint.MonetaryAccount
import nl.menio.moneybunqer.R
import nl.menio.moneybunqer.databinding.DialogChooseAccountBinding

class ChooseAccountDialog : DialogFragment(), ChooseAccountViewModel.Listener {

    private var binding: DialogChooseAccountBinding? = null
    private var viewModel: ChooseAccountViewModel? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this).get(ChooseAccountViewModel::class.java)
        viewModel?.setListener(this)
        viewModel?.init()

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_choose_account, container, false)
        binding?.viewModel = viewModel
        binding?.items?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding?.items?.itemAnimator = DefaultItemAnimator()
        binding?.items?.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        binding?.items?.adapter = viewModel?.getAdapter()

        viewModel?.showAccounts()
        return binding?.root
    }

    override fun onAccountSelected(monetaryAccount: MonetaryAccount) {
        Log.d(TAG, "Account selected: $monetaryAccount")
    }

    override fun onError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG: String = ChooseAccountDialog::class.java.simpleName

        fun getInstance() : ChooseAccountDialog {
            val fragment = ChooseAccountDialog()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}