package com.example.searchvideo

import com.example.searchvideo.util.ConstantUtils

@Suppress(ConstantUtils.SuppressWarningAttributes.SPELL_CHECKING_INSPECTION)
object MainBroadcastPreference {
    object Action{
        const val NEW_SEARCH_QUERY_INPUT = "com.example.searchvideo.Action.NEW_SEARCH_QUERY_INPUT"

        const val SORT_OPTION_CHANGED = "com.example.searchvideo.Action.SORT_OPTION_CHANGED"

        const val VIDEO_ITEM_CLICKED = "com.example.searchvideo.Action.IMAGE_ITEM_CLICKED"

        const val BACK_BUTTON_PRESSED = "com.example.searchvideo.Action.BACK_BUTTON_PRESSED"

        const val CLOSE_VIDEO_DETAIL_FRAGMENT = "com.example.searchvideo.Action.CLOSE_IMAGE_DETAIL_FRAGMENT"

        const val VIDEO_ITEM_SELECTION_MODE_CHANGED = "com.example.searchvideo.Action.IMAGE_ITEM_SELECTION_MODE_CHANGED"

        const val VIDEO_OPERATION_FINISHED = "com.example.searchvideo.Action.IMAGE_OPERATION_FINISHED"

        const val FINISH_APPLICATION = "com.example.searchvideo.Action.FINISH_APPLICATION"

        const val CHECK_IMAGE_OPERATION_PROCEEDING_WHEN_WIFI_DISCONNECTED = "com.example.searchvideo.Action.CHECK_IMAGE_OPERATION_PROCEEDING_WHEN_WIFI_DISCONNECTED"
    }
    object Target {
        /** 방송 수신 대상자의 키 값 */
        const val KEY = "com.example.searchvideo.Target.KEY"

        /** 미리 정의된 값 목록 */
        object PreDefinedValues {
            /** 매인 액티비티 */
            const val MAIN_ACTIVITY =
                "com.example.searchvideo.Target.PreDefinedValues.MAIN_ACTIVITY"
            /** 이미지 리스트 프래그먼트 */
            const val VIDEO_LIST = "com.example.searchvideo.Target.PreDefinedValues.IMAGE_LIST"
            /** 이미지 상세정보 프래그먼트 */
            const val VIDEO_DETAIL = "com.example.searchvideo.Target.PreDefinedValues.IMAGE_DETAIL"
        }
    }

    object Extra {

        object QueryString {
            /** 새롭게 입력된 검색어 키 값 */
            const val KEY = "com.example.searchvideo.Extra.QueryString.KEY"
        }
        /** 변경된 정렬 기준 */
        object SortOption {
            /** 변경된 정렬 기준 키 값 */
            const val KEY = "com.example.searchvideo.Extra.SortOption.KEY"
        }

        object DisplayCount {
            /** 변경된 이미지 표시 갯수 키 값 */
            const val KEY = "com.example.searchvideo.Extra.DisplayCount.KEY"
        }
        /** 선택된 이미지 아이템 */
        object VideoItem {
            /** 선택된 이미지 아이템 키 값 */
            const val KEY = "com.example.searchvideo.Extra.ImageItem.KEY"
        }
        /** 변경된 이미지 선택 모드 */
        object VideoItemSelectionMode {
            /** 변경된 이미지 선택 모드 키 값 */
            const val KEY = "com.example.searchvideo.Extra.ImageItemSelectionMode.KEY"
            /** 미리 정의된 값 목록 */
            enum class PreDefinedValues {
                /** 다중 선택 모드 */
                MULTI_SELECTION_MODE,
                /** 일반 단일 선택 모드 */
                SIGNLE_SELECTION_MODE,
            }
        }

        object VideoOperation {
            /** 이미지 다운로드/공유 정보 키 값 */
            const val KEY = "com.example.searchvideo.Extra.ImageOperation.KEY"
            /** 미리 정의된 값 목록 */
            enum class PreDefinedValues {
                /** 이미지 공유 */
                SHARE,
                /** 이미지 다운로드 */
                DOWNLOAD
            }
        }
    }
}