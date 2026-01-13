package com.fish.fishingplanner.ror.presentation.di

import com.fish.fishingplanner.ror.data.repo.SpinChoiceTimeRepository
import com.fish.fishingplanner.ror.data.shar.SpinChoiceTimeSharedPreference
import com.fish.fishingplanner.ror.data.utils.SpinChoiceTimePushToken
import com.fish.fishingplanner.ror.data.utils.SpinChoiceTimeSystemService
import com.fish.fishingplanner.ror.domain.usecases.SpinChoiceTimeGetAllUseCase
import com.fish.fishingplanner.ror.presentation.pushhandler.SpinChoiceTimePushHandler
import com.fish.fishingplanner.ror.presentation.ui.load.SpinChoiceTimeLoadViewModel
import com.fish.fishingplanner.ror.presentation.ui.view.SpinChoiceTimeViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val volcanoModule = module {
    factory {
        SpinChoiceTimePushHandler()
    }
    single {
        SpinChoiceTimeRepository()
    }
    single {
        SpinChoiceTimeSharedPreference(get())
    }
    factory {
        SpinChoiceTimePushToken()
    }
    factory {
        SpinChoiceTimeSystemService(get())
    }
    factory {
        SpinChoiceTimeGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        SpinChoiceTimeViFun(get())
    }
    viewModel {
        SpinChoiceTimeLoadViewModel(
            get(),
            get(),
            get()
        )
    }
}