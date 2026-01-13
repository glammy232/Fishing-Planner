package com.fish.fishingplanner.ror.domain.model

import com.google.gson.annotations.SerializedName


data class SpinChoiceTimeEntity (
    @SerializedName("ok")
    val chickenOk: String,
    @SerializedName("url")
    val chickenUrl: String,
    @SerializedName("expires")
    val chickenExpires: Long,
)