package com.example.searchvideo.Fragment

import android.app.AlertDialog
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Handler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR

import com.example.searchvideo.base.BaseFragment
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.MainBroadcastPreference
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.R
import com.example.searchvideo.viewmodel.DetailViewModel
import com.example.searchvideo.databinding.FragmentDetailBinding

@Suppress("SetJavaScriptEnabled")
class DetailFragment (application: Application ,videoModel:VideoSearchResponse.Document, mVideoOperationController: VideoOperationController):BaseFragment<FragmentDetailBinding, DetailViewModel>(){

    private lateinit var webView: WebView
    private val mVideoDetailViewModel : DetailViewModel = DetailViewModel(application, videoModel, mVideoOperationController)
    private val mVideoDetailBroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                        actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                            target ->
                        if(target == MainBroadcastPreference.Target.PreDefinedValues.VIDEO_DETAIL) {
                            when(actionString) {

                                // 뒤로가기 버튼이 눌렸을 경우
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    viewDataBinding.videoDetailWebView.let {
                                            videoDetailWebView ->
                                        if(videoDetailWebView.canGoBack() && videoDetailWebView.url != mVideoDetailViewModel.mKakaoVideoModel.url) videoDetailWebView.goBack()
                                        else application.sendBroadcast(Intent().apply {
                                            action =
                                                MainBroadcastPreference.Action.CLOSE_VIDEO_DETAIL_FRAGMENT
                                            putExtra(
                                                MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY
                                            )
                                        })
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }


    override fun layoutResourceId(): Int = R.layout.fragment_detail
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): DetailViewModel = mVideoDetailViewModel


    override fun setUp() {
        setBroadcastReceiver()
        setCollapsingToolBar()
        setWebView()
        setViewModelListener()
    }
    var mKakaoVideoModel : VideoSearchResponse.Document = videoModel

//    private fun webSetting(){
//        val webSettings = webView.settings
//        webSettings.javaScriptEnabled = true
//        webView.webViewClient = object :WebViewClient(){
//            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): Boolean {
//                view?.loadUrl(mKakaoVideoModel.url)
//                return true
//            }
//        }
//        webView.loadUrl(mKakaoVideoModel.url)
//    }

    private fun setBroadcastReceiver() {
        activity?.registerReceiver(mVideoDetailBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
            ).forEach {
                    eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mVideoDetailBroadcastReceiver)
    }

    private fun setCollapsingToolBar(){
        viewDataBinding.videoDetailCollapsingAppToolBar.apply {
            title = "리스트 자세히 보기"
            setCollapsedTitleTypeface(Typeface.DEFAULT_BOLD)
            setExpandedTitleColor(ContextCompat.getColor(context, R.color.colorTransparent))
        }
    }

    /**
     * 웹 뷰의 기본적인 설정들을 지정합니다.
     * 1. JS 사용 가능 여부 - True
     * 2. POP Up 창 사용 가능 여부 - True (이 부분은 조금 더 고려해봐야 합니다.)
     * 3. 웹의 사진을 표현할지 여부 - True
     * 4. 모바일 지원이 안 되는 웹을 모바일 화면에 끼워 맞추기 - True
     */
    private fun setWebView() {
        viewDataBinding.videoDetailWebView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadsImagesAutomatically = true
            useWideViewPort = true
            setSupportZoom(true)

        }
        Handler().postDelayed({ mVideoDetailViewModel.mIsWebViewLoading.set(false) }, 3000)
    }


    /**
     * View Model 에서 추가적으로 지정해줘야 하는 리스너들을 등록합니다.
     * 현재 상세정보 버튼을 눌렸을 경우 Fragment 에서 처리하도록 하며
     * Dialog 에 ImageModel 의 정보를 담아서 화면에 보이도록 합니다.
     */
    private fun setViewModelListener() {
        var play_time_h:Int = mKakaoVideoModel.play_time/(60*60)
        var play_time_m:Int = (mKakaoVideoModel.play_time-(play_time_h*60*60))/60
        var play_time_s:Int = mKakaoVideoModel.play_time-(play_time_h*60*60)-(play_time_m*60)
        mVideoDetailViewModel.apply {
            onInfoButtonClickListener = {
                AlertDialog.Builder(context).apply {
                    setTitle("영상 정보")
                    setMessage(
                        "${getString(R.string.video_detail_info_title, mKakaoVideoModel.title)}\n" +
                                "${getString(R.string.video_detail_info_author, mKakaoVideoModel.author)}\n" +
                                "${getString(R.string.video_detail_info_play_time)} ${play_time_h}시간 ${play_time_m}분 ${play_time_s}초\n" +
                                getString(R.string.video_detail_info_datetime, mKakaoVideoModel.datetime)
                    )
                    setPositiveButton(R.string.close, null)
                    show()
                }
            }
        }
    }

    companion object {
        /** 새로운 프래그먼트를 생성합니다. */
        fun newInstance(application: Application, videoModel: VideoSearchResponse.Document, videoOperationController: VideoOperationController) =
            DetailFragment(
                application,
                videoModel,
                videoOperationController
            )
    }
}