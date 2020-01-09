package com.example.searchvideo.Model

import io.reactivex.Single

interface DataModel {
    fun getData(query:String, sort: KakaoSearchSortEnum, page:Int, size:Int): Single<VideoSearchResponse>
}
