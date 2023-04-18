package com.shangyun.haile_manager_android.ui.view.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lsy.framelib.utils.SToast
import com.lsy.framelib.utils.StringUtils
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.business.apiService.CommonService
import com.shangyun.haile_manager_android.data.entities.AreaEntity
import com.shangyun.haile_manager_android.data.model.ApiRepository
import com.shangyun.haile_manager_android.databinding.DialogAreaSheetBinding
import com.shangyun.haile_manager_android.databinding.ItemAreaSelectBinding
import com.shangyun.haile_manager_android.ui.view.adapter.CommonRecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Title : 日期选择Dialog
 * Author: Lsy
 * Date: 2023/4/11 09:47
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
class AreaSelectDialog private constructor(private val builder: Builder) :
    BottomSheetDialogFragment() {
    private val mRepo = ApiRepository.apiClient(CommonService::class.java)

    private val AREA_SELECT_TAG = "area_select_tag"
    private lateinit var mBinding: DialogAreaSheetBinding

    // 0省 1市 2区
    private var selectType = -1

    private val provinceList: MutableList<AreaEntity> = mutableListOf()

    private val cityList: HashMap<Int, MutableList<AreaEntity>> = hashMapOf()

    private val districtList: HashMap<Int, MutableList<AreaEntity>> = hashMapOf()

    private var province: AreaEntity? = null
    private var city: AreaEntity? = null
    private var district: AreaEntity? = null

    private val mAdapter: CommonRecyclerAdapter<ItemAreaSelectBinding, AreaEntity> by lazy {
        CommonRecyclerAdapter(R.layout.item_area_select, BR.item) { mItemBinding, item ->
            mItemBinding?.root?.setOnClickListener {
                when (selectType) {
                    0 -> changeProvince(item)
                    1 -> changeCity(item)
                    2 -> changeDistrict(item)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DateTimeStyle)
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
        mBinding = DialogAreaSheetBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 关闭
        mBinding.ibAreaSelectClose.setOnClickListener {
            dismiss()
        }

        mBinding.tvAreaSelectProvince.setOnClickListener {
            requestArea(0, 0)
        }
        mBinding.tvAreaSelectCity.setOnClickListener {
            if (null == province) {
                SToast.showToast(requireContext(), "请先选择省份")
                return@setOnClickListener
            }
            requestArea(1, province!!.areaId)
        }
        mBinding.tvAreaSelectDistrict.setOnClickListener {
            if (null == city) {
                SToast.showToast(requireContext(), "请先选择城市")
                return@setOnClickListener
            }
            requestArea(2, city!!.areaId)
        }

        mBinding.rvAreaSelectList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvAreaSelectList.adapter = mAdapter

        // 省列表
        requestArea(0, 0)
    }

    /**
     * 请求数据
     */
    private fun requestArea(type: Int, parentId: Int) {
        if(selectType == type){
            return
        }

        this.selectType = type
        mBinding.tvAreaSelectListTitle.text = when (selectType) {
            0 -> StringUtils.getString(R.string.select_province)
            1 -> StringUtils.getString(R.string.select_city)
            2 -> StringUtils.getString(R.string.select_district)
            else -> ""
        }

        // 如果有缓存就不需要再请求
        var isRequest = true
        when (selectType) {
            0 -> {
                if (provinceList.isNotEmpty()) {
                    isRequest = false
                    refreshAreaList(provinceList)
                }
            }
            1 -> {
                if (!cityList[parentId].isNullOrEmpty()) {
                    isRequest = false
                    refreshAreaList(cityList[parentId]!!)
                }
            }
            2 -> {
                if (!districtList[parentId].isNullOrEmpty()) {
                    isRequest = false
                    refreshAreaList(districtList[parentId]!!)
                }
            }
        }

        if (isRequest) {
            lifecycleScope.launch {
                try {
                    val list = ApiRepository.dealApiResult(mRepo.shopTypeList(parentId))
                    list?.let {
                        when (selectType) {
                            0 -> {
                                provinceList.clear()
                                provinceList.addAll(it)
                            }
                            1 -> {
                                cityList[parentId] = it
                            }
                            2 -> districtList[parentId] = it
                        }
                        withContext(Dispatchers.Main) {
                            refreshAreaList(it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 刷新列表并返回头部
     */
    private fun refreshAreaList(list: MutableList<AreaEntity>) {
        mAdapter.refreshList(list, true)
        mBinding.rvAreaSelectList.scrollToPosition(0)
    }

    /**
     * 切换省
     */
    private fun changeProvince(area: AreaEntity) {
        province = area
        mBinding.tvAreaSelectProvince.text = area.areaName
        mBinding.viewAreaSelectProvinceDot.setBackgroundResource(R.drawable.shape_circle_f0a65f)
        changeCity(null)
        requestArea(1, area.areaId)
    }

    /**
     * 切换市
     */
    private fun changeCity(area: AreaEntity?) {
        city = area
        mBinding.tvAreaSelectCity.text =
            area?.areaName ?: StringUtils.getString(R.string.select_city)
        mBinding.viewAreaSelectCityDot.setBackgroundResource(area?.let { R.drawable.shape_circle_f0a65f }
            ?: R.drawable.shape_circle_stroke_f0a65f)
        changeDistrict(null)
        area?.let {
            requestArea(2, area.areaId)
        }
    }


    /**
     * 切换市
     */
    private fun changeDistrict(area: AreaEntity?) {
        district = area
        mBinding.tvAreaSelectDistrict.text =
            area?.areaName ?: StringUtils.getString(R.string.select_district)
        mBinding.viewAreaSelectDistrictDot.setBackgroundResource(area?.let { R.drawable.shape_circle_f0a65f }
            ?: R.drawable.shape_circle_stroke_f0a65f)

        if (null != province && null != city && null != district) {
            builder.onAreaSelect?.invoke(province!!, city!!, district!!)
            dismiss()
        }
    }

    /**
     * 默认显示
     */
    fun show(manager: FragmentManager) {
        //不可取消
        isCancelable = builder.isCancelable
        show(manager, AREA_SELECT_TAG)
    }

    internal class Builder {

        // 不可取消
        var isCancelable = true

        // 回调
        var onAreaSelect: ((AreaEntity, AreaEntity, AreaEntity) -> Unit)? = null

        /**
         * 构建
         */
        fun build(): AreaSelectDialog = AreaSelectDialog(this)
    }
}