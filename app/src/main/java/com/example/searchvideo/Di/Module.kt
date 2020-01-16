package com.example.searchvideo.Di

import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.ListAdapter
import com.example.searchvideo.Model.DataModel
import com.example.searchvideo.Model.KakaoSearchService
import com.example.searchvideo.Model.KakaoVideoModelManager
import com.example.searchvideo.viewmodel.DetailViewModel
import com.example.searchvideo.viewmodel.ListViewModel
import com.example.searchvideo.viewmodel.MainViewModel
import com.example.searchvideo.util.PreferenceUtils
import com.example.searchvideo.viewmodel.ListItemViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


var retrofitPart = module {
    single {
        KakaoVideoModelManager()
    }
}
val controller = module {
    single { VideoOperationController(get()) }
}

var adapterPart = module {
    single {
        ListAdapter(get(),get(),get(),get())
    }
}

var viewModelPart = module {
    viewModel{
        MainViewModel(get())
    }
    viewModel{
        ListViewModel(get(),get())
    }
    viewModel{
        ListItemViewModel(get(),get())
    }
    viewModel {
        DetailViewModel(get(),get(),get())
    }
}
val util = module {
    single { PreferenceUtils(get()) }
}

var myDiModule = listOf(viewModelPart,adapterPart,retrofitPart,controller, util)