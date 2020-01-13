package com.example.searchvideo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.example.searchvideo.Base.BaseFragment
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.ViewModel.ListViewModel
import com.example.searchvideo.databinding.FragmentListBinding
import com.example.searchvideo.util.PreferenceUtils
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : BaseFragment<FragmentListBinding, ListViewModel>() {

    private val viewModel by viewModel<ListViewModel>()
    private val mImageListViewModel : ListViewModel by viewModel()
    private val mVideoListRecyclerAdapter: ListAdapter by inject()
    private val mPreferenceUtils: PreferenceUtils by inject()
    private val mVideoOperationController: VideoOperationController by inject()

    private val mColumnCountRatio: Int by lazy {
        Resources.getSystem().displayMetrics.let {
            val widthPixels = it.widthPixels.toDouble()
            val heightPixels = it.heightPixels.toDouble()
            if (widthPixels > heightPixels) (widthPixels / heightPixels).roundToInt()
            else (heightPixels / widthPixels).roundToInt()
        }
    }
    private val mRefreshDisableHandler = Handler()
    private val mVideoListBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let { actionString ->
                    intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let { target ->
                        if (target == MainBroadcastPreference.Target.PreDefinedValues.VIDEO_LIST) {
                            when (actionString) {

                                // 새로운 검색어 입력됨
//                                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT -> {
//                                    val queryKeyword =
//                                        intent.getStringExtra(MainBroadcastPreference.Extra.QueryString.KEY)
//                                    queryKeyword?.let {
//                                        mVideoListViewModel.inputNewKeyword(queryKeyword)
//                                    }
//                                }

                                // 정렬 기준이 변경됨
                                MainBroadcastPreference.Action.SORT_OPTION_CHANGED -> {
                                    val sortOption =
                                        intent.getSerializableExtra(MainBroadcastPreference.Extra.SortOption.KEY) as KakaoImageSortOption
                                    mImageListViewModel.changeSortOption(sortOption)
                                }

                                MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED -> {
                                    val displayCount = intent.getIntExtra(
                                        MainBroadcastPreference.Extra.DisplayCount.KEY,
                                        30
                                    )
                                    mImageListViewModel.changeDisplayCount(displayCount)
                                }

                                // 사용자가 화면을 Pinch 함(줌 인 혹은 줌 아웃)
                                MainBroadcastPreference.Action.PINCHING -> {
                                    when (intent.getSerializableExtra(MainBroadcastPreference.Extra.PinchingOperation.KEY)) {
                                        MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_IN -> mImageListRecyclerAdapter.resizeOnPinch(
                                            true
                                        ) { setRecyclerViewLayoutManager() }
                                        MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_OUT -> mImageListRecyclerAdapter.resizeOnPinch(
                                            false
                                        ) { setRecyclerViewLayoutManager() }
                                    }
                                }

                                // Pinch 중에는 Refresh 비활성화
                                MainBroadcastPreference.Action.PINCH_STATE -> {
                                    when (intent.getSerializableExtra(MainBroadcastPreference.Extra.PinchingState.KEY)) {
                                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_START -> mViewDataBinding.imageListRefreshLayout.isEnabled =
                                            false
                                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_END -> {
                                            if (!mImageListViewModel.mFilterMenuVisibility.get()!!) {
                                                mRefreshDisableHandler.removeCallbacksAndMessages(
                                                    null
                                                )
                                                mRefreshDisableHandler.postDelayed({
                                                    mViewDataBinding.imageListRefreshLayout.isEnabled =
                                                        true
                                                }, 300)
                                            }
                                        }
                                    }
                                }

                                // 이미지 선택 모드(단일 & 다중) 변경 알림
                                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED -> {
                                    when (intent.getSerializableExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY)) {
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.MULTI_SELECTION_MODE -> {
                                            mImageListRecyclerAdapter.setSelectionMode(true)
                                            showFilterMenuWithAnimation()
                                        }
                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.SIGNLE_SELECTION_MODE -> {
                                            mImageListRecyclerAdapter.setSelectionMode(false)
                                            hideFilterMenuWithAnimation()
                                        }
                                    }
                                }

                                // 백 버튼 눌림
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    if (mImageListViewModel.mPageNumber > 1) mImageListViewModel.boundOnPrevPageButtonClick()
                                    else mActivity?.sendBroadcast(Intent().apply {
                                        action = MainBroadcastPreference.Action.FINISH_APPLICATION
                                        putExtra(
                                            MainBroadcastPreference.Target.KEY,
                                            MainBroadcastPreference.Target.PreDefinedValues.MAIN_ACTIVITY
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
