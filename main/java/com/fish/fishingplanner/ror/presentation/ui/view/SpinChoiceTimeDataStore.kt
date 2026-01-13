package com.fish.fishingplanner.ror.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SpinChoiceTimeDataStore : ViewModel(){
    val proBubbleBoPlingViList: MutableList<SpinChoiceTimeVi> = mutableListOf()
    private val _chickenIsFirstFinishPage: MutableStateFlow<Boolean> = MutableStateFlow(true)
    var chickenIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var chickenContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var proBubbleBoPlingView: SpinChoiceTimeVi

    fun chickenSetIsFirstFinishPage() {
        _chickenIsFirstFinishPage.value = false
    }
}