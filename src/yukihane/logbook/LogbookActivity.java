package yukihane.logbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.ItemAdapter.ReachLastItemListener;
import yukihane.logbook.entity.Item;
import yukihane.logbook.entity.Page;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;

public class LogbookActivity extends Activity {
    public static final String TAG = "LOGBOOK";
    private static final String FBAPP_ID = "368486299855660";
    private final ItemAdapter adapter = new ItemAdapter(this, new RequestNextPage());
    private final MeRequestListener pageLiquestListener = new MeRequestListener();
    private LoginButton mLoginButton;
    private static final int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
    private static final int COMMENT_ACTIVITY_RESULT_CODE = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LogbookApplication.mFacebook = new Facebook(FBAPP_ID);
        LogbookApplication.mAsyncRunner = new AsyncFacebookRunner(LogbookApplication.mFacebook);

        mLoginButton = (LoginButton) findViewById(R.id.login);

        // restore session if one exists
        SessionStore.restore(LogbookApplication.mFacebook, this);
        SessionEvents.addAuthListener(new FbAPIsAuthListener());
        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

        final String[] permissions = { "read_stream" };
        mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, LogbookApplication.mFacebook, permissions);

        final Button btnReload = (Button) findViewById(R.id.reloadbutton);
        btnReload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                adapter.clear();
                LogbookApplication.mAsyncRunner.request("me/feed", pageLiquestListener);
            }
        });

        final ListView list = (ListView) findViewById(R.id.list);
        final TextView footer = new TextView(list.getContext());
        footer.setText("here is footer");
        list.addFooterView(footer);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ListView lv = (ListView) parent;
                final Item we = (Item) lv.getItemAtPosition(position);
                final Intent intent = new Intent(LogbookActivity.this, CommentActivity.class);
                intent.putExtra("id", we.getId());
                startActivityForResult(intent, COMMENT_ACTIVITY_RESULT_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
        LogbookApplication.mFacebook.extendAccessTokenIfNeeded(this, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult: " + resultCode + ", " + resultCode);

        LogbookApplication.mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    private class MeRequestListener implements RequestListener {

        @Override
        public void onComplete(String response, Object state) {
            // TODO Auto-generated method stub
            Log.v(TAG, "MeRequestListener#onComplete");
            try {
                final JSONObject res = Util.parseJson(response);
                LogbookActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final Page feed = Page.fromJSONObject(res);
                            adapter.addPage(feed);
                        } catch (JSONException e) {
                            Log.e(TAG, "", e);
                        } catch (ParseException e) {
                            Log.e(TAG, "", e);
                        }
                    }
                });
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

    private class RequestNextPage implements ReachLastItemListener {

        @Override
        public void fire(Bundle nextParam) {
            if (nextParam != null) {
                LogbookApplication.mAsyncRunner.request("me/feed", nextParam, pageLiquestListener);
            }
        }
    }

    private class FbAPIsAuthListener implements AuthListener {

        @Override
        public void onAuthSucceed() {
            Log.i(TAG, "onAuthSucceed");
            Toast.makeText(getApplicationContext(), "logged in!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthFail(String error) {
            Log.i(TAG, "onAuthFail");
            Toast.makeText(getApplicationContext(), "login failed: " + error, Toast.LENGTH_LONG).show();
        }
    }

    /*
     * The Callback for notifying the application when log out starts and
     * finishes.
     */
    private class FbAPIsLogoutListener implements LogoutListener {
        @Override
        public void onLogoutBegin() {
            Log.i(TAG, "onLogoutBegin");
            Toast.makeText(getApplicationContext(), "logging out...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLogoutFinish() {
            Log.i(TAG, "onLogoutFinish");
            Toast.makeText(getApplicationContext(), "logged out!", Toast.LENGTH_SHORT).show();
        }
    }

}
