package com.yunshang.haile_manager_android.ui.view.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunshang.haile_manager_android.ui.theme.DisableTxtColor
import com.yunshang.haile_manager_android.ui.view.component.WidgetSpec
import com.yunshang.haile_manager_android.ui.view.component.WidgetState

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/4 14:44
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */

/**
 * 主要按钮
 * @param txt 文本
 * @param modifier 大小等参数
 * @param buttonSpec 按钮大小
 * @param buttonState 按钮状态
 * @param buttonColors 按钮颜色
 * @param contentPadding 按钮边距
 * @param iconId icon
 * @param iconSize icon大小
 * @param onclick 点击事件
 */
@Composable
fun PrimaryButton(
    txt: String,
    modifier: Modifier = Modifier,
    buttonSpec: WidgetSpec = WidgetSpec.Large,
    buttonState: WidgetState = WidgetState.InitState("加载中"),
    buttonColors: CustomSizeButtonColors = ButtonModel.primaryColorButton(),
    contentPadding: PaddingValues = ButtonModel.buttonPadding(buttonSpec = buttonSpec),
    iconId: Int? = null,
    iconSize: DpSize? = null,
    onclick: () -> Unit
) {
    val isEnable = buttonState !is WidgetState.DisableState

    // 内容
    if (contentPadding.calculateTopPadding() < 8.dp) {// button有最小宽高
        FillColorCustomSizeButton(
            modifier, buttonState, buttonColors = buttonColors, onclick = onclick
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    } else {
        Button(
            onClick = onclick,
            modifier = modifier,
            enabled = isEnable,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColors.containerColor,
                disabledContainerColor = buttonColors.disabledContainerColor,
                contentColor = buttonColors.contentColor,
                disabledContentColor = buttonColors.disabledContentColor,
            ),
            contentPadding = contentPadding,
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    }
}

/**
 * 次要按钮
 * @param txt 文本
 * @param modifier 大小等参数
 * @param buttonSpec 按钮大小
 * @param buttonState 按钮状态
 * @param buttonColors 按钮颜色
 * @param contentPadding 按钮边距
 * @param iconId icon
 * @param iconSize icon大小
 * @param onclick 点击事件
 */
@Composable
fun MinorButton(
    txt: String,
    modifier: Modifier = Modifier,
    buttonSpec: WidgetSpec = WidgetSpec.Large,
    buttonState: WidgetState = WidgetState.InitState("加载中"),
    buttonColors: CustomSizeButtonColors = ButtonModel.minorColorButton(),
    contentPadding: PaddingValues = ButtonModel.buttonPadding(buttonSpec = buttonSpec),
    border: BorderStroke = BorderStroke(1.dp, DisableTxtColor),
    iconId: Int? = null,
    iconSize: DpSize? = null,
    onclick: () -> Unit
) {
    val isEnable = buttonState !is WidgetState.DisableState

    // 内容
    if (contentPadding.calculateTopPadding() < 8.dp) {// button有最小宽高
        FillColorCustomSizeButton(
            modifier, buttonState, border = border, buttonColors = buttonColors, onclick = onclick
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    } else {
        OutlinedButton(
            onClick = onclick,
            modifier = modifier,
            enabled = isEnable,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColors.containerColor,
                disabledContainerColor = buttonColors.disabledContainerColor,
                contentColor = buttonColors.contentColor,
                disabledContentColor = buttonColors.disabledContentColor,
            ),
            contentPadding = contentPadding,
            border = border
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    }
}

/**
 * 警示按钮
 * @param txt 文本
 * @param modifier 大小等参数
 * @param buttonSpec 按钮大小
 * @param buttonState 按钮状态
 * @param buttonColors 按钮颜色
 * @param contentPadding 按钮边距
 * @param iconId icon
 * @param iconSize icon大小
 * @param onclick 点击事件
 */
@Composable
fun WarningButton(
    txt: String,
    modifier: Modifier = Modifier,
    buttonSpec: WidgetSpec = WidgetSpec.Large,
    buttonState: WidgetState = WidgetState.InitState("加载中"),
    buttonColors: CustomSizeButtonColors = ButtonModel.warningColorButton(),
    contentPadding: PaddingValues = ButtonModel.buttonPadding(buttonSpec = buttonSpec),
    iconId: Int? = null,
    iconSize: DpSize? = null,
    onclick: () -> Unit
) {
    val isEnable = buttonState !is WidgetState.DisableState

    // 内容
    if (contentPadding.calculateTopPadding() < 8.dp) {// button有最小宽高
        FillColorCustomSizeButton(
            modifier,
            buttonState,
            buttonColors = buttonColors,
            onclick = onclick
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    } else {
        Button(
            onClick = onclick,
            modifier = modifier,
            enabled = isEnable,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColors.containerColor,
                disabledContainerColor = buttonColors.disabledContainerColor,
                contentColor = buttonColors.contentColor,
                disabledContentColor = buttonColors.disabledContentColor,
            ),
            contentPadding = contentPadding,
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    }
}

/**
 * 自定义文本按钮按钮
 * @param txt 文本
 * @param modifier 大小等参数
 * @param buttonSpec 按钮大小
 * @param buttonState 按钮状态
 * @param buttonColors 按钮颜色
 * @param contentPadding 按钮边距
 * @param iconId icon
 * @param iconSize icon大小
 * @param onclick 点击事件
 */
@Composable
fun CustomTextButton(
    txt: String,
    modifier: Modifier = Modifier,
    buttonSpec: WidgetSpec = WidgetSpec.Large,
    buttonState: WidgetState = WidgetState.InitState("加载中"),
    buttonColors: CustomSizeButtonColors = ButtonModel.textColorButton(),
    contentPadding: PaddingValues = ButtonModel.buttonPadding(buttonSpec = buttonSpec),
    border: BorderStroke? = null,
    iconId: Int? = null,
    iconSize: DpSize? = null,
    onclick: () -> Unit
) {
    val isEnable = buttonState !is WidgetState.DisableState

    // 内容
    if (contentPadding.calculateTopPadding() < 8.dp) {// button有最小宽高
        FillColorCustomSizeButton(
            modifier, buttonState, border = border, buttonColors = buttonColors, onclick = onclick
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    } else {
        TextButton(
            onClick = onclick,
            modifier = modifier,
            enabled = isEnable,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColors.containerColor,
                disabledContainerColor = buttonColors.disabledContainerColor,
                contentColor = buttonColors.contentColor,
                disabledContentColor = buttonColors.disabledContentColor,
            ),
            contentPadding = contentPadding,
            border = border
        ) {
            ButtonContent(
                txt, buttonColors.buttonTxtColors, buttonSpec, buttonState, iconId, iconSize
            )
        }
    }
}

/**
 * 自定义按钮
 * @param modifier 大小等参数
 * @param buttonState 按钮状态
 * @param buttonColors 按钮颜色
 * @param contentPadding 按钮边距
 * @param onclick 点击事件
 * @param content 自定义按钮内容
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FillColorCustomSizeButton(
    modifier: Modifier = Modifier,
    buttonState: WidgetState = WidgetState.InitState("加载中"),
    contentPadding: PaddingValues = PaddingValues(12.dp, 4.dp),
    border: BorderStroke? = null,
    buttonColors: CustomSizeButtonColors,
    onclick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val isEnable = buttonState !is WidgetState.DisableState
    val containerColor = buttonColors.containerSelectColor(enabled = isEnable).value
    val contentColor = buttonColors.contentSelectColor(enabled = isEnable).value
    Surface(
        onClick = onclick,
        modifier = modifier,
        enabled = isEnable,
        shape = ButtonDefaults.shape,
        color = containerColor,
        contentColor = contentColor,
        border = border,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        Row(
            Modifier.padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * @param txt 按钮文字
 * @param txtColors 文字颜色
 * @param buttonSpec 按钮规格
 * @param buttonState 按钮状态
 */
@Composable
fun ButtonContent(
    txt: String,
    txtColors: ButtonTxtColors,
    buttonSpec: WidgetSpec,
    buttonState: WidgetState,
    iconId: Int? = null,
    iconSize: DpSize? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val txtColor =
            txtColors.txtSelectColor(enabled = buttonState !is WidgetState.DisableState).value
        if (buttonState is WidgetState.InitState) {
            CircularProgressIndicator(
                modifier = Modifier.size(if (buttonSpec is WidgetSpec.Small) 16.dp else 18.dp),
                color = txtColors.indicatorColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            iconId?.let {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    modifier = iconSize?.let { Modifier.size(it) } ?: Modifier
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        val txtVal =
            if (buttonState is WidgetState.InitState) buttonState.initName.ifEmpty { txt } else txt
        if (txtVal.isNotEmpty()) {
            Text(
                text = txtVal,
                fontSize = (if (buttonSpec is WidgetSpec.Large) 17 else 14).sp,
                color = txtColor
            )
        }
    }
}
