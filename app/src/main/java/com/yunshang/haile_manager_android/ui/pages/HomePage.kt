package com.yunshang.haile_manager_android.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.yunshang.haile_manager_android.R
import com.yunshang.haile_manager_android.ui.theme.BackgroundPageColor
import com.yunshang.haile_manager_android.ui.theme.Black25Color
import com.yunshang.haile_manager_android.ui.theme.Black45Color
import com.yunshang.haile_manager_android.ui.theme.Black85Color
import com.yunshang.haile_manager_android.ui.theme.FF3B30Color
import com.yunshang.haile_manager_android.ui.theme.LineColor
import com.yunshang.haile_manager_android.ui.theme.MoneyFamily
import com.yunshang.haile_manager_android.ui.theme.RedColor
import kotlin.math.ceil

/**
 * Title :
 * Author: Lsy
 * Date: 2024/1/23 15:22
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */

@Composable
fun HomePage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPageColor)
    ) {
        Image(
            painter = painterResource(id = R.mipmap.bg_home),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            HomePageTop()
            Spacer(modifier = Modifier.height(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                HomePageIncome()
                HomePageItemParent { HomePageIncomeTrend() }
                HomePageItemParent { HomePageLastMessage() }
                HomePageItemParent { HomePageOperateArea() }
            }
        }
    }
}

/**
 * 首页顶部（图标+消息+扫码）
 */
@Composable
fun HomePageTop() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.mipmap.icon_home_title),
            contentDescription = null
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.icon_home_msg),
                    contentDescription = null
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(FF3B30Color)
                )
            }
            Image(
                painter = painterResource(id = R.mipmap.icon_home_scan),
                contentDescription = null
            )
        }
    }
}

/**
 * 今日收益
 */
@Composable
fun HomePageIncome() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.icon_home_income_title),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.home_income_title),
                    fontSize = 14.sp,
                    color = Black85Color
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(id = R.string.unit_money),
                    fontSize = 24.sp,
                    color = Black85Color,
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 3.dp)
                )
                Text(
                    text = "20000",
                    fontSize = 36.sp,
                    color = Black85Color,
                    lineHeight = 42.sp,
                    fontFamily = MoneyFamily
                )
            }
        }
        Image(
            painter = painterResource(id = R.mipmap.icon_item_arrow_right),
            contentDescription = null
        )
    }
}

/**
 * 首页模块通用父样式
 */
@Composable
fun HomePageItemParent(homePageItem: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        homePageItem()
    }
}

/**
 * 首页收益趋势
 */
@Composable
fun HomePageIncomeTrend() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.home_trend_title),
            fontSize = 17.sp,
            color = Black85Color,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(BackgroundPageColor)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "2023年3月",
                fontSize = 12.sp,
                color = Black85Color,
            )
            Spacer(modifier = Modifier.width(2.dp))
            Image(
                painter = painterResource(id = R.mipmap.icon_triangle_down),
                contentDescription = null
            )
        }
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        thickness = 0.5.dp,
        color = LineColor
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(150.dp)
            .background(Color.Red)
    )
}

/**
 * 首页最新消息
 */
@Composable
fun HomePageLastMessage() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.home_msg_title),
            fontSize = 14.sp,
            color = Black45Color,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "6条新消息",
                fontSize = 14.sp,
                color = Black45Color,
            )
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(FF3B30Color)
            )
            Image(
                painter = painterResource(id = R.mipmap.icon_item_arrow_right),
                contentDescription = null
            )
        }
    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        thickness = 0.5.dp,
        color = LineColor
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HomePageLastMessageItem()
        HomePageLastMessageItem()
    }
}

/**
 * 最新消息条目
 */
@Composable
fun HomePageLastMessageItem() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.mipmap.icon_home_last_msg),
                contentDescription = null
            )
            Text(
                text = "设备告警2222222",
                fontSize = 14.sp,
                color = Black85Color,
                modifier = Modifier.widthIn(max = 56.dp),
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
            Text(
                text = "具体告警信息区过长省略111111111",
                fontSize = 14.sp,
                color = Black45Color,
                modifier = Modifier.widthIn(max = 168.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        Text(text = "2小时前", fontSize = 12.sp, color = Black25Color, maxLines = 1)
    }
}

/**
 * 首页操作区域
 */
@Composable
fun HomePageOperateArea() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        text = stringResource(id = R.string.home_trend_title),
        fontSize = 17.sp,
        color = Black85Color,
        fontWeight = FontWeight.Bold
    )
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        thickness = 0.5.dp,
        color = LineColor
    )
    HomePageOperateAreaItem()
}

@Composable
fun HomePageOperateAreaItem() {
    val size = 9
    val columnCount = 4
    val itemHeight = 90.dp
    val areaHeight = (itemHeight * ceil((size * 1f / columnCount)))

    Spacer(modifier = Modifier.height(4.dp))
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .height(areaHeight),
        columns = GridCells.Fixed(columnCount),
        userScrollEnabled = false
    ) {
        items(size) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight)
                    .padding(top = 3.dp),
            ) {
                val (column, text) = createRefs()
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .constrainAs(column) {
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.icon_device_manager),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "设备告警2222222",
                        fontSize = 14.sp,
                        color = Black85Color,
                        modifier = Modifier.widthIn(max = 56.dp),
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )
                }
                Text(
                    text = "12",
                    modifier = Modifier
                        .width(23.dp)
                        .height(18.dp)
                        .clip(CircleShape)
                        .background(RedColor)
                        .constrainAs(text) {
                            end.linkTo(column.end, margin = (-8).dp)
                        },
                    fontSize = 12.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}