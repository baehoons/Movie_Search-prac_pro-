package com.example.searchvideo.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.searchvideo.base.BaseViewModel
import com.example.searchvideo.Model.DataModel
import com.example.searchvideo.Model.KakaoSearchSortEnum
import com.example.searchvideo.Model.VideoSearchResponse
import com.example.searchvideo.R
import com.example.searchvideo.util.PreferenceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ListViewModel(application: Application, mPreferenceUtils: PreferenceUtils) : BaseViewModel(application){
    private val mApplication : Application = application
    private val mImageListItemViewModelList : ArrayList<ListViewModel> = ArrayList()
    private val mCompositeDisposable = CompositeDisposable()
    private val TAG = "ListViewModel"

    private val _videoSearchPersonLiveData = MutableLiveData<VideoSearchResponse>()
    val videoSearchPersonLiveData: LiveData<VideoSearchResponse>
        get() = _videoSearchPersonLiveData
    var mSearchResultTitle = MutableLiveData<CharSequence>().apply {
        value = if(::mRecentQueryKeyword.isInitialized) mApplication.getString(R.string.search_list_title, mRecentQueryKeyword)
            else application.getString(R.string.search_list_title_hint)
    }
    var mAbnormalResultMessage = MutableLiveData<CharSequence>().apply { value = application.getString(R.string.no_search_result_caption) }
    private val initialPageNumber = 1
    private val maxPageNumber = 50

    var mPageNumberText = MutableLiveData<CharSequence>()
    var mNoSearchResult = ObservableField(false)
    var mSortOption = mPreferenceUtils.getSortOption()
    var mPrevPageButtonAvailability = ObservableField(false)
    var mNextPageButtonAvailability = ObservableField(false)
    var mPageButtonVisibility = ObservableField(false)
    var mPageNumber = initialPageNumber
    var mDisplayCount = mPreferenceUtils.getDisplayCount()
    var mFilterMenuVisibility = ObservableField(false)
    private lateinit var mRecentQueryKeyword : String

    lateinit var onQueryChangedListener : (queryKeyword : String, sortOption : KakaoSearchSortEnum, pageNumber : Int, displayCount : Int) -> Unit

    fun inputNewKeyword(queryKeyword : String) {
        mRecentQueryKeyword = queryKeyword
        onQueryChangedListener(queryKeyword, mSortOption, mPageNumber, mDisplayCount)
    }

    fun setSearchResult(isError : Boolean, errorMessage : String?, pageNumber : Int, isEmpty : Boolean, isEnd : Boolean) {
        if(isError) {
            mPageButtonVisibility.set(false)
            mNoSearchResult.set(isError)
            return
        }
        mPageNumber = pageNumber
        mPageNumberText.value = mApplication.getString(R.string.page_count, mPageNumber)
        mNoSearchResult.set(isEmpty)
        if(isEmpty) mSearchResultTitle.value = mApplication.getString(R.string.no_search_result_title, mRecentQueryKeyword)
        else mSearchResultTitle.value = mApplication.getString(R.string.search_list_title, mRecentQueryKeyword)

        if(isEmpty) mPageButtonVisibility.set(false)
        else {
            mPageButtonVisibility.set(true)
            if(mPageNumber == initialPageNumber) mPrevPageButtonAvailability.set(false)
            else mPrevPageButtonAvailability.set(true)
            if(mPageNumber == maxPageNumber || isEnd) mNextPageButtonAvailability.set(false)
            else mNextPageButtonAvailability.set(true)
        }
    }

    fun changeSortOption(sortOption: KakaoSearchSortEnum) {
        mSortOption = sortOption
        if(::mRecentQueryKeyword.isInitialized) onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
    }

    fun changeDisplayCount(displayCount : Int) {
        val prevDisplayCount = mDisplayCount
        mDisplayCount = displayCount
        if(::mRecentQueryKeyword.isInitialized)  {
            mPageNumber = kotlin.math.ceil(prevDisplayCount * mPageNumber / displayCount.toDouble()).toInt()
            if(mPageNumber > maxPageNumber) mPageNumber = maxPageNumber
            onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
        }
    }

    fun refresh() {
        if(::mRecentQueryKeyword.isInitialized) {
            onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber, mDisplayCount)
        }
    }

    /** 필터 메뉴가 보이게 설정합니다.*/
    fun showFilterMenu() {
        mFilterMenuVisibility.set(true)
    }

    /** 필터 메뉴가 보이지 않게 설정합니다.*/
    fun hideFilterMenu() {
        mFilterMenuVisibility.set(false)
    }

    // Layout 과 바인딩 된 메소드
    /** 이전 페이지로 이동합니다.*/
    fun boundOnPrevPageButtonClick() {
        onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber - 1, mDisplayCount)
    }

    /** 다음 페이지로 이동합니다.*/
    fun boundOnNextPageButtonClick() {
        onQueryChangedListener(mRecentQueryKeyword, mSortOption, mPageNumber + 1, mDisplayCount)
    }

//    fun getVideoSearch(query:String, page:Int, size:Int){
//        mImageListItemViewModelList.clear()
//        mCompositeDisposable.clear()
//        mCompositeDisposable.add(model.getData(query, KakaoSearchSortEnum.Accuracy, page, size)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                it.run {
//                    if (documents.size > 0) {
//                        Log.d(TAG, "documents : $documents")
//                        _videoSearchPersonLiveData.postValue(this)
//                    }
//                    Log.d(TAG, "meta : $meta")
//                }
//            }, {
//                Log.d(TAG, "response error, message : ${it.message}")
//            }))
//    }

}
