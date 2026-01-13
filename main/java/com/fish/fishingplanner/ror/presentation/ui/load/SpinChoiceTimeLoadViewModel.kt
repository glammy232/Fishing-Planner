package com.fish.fishingplanner.ror.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fish.fishingplanner.ror.data.shar.SpinChoiceTimeSharedPreference
import com.fish.fishingplanner.ror.data.utils.SpinChoiceTimeSystemService
import com.fish.fishingplanner.ror.domain.usecases.SpinChoiceTimeGetAllUseCase
import com.fish.fishingplanner.ror.presentation.app.FishingPlannerApp
import com.fish.fishingplanner.ror.presentation.app.FishingPlannerAppsFlyerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpinChoiceTimeLoadViewModel(
    private val spinChoiceTimeGetAllUseCase: SpinChoiceTimeGetAllUseCase,
    private val chickenSharedPreference: SpinChoiceTimeSharedPreference,
    private val volcanoSystemService: SpinChoiceTimeSystemService
) : ViewModel() {

    private val _chickenHomeScreenState: MutableStateFlow<ChickenHomeScreenState> =
        MutableStateFlow(ChickenHomeScreenState.ChickenLoading)
    val chickenHomeScreenState = _chickenHomeScreenState.asStateFlow()

    private var chickenGetApps = false


    init {
        viewModelScope.launch {
            when (chickenSharedPreference.chickenAppState) {
                0 -> {
                    if (volcanoSystemService.volcanoIsOnline()) {
                        FishingPlannerApp.Companion.chickenConversionFlow.collect {
                            when(it) {
                                FishingPlannerAppsFlyerState.FishingPlannerDefault -> {}
                                FishingPlannerAppsFlyerState.FishingPlannerError -> {
                                    chickenSharedPreference.chickenAppState = 2
                                    _chickenHomeScreenState.value =
                                        ChickenHomeScreenState.ChickenError
                                    chickenGetApps = true
                                }
                                is FishingPlannerAppsFlyerState.FishingPlannerSuccess -> {
                                    if (!chickenGetApps) {
                                        chickenGetData(it.chickenData)
                                        chickenGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickenHomeScreenState.value =
                            ChickenHomeScreenState.ChickenNotInternet
                    }
                }
                1 -> {
                    if (volcanoSystemService.volcanoIsOnline()) {
                        if (FishingPlannerApp.Companion.CHICKEN_FB_LI != null) {
                            _chickenHomeScreenState.value =
                                ChickenHomeScreenState.ChickenSuccess(
                                    FishingPlannerApp.Companion.CHICKEN_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > chickenSharedPreference.chickenExpired) {
                            Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "Current time more then expired, repeat request")
                            FishingPlannerApp.Companion.chickenConversionFlow.collect {
                                when(it) {
                                    FishingPlannerAppsFlyerState.FishingPlannerDefault -> {}
                                    FishingPlannerAppsFlyerState.FishingPlannerError -> {
                                        _chickenHomeScreenState.value =
                                            ChickenHomeScreenState.ChickenSuccess(
                                                chickenSharedPreference.chickenSavedUrl
                                            )
                                        chickenGetApps = true
                                    }
                                    is FishingPlannerAppsFlyerState.FishingPlannerSuccess -> {
                                        if (!chickenGetApps) {
                                            chickenGetData(it.chickenData)
                                            chickenGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(FishingPlannerApp.Companion.CHICKEN_MAIN_TAG, "Current time less then expired, use saved url")
                            _chickenHomeScreenState.value =
                                ChickenHomeScreenState.ChickenSuccess(
                                    chickenSharedPreference.chickenSavedUrl
                                )
                        }
                    } else {
                        _chickenHomeScreenState.value =
                            ChickenHomeScreenState.ChickenNotInternet
                    }
                }
                2 -> {
                    _chickenHomeScreenState.value =
                        ChickenHomeScreenState.ChickenError
                }
            }
        }
    }


    private suspend fun chickenGetData(conversation: MutableMap<String, Any>?) {
        val chickenData = spinChoiceTimeGetAllUseCase.invoke(conversation)
        if (chickenSharedPreference.chickenAppState == 0) {
            if (chickenData == null) {
                chickenSharedPreference.chickenAppState = 2
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenError
            } else {
                chickenSharedPreference.chickenAppState = 1
                chickenSharedPreference.apply {
                    chickenExpired = chickenData.chickenExpires
                    chickenSavedUrl = chickenData.chickenUrl
                }
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenSuccess(chickenData.chickenUrl)
            }
        } else  {
            if (chickenData == null) {
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenSuccess(chickenSharedPreference.chickenSavedUrl)
            } else {
                chickenSharedPreference.apply {
                    chickenExpired = chickenData.chickenExpires
                    chickenSavedUrl = chickenData.chickenUrl
                }
                _chickenHomeScreenState.value =
                    ChickenHomeScreenState.ChickenSuccess(chickenData.chickenUrl)
            }
        }
    }


    sealed class ChickenHomeScreenState {
        data object ChickenLoading : ChickenHomeScreenState()
        data object ChickenError : ChickenHomeScreenState()
        data class ChickenSuccess(val data: String) : ChickenHomeScreenState()
        data object ChickenNotInternet: ChickenHomeScreenState()
    }
}