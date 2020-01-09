package com.example.searchvideo.Model

import io.reactivex.Single

class DataModelImpl(private val service:KakaoSearchService):DataModel{

    private val KAKAO_APP_KEY = "949ac6d8f8098df0314e71c4123a3db2"

    override fun getData(query:String, sort:KakaoSearchSortEnum, page:Int, size:Int): Single<VideoSearchResponse> {
        return service.searchVideo(auth = "KakaoAK $KAKAO_APP_KEY", query = query, sort = sort.sort, page = page, size = size)
    }
}