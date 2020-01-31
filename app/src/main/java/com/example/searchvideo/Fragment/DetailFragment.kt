package com.example.searchvideo.Fragment

import android.app.AlertDialog
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.MainBroadcastPreference
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.R
import com.example.searchvideo.base.BaseFragment
import com.example.searchvideo.databinding.FragmentDetailBinding
import com.example.searchvideo.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.*
import java.lang.Exception
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection


@Suppress("SetJavaScriptEnabled")
class DetailFragment (private val application: Application ,videoModel:VideoSearchResponse.Document, private val mVideoOperationController: VideoOperationController):BaseFragment<FragmentDetailBinding, DetailViewModel>(){

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
        click_share()
        click_down()
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
    private fun click_share(){
        var context:Context = this.requireContext()
        videoDetailShareButton.setOnClickListener{
            val shareIntent= Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, mKakaoVideoModel.url)
                type ="text/plain"
            }
            val sharedIntent = Intent.createChooser(shareIntent,null)
            context.startActivity(sharedIntent)
        }
    }
    private fun click_down() {

        val url: String = mKakaoVideoModel.thumbnail

        val imageBytes:ByteArray ?= Base64.decode(url, Base64.NO_WRAP)

        val imageName: String = mKakaoVideoModel.title
        val imageNames:String  = imageName.replace(",","").replace(".","").replace("","").replace("'","").replace("(","")
            .replace(")","").replace("[","").replace("]","").replace("{","").replace("}","")
            .replace(":","").replace(";","").replace("~","").replace("!","").replace("^","").replace("*","")
            .replace("/","").replace("<","").replace(">","").trim()
        Log.d("DetailFragment", "download begining");
        Log.d("DetailFragment", "download url:" + url);
        Log.d("DetailFragment", "download byte:" + imageBytes);
        Log.d("DetailFragment", "downloaded file name:" + imageName);
        val mDownloadDirectory : File by lazy {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).also {
                if(!it.exists()) it.mkdir()
            }
        }


        videoDetailDownloadButton.setOnClickListener {

            Glide.with(application)
                .asBitmap()
                .load(url)
                .into(object :CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val imageFile = File(mDownloadDirectory,imageNames+".jpg")
                        try{
                            FileOutputStream(imageFile).also {fileOutputStream ->
                                resource.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
                                fileOutputStream.close()
                                Toast.makeText(application, "다운로드 완료", Toast.LENGTH_LONG).show()
                            }
                        } catch (e:Exception) {e.printStackTrace()}
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })

        }
    }


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
        fun newInstance(application: Application, videoModel: VideoSearchResponse.Document, videoOperationController: VideoOperationController) =
            DetailFragment(
                application,
                videoModel,
                videoOperationController
            )
    }
}