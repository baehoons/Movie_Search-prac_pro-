package com.example.searchvideo.Model

import com.example.searchvideo.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

private val KAKAO_APP_KEY = "949ac6d8f8098df0314e71c4123a3db2"

interface KakaoSearchService {
    @Headers("Authorization: KakaoAK ${com.gmail.ayteneve93.api.BuildConfig.KakaoSearchV2ApiKey}")
    @GET("v2/search/vclip")
    fun searchVideo(
        @Query("query", encoded = true) query:String,
        @Query("sort", encoded = true) sort:String,
        @Query("page", encoded = true) page:Int,
        @Query("size", encoded = true) size:Int
    ): Single<VideoSearchResponse.KakaoVideoModelList>


}