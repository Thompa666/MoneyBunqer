package nl.menio.moneybunqer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.StringRes

class Localizer(val context: Context) {

    fun getString(@StringRes stringResId: Int) : String {
        return context.getString(stringResId)
    }

    companion object {
        val TAG: String = Localizer::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private var singleton: Localizer? = null

        fun init(context: Context) {
            singleton = Localizer(context)
        }

        fun getInstance() : Localizer {
            return singleton ?: throw RuntimeException("Not initialized!")
        }

        fun getString(@StringRes stringResId: Int) : String {
            return getInstance().getString(stringResId)
        }
    }
}