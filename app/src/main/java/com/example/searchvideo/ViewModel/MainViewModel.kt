package com.example.searchvideo.ViewModel

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

class MainViewModel (private val model: DataModel) : BaseViewModel(){

    private val TAG = "MainViewModel"



    private val _videoSearchPersonLiveData = MutableLiveData<VideoSearchResponse>()
    val videoSearchPersonLiveData:LiveData<VideoSearchResponse>
        get() = _videoSearchPersonLiveData

    val mFragmentVisibility = ObservableField(true)
    val mProgressIndicatorVisibility = ObservableField(false)

    fun getVideoSearch(query:String, page:Int, size:Int){
        addDisposable(model.getData(query, KakaoSearchSortEnum.Accuracy, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.run {
                    if (documents.size > 0) {
                        Log.d(TAG, "documents : $documents")
                        _videoSearchPersonLiveData.postValue(this)
                    }
                    Log.d(TAG, "meta : $meta")
                }
            }, {
                Log.d(TAG, "response error, message : ${it.message}")
            }))
    }
}
