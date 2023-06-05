package com.yunshang.haile_manager_android.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import com.yunshang.haile_manager_android.data.rule.CategoryAdapterEntity


/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/11 20:32
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
abstract class CategoryRecyclerAdapter<D : CategoryAdapterEntity> : BaseRecyclerAdapter<D>() {

    override fun getItemViewType(position: Int): Int = list[position].itemViewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(typeLayoutId(viewType), parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        onItemBind(
            holder.bind(BR.item, list[position].contentEntity),
            position,
            list[position].contentEntity,
            getItemViewType(position)
        )
    }

    abstract fun typeLayoutId(viewType: Int): Int

    abstract fun onItemBind(mItemBinding: ViewDataBinding?, pos: Int, item: Any?, itemViewType: Int)
}
