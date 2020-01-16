package com.example.searchvideo

import android.app.Activity
import android.app.Application
import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Handler
import android.provider.SearchRecentSuggestions
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.searchvideo.Fragment.ListFragment
import com.example.searchvideo.base.BaseActivity_ko
import com.example.searchvideo.Controller.VideoOperationController
import com.example.searchvideo.Fragment.DetailFragment
import com.example.searchvideo.Model.KakaoSearchSortEnum
import com.example.searchvideo.Model.KakaoVideoModelManager
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.viewmodel.MainViewModel
import com.example.searchvideo.databinding.ActivityMainBinding
import com.example.searchvideo.util.PreferenceUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.android.ext.android.inject


class MainActivity :BaseActivity_ko<ActivityMainBinding, MainViewModel>() {
    private val mMainViewModel : MainViewModel by viewModel()
    private lateinit var mMainFragmentState: MainFragmentState
    private val mVideoOperationController:VideoOperationController by inject()
    private lateinit var mFragmentManager: FragmentManager
    private lateinit var mVideoListFragment: ListFragment
    private lateinit var mVideoDetailFragment: DetailFragment
    private var mBackButtonEnabledFromDetail = true
    private var mAppTerminateConfirmFlag = false
    private lateinit var mSearchView : SearchView
    private lateinit var mScaleGestureDetector : ScaleGestureDetector
    private val mAppTerminateConfirmHandler = Handler()
    private val mPreferenceUtils : PreferenceUtils by inject()
    private var mIsSearchViewShownAtFirstTime = false
    private lateinit var mKakaoVideoModel:VideoSearchResponse.Document

    private val mMainBroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                it.action?.let {
                        actionString ->
                        intent.getStringExtra(MainBroadcastPreference.Target.KEY)?.let {
                            when(actionString){

                                MainBroadcastPreference.Action.VIDEO_ITEM_CLICKED->{
                                    showDetailFragment(intent.getSerializableExtra(MainBroadcastPreference.Extra.VideoItem.KEY) as VideoSearchResponse.Document)
                                }

                                MainBroadcastPreference.Action.CLOSE_VIDEO_DETAIL_FRAGMENT -> {
                                    mMainFragmentState = MainFragmentState.VIDEO_LIST
                                    super@MainActivity.onBackPressed()
                                }

                                MainBroadcastPreference.Action.VIDEO_OPERATION_FINISHED -> {
                                    when(intent.getSerializableExtra(MainBroadcastPreference.Extra.VideoOperation.KEY)) {
                                        MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues.SHARE -> {
                                            this@MainActivity.startActivity(
                                                Intent.createChooser(intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT), "어디에 공유 하시겠습니까?"))
                                        }
                                        MainBroadcastPreference.Extra.VideoOperation.PreDefinedValues.DOWNLOAD -> {
                                            Toast.makeText(this@MainActivity,"다운로드 완료", Toast.LENGTH_LONG).show()
                                            mVideoOperationController.clearDisposable()
                                        }
                                    }
                                }
                                MainBroadcastPreference.Action.FINISH_APPLICATION -> finishApplication()

                            }
                        }


                }
            }
        }

    }

    override fun getLayoutId(): Int = R.layout.activity_main
    override fun getViewModel():MainViewModel = mMainViewModel
    override fun getBindingVariable(): Int = BR.viewModel

    private val listAdapter:ListAdapter by inject()

    override fun setUp() {
        setBroadcastReceiver()
        setToolBar()
        setFragmentManager()

    }

    override fun onResume() {
        super.onResume()
        mVideoOperationController.clearSharedDriectory()
    }

    private fun setBroadcastReceiver(){
        registerReceiver(mMainBroadcastReceiver, IntentFilter().also {
            arrayOf(
                MainBroadcastPreference.Action.VIDEO_ITEM_CLICKED,
                MainBroadcastPreference.Action.CLOSE_VIDEO_DETAIL_FRAGMENT,
                MainBroadcastPreference.Action.VIDEO_ITEM_SELECTION_MODE_CHANGED,
                MainBroadcastPreference.Action.VIDEO_OPERATION_FINISHED,
                MainBroadcastPreference.Action.FINISH_APPLICATION
                //MainBroadcastPreference.Action.CHECK_IMAGE_OPERATION_PROCEEDING_WHEN_WIFI_DISCONNECTED
            ).forEach {
                eachAction ->
                it.addAction(eachAction)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMainBroadcastReceiver)
    }

    private fun setToolBar() {
        setSupportActionBar(viewDataBinding.mainToolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setIcon(R.drawable.ic_search_black_24dp)
            it.title="  영상 검색"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_app_bar,menu)
        val searchManager : SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val suggestions = SearchRecentSuggestions(this,MainRecentSearchSuggestionsProvider.AUTHORITY,
            MainRecentSearchSuggestionsProvider.MODE)

        mSearchView = menu!!.findItem(R.id.menuMainAppBarSearch).actionView as SearchView
        mSearchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = "Image Search"
            setOnQueryTextFocusChangeListener{
                _, isFocused ->
                mMainViewModel.mFragmentVisibility.set(!isFocused)
            }
            setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    (getSystemService(Activity.INPUT_METHOD_SERVICE)as InputMethodManager).hideSoftInputFromWindow((currentFocus?:View(this@MainActivity)).windowToken,0)
                    this@apply.onActionViewCollapsed()
                    if(mMainFragmentState == MainFragmentState.VIDEO_DETAIL) onBackPressed()
                    sendBroadcast(Intent().apply {
                        action = MainBroadcastPreference.Action.NEW_SEARCH_QUERY_INPUT
                        putExtra(MainBroadcastPreference.Target.KEY,MainBroadcastPreference.Target.PreDefinedValues.VIDEO_LIST)
                        putExtra(MainBroadcastPreference.Extra.QueryString.KEY,query)
                    })
                    suggestions.saveRecentQuery(query,null)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean = newText?.isNotEmpty()?:false
            })
            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean = true
                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = suggestionsAdapter.getItem(position) as Cursor
                    val index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1)
                    setQuery(cursor.getString(index), true)
                    return true
                }
            })
            setOnCloseListener { false }
            isQueryRefinementEnabled = true
            if(!mIsSearchViewShownAtFirstTime) {
                isIconified = false
                requestFocus(0)
                mIsSearchViewShownAtFirstTime = true
            }
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when(mPreferenceUtils.getSortOption()) {
            KakaoSearchSortEnum.Accuracy -> menu!!.findItem(R.id.menuMainAppBarSortByAccuracy).isChecked = true
            KakaoSearchSortEnum.Recency -> menu!!.findItem(R.id.menuMainAppBarSortByRecency).isChecked = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuMainAppBarSortByAccuracy ->{
                mPreferenceUtils.setSortOption(KakaoSearchSortEnum.Accuracy)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.SORT_OPTION_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.VIDEO_LIST)
                    putExtra(MainBroadcastPreference.Extra.SortOption.KEY, KakaoSearchSortEnum.Accuracy)
                })
            }
            R.id.menuMainAppBarSortByRecency -> {
                mPreferenceUtils.setSortOption(KakaoSearchSortEnum.Recency)
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.SORT_OPTION_CHANGED
                    putExtra(MainBroadcastPreference.Target.KEY, MainBroadcastPreference.Target.PreDefinedValues.VIDEO_LIST)
                    putExtra(MainBroadcastPreference.Extra.SortOption.KEY, KakaoSearchSortEnum.Recency)
                })
            }
            R.id.menuMainAppBarClearSearchHistory -> {
                SearchRecentSuggestions(this, MainRecentSearchSuggestionsProvider.AUTHORITY,
                    MainRecentSearchSuggestionsProvider.MODE).clearHistory()
            }
        }
        if(mMainFragmentState == MainFragmentState.VIDEO_DETAIL) onBackPressed()
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(::mScaleGestureDetector.isInitialized) mScaleGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if(!mSearchView.isIconified){
            mSearchView.onActionViewCollapsed()
            return
        }
        if(mMainFragmentState == MainFragmentState.VIDEO_LIST){
            if(mBackButtonEnabledFromDetail) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PreDefinedValues.VIDEO_LIST
                    )
                })
            }
            return
        }

        if(mMainFragmentState == MainFragmentState.VIDEO_DETAIL){
            if(mBackButtonEnabledFromDetail) {
                sendBroadcast(Intent().apply {
                    action = MainBroadcastPreference.Action.BACK_BUTTON_PRESSED
                    putExtra(
                        MainBroadcastPreference.Target.KEY,
                        MainBroadcastPreference.Target.PreDefinedValues.VIDEO_DETAIL
                    )
                })
            }
            return
        }
    }

    private fun finishApplication() {
        if(!mAppTerminateConfirmFlag) {
            Toast.makeText(this, "If you want quit one more press", Toast.LENGTH_LONG).show()
            mAppTerminateConfirmFlag = true
            mAppTerminateConfirmHandler.removeCallbacksAndMessages(null)
            mAppTerminateConfirmHandler.postDelayed({
                mAppTerminateConfirmFlag = false
            }, 3000)
            return
        }
        finish()
    }

    private fun setFragmentManager(){
        mMainFragmentState = MainFragmentState.VIDEO_LIST
        mFragmentManager = supportFragmentManager
        mVideoListFragment = ListFragment.newInstance()
        mFragmentManager
            .beginTransaction()
            .add(viewDataBinding.mainFragmentContainer.id,mVideoListFragment)
            .show(mVideoListFragment)
            .commit()
    }

    private fun showDetailFragment(videoModel : VideoSearchResponse.Document){
        mMainFragmentState = MainFragmentState.VIDEO_DETAIL
        mVideoDetailFragment = DetailFragment.newInstance(application, videoModel, mVideoOperationController)
        mFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.anim_fragment_enter_from_right, R.anim.anim_fragment_exit_to_left,
                R.anim.anim_fragment_enter_from_left, R.anim.anim_fragment_exit_to_right)
            .hide(mVideoListFragment)
            .add(viewDataBinding.mainFragmentContainer.id, mVideoDetailFragment)
            .show(mVideoDetailFragment)
            .addToBackStack(null)
            .commit()
        mBackButtonEnabledFromDetail = false
        Handler().postDelayed({mBackButtonEnabledFromDetail = true}, 500)


    }

//    private fun cre(){
//        val adapter = MyViewPagerAdapter(supportFragmentManager)
//        adapter.addFragment(ListFragment(),"list")
//        adapter.addFragment(DetailFragment(mVideoOperationController = VideoOperationController(application),application = Application(),videoModel = mKakaoVideoModel),"detail")
//
//    }
//    class MyViewPagerAdapter(manager:FragmentManager) : FragmentPagerAdapter(manager)
//    {
//
//        private val fragmentList:MutableList<Fragment> = ArrayList()
//        private val titleList:MutableList<String> = ArrayList()
//
//        override fun getItem(position: Int): Fragment {
//            return fragmentList[position]
//        }
//
//        override fun getCount(): Int {
//            return fragmentList.size
//        }
//
//        fun addFragment(fragment: Fragment,title:String){
//            fragmentList.add(fragment)
//            titleList.add(title)
//        }
//
//        override fun getPageTitle(position: Int): CharSequence? {
//            return titleList[position]
//        }
//
//    }



}
