package com.yunshang.haile_manager_android.ui.activity.device

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lsy.framelib.ui.base.activity.BaseBindingActivity
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.CategoryService
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.data.arguments.IntentParams
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.entities.CategoryEntity
import com.yunshang.haile_manager_android.data.entities.DeviceAdvancedSettingEntity
import com.yunshang.haile_manager_android.data.entities.ShopAndPositionSelectEntity
import com.yunshang.haile_manager_android.data.model.ApiRepository
import com.yunshang.haile_manager_android.databinding.ActivityDeviceBatchAdvanceBinding
import com.yunshang.haile_manager_android.ui.activity.common.SearchSelectRadioActivity
import com.yunshang.haile_manager_android.ui.activity.common.ShopPositionSelectorActivity
import com.yunshang.haile_manager_android.ui.view.dialog.CommonBottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeviceBatchAdvanceActivity : BaseBindingActivity<ActivityDeviceBatchAdvanceBinding>() {
    private val mCategoryRepo = ApiRepository.apiClient(CategoryService::class.java)
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    private val selectDepartments = mutableStateListOf<ShopAndPositionSelectEntity>()
    private val categoryList: MutableList<CategoryEntity> = mutableListOf()
    private var selectCategory by mutableStateOf<CategoryEntity?>(null)
    private val advanceValueList = mutableStateListOf<DeviceAdvancedSettingEntity>()
    private var selectDeviceModel by mutableStateOf<SearchSelectParam?>(null)

    // 跳转回调界面
    private val startNext =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { intent ->
                when (it.resultCode) {
                    // 搜索店铺
                    IntentParams.ShopPositionSelectorParams.ShopPositionSelectorResultCode -> {
                        IntentParams.ShopPositionSelectorParams.parseSelectList(intent)
                            ?.let { selects ->
                                selectDepartments.clear()
                                selectDepartments.addAll(selects)
                            }
                    }
                    // 搜索设备型号
                    IntentParams.SearchSelectTypeParam.DeviceModelResultCode -> {
                        intent.getStringExtra(IntentParams.SearchSelectTypeParam.ResultData)
                            ?.let { json ->
                                selectDeviceModel =
                                    GsonUtils.json2List(json, SearchSelectParam::class.java)
                                        ?.firstOrNull()

                                requestAdvanceList()
                            }
                    }
                }
            }
        }

    override fun layoutId(): Int = R.layout.activity_device_batch_advance

    override fun backBtn(): View? = mBinding.barDeviceBatchAdvanceTitle.getBackBtn()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.WHITE

        mBinding.cvDeviceBatchAdvanceContent.setContent {
            BatchAdvanceContent()
        }
    }

    @Composable
    fun BatchAdvanceContent() {
        // 所属门店

        Column(
            modifier = Modifier
                .padding(top = 12.dp)
        ) {
            BatchAdvanceItem(
                "营业点", when (val count: Int = selectDepartments.size) {
                    0 -> ""
                    1 -> selectDepartments.firstOrNull()?.name ?: ""
                    else -> "已选中${count}个营业点"
                }
            ) {
                startNext.launch(
                    Intent(
                        this@DeviceBatchAdvanceActivity,
                        ShopPositionSelectorActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.ShopPositionSelectorParams.pack(
                                selectList = selectDepartments
                            )
                        )
                    }
                )
            }
            Divider(
                Modifier.padding(horizontal = 16.dp),
                0.5.dp,
                color = colorResource(id = R.color.dividing_line_color)
            )
            BatchAdvanceItem("设备类型", selectCategory?.name ?: "") {
                checkDeviceCategoryList()
            }
            Divider(
                Modifier.padding(horizontal = 16.dp),
                0.5.dp,
                color = colorResource(id = R.color.dividing_line_color)
            )
            BatchAdvanceItem("设备型号", selectDeviceModel?.name ?: "") {
                if (selectDepartments.isEmpty()) {
                    SToast.showToast(this@DeviceBatchAdvanceActivity, "请选择营业点")
                    return@BatchAdvanceItem
                }
                if (null == selectCategory) {
                    SToast.showToast(this@DeviceBatchAdvanceActivity, "请选择设备类型")
                    return@BatchAdvanceItem
                }
                startNext.launch(
                    Intent(
                        this@DeviceBatchAdvanceActivity,
                        SearchSelectRadioActivity::class.java
                    ).apply {
                        putExtras(
                            IntentParams.SearchSelectTypeParam.pack(
                                IntentParams.SearchSelectTypeParam.SearchSelectTypeDeviceModel,
                                selectCategory?.id ?: -1,
                                shopIdList = selectDepartments.mapNotNull { it.id }.toIntArray(),
                                positionIdList = selectDepartments.flatMap {
                                    it.positionList?.mapNotNull { item -> item.id } ?: listOf()
                                }.toIntArray(),
                                mustSelect = true, isAdvance = true
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            AdvanceListEntrance()
        }
    }

    @Composable
    fun BatchAdvanceItem(title: String, content: String? = null, onclick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(0.dp, 12.dp, 16.dp, 12.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onclick
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "*",
                        modifier = Modifier
                            .width(16.dp)
                            .padding(end = 2.dp),
                        fontSize = 17.sp,
                        textAlign = TextAlign.End,
                        color = colorResource(id = R.color.color_ff5219),
                    )
                    Text(
                        text = title,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 17.sp,
                        color = colorResource(id = R.color.color_black_85),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (content.isNullOrEmpty()) "点击选择" else content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                    fontSize = 17.sp,
                    color = colorResource(id = if (content.isNullOrEmpty()) R.color.color_black_45 else R.color.color_black_85),
                )
            }
            Image(
                painter = painterResource(id = R.drawable.icon_arrow_right),
                contentDescription = null
            )
        }
    }

    @Composable
    fun AdvanceListEntrance() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    if (selectDepartments.isEmpty()) {
                        SToast.showToast(this@DeviceBatchAdvanceActivity, "请选择营业点")
                        return@clickable
                    }
                    if (null == selectCategory) {
                        SToast.showToast(this@DeviceBatchAdvanceActivity, "请选择设备类型")
                        return@clickable
                    }
                    if (null == selectDeviceModel?.id) {
                        SToast.showToast(this@DeviceBatchAdvanceActivity, "请选择设备型号")
                        return@clickable
                    }

                    // 高级设置
                    startActivity(
                        Intent(
                            this@DeviceBatchAdvanceActivity,
                            DeviceAdvancedActivity::class.java
                        ).apply {
                            putExtras(
                                IntentParams.DeviceAdvanceParams.pack(
                                    advanceList = advanceValueList,
                                    shopIdList = selectDepartments
                                        .mapNotNull { it.id }
                                        .toIntArray(),
                                    positionIdList = selectDepartments
                                        .flatMap {
                                            it.positionList?.mapNotNull { item -> item.id }
                                                ?: listOf()
                                        }
                                        .toIntArray(),
                                    categoryId = selectCategory?.id,
                                    spuId = selectDeviceModel?.id
                                )
                            )
                        }
                    )
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.advanced_setup),
                modifier = Modifier.weight(1f),
                fontSize = 17.sp,
                color = colorResource(
                    id = R.color.color_black_85
                )
            )
            Image(
                painter = painterResource(id = R.drawable.icon_arrow_right),
                contentDescription = null
            )
        }
    }

    private fun checkDeviceCategoryList() {
        if (categoryList.isEmpty()) {
            launch({
                ApiRepository.dealApiResult(
                    mCategoryRepo.category(1)
                )?.let {
                    categoryList.clear()
                    categoryList.addAll(it)

                    withContext(Dispatchers.Main) {
                        showDeviceCategoryDialog()
                    }
                }
            })
        } else {
            showDeviceCategoryDialog()
        }
    }

    /**
     * 显示设备类型弹窗
     */
    private fun showDeviceCategoryDialog() {
        CommonBottomSheetDialog.Builder(
            getString(R.string.device_category),
            categoryList
        ).apply {
            mustSelect = false
            onValueSureListener =
                object :
                    CommonBottomSheetDialog.OnValueSureListener<CategoryEntity> {
                    override fun onValue(data: CategoryEntity?) {
                        selectCategory = data
                    }
                }
        }.build().show(supportFragmentManager)
    }

    private fun requestAdvanceList() {
        if (null == selectDeviceModel?.id) return
        launch({
            ApiRepository.dealApiResult(
                mDeviceRepo.requestAdvanceListBySpuId(selectDeviceModel!!.id)
            )?.let {
                advanceValueList.clear()
                advanceValueList.addAll(it)
            }
        })
    }
}
