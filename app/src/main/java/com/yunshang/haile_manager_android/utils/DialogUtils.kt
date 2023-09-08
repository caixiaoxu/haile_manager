package com.yunshang.haile_manager_android.utils

import androidx.fragment.app.FragmentActivity
import com.lsy.framelib.utils.StringUtils
import com.luck.picture.lib.entity.LocalMedia
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog

/**
 * Title :
 * Author: Lsy
 * Date: 2023/5/31 17:34
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object DialogUtils {

    /**
     * 显示图片选择弹窗
     */
    fun showImgSelectorDialog(
        activity: FragmentActivity,
        maxNum: Int,
        showTake: Boolean = true,
        showAlbum: Boolean = true,
        callback: (isSuccess: Boolean, result: ArrayList<LocalMedia>?) -> Unit
    ) {
        val list = arrayListOf<SearchSelectParam>()
        if (showTake) {
            list.add(SearchSelectParam(1, StringUtils.getString(R.string.for_take)))
        }
        if (showAlbum) {
            list.add(SearchSelectParam(2, StringUtils.getString(R.string.for_album)))
        }

        CommonBottomSheetDialog.Builder("", list).apply {
            onValueSureListener = object :
                CommonBottomSheetDialog.OnValueSureListener<SearchSelectParam> {
                override fun onValue(data: SearchSelectParam?) {
                    if (1 == data!!.id) {
                        PictureSelectUtils.takePicture(activity, callback)
                    } else {
                        PictureSelectUtils.pictureForAlbum(activity, maxNum, callback = callback)
                    }
                }
            }
        }.build().show(activity.supportFragmentManager)
    }
}