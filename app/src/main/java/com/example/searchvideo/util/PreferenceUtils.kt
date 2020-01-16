package com.example.searchvideo.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.searchvideo.Model.KakaoSearchSortEnum

private const val USER_PREF_KEY = "util.User.KEY"

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
class PreferenceUtils(
    /** DI를 통해 받아오는 Constructor Field 입니다. SharedPreference 객체를 가져오는데 사용합니다. */
    application: Application
) {
    private val userPreference : SharedPreferences = application.getSharedPreferences(USER_PREF_KEY, Context.MODE_PRIVATE)


    fun getSortOption(): KakaoSearchSortEnum = KakaoSearchSortEnum.getSortOptionFromString(userPreference.getString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName, KakaoSearchSortEnum.Accuracy.sort)!!)

    fun setSortOption(sortOption:KakaoSearchSortEnum) = userPreference.edit().putString(PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION.attributeName,sortOption.sort).apply()

    fun getDisplayCount() : Int =
        userPreference.getInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, 30)

    fun getVideoColumnCount() : Int =
        userPreference.getInt(PreferenceCategory.User.VIDEO_COLUMN_COUNT.attributeName, 3)

    /**
     * 페이지당 사진 표시 갯수를 저장합니다.
     *
     * @param displayCount 저장할 사진 표시 갯수
     */
    fun setDisplayCount(displayCount : Int) =
        userPreference.edit().putInt(PreferenceCategory.User.DISPLAY_COUNT.attributeName, displayCount).apply()

    fun getImageSizePercentage() : Float =
        userPreference.getFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, 1.0f)

    /**
     * 이미지 확대 수치를 저장합니다.
     *
     * @param imageSizePercentage 저장할 이미지 확대 수치입니다.
     */
    fun setImageSizePercentage(imageSizePercentage : Float) =
        userPreference.edit().putFloat(PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE.attributeName, imageSizePercentage).apply()

}

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
private object PreferenceCategory {
    /** 유저와 관련된 환경설정 정보 범주입니다. */
    enum class User(val attributeName : String) {
        DISPLAY_COUNT("util.PreferenceCategory.User.DISPLAY_COUNT"),
        /** 이미지 정렬 기준 */
        KAKAO_IMAGE_SORT_OPTION("util.PreferenceCategory.User.KAKAO_IMAGE_SORT_OPTION"),
        /** 이미지 확대 수치 */
        IMAGE_SIZE_PERCENTAGE("util.PreferenceCategory.User.IMAGE_SIZE_PERCENTAGE"),

        VIDEO_COLUMN_COUNT("util.PreferenceCategory.User.VIDEO_COLUMN_COUNT")
    }
}