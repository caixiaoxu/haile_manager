/**
 * Android 配置
 */
object AndroidConfig {
    const val compileSdk = 33
    const val namespace = "com.yunshang.haile_manager_android"
    val defaultConfig = DefaultConfig()

    class DefaultConfig {
        //        val applicationId = "com.yunshang.haile_manager_android"
        val applicationId = "com.yunshang.haileshenghuo"
        val minSdk = 21
        val targetSdk = 33
        val versionCode = 225
        val versionName = "2.1.9"
    }
}