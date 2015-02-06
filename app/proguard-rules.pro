# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class * extends android.app.Activity{*;}
-keep class * extends android.app.Service{*;}


-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

-keep class com.google.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }

#Bugly接口
-keep public class com.tencent.bugly.crashreport.crash.jni.NativeCrashHandler{public *; native <methods>;}
-keep public interface com.tencent.bugly.crashreport.crash.jni.NativeExceptionHandler{*;}

#Wechat
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.** {*;}

#Jack
-include
-basedirectory
-injars
-outjars // only 1 output jar supported
-libraryjars
-dontoptimize  // required: Jack does not optimize
-dontpreverify // required: Jack does not preverify
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-forceprocessing
-keep
-keepclassmembers
-keepclasseswithmembers
-keepnames
-keepclassmembernames
-keepclasseswithmembernames
-printseeds
-keep class com.tencent.** {*;}