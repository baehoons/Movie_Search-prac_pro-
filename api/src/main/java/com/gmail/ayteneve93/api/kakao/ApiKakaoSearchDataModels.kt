package com.gmail.ayteneve93.api.kakao

import androidx.annotation.Keep
import com.squareup.moshi.Json
import java.util.*

object ApiKakaoVideoSearchDataModels {
    data class GetVideoClipRequestUrlQuery (
        val query : String
    )
    data class GetVideoClipResultBody (
        val documents : Array<Document>,
        val meta : Meta
    ) {
        @Keep
        data class Document(
            val author : String,
            val datetime : Date,
            @field:Json(name = "play_time") val playTime : Int,
            val thumbnail : String,
            val title : String,
            val url : String
        )
        @Keep
        data class Meta(
            val clusters : Clusters,
            @field:Json(name = "group_filter_groups") val groupFilterGroups : Int,
            @field:Json(name = "is_end") val isEnd : Boolean,
            @field:Json(name = "pageable_count") val pageableCount : Int,
            @field:Json(name = "session_id") val sessionId : Long,
            @field:Json(name = "total_count") val totalCount : Int,
            @field:Json(name = "total_time") val totalTime : Double
        ) {
            data class Clusters(
                val search : ClusterDetail,
                val summary : ClusterDetail
            ) {
                @Keep
                data class ClusterDetail (
                    @field:Json(name = "ET") val et : Int,
                    val failed : Int,
                    val total : Int
                )
            }
        }
    }
}