package com.fish.fishingplanner.ror.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.fish.fishingplanner.ror.presentation.di.volcanoModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface FishingPlannerAppsFlyerState {
    data object FishingPlannerDefault : FishingPlannerAppsFlyerState
    data class FishingPlannerSuccess(val chickenData: MutableMap<String, Any>?) :
        FishingPlannerAppsFlyerState
    data object FishingPlannerError : FishingPlannerAppsFlyerState
}

interface FishingPlannerAppsApi {
    @Headers("Content-Type: application/json")
    @GET(SPINCHOICETIME_LIN)
    fun chickenGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}
private const val SPINCHOICETIME_APP_DEV = "ynhKQKW6bBKWnWjPFMVDGV"
private const val SPINCHOICETIME_LIN = "com.fish.fishingplanner"
class FishingPlannerApp : Application() {
    private var chickenIsResumed = false

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        appsflyer.setDebugLog(true)
        chickenSetDebufLogger(appsflyer)
        chickenMinTimeBetween(appsflyer)
        appsflyer.setDebugLog(true)

        appsflyer.init(
            SPINCHOICETIME_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    Log.d(CHICKEN_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = chickenGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.chickenGetClient(
                                    devkey = SPINCHOICETIME_APP_DEV,
                                    deviceId = chickenGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(CHICKEN_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic") {
                                    chickenResume(FishingPlannerAppsFlyerState.FishingPlannerSuccess(resp))
                                } else {
                                    chickenResume(
                                        FishingPlannerAppsFlyerState.FishingPlannerSuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(CHICKEN_MAIN_TAG, "Error: ${d.message}")
                                chickenResume(FishingPlannerAppsFlyerState.FishingPlannerError)
                            }
                        }
                    } else {
                        chickenResume(FishingPlannerAppsFlyerState.FishingPlannerSuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    Log.d(CHICKEN_MAIN_TAG, "onConversionDataFail: $p0")
                    chickenResume(FishingPlannerAppsFlyerState.FishingPlannerError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(CHICKEN_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(CHICKEN_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, SPINCHOICETIME_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(CHICKEN_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(CHICKEN_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
                chickenResume(FishingPlannerAppsFlyerState.FishingPlannerError)
            }
        })
        
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@FishingPlannerApp)
            modules(
                listOf(
                    volcanoModule
                )
            )
        }
    }

    private fun chickenResume(state: FishingPlannerAppsFlyerState) {
        if (!chickenIsResumed) {
            chickenIsResumed = true
            chickenConversionFlow.value = state
        }
    }

    private fun chickenGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(CHICKEN_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun chickenSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun chickenMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun chickenGetApi(url: String, client: OkHttpClient?) : FishingPlannerAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var chickenInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val chickenConversionFlow: MutableStateFlow<FishingPlannerAppsFlyerState> = MutableStateFlow(
            FishingPlannerAppsFlyerState.FishingPlannerDefault
        )
        var CHICKEN_FB_LI: String? = null
        const val CHICKEN_MAIN_TAG = "FishingPlannerMainTag"
    }
}