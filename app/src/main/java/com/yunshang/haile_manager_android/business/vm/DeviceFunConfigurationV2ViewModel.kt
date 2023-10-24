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
import com.yunshang.haile_manager_android.data.extend.isGreaterThan0
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

    // 第一次请求，防止开始时多次请求数据,-1不请求,0未请求，1请求中，2已请求
    var isFirstData: Int = 0

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

    // 是否是投放器
    val isDispenser: LiveData<Boolean> = categoryCode.map {
        DeviceCategory.isDispenser(it)
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
        selectPriceModel.value =
            oldConfigureList?.firstOrNull()?.extAttrDto?.items?.firstOrNull()?.let { oldFirst ->
                priceModelList.find { item -> item.id == oldFirst.priceType }
            } ?: run {
                spuExtAttrDto?.let {
                    if (1 == it.priceType.size) {
                        priceModelList.find { item -> item.id == it.priceType.first() }
                    } else null
                }
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
        selectCalculateModel.value =
            oldConfigureList?.firstOrNull()?.extAttrDto?.items?.firstOrNull()?.let { oldFirst ->
                calculateModelList.find { item -> item.id == oldFirst.priceCalculateMode }
            } ?: run {
                spuExtAttrDto?.let {
                    if (1 == it.priceCalculateMode.size) {
                        calculateModelList.find { item -> item.id == it.priceCalculateMode.first() }
                    } else null
                }
            } ?: calculateModelList[0]
    }

    // 模版配置
    val configureList: MutableLiveData<MutableList<SkuFunConfigurationV2Param>> by lazy {
        MutableLiveData()
    }

    // 参数齐全
    val hasAllParams: MediatorLiveData<Boolean> = MediatorLiveData(false).apply {
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
    private fun filterConfigureList(): Boolean {
        return null != selectPriceModel.value && null != selectCalculateModel.value
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
        }, showLoading = false)
    }

    fun requestConfigureList() {
        launch({
            val isWashingOrShoes = DeviceCategory.isWashingOrShoes(categoryCode.value)
            ApiRepository.dealApiResult(
                mDeviceRepo.skuV2(
                    ApiRepository.createRequestBody(
                        hashMapOf(
                            "spuId" to spuId,
                            "priceType" to selectPriceModel.value?.id,
                            "priceCalculateMode" to selectCalculateModel.value?.id
                        )
                    )
                )
            )?.let {
                // 转换数据
                configureList.postValue(it.apply {
                    val newSold =
                        null == oldConfigureList || oldConfigureList!!.any { item -> 1 == item.soldState && item.channelCode.isNullOrEmpty() }
                    it.forEachIndexed { index, param ->
                        val channelCount = spuExtAttrDto.value?.channelCount
                        if (channelCount.isGreaterThan0()) {
                            // 如果旧数据为空或者有开始的功能但通道为null,开启前3个, 否则寻找相同的配置
                            param.soldState = if (newSold) {
                                if (index < channelCount!!) {
                                    param.channelCode = (index + 1).toString()
                                    1
                                } else 2
                            } else {
                                oldConfigureList?.find { item -> item.skuId == param.skuId }
                                    ?.let { same ->
                                        param.channelCode = same.channelCode
                                        same.soldState
                                    } ?: 2
                            }
                        }
                        oldConfigureList?.find { item -> item.skuId == param.skuId }?.let { same ->
                            param.mergeSku(same, channelCount.isGreaterThan0())
                        } ?: run {
                            // 默认全选，json转换不走构造函数，值为默认false，需要初始化
                            if (param.extAttrDto.items.all { item -> !item.isCheck }) {
                                if (isWashingOrShoes) {
                                    param.extAttrDto.items.firstOrNull()?.isCheck = true
                                } else {
                                    param.extAttrDto.items.forEach { item ->
                                        item.isCheck = true
                                    }
                                }
                            }
                        }
                    }
                })
            }
            isFirstData = 2
        })
    }

    fun save(context: Context, callBack: (json: String?) -> Unit) {
        configureList.value?.let { configureList ->
            configureList.forEachIndexed { i, param ->
                val index = i + 1
                if (param.nameVal.trim().isEmpty()) {
                    SToast.showToast(context, "请输入功能${index}的模式名称")
                    return@let
                }
//                if ((DeviceCategory.isWashingOrShoes(categoryCode.value) || DeviceCategory.isDryer(
//                        categoryCode.value
//                    )) && param.featureVal.trim().isEmpty()
//                ) {
//                    SToast.showToast(context, "请输入功能${index}的描述信息")
//                    return@let
//                }
                if (param.extAttrDto.items.all { item -> !item.isCheck }) {
                    SToast.showToast(context, "请选择功能${index}的配置")
                    return@let
                }

                if (param.extAttrDto.items.any { item -> item.unitAmount.isEmpty() }) {
                    SToast.showToast(
                        context,
                        "请输入功能${index}配置的时间"
                    )
                    return@let
                }

                if (param.extAttrDto.items.any { item -> item.unitPriceVal.isEmpty() }) {
                    SToast.showToast(
                        context,
                        "请${if (1 == selectPriceModel.value?.id) "选择" else "输入"}功能${index}配置的金额"
                    )
                    return@let
                }

                if (20 == communicationType && param.extAttrDto.items.any { item -> item.pulseVal.isEmpty() }) {
                    SToast.showToast(context, "请输入功能${index}配置的脉冲")
                    return@let
                }
            }

            // 如果有goodId，就是修改
            if (0 < goodId) {
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