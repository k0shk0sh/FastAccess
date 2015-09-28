-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-allowaccessmodification
-repackageclasses ''
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keepattributes SourceFile,LineNumberTable,*Annotation*
-keep class com.crashlytics.android.**
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-keepnames class * { @butterknife.Bind *;}
-keepnames class * { @butterknife.onClick *;}
-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer
-keep class * extends com.google.gson.JsonSerializer
-keepattributes Column
-keepattributes Table
-keepclasseswithmembers class * { @com.activeandroid.annotation.Column <fields>; }
-keep class * implements android.os.Parcelable {
    public static android.os.Parcelable$Creator *;
}
-keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
}
-keep class com.styleme.floating.toolbox.pro.** { *; }
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keep class android.support.v7.widget.** { *; }
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class android.support.v13.app.** { *; }
-keep interface android.support.v13.app.** { *; }
