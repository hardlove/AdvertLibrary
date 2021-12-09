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
#
#
# 保持类CommonAdvertLoadHelper种 public 方法和 public 变量 不被混淆
#不混淆资源类下static的
-keep class com.thl.thl_advertlibrary.config.AdvertConfig {
    public <fields>;
    public <methods>;
}
-keep class  com.thl.thl_advertlibrary.config.AppBuildConfig {
    public <methods>;
}
-keep enum  com.thl.thl_advertlibrary.** {*;}
-keep class com.stx.xhb.xbanner.entity.**{*;}
-keep class com.thl.thl_advertlibrary.network.bean.**{*;}
-keep class  com.thl.thl_advertlibrary.utils.**{*;}
-keep class  com.thl.thl_advertlibrary.helper.**{
    public <methods>;
}




#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }


# 保留R下面的资源
-keep class **.R$* {
 *;
}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}


#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
