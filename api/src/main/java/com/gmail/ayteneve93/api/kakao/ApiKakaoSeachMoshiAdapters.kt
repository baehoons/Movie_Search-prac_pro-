package com.gmail.ayteneve93.api.kakao

import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*


class KakaoVideoSearchDocumentDateAdapter : JsonAdapter<Date>() {
    private val mDateFormat = SimpleDateFormat(KAKAO_VIDEO_SEARCH_DOCUMENT_DATE_ADAPTER, Locale.getDefault())
    @FromJson
    override fun fromJson(reader: JsonReader): Date? =
        try { mDateFormat.parse(reader.nextString()) }
        catch(exception : Exception) { null }
    @ToJson
    override fun toJson(writer: JsonWriter, value: Date?) { value?.let { writer.value(value.toString()) } }
    companion object {
        private const val KAKAO_VIDEO_SEARCH_DOCUMENT_DATE_ADAPTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    }
}


