package nl.menio.moneybunqer.utils

import android.icu.text.DecimalFormat
import android.icu.util.Currency
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import com.bunq.sdk.model.generated.`object`.Amount
import java.util.*

class FormattingUtils {

    companion object {
        val TAG: String = FormattingUtils::class.java.simpleName

        const val EMPTY_AMOUNT_STRING = "-,-"
        const val PREFIX_SIGN_PLUS = "+"
        const val EMPTY_STRING = ""

        fun getFormattedAmount(amount: Amount, showCurrency: Boolean = false, showSign: Boolean = false) : Spannable {
            val locale = Locale.getDefault()
            val amountDouble = amount.value.toDouble()

            // Build the spannable string and return the builder (its spannable itself)
            val spannableBuilder = SpannableStringBuilder()
            val currencyStyleSpan = RelativeSizeSpan(.5f)
            val fractionStyleSpan = RelativeSizeSpan(.45f)
            if (showCurrency) {
                spannableBuilder.append(Currency.getInstance(amount.currency).getSymbol(locale), currencyStyleSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            val prefixSign = if (showSign && amountDouble > 0) PREFIX_SIGN_PLUS else EMPTY_STRING
            spannableBuilder.append(prefixSign)
            spannableBuilder.append(Math.floor(amountDouble).toInt().toString())
            spannableBuilder.append(DecimalFormat("#.00").format(amountDouble - Math.floor(amountDouble)), fractionStyleSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableBuilder
        }

        fun getDecimalSeparator() : String = DecimalFormat().decimalFormatSymbols.decimalSeparator.toString()
    }
}