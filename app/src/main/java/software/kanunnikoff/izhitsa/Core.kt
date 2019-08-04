package software.kanunnikoff.izhitsa

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.*

object Core {
    const val APP_TAG = "Izhitsa"
    private const val IS_PREMIUM_PURCHASED = "is_premium_purchased"
    const val PREMIUM_SKU_ID = "premium"

    val PRICE = 1.usd()
    val USD: Currency = Currency.getInstance("USD")

    var sharedPreferences: SharedPreferences? = null

    var isPremiumPurchased: Boolean
        get() = sharedPreferences!!.getBoolean(IS_PREMIUM_PURCHASED, false)
        set(value) {
            sharedPreferences?.edit {
                putBoolean(IS_PREMIUM_PURCHASED, value)
            }
        }
}