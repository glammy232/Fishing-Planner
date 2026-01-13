package com.fish.fishingplanner.ror.domain.model

import com.google.gson.annotations.SerializedName


private const val SPINCHOICETIME_A = "com.fish.fishingplanner"
data class SpinChoiceTimeParam (
    @SerializedName("af_id")
    val chickenAfId: String,
    @SerializedName("bundle_id")
    val chickenBundleId: String = SPINCHOICETIME_A,
    @SerializedName("os")
    val chickenOs: String = "Android",
    @SerializedName("store_id")
    val chickenStoreId: String = SPINCHOICETIME_A,
    @SerializedName("locale")
    val chickenLocale: String,
    @SerializedName("push_token")
    val chickenPushToken: String,
    @SerializedName("firebase_project_id")
    val chickenFirebaseProjectId: String = "fishing-planner-280b7",

    )