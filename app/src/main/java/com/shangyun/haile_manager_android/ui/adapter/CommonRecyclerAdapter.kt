package com.shangyun.haile_manager_android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


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
class CommonRecyclerAdapter<T : ViewDataBinding, D>(
    private val layoutId: Int,
    private val br: Int,
    private val onItemBind: ((mBinding: T?, date: D) -> Unit)?,
) : RecyclerView.Adapter<MyViewHolder<T, D>>() {

    protected val list: MutableList<D> = mutableListOf()

    fun refreshList(list: MutableList<D>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<T, D> {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder<T, D>, position: Int) {
        holder.bind(br, list[position])
        onItemBind?.invoke(holder.mBinding, list[position])
    }
}

class MyViewHolder<T : ViewDataBinding, D>(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val mBinding: T? = DataBindingUtil.bind(itemView)

    fun bind(br: Int, entity: D) = mBinding?.setVariable(br, entity)
}