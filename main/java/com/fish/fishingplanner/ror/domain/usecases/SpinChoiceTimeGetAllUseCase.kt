package com.fish.fishingplanner.ror.domain.usecases

import android.util.Log
import com.fish.fishingplanner.ror.data.repo.SpinChoiceTimeRepository
import com.fish.fishingplanner.ror.data.utils.SpinChoiceTimePushToken
import com.fish.fishingplanner.ror.data.utils.SpinChoiceTimeSystemService
import com.fish.fishingplanner.ror.domain.model.SpinChoiceTimeEntity
import com.fish.fishingplanner.ror.domain.model.SpinChoiceTimeParam
import com.fish.fishingplanner.ror.presentation.app.FishingPlannerApp

class SpinChoiceTimeGetAllUseCase(
    private val spinChoiceTimeRepository: SpinChoiceTimeRepository,
    private val volcanoSystemService: SpinChoiceTimeSystemService,
    private val spinChoiceTimePushToken: SpinChoiceTimePushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : SpinChoiceTimeEntity?{
        val params = SpinChoiceTimeParam(
            chickenLocale = volcanoSystemService.volcanoGetLocale(),
            chickenPushToken = spinChoiceTimePushToken.chickenGetToken(),
            chickenAfId = volcanoSystemService.volcanoGetAppsflyerId()
        )
        Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "Params for request: $params")
        return spinChoiceTimeRepository.chickenGetClient(params, conversion)
    }



}