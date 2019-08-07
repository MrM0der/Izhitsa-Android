package software.kanunnikoff.izhitsa.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.UiThread
import com.android.billingclient.api.BillingClient
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.*
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.*
import org.jetbrains.anko.design.longSnackbar
import software.kanunnikoff.izhitsa.Core
import software.kanunnikoff.izhitsa.Core.PRICE
import software.kanunnikoff.izhitsa.Core.USD
import software.kanunnikoff.izhitsa.R
import software.kanunnikoff.izhitsa.billing.BillingManager
import software.kanunnikoff.izhitsa.billing.BillingManager.BILLING_MANAGER_NOT_INITIALIZED
import software.kanunnikoff.izhitsa.billing.BillingProvider
import software.kanunnikoff.izhitsa.billing.MainViewController
import software.kanunnikoff.izhitsa.percentOf

class MainActivity : AppCompatActivity(), AnkoLogger, BillingProvider {
    private var billingManager: BillingManager? = null
    private var viewController: MainViewController? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.rateButton).setOnClickListener {
            browse("https://play.google.com/store/apps/details?id=$packageName")
            Answers.getInstance().logRating(
                RatingEvent()
                    .putContentName("Rating of the app in Google Play.")
                    .putContentType("app")
                    .putContentId(packageName))
        }

        findViewById<Button>(R.id.shareButton).setOnClickListener {
            share("Google Play: https://play.google.com/store/apps/details?id=$packageName", getString(R.string.app_name))
            Answers.getInstance().logShare(
                ShareEvent()
                    .putContentName("Link to the app in Google Play.")
                    .putContentType("link")
                    .putContentId(packageName))
        }

        findViewById<Button>(R.id.otherAppsButton).setOnClickListener {
            browse("https://play.google.com/store/apps/dev?id=9118553902079488918")
            Answers.getInstance().logCustom(CustomEvent("Developer's page visited."))
        }

//        findViewById<Button>(R.id.translatorButton).setOnClickListener {
//            browse("https://play.google.com/store/apps/details?id=software.kanunnikoff.yat")
//            Answers.getInstance().logCustom(CustomEvent("Yat's page visited."))
//        }

        findViewById<Button>(R.id.donateButton).setOnClickListener {
            if (!Core.isPremiumPurchased) {
                if (billingManager != null && billingManager!!.billingClientResponseCode > BILLING_MANAGER_NOT_INITIALIZED) {
                    billingManager?.initiatePurchaseFlow(Core.PREMIUM_SKU_ID, BillingClient.SkuType.INAPP)

                    Answers.getInstance().logAddToCart(
                        AddToCartEvent()
                            .putItemPrice(PRICE)
                            .putCurrency(USD)
                            .putItemName("Premium")
                            .putItemType("In-App Purchases")
                            .putItemId(Core.PREMIUM_SKU_ID))
                }
            } else {
                contentView?.longSnackbar(getString(R.string.premium_already_purchased))
            }
        }

        Core.sharedPreferences = getSharedPreferences(Core.APP_TAG, Context.MODE_PRIVATE)

// ------------------------------------------- In-App Billing

        viewController = MainViewController(this)
        billingManager = BillingManager(this, viewController!!.updateListener)

// ------------------------------------------- Firebase Analytics

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

// ------------------------------------------- Crashlytics

        Fabric.with(this, Crashlytics())
    }

    override fun isPremiumPurchased(): Boolean {
        return viewController!!.isPremiumPurchased
    }

    fun onBillingManagerSetupFinished() {
        debug("In-App Billing client is configured")
    }

    @UiThread
    fun premiumPurchased() {  // покупка подтверждена
        if (!Core.isPremiumPurchased) {
            Core.isPremiumPurchased = true

            contentView?.longSnackbar(getString(R.string.premium_purchased))

            Answers.getInstance().logStartCheckout(
                StartCheckoutEvent()
                    .putTotalPrice(PRICE)
                    .putCurrency(USD)
                    .putItemCount(1))

            Answers.getInstance().logPurchase(
                PurchaseEvent()
                    .putItemPrice(70 percentOf PRICE)
                    .putCurrency(USD)
                    .putItemName("Premium")
                    .putItemType("In-App Purchases")
                    .putItemId(Core.PREMIUM_SKU_ID)
                    .putSuccess(true))
        }
    }
}
