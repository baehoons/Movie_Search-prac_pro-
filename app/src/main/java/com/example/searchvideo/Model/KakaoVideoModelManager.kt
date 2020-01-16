package com.example.searchvideo.Model

import com.example.searchvideo.util.ConstantUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDate

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class KakaoVideoModelManager {
    private val baseUrl = "https://dapi.kakao.com"

    private class KakaoVideoModelDeserializer : JsonDeserializer<VideoSearchResponse.KakaoVideoModelList> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): VideoSearchResponse.KakaoVideoModelList {
            val jsonObject = json!!.asJsonObject
            val metaData = jsonObject.getAsJsonObject("meta")
            val kakaoVideoModelList = VideoSearchResponse.KakaoVideoModelList(
                isEnd = metaData.get("is_end").asBoolean,
                pageableCount = metaData.get("pageable_count").asInt,
                totalCount = metaData.get("total_count").asInt,
                documents = ArrayList()
            )
            jsonObject.getAsJsonArray("documents").forEach {
                val eachJsonObject = it.asJsonObject
                kakaoVideoModelList.documents.add(
                    VideoSearchResponse.Document(
                        title = eachJsonObject.get("title").asString,
                        url = eachJsonObject.get("url").asString,
                        datetime = eachJsonObject.get("datetime").asString,
                        play_time = eachJsonObject.get("play_time").asInt,
                        thumbnail = eachJsonObject.get("thumbnail").asString,
                        author = eachJsonObject.get("author").asString
                    )
                )
            }
            return kakaoVideoModelList
        }
    }

    private val kakaoVideoModelGson = GsonBuilder()
        .registerTypeAdapter(
            VideoSearchResponse.KakaoVideoModelList::class.java,
            KakaoVideoModelDeserializer()
        ).create()

    fun rxKakaoImageSearchByKeyword(queryKeyword : String, sortOption: KakaoSearchSortEnum, pageNumber : Int, size : Int) : Single<VideoSearchResponse.KakaoVideoModelList> {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(kakaoVideoModelGson))
            .build()
            .create(KakaoSearchService::class.java)
            .searchVideo(queryKeyword, sortOption.sort, pageNumber, size)
    }
}