package com.yunshang.haile_manager_android.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding


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
open class CommonRecyclerAdapter<T : ViewDataBinding, D>(
    private val layoutId: Int,
    private val br: Int? = null,
    private val onItemBind: ((mItemBinding: T?, pos: Int, item: D) -> Unit)? = null,
) : BaseRecyclerAdapter<D>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    open fun bindingData(item: D): Any? = item

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        onItemBind?.invoke(holder.bind(br, bindingData(list[position])), position, list[position])
    }
}