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

-obfuscationdictionary mfilename.txt
-classobfuscationdictionary mfilename.txt
-packageobfuscationdictionary mfilename.txt

#
# 保持类CommonAdvertLoadHelper种 public 方法和 public 变量 不被混淆
#不混淆资源类下static的
-keep class com.sqm.advert_helper.adv.CommonAdvertLoadHelper {
    public <fields>;
    public <methods>;
}

-keep enum  com.sqm.advert_helper.adv.** {*;}
-keep class  com.sqm.advert_helper.adv.BaseSplashActivity {*;}
-keep class  com.sqm.advert_helper.adv.AdvManagerAbstract {*;}



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


