# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#---------------------- base -----------------------------
# 代码混淆压缩比，在0和7之间，默认为5，一般不需要改
-optimizationpasses 5

# 混淆时不使用大小写混合，混淆后的类名为小写，Windows用户必须指定，否则当你的项目中有超过26个类的
#话，ProGuard就会默认混用大小写文件名，而导致class文件相互覆盖。
-dontusemixedcaseclassnames

# 指定不去忽略非公共的库和类，不要跳过对非公开类的处理，默认情况下是跳过的
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers

# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify

# 有了verbose这句话，混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后使用printmapping指定映射文件的名称
-verbose
-printmapping proguardMapping.txt

# 代码优化
-dontshrink

# 指定混淆时采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 保护代码中的Annotation不被混淆，这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型，这在JSON实体映射时非常重要，比如fastJson
-keepattributes Signature

# 抛出异常时保留代码行号在异常分析中可以方便定位
-keepattributes SourceFile,LineNumberTable
#---------------------- base -----------------------------
#---------------------- keep -----------------------------
# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留了继承自Activity、Application这些类的子类
# 因为这些子类，都有可能被外部调用
# 比如说，第一行就保证了所有Activity的子类不要被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View

# 保留androidx下的所有类及其内部类
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-keep class com.google.android.material.** {*;}
-dontwarn androidx.**
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

# 保留在Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会被影响
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 枚举类不能被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 关闭 Log日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 保留自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留Parcelable序列化的类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 避免资源混淆
-keep class **.R$* {
    *;
}

# 避免回调函数 onXXEvent 混淆
-keepclassmembers class * {
    void *(*Event);
}

# WebView混淆配置
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class com.github.lzyzsd.jsbridge.BridgeWebView {
    public *;
}
-keepclassmembers class com.yunshang.haile_manager_android.web.MainJavascriptInterface {
    <methods>;
}

-keep public class com.yunshang.haile_manager_android.data.entities.** {
    public void set*(***);
    public *** get*();
    public *** is*();
}

#---------------------- keep -----------------------------
#---------------------- kotlin -----------------------------
#kotlin 相关
-dontwarn kotlin.**
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-keepclasseswithmembers @kotlin.Metadata class * { *; }
-keepclassmembers class **.WhenMappings {
    <fields>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-keep class kotlinx.** { *; }
-keep interface kotlinx.** { *; }
-dontwarn kotlinx.**
-dontnote kotlinx.serialization.SerializationKt

-keep class org.jetbrains.** { *; }
-keep interface org.jetbrains.** { *; }
-dontwarn org.jetbrains.**
#---------------------- kotlin -----------------------------
#---------------------- okhttp -----------------------------
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
#---------------------- keep -----------------------------
#---------------------- retrofit -----------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
#---------------------- keep -----------------------------
#---------------------- glide -----------------------------
-keep public class * implements com.bumptech.glide.module.AppGlideModule
-keep public class * implements com.bumptech.glide.module.LibraryGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#---------------------- glide -----------------------------
#---------------------- gson -----------------------------
-keep class com.google.gson.** {*;}
-keep class com.google.**{*;}
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-dontwarn sun.misc.**
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.yunshang.haile_manager_android.data.entities.** {*;}
# 保留被 Gson 注解标记的类和成员不被混淆
-keepclasseswithmembers class * {
    @com.google.gson.annotations.* <fields>;
}
# 保留具有 Gson 注解的类和成员不被混淆
-keepclasseswithmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
#---------------------- gson -----------------------------
#---------------------- pictureselector -----------------------------
-keep class com.luck.picture.lib.** { *; }

#use Camerax
-keep class com.luck.lib.camerax.** { *; }

#use uCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
#---------------------- pictureselector -----------------------------
#---------------------- aMap -----------------------------
-dontwarn com.amap.api.**
-dontwarn com.a.a.**
-dontwarn com.autonavi.**
-keep class com.amap.api.**  {*;}
-keep class com.autonavi.**  {*;}
-keep class com.a.a.**  {*;}
#---------------------- aMap -----------------------------
#---------------------- tMap -----------------------------
-keep public class com.tencent.lbssearch.** {*;}
-keep public class com.tencent.map.** {*;}
-keep public class com.tencent.mapsdk.** {*;}
-keep public class com.tencent.tencentmap.**{*;}
-keep public class com.tencent.tmsbeacon.**{*;}
-keep public class com.tencent.tmsbeacon.**{*;}
-dontwarn com.qq.**
-dontwarn com.tencent.**
#---------------------- tMap -----------------------------
#---------------------- MPAndroidChart -----------------------------
-dontwarn com.github.mikephil.**
-keep class com.github.mikephil.**{ *; }
#---------------------- MPAndroidChart -----------------------------