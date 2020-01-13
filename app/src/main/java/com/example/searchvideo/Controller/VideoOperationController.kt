package com.example.searchvideo.Controller

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.ObservableField
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.searchvideo.MainBroadcastPreference
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.R
import com.example.searchvideo.util.ConstantUtils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileOutputStream
import java.util.ArrayList

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class VideoOperationController ( private val application: Application ) {
    private val mDownloadDirectory: File by lazy {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).also {
            if (!it.exists()) it.mkdir()
        }
    }

    private val mShareDirectory: File by lazy {
        File(application.filesDir.canonicalPath + "/sharedImages").also {
            if (!it.exists()) it.mkdir()
        }

    }

    private val mImageModelMap: HashMap<String, VideoSearchResponse.Document> = HashMap()
    val mClonedImageModelMap: HashMap<String, VideoSearchResponse.Document> = HashMap()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    var mIsOnOperation = ObservableField(false)
    private var mIsImageOnSharing = false

    private enum class VideoOpertation {
        SHARE,

        DOWNLOAD
    }

    fun isVideoMddelExists(videoModel: VideoSearchResponse.Document): Boolean =
        mImageModelMap.containsKey(videoModel.thumbnail)

    fun addImageModel(videoModel: VideoSearchResponse.Document) =
        mImageModelMap.put(videoModel.thumbnail, videoModel)

    fun removeVideoModel(videoModel: VideoSearchResponse.Document) =
        mImageModelMap.remove(videoModel.thumbnail)

    fun clearVideoModels() = mImageModelMap.clear()

//    fun startShare() = checkPermsiionAndLoadImagesForOperation(VideoOpertation.SHARE)
//
//    fun startDownload() = checkPermsiionAndLoadImagesForOperation(VideoOpertation.DOWNLOAD)

    fun clearSharedDriectory() {
        if (mIsImageOnSharing) {
            mIsImageOnSharing = false
            mShareDirectory.listFiles().forEach {
                if (it.exists()) it.canonicalFile.delete()
            }
            mCompositeDisposable.clear()
        }
    }

    fun clearDisposable() = mCompositeDisposable.clear()

    fun runRetardedImageOperation(
        doStart: Boolean,
        prefImageOperation: MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues? = null
    ) {
        if (doStart && prefImageOperation != null) {
            loadImageTo(
                when (prefImageOperation) {
                    MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues.SHARE -> VideoOpertation.SHARE
                    MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues.DOWNLOAD -> VideoOpertation.DOWNLOAD
                }
            )
        } else mClonedImageModelMap.clear()
    }

//    private fun checkPermsiionAndLoadImagesForOperation(videoOperation: VideoOpertation) {
//        mImageModelMap.forEach { mClonedImageModelMap[it.key] = it.value}
//        TedPermission.with(application)
//            .setPermissionListener(object : PermissionListener {
//                override fun onPermissionGranted() {
//                    with((application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)) {
//                        activeNetworkInfo.let {
//                                networkInfo ->
//                            if(networkInfo == null) {
//                                preProcessRejected()
//                                return
//                            }
//                            val isWifiConnected : Boolean = with((application.getSystemService(
//                                Context.WIFI_SERVICE) as WifiManager)) {
//                                isWifiEnabled && connectionInfo.networkId != -1
//                            }
//                            if(isWifiConnected) loadImageTo(imageOperation)
//                            else {
//                                application.sendBroadcast(Intent().apply {
//                                    action = MainBroadcastPreference.Action.CHECK_IMAGE_OPERATION_PROCEEDING_WHEN_WIFI_DISCONNECTED
//                                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY)
//                                    putExtra(MainBroadcastPreference.Extra.ImageOperation.KEY, when(imageOperation) {
//                                        ImageOperation.SHARE -> MainBroadcastPreference.Extra.ImageOperation.PreDefinedValues.SHARE
//                                        ImageOperation.DOWNLOAD -> MainBroadcastPreference.Extra.ImageOperation.PreDefinedValues.DOWNLOAD
//                                    })
//                                })
//                            }
//                        }
//                    }
//                }
//
//                override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
//                    preProcessRejected()
//                }
//
//                private fun preProcessRejected() {
//                    Toast.makeText(application, R.string.txt_image_operation_failed, Toast.LENGTH_LONG).show()
//                    mClonedImageModelMap.clear()
//                }
//
//            })
//            .setRationaleMessage(R.string.permission_external_storage_rational_message)
//            .setDeniedMessage(R.string.permission_external_storage_denied_message)
//            .setGotoSettingButton(true)
//            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            .check()
//    }

    private fun loadImageTo(videoOperation: VideoOpertation) {
        val totalImageCount = mClonedImageModelMap.size
        var currentImageCount = 0
        val directoryToStore =
            if (videoOperation == VideoOpertation.SHARE) mShareDirectory else mDownloadDirectory
        mIsOnOperation.set(true)
        mCompositeDisposable.add(
            Completable.create { emitter ->
                mClonedImageModelMap.forEach {
                    Glide.with(application)
                        .asBitmap()
                        .load(it.value.thumbnail)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                currentImageCount++
                                if (currentImageCount == totalImageCount) emitter.onComplete()
                            }

                            override fun onLoadCleared(placeholder: Drawable?) = Unit
                            override fun onResourceReady(
                                bitmapImage: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val imageFile = File(
                                    directoryToStore,
                                    application.getString(
                                        R.string.download_image_prefix,
                                        it.value.hashCode()
                                    ) + ".jpg"
                                )
                                try {
                                    FileOutputStream(imageFile).also { fileOutputStream ->
                                        bitmapImage.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100,
                                            fileOutputStream
                                        )
                                        fileOutputStream.close()
                                        if (videoOperation == VideoOpertation.DOWNLOAD) notifyAndroidNewImageAdded(
                                            imageFile
                                        )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                currentImageCount++
                                if (currentImageCount == totalImageCount) emitter.onComplete()
                            }
                        })
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mIsOnOperation.set(false)
                    application.sendBroadcast(Intent().apply {
                        action = MainBroadcastPreference.Action.IMAGE_OPERATION_FINISHED
                        putExtra(
                            MainBroadcastPreference.Target.KEY,
                            MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY
                        )
                        when (videoOperation) {
                            VideoOpertation.SHARE -> {
                                putExtra(
                                    MainBroadcastPreference.Extra.VideoOperation.KEY,
                                    MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues.SHARE
                                )
                                putExtra(Intent.EXTRA_INTENT, Intent().apply {
                                    action = Intent.ACTION_SEND_MULTIPLE
                                    type = "image/jpeg"
                                    putParcelableArrayListExtra(
                                        Intent.EXTRA_STREAM,
                                        ArrayList<Uri>().also {
                                            mShareDirectory.listFiles().forEach { eachFileToShare ->
                                                if (eachFileToShare.extension == "jpg") it.add(
                                                    FileProvider.getUriForFile(
                                                        application,
                                                        "com.example.searchvideo",
                                                        eachFileToShare
                                                    )
                                                )
                                            }
                                        })
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                })
                                mIsImageOnSharing = true
                            }
                            VideoOpertation.DOWNLOAD -> {
                                putExtra(
                                    MainBroadcastPreference.Extra.VideoOperation.KEY,
                                    MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues.DOWNLOAD
                                )
                            }
                        }
                    })
                    mClonedImageModelMap.clear()
                }
        )
    }

    private fun notifyAndroidNewImageAdded(videoFile:File){
        application.sendBroadcast(Intent().apply {
            action = Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
            data = Uri.fromFile(videoFile)
        })
    }
}