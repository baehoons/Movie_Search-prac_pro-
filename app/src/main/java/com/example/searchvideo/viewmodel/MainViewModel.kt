package com.example.searchvideo.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.example.searchvideo.base.BaseViewModel

class MainViewModel (application: Application) : BaseViewModel(application){

    val mFragmentVisibility = ObservableField(true)
    val mProgressIndicatorVisibility = ObservableField(false)

}
