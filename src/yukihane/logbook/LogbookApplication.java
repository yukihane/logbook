package yukihane.logbook;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionEvents;
import com.facebook.android.Facebook.DialogListener;

public class LogbookApplication extends Application {
    public static String TAG = "LOGBOOK";
    public static Facebook mFacebook;
    public static AsyncFacebookRunner mAsyncRunner;
    public static String userUID = null;
    public static String objectID = null;
    private static final String[] defaultPermissions = { "read_stream" };
    private static Handler mHandler;

    public static void changeLoginStatus(Activity activity, int activityCode) {
        if (mFacebook.isSessionValid()) {
            SessionEvents.onLogoutBegin();
            mAsyncRunner.logout(activity, new LogoutRequestListener());
        } else {
            mFacebook.authorize(activity, defaultPermissions, activityCode, new LoginDialogListener());
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged " + getClass().getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate " + getClass().getSimpleName());
        mHandler = new Handler();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory " + getClass().getSimpleName());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminate " + getClass().getSimpleName());
    }

    private static final class LoginDialogListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
        }

        @Override
        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
        }

        @Override
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }

    private static class LogoutRequestListener extends BaseRequestListener {
        @Override
        public void onComplete(String response, final Object state) {
            /*
             * callback should be run in the original thread, not the background
             * thread
             */
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    SessionEvents.onLogoutFinish();
                }
            });
        }
    }

}
