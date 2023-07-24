package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridLayout.LayoutParams
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.DimensionUtils
import com.lsy.framelib.utils.ScreenUtils
import com.lsy.framelib.utils.StringUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.databinding.DialogDeviceCategoryBinding
import com.yunshang.haile_manager_android.databinding.ItemDialogDeviceCategoryBinding
import timber.log.Timber

/**
 * Title :
 * Author: Lsy
 * Date: 2023/7/24 09:58
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceCategoryDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val DEVICE_CATEGORY_SHEET_TAG = "device_category_sheet_tag"
    private lateinit var mBinding: DialogDeviceCategoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CommonBottomSheetDialogStyle)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            if (this is BottomSheetDialog) {
                setOnShowListener {
                    behavior.isHideable = false
                    // dialog上还有一层viewGroup，需要设置成透明
                    (requireView().parent as ViewGroup).setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogDeviceCategoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.glDeviceCategory.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())
        val columnCount = mBinding.glDeviceCategory.columnCount
        val space = DimensionUtils.dip2px(requireContext(), 12f)
        val itemW = (ScreenUtils.screenWidth - 3 * space) / columnCount
        val itemH = DimensionUtils.dip2px(requireContext(), 120f)
        builder.categoryList.forEachIndexed { index, deviceCategoryItem ->
            val rowIndex = index / columnCount
            val columnIndex = index % columnCount
            mBinding.glDeviceCategory.addView(
                ItemDialogDeviceCategoryBinding.inflate(inflater).let { itemBinding ->
                    itemBinding.icon = deviceCategoryItem.icon
                    itemBinding.name = deviceCategoryItem.name
                    itemBinding.desc = deviceCategoryItem.desc
                    itemBinding.root.setOnClickListener {
                        builder.onDeviceCodeSelectListener?.invoke(deviceCategoryItem.type)
                    }
                    itemBinding.root
                },
                LayoutParams(
                    GridLayout.spec(index / columnCount, 1f),
                    GridLayout.spec(index % columnCount, 1f)
                ).apply {
                    width = itemW
                    height = itemH
                    topMargin = if (0 == rowIndex) 0 else space
                    if (0 == columnIndex)
                        rightMargin = space / 2
                    else
                        leftMargin = space / 2
                }
            )
        }

        // 滑动关闭，通过behavior
        if (dialog is BottomSheetDialog) {
            (dialog as? BottomSheetDialog)?.behavior?.let {
                it.isDraggable = true
                it.peekHeight = 0
                it.state = BottomSheetBehavior.STATE_EXPANDED

                it.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        Timber.i("移动量：$slideOffset")
                        if (0f == slideOffset) {
                            dismiss()
                        }
                    }
                })
            }
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = true
        show(manager, DEVICE_CATEGORY_SHEET_TAG)
    }

    internal class Builder {
        var categoryList = arrayListOf(
            DeviceCategoryItem(
                R.mipmap.icon_device_category_wash_dryer_small,
                StringUtils.getString(R.string.category_wash_dryer),
                StringUtils.getString(R.string.category_wash_dryer_desc),
                0
            ),
            DeviceCategoryItem(
                R.mipmap.icon_device_category_hair_small,
                StringUtils.getString(R.string.hair),
                StringUtils.getString(R.string.category_hair_desc),
                1
            ),
            DeviceCategoryItem(
                R.mipmap.icon_device_category_shower_small,
                StringUtils.getString(R.string.category_shower),
                StringUtils.getString(R.string.category_shower_desc),
                2
            ),
            DeviceCategoryItem(
                R.mipmap.icon_device_category_dispenser_small,
                StringUtils.getString(R.string.category_dispenser),
                StringUtils.getString(R.string.category_dispenser_desc),
                3
            ),
            DeviceCategoryItem(
                R.mipmap.icon_device_category_drink_small,
                StringUtils.getString(R.string.category_drink),
                StringUtils.getString(R.string.category_drink_desc),
                4
            ),
        )

        data class DeviceCategoryItem(
            val icon: Int,
            val name: String,
            val desc: String,
            val type: Int,
        )

        // 选择监听
        var onDeviceCodeSelectListener: ((type: Int)->Unit)? = null

        /**
         * 构建
         */
        fun build(): DeviceCategoryDialog = DeviceCategoryDialog(this)
    }
}