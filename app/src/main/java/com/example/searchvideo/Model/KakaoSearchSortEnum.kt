package com.example.searchvideo.Model


enum class KakaoSearchSortEnum(val sort:String){
    Accuracy("accuracy"),
    Recency("recency");

    companion object {
        fun getSortOptionFromString(optionString : String) : KakaoSearchSortEnum = values().find { it.sort == optionString }!!
    }
}