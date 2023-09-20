package com.yunshang.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.MultiSelectParam
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.arguments.StaffPermissionParams
import com.yunshang.haile_manager_android.data.entities.DataPermissionShopDto
import com.yunshang.haile_manager_android.data.entities.UserPermissionEntity
import com.yunshang.haile_manager_android.databinding.DialogProfitDataSheetBinding
import com.yunshang.haile_manager_android.databinding.ItemProfitDataBtnBinding
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity

/**
 * Title :
 * Author: Lsy
 * Date: 2023/9/19 18:20
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class ProfitDataDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val PROFIT_DATA_TAG = "profit_data_tag"
    private lateinit var mBinding: DialogProfitDataSheetBinding

    // 搜索选择界面
    private val startSearchSelect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)?.let { json ->
                    when (it.resultCode) {
                        IntentParams.SearchSelectTypeParam.ShopResultCode -> {
                            builder.shopList =
                                GsonUtils.json2List(json, SearchSelectParam::class.java)
                                    ?.map { item ->
                                        DataPermissionShopDto(
                                            item.id,
                                            item.name,
                                            builder.shopList?.find { shop -> shop.id == item.id }?.updateFlag
                                                ?: true,
                                            true
                                        )
                                    }
                            buildShopList()
                        }
                    }
                }
            }
        }

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
        mBinding = DialogProfitDataSheetBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.tvCommonDialogClose.setOnClickListener {
            dismiss()
        }

        mBinding.tvCommonDialogCancel.setOnClickListener {
            dismiss()
        }

        mBinding.tvCommonDialogSure.setOnClickListener {
            dismiss()
            builder.callback?.invoke(
                builder.staffPermissionParams?.child,
                builder.shopList?.filter { item -> item.isCheck },
                builder.subAccountList.filter { item -> item.isCheck }.map { item -> item.id })
        }

        mBinding.clFunPermissionList.buildChild<ItemProfitDataBtnBinding, UserPermissionEntity>(
            builder.staffPermissionParams?.child, R.layout.item_profit_data_btn
        ) { _, childBinding, data ->
            childBinding.child = data
        }

        buildShopList()

        mBinding.clFunPermissionSubAccount.buildChild<ItemProfitDataBtnBinding, MultiSelectParam>(
            builder.subAccountList, R.layout.item_profit_data_btn
        ) { _, childBinding, data ->
            childBinding.child = data
        }
    }

    private fun buildShopList() {
        val list = mutableListOf<DataPermissionShopDto>()
        list.add(DataPermissionShopDto(0, "选择门店"))
        builder.shopList?.let {
            list.addAll(it)
        }
        mBinding.clFunPermissionShopList.buildChild<ItemProfitDataBtnBinding, DataPermissionShopDto>(
            list, R.layout.item_profit_data_btn
        ) { _, childBinding, data ->
            childBinding.child = data

            if (0 == data.id) {
                childBinding.cbProfitDataBtn.setCompoundDrawablesWithIntrinsicBounds(
                    R.mipmap.icon_profit_data_dialog_add_shop, 0, 0, 0
                )
                childBinding.cbProfitDataBtn.setOnCheckClickListener {
                    startSearchSelect.launch(
                        Intent(
                            requireContext(),
                            SearchSelectRadioActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.SearchSelectTypeParam.pack(
                                    IntentParams.SearchSelectTypeParam.SearchSelectTypeCouponShop,
                                    mustSelect = true,
                                    moreSelect = true,
                                    selectArr = list.filter { item -> item.isCheck && item.id != 0 }
                                        .map { item -> item.id }.toIntArray(),
                                    noUpdateArr = list.filter { item -> item.isCheck && item.id != 0 && !item.updateFlag }
                                        .map { item -> item.id }.toIntArray(),
                                )
                            )
                        }
                    )
                    true
                }
            } else {
                childBinding.cbProfitDataBtn.setOnCheckClickListener {
                    (!data.updateFlag).also { flag ->
                        if (flag) {
                            SToast.showToast(requireContext(), "不可取消")
                        }
                    }
                }
            }
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        show(manager, PROFIT_DATA_TAG)
    }

    internal class Builder(
        val staffPermissionParams: StaffPermissionParams?,
        var shopList: List<DataPermissionShopDto>?,
        fundsDistributionTypes: List<Int>?,
        val callback: ((childPermission: List<UserPermissionEntity>?, shopList: List<DataPermissionShopDto>?, types: List<Int>) -> Unit)? = null
    ) {

        val subAccountList = listOf(
            MultiSelectParam(1, "自己的分账", fundsDistributionTypes?.contains(1) ?: false),
            MultiSelectParam(2, "所有人的分账", fundsDistributionTypes?.contains(2) ?: false)
        )

        /**
         * 构建
         */
        fun build(): ProfitDataDialog = ProfitDataDialog(this)
    }
}