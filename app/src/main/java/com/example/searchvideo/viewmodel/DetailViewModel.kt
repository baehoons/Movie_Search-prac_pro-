package com.example.searchvideo.viewmodel

import android.app.Application
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.ObservableField
import com.example.searchvideo.base.BaseViewModel
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.util.ConstantUtils

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class DetailViewModel (
    application: Application,
    videoModel: VideoSearchResponse.Document,
    private val mVideoOperationController: VideoOperationController
):BaseViewModel(application){


    var mKakaoVideoModel : VideoSearchResponse.Document = videoModel
    val mIsWebViewLoading = ObservableField(true)
    val mVideoDetailDocumentClient = object : WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mIsWebViewLoading.set(false)
        }

        override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
            super.onReceivedError(view, request, error)
            mIsWebViewLoading.set(false)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            return false
        }
    }

    fun boundOnShareButtonClick() {
        if(!mVideoOperationController.mIsOnOperation.get()!!) {
            mVideoOperationController.addImageModel(mKakaoVideoModel)
            mVideoOperationController.startShare()
        }
    }

    lateinit var onInfoButtonClickListener : () -> Unit

    fun boundOnInfoButtonClick() {
        onInfoButtonClickListener()
    }

    fun boundOnDownloadButtonClick() {
        if(!mVideoOperationController.mIsOnOperation.get()!!) {
            mVideoOperationController.addImageModel(mKakaoVideoModel)
            mVideoOperationController.startDownload()
        }
    }

}