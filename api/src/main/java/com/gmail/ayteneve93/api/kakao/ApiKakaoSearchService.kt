package com.gmail.ayteneve93.api.kakao

import com.gmail.ayteneve93.api.BuildConfig
import com.gmail.ayteneve93.serializeToStringMap
import com.squareup.moshi.Moshi
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiKakaoSearchService {
    private const val BASE_URL = "https://dapi.kakao.com"
    private val mMoshi = Moshi.Builder()
        .add(KakaoVideoSearchDocumentDateAdapter())
        .build()
    private val mKakaoSearchApiImplement =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().apply {
                addInterceptor { interceptorChain ->
                    interceptorChain.request().let { originalRequest ->
                        interceptorChain.proceed(originalRequest.newBuilder()
                            .header("Authorization", "KakaoAK ${BuildConfig.KakaoSearchV2ApiKey}")
                            .method(originalRequest.method(), originalRequest.body())
                            .build())
                    }
                }
            }.build())
            .addConverterFactory(MoshiConverterFactory.create(mMoshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiKakaoSearchInterface::class.java)


    fun rxGetVideoClip(requestUrlQuery: ApiKakaoVideoSearchDataModels.GetVideoClipRequestUrlQuery) : Single<ApiKakaoVideoSearchDataModels.GetVideoClipResultBody> =
        mKakaoSearchApiImplement
            .rxGetKakaoVideoClip(requestUrlQuery.serializeToStringMap())

}