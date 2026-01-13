package com.fish.fishingplanner.ror.data.utils

import android.util.Log
import com.fish.fishingplanner.ror.presentation.app.FishingPlannerApp
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpinChoiceTimePushToken {

    suspend fun chickenGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume("")
                    Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}