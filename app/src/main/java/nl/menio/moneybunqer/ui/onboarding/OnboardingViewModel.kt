package nl.menio.moneybunqer.ui.onboarding

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import nl.menio.moneybunqer.utils.ApiUtils

class OnboardingViewModel : ViewModel() {

    public val uiApiKey = ObservableField<String>()
    public val uiCanSave = ObservableBoolean()

    private var listener: Listener? = null

    fun init() {
        // So something here
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun checkInputAndUpdateControls() {
        val apiKeyProvided = !TextUtils.isEmpty(uiApiKey.get())
        uiCanSave.set(apiKeyProvided)
    }

    fun save() {
        val apiKey = uiApiKey.get()
        if (!TextUtils.isEmpty(apiKey)) {
            ApiUtils.create(apiKey)
        } else {
            return
        }
    }

    public val apiKeyChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            // Do nothing
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!s.toString().equals(uiApiKey.get())) {
                uiApiKey.set(s.toString())
                checkInputAndUpdateControls()
            }
        }
    }

    companion object {
        val TAG = OnboardingViewModel::class.java.simpleName
    }

    public interface Listener {
        fun onSave()
    }
}