package com.yunshang.haile_manager_android.business.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.lsy.framelib.async.LiveDataBus
import com.lsy.framelib.ui.base.BaseViewModel
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.lsy.framelib.utils.gson.GsonUtils
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.business.apiService.DeviceService
import com.yunshang.haile_manager_android.business.event.BusEvents
import com.yunshang.haile_manager_android.data.arguments.SearchSelectParam
import com.yunshang.haile_manager_android.data.common.DeviceCategory
import com.yunshang.haile_manager_android.data.entities.SkuFunConfigurationV2Param
import com.yunshang.haile_manager_android.data.entities.SpuExtAttrDto
import com.yunshang.haile_manager_android.data.model.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/10 11:19
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class DeviceFunConfigurationV2ViewModel : BaseViewModel() {
    private val mDeviceRepo = ApiRepository.apiClient(DeviceService::class.java)

    // goodId
    var goodId: Int = -1

    // spuid
    var spuId: Int = -1

    // 设备类型
    val categoryCode: MutableLiveData<String> by lazy {
        MutableLiveData()
    }

    // 10-串口 20-脉冲
    var communicationType: Int = -1

    // 是否是洗衣机和洗鞋机
    val isWashingOrShoes: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isWashingOrShoes(it)
    }

    // 是否是洗衣机、洗鞋机和烘干机
    val isWashingOrShoesOrDryer: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isWashingOrShoes(it) || DeviceCategory.isDryer(it)
    }

    // spu配置
    val spuExtAttrDto: MutableLiveData<SpuExtAttrDto?> by lazy {
        MutableLiveData()
    }

    // 老数据
    var oldConfigureList: MutableList<SkuFunConfigurationV2Param>? = null

    // 价格模式
    val priceModelList = listOf(
        SearchSelectParam(1, StringUtils.getString(R.string.price_model_type1)),
        SearchSelectParam(2, StringUtils.getString(R.string.price_model_type2)),
    )

    // spu是否只有一个价格模式
    val isSinglePriceModel: LiveData<Boolean> = spuExtAttrDto.map {
        it?.let { it.priceType.size == 1 } ?: false
    }

    // 选择的价格模式
    val selectPriceModel: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    // 初始化
    fun initSelectPriceModel(spuExtAttrDto: SpuExtAttrDto?) {
        selectPriceModel.value = spuExtAttrDto?.let {
            if (1 == it.priceType.size) {
                priceModelList.find { item -> item.id == it.priceType.first() }
            } else null
        } ?: priceModelList[0]
    }

    // 计费模式
    val calculateModelList = listOf(
        SearchSelectParam(1, StringUtils.getString(R.string.for_quantity)),
        SearchSelectParam(2, StringUtils.getString(R.string.for_time)),
    )

    // spu是否只有一个计费模式
    val isSingleCalculateModel: LiveData<Boolean> = spuExtAttrDto.map {
        it?.let { it.priceCalculateMode.size == 1 } ?: false
    }

    // 选择的计费模式
    val selectCalculateModel: MutableLiveData<SearchSelectParam> by lazy {
        MutableLiveData()
    }

    fun initSelectCalculateModel(spuExtAttrDto: SpuExtAttrDto?) {
        selectCalculateModel.value = spuExtAttrDto?.let {
            if (1 == it.priceCalculateMode.size) {
                calculateModelList.find { item -> item.id == it.priceCalculateMode.first() }
            } else null
        } ?: calculateModelList[0]
    }

    // 模版配置
    val configureList: MutableLiveData<MutableList<SkuFunConfigurationV2Param>> by lazy {
        MutableLiveData()
    }

    // 过滤后的配置列表
    val dealConfigureList: MediatorLiveData<MutableList<SkuFunConfigurationV2Param>> =
        MediatorLiveData(mutableListOf<SkuFunConfigurationV2Param>()).apply {
            addSource(configureList) {
                value = filterConfigureList()
            }
            addSource(selectPriceModel) {
                value = filterConfigureList()
            }
            addSource(selectCalculateModel) {
                value = filterConfigureList()
            }
        }

    /**
     * 过滤价格模式和计费模式后的配置列表
     */
    private fun filterConfigureList(): MutableList<SkuFunConfigurationV2Param> {
        val list = configureList.value
        val isWashingOrShoes = DeviceCategory.isWashingOrShoes(categoryCode.value)
        return if (!list.isNullOrEmpty() && null != selectPriceModel.value && null != selectCalculateModel.value) {
            list.forEach { param ->
                // 如果有相同的配置，合并
                oldConfigureList?.find { item -> item.skuId == param.skuId }?.let { sameParam ->
                    param.mergeSku(sameParam)
                }
                // 初始化过滤列表
                param.initSelectExtAttr(
                    isWashingOrShoes,
                    selectPriceModel.value!!.id,
                    selectCalculateModel.value!!.id
                )
            }
            list.filter { item -> !item.selectExtAttr.isNullOrEmpty() }.toMutableList()
        } else mutableListOf()
    }

    fun requestData() {
        if (spuId <= 0) {
            return
        }

        launch({
            // 如果没有spu数据，先请求
            if (null == spuExtAttrDto.value) {
                ApiRepository.dealApiResult(mDeviceRepo.spuDetail(spuId))?.let {
                    spuExtAttrDto.postValue(it.extAttrDto)
                }
            }

            val isWashingOrShoes = DeviceCategory.isWashingOrShoes(categoryCode.value)
            ApiRepository.dealApiResult(mDeviceRepo.sku(spuId))?.let {
                // 转换数据
                configureList.postValue(it.map { sku ->
                    sku.toFunConfigureV2Param(
                        isWashingOrShoes
                    )
                }.toMutableList())
            }
        })
    }

    fun save(context: Context, callBack: (json: String?) -> Unit) {
        dealConfigureList.value?.let { configureList ->
            var washDefaultOpen = false
            configureList.forEachIndexed { i, param ->
                val index = i + 1
                if (param.nameVal.isEmpty()) {
                    SToast.showToast(context, "请输入功能${index}的模式名称")
                    return@let
                }
                if ((DeviceCategory.isWashingOrShoes(categoryCode.value) || DeviceCategory.isDryer(
                        categoryCode.value
                    )) && param.featureVal.isEmpty()
                ) {
                    SToast.showToast(context, "请输入功能${index}的描述信息")
                    return@let
                }
                if (param.selectExtAttr.isEmpty()) {
                    SToast.showToast(context, "请选择功能${index}的配置")
                    return@let
                }

                if (param.selectExtAttr.any { item -> item.unitAmount.isEmpty() }) {
                    SToast.showToast(
                        context,
                        "请输入功能${index}配置的时间"
                    )
                    return@let
                }

                if (param.selectExtAttr.any { item -> item.unitPriceVal.isEmpty() }) {
                    SToast.showToast(
                        context,
                        "请${if (1 == selectPriceModel.value?.id) "选择" else "输入"}功能${index}配置的金额"
                    )
                    return@let
                }

                if (param.selectExtAttr.any { item -> item.pulseVal.isEmpty() }) {
                    SToast.showToast(context, "请输入功能${index}配置的脉冲")
                    return@let
                }

                // 只有固定价格才有默认选中
                if (1 == selectPriceModel.value?.id) {
                    if (DeviceCategory.isWashingOrShoes(categoryCode.value)) {
                        if (true == param.selectExtAttr.firstOrNull()?.isDefault) {
                            washDefaultOpen = true
                            return@forEachIndexed
                        }
                    } else {
                        washDefaultOpen = true
                        if (!param.selectExtAttr.any { item -> item.isDefault }) {
                            SToast.showToast(context, "请选择功能${index}的默认选中项")
                            return@let
                        }
                    }
                } else washDefaultOpen = true
            }

            if (!washDefaultOpen) {
                SToast.showToast(context, "请选择至少开启一个默认选中项")
                return@let
            }

            configureList.forEach {
                it.extAttrDto.items = it.selectExtAttr
            }

            // 如果有goodId，就是修改
            if (0 < goodId){
                launch({
                    ApiRepository.dealApiResult(
                        mDeviceRepo.deviceUpdateV2(
                            ApiRepository.createRequestBody(
                                hashMapOf(
                                    "id" to goodId,
                                    "items" to configureList
                                )
                            )
                        )
                    )
                    withContext(Dispatchers.Main) {
                        SToast.showToast(msgResId = R.string.update_success)
                    }
                    LiveDataBus.post(BusEvents.DEVICE_DETAILS_STATUS, true)
                    callBack(null)
                })
            } else {
                callBack(GsonUtils.any2Json(configureList))
            }
        }
    }
}