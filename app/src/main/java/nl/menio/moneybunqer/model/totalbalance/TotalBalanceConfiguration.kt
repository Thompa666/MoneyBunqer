package nl.menio.moneybunqer.model.totalbalance

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TotalBalanceConfiguration(
        val title: String,
        val monetaryAccountIds: List<Int>
) : Serializable