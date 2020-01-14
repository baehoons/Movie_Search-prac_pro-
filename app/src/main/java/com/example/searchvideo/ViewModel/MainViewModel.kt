package com.example.searchvideo.ViewModel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.searchvideo.Base.BaseViewModel
import com.example.searchvideo.Model.DataModel
import com.example.searchvideo.Model.KakaoSearchSortEnum
import com.example.searchvideo.Model.VideoSearchResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel (application: Application) : BaseViewModel(application){

    val mFragmentVisibility = ObservableField(true)
    val mProgressIndicatorVisibility = ObservableField(false)

}
