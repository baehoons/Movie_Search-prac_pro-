package com.example.searchvideo.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.searchvideo.Model.KakaoSearchSortEnum

private const val USER_PREF_KEY = "util.User.KEY"

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class PreferenceUtils(application: Application)
{

    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)

    fun getSortOption(): KakaoSearchSortEnum = KakaoSearchSortEnum.getSortOptionFromString(userPreference.getString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, KakaoSearchSortEnum.Accuracy.sort)!!)

    fun setSortOption(sortOption:KakaoSearchSortEnum) = userPreference.edit().putString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName,sortOption.sort).apply()

    fun getDisplayCount() : Int =
        userPreference.getInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, 30)

    fun getVideoColumnCount() : Int =
        userPreference.getInt(PreferenceCategory.User.VIDEO_COLUMN_COUNT.attributeName, 3)

    fun getImageSizePercentage() : Float =
        userPreference.getFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, 1.0f)


}

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
private object PreferenceCategory {

    enum class User(val attributeName : String) {
        DISPLAY_COUNT("util.PreferenceCategory.User.DISPLAY_COUNT"),
        KAKAO_IMAGE_SORT_OPTION("util.PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION"),
        IMAGE_SIZE_PERCENTAGE("util.PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE"),
        VIDEO_COLUMN_COUNT("util.PreferenceCategory.User.VIDEO_COLUMN_COUNT")
    }
}