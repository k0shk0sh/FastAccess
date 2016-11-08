-keepattributes Signature
-keepattributes *Annotation*

#data
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keepclassmembers class com.fastaccess.data.** {
  *;
}

-keep class com.fastaccess.ui.widgets.** { *; }

-keep class .R
-keep class **.R$* {
    <fields>;
}

#butterknife
-keep public class * implements butterknife.Unbinder { public <init>(...); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

#icePick
-dontwarn icepick.**
-keep class **$$Icepick { *; }
-keepclasseswithmembernames class * {
    @icepick.* <fields>;
}

# eventbus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#javax
-dontwarn javax.annotation.**

#almighty google
-keep class com.google.**
-dontwarn com.google.**

#log
-assumenosideeffects class android.util.Log {
    public static *** w(...);
    public static *** d(...);
    public static *** v(...);
    public static *** e(...);
}

