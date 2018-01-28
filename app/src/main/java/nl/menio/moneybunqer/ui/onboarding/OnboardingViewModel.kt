package nl.menio.moneybunqer.ui.onboarding

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import nl.menio.moneybunqer.data.BunqConfiguration

class OnboardingViewModel : ViewModel() {

    public val uiApiKey = ObservableField<String>()
    public val uiCanSave = ObservableBoolean()

    private val bunqConfiguration: BunqConfiguration = BunqConfiguration.getInstance()

    private var listener: Listener? = null

    fun init() {
        // So something here
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun showCurrentConfiguration() {

        // Show current API key
        val apiKey = bunqConfiguration.getAPIKey()
        if (apiKey != null) {
            uiApiKey.set(apiKey)
        }
    }

    fun checkInputAndUpdateControls() {
        val apiKeyProvided = !TextUtils.isEmpty(uiApiKey.get())
        uiCanSave.set(apiKeyProvided)
    }

    fun save() {
        val apiKey = uiApiKey.get()
        bunqConfiguration.setAPIKey(apiKey)
        listener?.onSave()
    }

    public val apiKeyChangedListener = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            // Do nothing
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do notging
        }

        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!s.toString().equals(uiApiKey.get())) {
                uiApiKey.set(s.toString())
                checkInputAndUpdateControls()
            }
        }

    }

    public interface Listener {
        fun onSave()
    }
}