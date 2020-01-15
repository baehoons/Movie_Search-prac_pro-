package com.example.searchvideo.Fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.searchvideo.Base.BaseFragment
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.ListAdapter
import com.example.searchvideo.MainBroadcastPreference
import com.example.searchvideo.Model.KakaoSearchSortEnum
import com.example.searchvideo.R
import com.example.searchvideo.ViewModel.ListViewModel
import com.example.searchvideo.databinding.FragmentListBinding
import com.example.searchvideo.util.PreferenceUtils
import com.linroid.filtermenu.library.FilterMenu
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.ext.android.inject

import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList
import kotlin.math.roundToInt



class ListFragment : BaseFragment<FragmentListBinding, ListViewModel>() {


    private val mVideoListViewModel : ListViewModel by viewModel()
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
                                        intent.getSerializableExtra(MainBroadcastPreference.Extra.SortOption.KEY) as KakaoSearchSortEnum
                                    mVideoListViewModel.changeSortOption(sortOption)
                                }

//                                MainBroadcastPreference.Action.DISPLAY_COUNT_CHANGED -> {
//                                    val displayCount = intent.getIntExtra(
//                                        MainBroadcastPreference.Extra.DisplayCount.KEY,
//                                        30
//                                    )
//                                    mVideoListViewModel.changeDisplayCount(displayCount)
//                                }

                                // 사용자가 화면을 Pinch 함(줌 인 혹은 줌 아웃)
//                                MainBroadcastPreference.Action.PINCHING -> {
//                                    when (intent.getSerializableExtra(MainBroadcastPreference.Extra.PinchingOperation.KEY)) {
//                                        MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_IN -> mImageListRecyclerAdapter.resizeOnPinch(
//                                            true
//                                        ) { setRecyclerViewLayoutManager() }
//                                        MainBroadcastPreference.Extra.PinchingOperation.PreDefinedValues.ZOOM_OUT -> mImageListRecyclerAdapter.resizeOnPinch(
//                                            false
//                                        ) { setRecyclerViewLayoutManager() }
//                                    }
//                                }

                                // Pinch 중에는 Refresh 비활성화
//                                MainBroadcastPreference.Action.PINCH_STATE -> {
//                                    when (intent.getSerializableExtra(MainBroadcastPreference.Extra.PinchingState.KEY)) {
//                                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_START -> mViewDataBinding.imageListRefreshLayout.isEnabled =
//                                            false
//                                        MainBroadcastPreference.Extra.PinchingState.PreDefinedValues.PINCH_END -> {
//                                            if (!mVideoListViewModel.mFilterMenuVisibility.get()!!) {
//                                                mRefreshDisableHandler.removeCallbacksAndMessages(
//                                                    null
//                                                )
//                                                mRefreshDisableHandler.postDelayed({
//                                                    mViewDataBinding.imageListRefreshLayout.isEnabled =
//                                                        true
//                                                }, 300)
//                                            }
//                                        }
//                                    }
//                                }

                                // 이미지 선택 모드(단일 & 다중) 변경 알림
//                                MainBroadcastPreference.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED -> {
//                                    when (intent.getSerializableExtra(MainBroadcastPreference.Extra.ImageItemSelectionMode.KEY)) {
//                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.MULTI_SELECTION_MODE -> {
//                                            mImageListRecyclerAdapter.setSelectionMode(true)
//                                            showFilterMenuWithAnimation()
//                                        }
//                                        MainBroadcastPreference.Extra.ImageItemSelectionMode.PreDefinedValues.SIGNLE_SELECTION_MODE -> {
//                                            mImageListRecyclerAdapter.setSelectionMode(false)
//                                            hideFilterMenuWithAnimation()
//                                        }
//                                    }
//                                }

                                // 백 버튼 눌림
                                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED -> {
                                    if (mVideoListViewModel.mPageNumber > 1) mVideoListViewModel.boundOnPrevPageButtonClick()
                                    else mActivity?.sendBroadcast(Intent().apply {
                                        action =
                                            MainBroadcastPreference.Action.FINISH_APPLICATION
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

    internal var suggestList:MutableList<String> = ArrayList()

    override fun layoutResourceId(): Int = R.layout.fragment_list
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ListViewModel =mVideoListViewModel

    private val listAdapter: ListAdapter by inject()

    override fun setUp() {
        setBroadcastReceiver()
        initStartView()
        initDataBinding()
        initAfterBinding()
        searchbar()
        setRefreshLayout()
        setFilterMenu()
    }
    private fun setRecyclerViewLayoutManager(){
        viewDataBinding.recyclerView.apply{
            adapter = listAdapter
            layoutManager = StaggeredGridLayoutManager(2,1).apply{
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                orientation = StaggeredGridLayoutManager.VERTICAL
            }
            setHasFixedSize(true)

        }
    }

    fun initStartView() {
        setRecyclerViewLayoutManager()
    }

    fun initDataBinding() {
        getViewModel().videoSearchPersonLiveData.observe(this, Observer {
            it.documents.forEach { document ->
                //val url2:String = document.thumbnail+".jpg"

                listAdapter.addVideoItem(document.thumbnail,document.url)
            }
            listAdapter.notifyDataSetChanged()
        })
    }

    fun initAfterBinding() {
        getViewModel().getVideoSearch(videoListSearchResultTitle.text.toString(),1,30)
    }






    private fun setBroadcastReceiver(){
        activity?.registerReceiver(mVideoListBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT,
                MainBroadcastPreference.Action.SORT_OPTION_CHANGED,
                MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
            ).forEach {
                    eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(mVideoListBroadcastReceiver)
        mVideoListRecyclerAdapter.clear()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager()
    }

//    private fun setViewModelListener() {
//        mVideoListViewModel.apply {
//            onQueryChangedListener = {
//                    queryKeyword, sortOption, pageNumber, displayCount ->
//                mVideoListViewModel.getVideoSearch(queryKeyword, sortOption, pageNumber, displayCount) {
//                        isError, errorMessage, isEmpty, isEnd ->
//                    mVideoListViewModel.setSearchResult(isError, errorMessage, pageNumber, isEmpty, isEnd)
//                    with(mViewDataBinding.imageListRefreshLayout) {
//                        isEnabled = true
//                        if(isRefreshing) isRefreshing = false
//                    }
//                }
//            }
//        }
//    }

    private fun setRefreshLayout() {
        viewDataBinding.videoListRefreshLayout.apply {
            setWaveRGBColor(255, 237, 163)
            isEnabled = false
            setOnRefreshListener {
                viewDataBinding.recyclerView.startAnimation(AnimationUtils.loadAnimation(context,
                    R.anim.anim_alpha_disappear
                ).apply {
                    setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) = Unit
                        override fun onAnimationEnd(p0: Animation?) {
                            mVideoListRecyclerAdapter.clear()
                            mVideoListViewModel.refresh()
                        }
                        override fun onAnimationStart(p0: Animation?) = Unit
                    })
                })
            }
        }
    }

    private fun setFilterMenu() {
        val downloadButton = 0
        val shareButton = 1
        FilterMenu.Builder(context)
            .addItem(R.drawable.ic_download)
            .addItem(R.drawable.ic_share)
            .attach(viewDataBinding.videoListFilterMenu)
            .withListener(object : FilterMenu.OnMenuChangeListener {
                override fun onMenuItemClick(view: View?, position: Int) {
                    when(position) {
                        downloadButton -> mVideoOperationController.startDownload()
                        shareButton -> mVideoOperationController.startShare()
                    }
                    hideFilterMenuWithAnimation()
                    mVideoOperationController.clearVideoModels()
                }
                override fun onMenuCollapse() = Unit
                override fun onMenuExpand() = Unit
            })
            .build()
    }

    private fun showFilterMenuWithAnimation() {
        mVideoListViewModel.showFilterMenu()
        val filterAppearAnim = AnimationUtils.loadAnimation(context,
            R.anim.anim_filter_menu_appear
        )
        viewDataBinding.videoListFilterMenu.startAnimation(filterAppearAnim)
        viewDataBinding.videoListRefreshLayout.isEnabled = false
    }

    /** Filter Menu 를 Translation 애니메이션과 함께 화면에서 제거합니다. */
    private fun hideFilterMenuWithAnimation() {
        if(mVideoListViewModel.mFilterMenuVisibility.get()!!) {
            val filterDisappearAnim = AnimationUtils.loadAnimation(context,
                R.anim.anim_filter_menu_disappear
            )
            filterDisappearAnim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(p0: Animation?) {
                    mVideoListViewModel.hideFilterMenu()
                }
                override fun onAnimationRepeat(p0: Animation?) = Unit
                override fun onAnimationStart(p0: Animation?) = Unit
            })
            viewDataBinding.videoListFilterMenu.startAnimation(filterDisappearAnim)
            viewDataBinding.videoListRefreshLayout.isEnabled = true
        }
    }

    private fun searchbar(){
        videoListSearchResultTitle.setCardViewElevation(10)
        videoListSearchResultTitle.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val suggest = ArrayList<String>()
                for(search_term in suggestList)
                    if(search_term.toLowerCase().contentEquals(videoListSearchResultTitle.text.toLowerCase()))
                        suggest.add(search_term)
                videoListSearchResultTitle.lastSuggestions = suggest
            }

        })
        videoListSearchResultTitle.setOnSearchActionListener(object: MaterialSearchBar.OnSearchActionListener{
            override fun onButtonClicked(buttonCode: Int) {

            }

            override fun onSearchStateChanged(enabled: Boolean) {

            }

            override fun onSearchConfirmed(text: CharSequence?) {
                getViewModel().getVideoSearch(videoListSearchResultTitle.text.toString(),1,30)
            }

        })
    }
    companion object {
        /** 새로운 프래그먼트를 생성합니다. */
        fun newInstance() = ListFragment()
    }
}
