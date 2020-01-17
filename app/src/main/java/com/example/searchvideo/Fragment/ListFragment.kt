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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.searchvideo.base.BaseFragment
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.ListAdapter
import com.example.searchvideo.MainBroadcastPreference
import com.example.searchvideo.Model.KakaoSearchSortEnum
import com.example.searchvideo.R
import com.example.searchvideo.viewmodel.ListViewModel
import com.example.searchvideo.databinding.FragmentListBinding
import com.example.searchvideo.util.PreferenceUtils
import com.example.searchvideo.viewmodel.ListItemViewModel
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


                                MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT -> {
                                    val queryKeyword =
                                        intent.getStringExtra(MainBroadcastPreference.Extra.QueryString.KEY)
                                    queryKeyword?.let {
                                        mVideoListViewModel.inputNewKeyword(queryKeyword)
                                    }
                                }


                                MainBroadcastPreference.Action.SORT_OPTION_CHANGED -> {
                                    val sortOption =
                                        intent.getSerializableExtra(MainBroadcastPreference.Extra.SortOption.KEY) as KakaoSearchSortEnum
                                    mVideoListViewModel.changeSortOption(sortOption)
                                }


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


    override fun layoutResourceId(): Int = R.layout.fragment_list
    override fun getBindingVariable(): Int = BR.viewModel
    override fun getViewModel(): ListViewModel =mVideoListViewModel


    override fun setUp() {
        setBroadcastReceiver()
        setVideoListRecyclerAdapter()
        setViewModelListener()
        setRefreshLayout()
        setFilterMenu()
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


    private fun setVideoListRecyclerAdapter() {
        viewDataBinding.recyclerView.apply {
            adapter = mVideoListRecyclerAdapter
        }
        setRecyclerViewLayoutManager()
    }

    private fun setRecyclerViewLayoutManager() {
        val portraitImageColumnCount = mPreferenceUtils.getVideoColumnCount()
        viewDataBinding.recyclerView.apply {
            val position = if(layoutManager != null) (layoutManager as GridLayoutManager).findFirstVisibleItemPosition() else 0
            layoutManager = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) GridLayoutManager(mActivity, portraitImageColumnCount)
            else GridLayoutManager(mActivity, portraitImageColumnCount * mColumnCountRatio)
            scrollToPosition(position)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerViewLayoutManager()
    }

    private fun setViewModelListener() {
        mVideoListViewModel.apply {
            onQueryChangedListener = {
                    queryKeyword, sort, pageNumber, displayCount ->
                mVideoListRecyclerAdapter.searchImage(queryKeyword, sort, pageNumber, displayCount) {
                        isError, errorMessage, isEmpty, isEnd ->
                    mVideoListViewModel.setSearchResult(isError, errorMessage, pageNumber, isEmpty, isEnd)
                    with(viewDataBinding.videoListRefreshLayout) {
                        isEnabled = true
                        if(isRefreshing) isRefreshing = false
                    }
                }
            }
        }
    }

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


    companion object {
        fun newInstance() = ListFragment()
    }
}
