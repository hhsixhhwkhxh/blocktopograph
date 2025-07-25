package com.mithrilmania.blocktopograph;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Log {

    public static final String ANA_PARAM_CREATE_WORLD_TYPE = "cw_type";
    public static final String ANA_PARAM_MAINACT_MENU_TYPE = "mam_type";
    public static final int ANA_PARAM_MAINACT_MENU_TYPE_OPEN = 123;
    public static final int ANA_PARAM_MAINACT_MENU_TYPE_HELP = 130;
    public static final int ANA_PARAM_MAINACT_MENU_TYPE_ABOUT = 199;

    private static final String LOG_TAG = "Blocktopo";

    private static FirebaseAnalytics mFirebaseAnalytics;

//    private static PrintWriter mFileLogger;

    private static boolean mIsFirebaseAnalyticsEnabled = false;
    private static boolean mIsCrashlyticsEnabled = false;

    private static String prependClassName(@NonNull Object caller, @NonNull String msg) {
        Class<?> clazz = caller instanceof Class ? (Class<?>) caller : caller.getClass();
        return clazz.getSimpleName() + ": " + msg;
    }

    public static void enableFirebaseAnalytics(@NonNull Context context) {
        getFirebaseAnalytics(context).setAnalyticsCollectionEnabled(true);
        mIsFirebaseAnalyticsEnabled = true;
    }

    public static void enableCrashlytics() {
        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
            mIsCrashlyticsEnabled = true;
        }
    }

    public static void d(@NonNull Object caller, @NonNull String msg) {
        android.util.Log.d(LOG_TAG, prependClassName(caller, msg));
    }

    public static void d(@NonNull Object caller, @NonNull Throwable throwable) {
        StringWriter sw = new StringWriter(4096);
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        android.util.Log.e(LOG_TAG, prependClassName(caller, sw.toString()));
        pw.close();
    }

    public static void e(@NonNull Object caller, @NonNull String msg) {
        d(caller, msg);
        if (mIsCrashlyticsEnabled)
            FirebaseCrashlytics.getInstance().log(prependClassName(caller, msg));
    }

    public static void e(@NonNull Object caller, @NonNull Throwable throwable) {
        d(caller, throwable);
        if (mIsCrashlyticsEnabled) FirebaseCrashlytics.getInstance().recordException(throwable);
    }

    private synchronized static FirebaseAnalytics getFirebaseAnalytics(@NonNull Context context) {
        if (mFirebaseAnalytics == null) mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return mFirebaseAnalytics;
    }

    public static void logFirebaseEvent(@NonNull Context context, @NonNull CustomFirebaseEvent firebaseEvent) {
        if (mIsFirebaseAnalyticsEnabled)
            getFirebaseAnalytics(context).logEvent(firebaseEvent.eventID, new Bundle());
    }

    public static void logFirebaseEvent(@NonNull Context context, @NonNull CustomFirebaseEvent firebaseEvent, @NonNull Bundle eventContent) {
        if (mIsFirebaseAnalyticsEnabled)
            getFirebaseAnalytics(context).logEvent(firebaseEvent.eventID, eventContent);
    }

    // Firebase events, these are meant to be as anonymous as possible,
    //  pure counters for usage analytics.
    // Do not remove! Removing analytics in a fork skews the results to the original userbase!
    // Forks should stay in touch, all new features are welcome.
    //Wonder why you put these things in a certain Activity.
    //That should be global.
    public enum CustomFirebaseEvent {

        //max 32 chars:     "0123456789abcdef0123456789abcdef"
        MAPFRAGMENT_OPEN("map_fragment_open"),
        MAPFRAGMENT_RESUME("map_fragment_resume"),
        MAPFRAGMENT_RESET("map_fragment_reset"),
        NBT_EDITOR_OPEN("nbt_editor_open"),
        NBT_EDITOR_SAVE("nbt_editor_save"),
        WORLD_OPEN("world_open"),
        WORLD_RESUME("world_resume"),
        GPS_LOCATE("gps_player"),
        MAINACT_MENU_OPEN("mainact_menu"),
        CREATE_WORLD_OPEN("create_world_open"),
        CREATE_WORLD_SAVE("create_world_save"),
        SELECTION("selection_begin"),
        SNR_OPEN("snr_open"),
        SNR_EXEC("snr_exec"),
        DCHUNK("dchunk_open"),
        CH_BIOME("ch_biome_open");

        public final String eventID;

        CustomFirebaseEvent(String eventID) {
            this.eventID = eventID;
        }
    }

}
