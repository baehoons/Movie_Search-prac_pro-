package com.example.searchvideo.Di

import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.ListAdapter
import com.example.searchvideo.Model.DataModel
import com.example.searchvideo.Model.DataModelImpl
import com.example.searchvideo.Model.KakaoSearchService
import com.example.searchvideo.ViewModel.DetailViewModel
import com.example.searchvideo.ViewModel.ListViewModel
import com.example.searchvideo.ViewModel.MainViewModel
import com.example.searchvideo.util.PreferenceUtils
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


var retrofitPart = module {
    single<KakaoSearchService> {
        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(KakaoSearchService::class.java)
    }
}
val controller = module {
    single { VideoOperationController(get()) }
}

var adapterPart = module {
    factory {
        ListAdapter()
    }
}
var modelPart = module {
    factory<DataModel> {
        DataModelImpl(get())
    }
}

var viewModelPart = module {
    viewModel{
        MainViewModel(get())
    }
    viewModel{
        ListViewModel(get(),get(),get())
    }
    viewModel {
        DetailViewModel(get(),get(),get())
    }
}
val util = module {
    single { PreferenceUtils(get()) }
}

var myDiModule = listOf(retrofitPart, adapterPart, modelPart, viewModelPart,controller, util)