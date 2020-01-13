package com.example.searchvideo.Model


enum class KakaoSearchSortEnum(val sort:String){
    Accuracy("accuracy"),
    Recency("recency");

    companion object {
        /** String 을 입력하면 그에 맞는 SortOption 을 Retrun 합니다.
         *
         * @param optionString 입력한 문자열
         * @return 입력한 문자열에 해당하는 SortOption
         */
        fun getSortOptionFromString(optionString : String) : KakaoSearchSortEnum = values().find { it.sort == optionString }!!
    }
}