package com.gmail.ayteneve93.api.kakao

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

internal interface ApiKakaoSearchInterface {

    @GET("/v2/search/vclip")
    fun rxGetKakaoVideoClip(@QueryMap option : Map<String, String>) : Single<ApiKakaoVideoSearchDataModels.GetVideoClipResultBody>

}