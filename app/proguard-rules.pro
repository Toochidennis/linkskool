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

#  -dontwarn okhttp3.internal.platform.*
#  -dontwarn android.net.http.*
#  -keep class com.digitaldream.linkskool.models.*
#  -keep class com.digitaldream.linkskool.utils.*
#  -dontwarn org.ietf.jgss.GSSContext
#  -dontwarn org.ietf.jgss.GSSCredential
#  -dontwarn org.ietf.jgss.GSSException
#  -dontwarn org.ietf.jgss.GSSManager
#  -dontwarn org.ietf.jgss.GSSName
#  -dontwarn org.ietf.jgss.Oid
  -printconfiguration app/build/tmp/full-r8-config.txt

  ## Conscrypt
  # -keep class org.conscrypt.** { *; }
