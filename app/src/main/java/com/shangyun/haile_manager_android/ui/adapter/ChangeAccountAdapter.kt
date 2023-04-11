package com.shangyun.haile_manager_android.ui.adapter

import androidx.databinding.ViewDataBinding
import com.shangyun.haile_manager_android.BR
import com.shangyun.haile_manager_android.R
import com.shangyun.haile_manager_android.data.entities.UserInfo
import com.shangyun.haile_manager_android.databinding.ItemChangeAccountBinding


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
class ChangeAccountAdapter(list: MutableList<UserInfo>) :
    CommonRecyclerAdapter<ItemChangeAccountBinding, UserInfo>(
        R.layout.item_change_account,
        BR.item,
        list
    ) {
    override fun onBindView(mBinding: ItemChangeAccountBinding?, d: UserInfo) {
    }
}