/**
 * Title :
 * Author: Lsy
 * Date: 2023/4/3 14:12
 * Version: 1
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
object EnvConfig {
    // 测试
    const val HTTP_BASE_DEBUG = "\"http://192.168.5.140:9083\""

    // 预生产
    const val HTTP_BASE_PRE = "\"https://pre-merchant.haier-ioc.com\""

    // 生产
    const val HTTP_BASE_RELEASE = "\"https://yshz-merchant.haier-ioc.com\""

    const val H5_WORK_OREDER_DEBUG = "\"http://115.238.82.114:1409\""
    const val H5_WORK_OREDER_RELEASE = "\"https://taskflow.haier-ioc.com\""

    const val SERVICE_CHECK_DEBUG = "\"https://pre-state.haier-ioc.com/mms\""
    const val SERVICE_CHECK_RELEASE = "\"https://state.haier-ioc.com/mms\""

    const val PRIVACY_POLICY = "\"https://h5.haier-ioc.com/#/pages/private/appPrivate\""
}