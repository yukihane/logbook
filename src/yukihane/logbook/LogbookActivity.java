package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.FeedPage;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

public class LogbookActivity extends FacebookListActivity<StatusMessage, FeedPage> {
    private static final int COMMENT_ACTIVITY_RESULT_CODE = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionEvents.addAuthListener(new FbAPIsAuthListener());
        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

        //        if (LogbookApplication.mFacebook.isSessionValid()) {
        //            onLoginValidated();
        //        }
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

    @Override
    protected void onLoginValidated() {
        getItemAdapter().clear();
        final Bundle b = new Bundle();
        b.putString("limit", "100");
        requestPage(b);
    }

    @Override
    protected void onListItemClicked(StatusMessage item) {
        final Intent intent = new Intent(LogbookActivity.this, CommentActivity.class);
        intent.putExtra("id", item.getID());
        startActivityForResult(intent, COMMENT_ACTIVITY_RESULT_CODE);
    }

    @Override
    protected FeedPage createPage(JSONObject obj) throws JSONException, ParseException {
        return FeedPage.fromJSONObject(obj);
    }

    @Override
    protected String getGraphPath() {
        return "me/feed";
    }

    @Override
    protected String getPostGraphPath() {
        return getGraphPath();
    }

    private class FbAPIsAuthListener implements AuthListener {

        @Override
        public void onAuthSucceed() {
            Log.i(TAG, "onAuthSucceed");
            Toast.makeText(getApplicationContext(), "logged in!", Toast.LENGTH_SHORT).show();
            onLoginValidated();
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
