package yukihane.logbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.R.id;
import com.facebook.android.Util;

public class LogbookActivity extends Activity {
    public static final String TAG = "LOGBOOK";
    private static final String FBAPP_ID = "368486299855660";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_EXPIRES = "access_expires";
    private final Facebook facebook = new Facebook(FBAPP_ID);
    private final AsyncFacebookRunner runner = new AsyncFacebookRunner(facebook);
    private SharedPreferences mPrefs;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initSession();

        runner.request("me/feed", new MeRequestListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        facebook.extendAccessTokenIfNeeded(this, null);
    }

    private void initSession() {
        final String[] permissions = { "read_stream" };
        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString(ACCESS_TOKEN, null);
        long expires = mPrefs.getLong(ACCESS_EXPIRES, 0);
        if (access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if (expires != 0) {
            facebook.setAccessExpires(expires);
        }

        if (!facebook.isSessionValid()) {
            facebook.authorize(this, permissions, new AuthorizeDialogListener());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult");

        facebook.authorizeCallback(requestCode, resultCode, data);
    }

    private class AuthorizeDialogListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {
            // TODO Auto-generated method stub
            Log.v(TAG, "onComplete");

            final SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(ACCESS_TOKEN, facebook.getAccessToken());
            editor.putLong(ACCESS_EXPIRES, facebook.getAccessExpires());
            editor.commit();
        }

        @Override
        public void onFacebookError(FacebookError e) {
            // TODO Auto-generated method stub
            Log.v(TAG, "onFacebookError");

        }

        @Override
        public void onError(DialogError e) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onError", e);

        }

        @Override
        public void onCancel() {
            // TODO Auto-generated method stub
            Log.v(TAG, "onCancel");

        }
    }

    private class MeRequestListener implements RequestListener {

        @Override
        public void onComplete(String response, Object state) {
            // TODO Auto-generated method stub
            Log.v(TAG, "MeRequestListener#onComplete");
            try {
                final JSONObject res = Util.parseJson(response);
                final String text = res.toString(1);
                LogbookActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final TextView view = (TextView) findViewById(id.myview);
                        view.setText(text);
                    }
                });
//                final List<? extends Drawable> drawables = Feed.fromJSONObject(res);
            } catch (FacebookError e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "MeRequestListener#onComplete", e);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "MeRequestListener#onComplete", e);
            }
        }

        @Override
        public void onIOException(IOException e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onIOException", e);
        }

        @Override
        public void onFileNotFoundException(FileNotFoundException e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onComplete", e);
        }

        @Override
        public void onMalformedURLException(MalformedURLException e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onComplete", e);
        }

        @Override
        public void onFacebookError(FacebookError e, Object state) {
            // TODO Auto-generated method stub
            Log.e(TAG, "MeRequestListener#onComplete", e);
        }
    }
}
