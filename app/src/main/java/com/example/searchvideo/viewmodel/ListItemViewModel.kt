package com.example.searchvideo.viewmodel

import android.app.Application
import android.content.Intent
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.MainBroadcastPreference
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.R
import com.example.searchvideo.base.BaseViewModel
import com.example.searchvideo.util.ConstantUtils


@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ListItemViewModel (
    application: Application,private val mVideoOperationController: VideoOperationController) : BaseViewModel(application){

    private val mApplication = application
    lateinit var mKakaoImageModel : VideoSearchResponse.Document
    lateinit var mImageSizePercentage : ObservableField<Float>
    lateinit var mIsItemSelected : ObservableField<Boolean>



    fun boundOnImageItemClick() {

        mApplication.sendBroadcast(Intent().apply {
            action = MainBroadcastPreference.Action.VIDEO_ITEM_CLICKED
            putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY)
            putExtra(MainBroadcastPreference.Extra.VideoItem.KEY, mKakaoImageModel)
        })

    }
}