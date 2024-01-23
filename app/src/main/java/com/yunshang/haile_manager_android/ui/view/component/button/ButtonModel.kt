package com.yunshang.haile_manager_android.ui.view.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunshang.haile_manager_android.ui.theme.DisableTxtColor
import com.yunshang.haile_manager_android.ui.theme.PrimaryColor500
import com.yunshang.haile_manager_android.ui.theme.PrimaryPressColor
import com.yunshang.haile_manager_android.ui.theme.PrimaryTxtColor
import com.yunshang.haile_manager_android.ui.theme.WarningColor
import com.yunshang.haile_manager_android.ui.theme.WarningColor500
import com.yunshang.haile_manager_android.ui.theme.WarningPressColor
import com.yunshang.haile_manager_android.ui.theme.WarningPressColor500
import com.yunshang.haile_manager_android.ui.theme.PrimaryColor
import com.yunshang.haile_manager_android.ui.view.component.WidgetSpec

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/8 18:15
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */

object ButtonModel {

    /**
     * 默认按钮边距
     */
    @Composable
    fun buttonPadding(buttonSpec: WidgetSpec) = when (buttonSpec) {
        is WidgetSpec.Medium -> PaddingValues(16.dp, 8.dp)
        is WidgetSpec.Small -> PaddingValues(12.dp, 4.dp)
        else -> ButtonDefaults.ContentPadding
    }

    /**
     * 主要按钮颜色值
     */
    @Composable
    fun primaryColorButton(
        containerColor: Color = PrimaryColor,
        disabledContainerColor: Color = PrimaryColor500,
        contentColor: Color = PrimaryPressColor,
        disabledContentColor: Color = PrimaryColor500,
        buttonTxtColors: ButtonTxtColors = ButtonTxtColors(
            Color.White,
            Color(0x66FFFFFF),
            Color.White,
        )
    ): CustomSizeButtonColors = CustomSizeButtonColors(
        containerColor, contentColor, disabledContainerColor, disabledContentColor, buttonTxtColors
    )

    /**
     * 次要按钮颜色值
     */
    @Composable
    fun minorColorButton(
        containerColor: Color = Color.White,
        disabledContainerColor: Color = Color.White,
        contentColor: Color = DisableTxtColor,
        disabledContentColor: Color = DisableTxtColor,
        buttonTxtColors: ButtonTxtColors = ButtonTxtColors(
            PrimaryTxtColor,
            DisableTxtColor,
            Color(0xFF171A1D)
        )
    ): CustomSizeButtonColors = CustomSizeButtonColors(
        containerColor, contentColor, disabledContainerColor, disabledContentColor, buttonTxtColors
    )

    /**
     * 警告按钮颜色值
     */
    @Composable
    fun warningColorButton(
        containerColor: Color = WarningColor,
        disabledContainerColor: Color = WarningColor500,
        contentColor: Color = WarningPressColor,
        disabledContentColor: Color = WarningPressColor500,
        buttonTxtColors: ButtonTxtColors = ButtonTxtColors(
            Color.White,
            Color(0x66FFFFFF),
            Color.White,
        )
    ): CustomSizeButtonColors = CustomSizeButtonColors(
        containerColor, contentColor, disabledContainerColor, disabledContentColor, buttonTxtColors
    )

    /**
     * 文本按钮颜色值
     */
    @Composable
    fun textColorButton(
        containerColor: Color = Color.Transparent,
        disabledContainerColor: Color = Color.Transparent,
        contentColor: Color = Color.Transparent,
        disabledContentColor: Color = Color.Transparent,
        buttonTxtColors: ButtonTxtColors = ButtonTxtColors(
            PrimaryTxtColor,
            DisableTxtColor,
            Color(0xFF171A1D)
        )
    ): CustomSizeButtonColors = CustomSizeButtonColors(
        containerColor, contentColor, disabledContainerColor, disabledContentColor, buttonTxtColors
    )
}

/**
 * 自定义大小按钮的颜色
 * @param containerColor 按钮容器颜色
 * @param contentColor 按钮按下后颜色
 * @param disabledContainerColor 禁用后按钮容器颜色
 * @param disabledContentColor 禁用后按钮按下后颜色
 */
class CustomSizeButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val buttonTxtColors: ButtonTxtColors
) {
    /**
     * Represents the container color for this button, depending on [enabled].
     *
     * @param enabled whether the button is enabled
     */
    @Composable
    fun containerSelectColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) containerColor else disabledContainerColor)
    }

    /**
     * Represents the content color for this button, depending on [enabled].
     *
     * @param enabled whether the button is enabled
     */
    @Composable
    fun contentSelectColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
    }
}

/**
 * 按钮文字颜色
 * @param txtColor 文字颜色
 * @param disabledTxtColor 文字禁用后颜色
 * @param indicatorColor 指示器颜色
 */
class ButtonTxtColors(
    val txtColor: Color = PrimaryTxtColor,
    val disabledTxtColor: Color = DisableTxtColor,
    val indicatorColor: Color = Color.White,
) {
    @Composable
    fun txtSelectColor(enabled: Boolean): State<Color> {
        return rememberUpdatedState(if (enabled) txtColor else disabledTxtColor)
    }
}