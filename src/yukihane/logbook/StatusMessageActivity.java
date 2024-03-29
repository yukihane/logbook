package yukihane.logbook;

import static yukihane.logbook.Constants.CONTEXT_MENU_GROUP_STATUS_MESSAGE;
import static yukihane.logbook.Constants.STATUS_MESSAGE_MENU_ITEM_COMMENT;
import static yukihane.logbook.Constants.STATUS_MESSAGE_MENU_ITEM_LINK;
import static yukihane.logbook.LogbookApplication.TAG;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.FeedPage;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;
import com.j256.ormlite.dao.Dao;

public class StatusMessageActivity extends FacebookListActivity<StatusMessage, FeedPage> {
    private static final int COMMENT_ACTIVITY_RESULT_CODE = 1;
    private StatusMessageAdapter adapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        adapter = new StatusMessageAdapter(this, new RequestNextPage());

        super.onCreate(savedInstanceState);

        SessionEvents.addAuthListener(new FbAPIsAuthListener());
        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

        //        if (LogbookApplication.mFacebook.isSessionValid()) {
        //            onLoginValidated();
        //        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult: " + resultCode + ", " + resultCode);

        LogbookApplication.mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        Log.v(TAG, "onCreateContextMenu " + getClass().getSimpleName());
        super.onCreateContextMenu(menu, v, menuInfo);

        final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
        final ListView lv = (ListView) v;
        final StatusMessage sm = (StatusMessage) lv.getItemAtPosition(acmi.position);

        menu.setHeaderTitle("Action");

        final MenuItem itemComment = menu.add(CONTEXT_MENU_GROUP_STATUS_MESSAGE, STATUS_MESSAGE_MENU_ITEM_COMMENT, 0,
                "COMMENT");
        final Intent intentComment = new Intent();
        intentComment.putExtra("id", sm.getID());
        itemComment.setIntent(intentComment);

        int num = 1;
        if (sm.getLink() != null) {
            final MenuItem itemLink = menu.add(CONTEXT_MENU_GROUP_STATUS_MESSAGE, STATUS_MESSAGE_MENU_ITEM_LINK, 1,
                    "LINK");
            final Intent intentLink = new Intent(Intent.ACTION_VIEW, Uri.parse(sm.getLink()));
            itemLink.setIntent(intentLink);
            num++;
        }

        addTextLinkToContextMenu(menu, sm.getBody(), num);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == CONTEXT_MENU_GROUP_STATUS_MESSAGE) {
            if (item.getItemId() == STATUS_MESSAGE_MENU_ITEM_COMMENT) {
                final Intent intent = item.getIntent();
                final String id = intent.getExtras().getString("id");
                startCommentActivity(id);
                return true;
            } else if (item.getItemId() == STATUS_MESSAGE_MENU_ITEM_LINK) {
                startActivity(item.getIntent());
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onLoginValidated() {
        final Bundle b = new Bundle();
        b.putString("limit", "25");
        requestPage(b);
    }

    @Override
    protected void onListItemClicked(Object item, int position) {
        startCommentActivity(((StatusMessage) item).getID());
    }

    private void startCommentActivity(String id) {
        final Intent intent = new Intent(StatusMessageActivity.this, CommentActivity.class);
        intent.putExtra("id", id);
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

    @Override
    protected ItemAdapter<StatusMessage, FeedPage> getItemAdapter() {
        return adapter;
    }

    @Override
    protected Dao<StatusMessage, String> getDao() throws SQLException {
        return getHelper().getStatusMessageDao();
    }

    @Override
    protected List<StatusMessage> getPersistedItems() throws SQLException {
        final Dao<StatusMessage, String> dao = getHelper().getStatusMessageDao();
        return dao.queryForAll();
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
