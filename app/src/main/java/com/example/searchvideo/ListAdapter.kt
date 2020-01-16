package com.example.searchvideo

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.Model.KakaoSearchSortEnum
import com.example.searchvideo.Model.KakaoVideoModelManager
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.databinding.ItemVideoListBinding
import com.example.searchvideo.util.ConstantUtils
import com.example.searchvideo.util.PicassoTransformations
import com.example.searchvideo.util.PreferenceUtils
import com.example.searchvideo.viewmodel.ListItemViewModel
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_main.view.*

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class ListAdapter(
    private val application : Application,
    private val mKakaoImageModelManager : KakaoVideoModelManager,
    private val mPreferenceUtils: PreferenceUtils,
    private val mVideoOperationController: VideoOperationController
) : RecyclerView.Adapter<ListAdapter.VideoListItemViewHolder>(){


    private val mVideoListItemViewModelList : ArrayList<ListItemViewModel> = ArrayList()
    private val mCompositeDisposable = CompositeDisposable()
    private var mDisableAppearAnim = false
    private val mDiasbleAppearAnimHandler = Handler()
    private val mAnimAppearMills = 800L
    private val mVideoSizePercentage = ObservableField(mPreferenceUtils.getImageSizePercentage())


    data class VideoItem(var VideoUrl:String , var documentUrl:String)


    class Videoholder(parent: ViewGroup):RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
    ) {
        fun onBind(item:VideoItem){
            lateinit var mKakaoVideoModel :VideoSearchResponse.Document

            itemView.run {
                Picasso.with(context).load(item.VideoUrl).placeholder(R.drawable.ic_image_black_24dp).resize(800,700).into(video_view)
                video_view.setOnClickListener {
                    context.sendBroadcast(Intent().apply {
                        fun boundOnImageItemClick() {
                            context.sendBroadcast(Intent().apply {
                                action = MainBroadcastPreference.Action.VIDEO_ITEM_CLICKED
                                putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY)
                                putExtra(MainBroadcastPreference.Extra.VideoItem.KEY, mKakaoVideoModel)
                            })
                        }
                        boundOnImageItemClick()
                    })
//                    ContextCompat.startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(item.documentUrl)), null)
                }
            }
        }
    }



    override fun getItemCount(): Int = mVideoListItemViewModelList.size

    override fun onBindViewHolder(holder:VideoListItemViewHolder, position: Int) {
        val eachImageListItemViewModel = mVideoListItemViewModelList[position]
        holder.apply {
            bind(eachImageListItemViewModel)
            itemView.tag = eachImageListItemViewModel
            if (!mDisableAppearAnim) itemView.startAnimation(
                AnimationUtils.loadAnimation(
                    application,
                    R.anim.anim_item_video
                ).apply {
                    duration = mAnimAppearMills
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListItemViewHolder {
        return VideoListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),R.layout.item_video_list,parent,false
            )

        )
    }

    fun searchImage(queryKeyword : String, sortOption: KakaoSearchSortEnum, pageNumber : Int, size : Int,
                    onSearchResult : (isError : Boolean, errorMessage : String?, isEmpty : Boolean, isEnd : Boolean) -> Unit) {
        mVideoListItemViewModelList.clear()
        mCompositeDisposable.clear()
        mCompositeDisposable.add(
            mKakaoImageModelManager.rxKakaoImageSearchByKeyword(queryKeyword, sortOption, pageNumber, size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        it.documents.forEach { eachKakaoImageModel ->
                            mVideoListItemViewModelList.add(ListItemViewModel(application, mVideoOperationController).apply {
                                mKakaoImageModel = eachKakaoImageModel
                                mImageSizePercentage = this@ListAdapter.mVideoSizePercentage
                                mIsItemSelected = ObservableField(mVideoOperationController.isVideoMddelExists(mKakaoImageModel))
                                //setEventHandlerOnSelectionModeChanged()
                            })
                        }
                        notifyDataSetChanged()
                        onSearchResult(false, null, mVideoListItemViewModelList.isEmpty(), it.isEnd)
                    },
                    {
                        notifyDataSetChanged()
                        onSearchResult(true, it.message, true, true)
                    })
        )
    }

    fun clear(){
        mVideoListItemViewModelList.clear()
        mCompositeDisposable.clear()
        notifyDataSetChanged()
    }

    class VideoListItemViewHolder(
        private val binding : ItemVideoListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /** 이미지 뷰 Layout 바인딩에 viewModel 을 입력합니다. */
        fun bind(item : ListItemViewModel) {
            binding.apply {
                viewModel = item
            }
        }
    }


}