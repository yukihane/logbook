package yukihane.logbook;

import static yukihane.logbook.Constants.MENU_GROUP_COMMENT_ORIGINAL;
import static yukihane.logbook.LogbookApplication.TAG;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.entity.Listable;
import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.CommentsPage;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;

public class CommentActivity extends FacebookListActivity<Comment, CommentsPage> {

    private ItemAdapter<Comment, CommentsPage> adapter;
    private String threadID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        adapter = new CommentAdapter(this, new RequestNextPage());
        threadID = getIntent().getStringExtra("id");
        Log.i(TAG, "threadID: " + threadID);

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.add(MENU_GROUP_COMMENT_ORIGINAL, Menu.NONE, Menu.NONE, "original");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() == MENU_GROUP_COMMENT_ORIGINAL) {
            final String[] s = threadID.split("_");
            final Uri uri = Uri.parse("https://wwww.facebook.com/" + s[0] + "/posts/" + s[1]);
            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        Log.v(TAG, "onCreateContextMenu " + getClass().getSimpleName());
        super.onCreateContextMenu(menu, v, menuInfo);

        final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
        final ListView lv = (ListView) v;
        final Listable<?> e = (Listable<?>) lv.getItemAtPosition(acmi.position);

        menu.setHeaderTitle("Action");
        addTextLinkToContextMenu(menu, e.getBody(), 0);
    }

    @Override
    protected void onListItemClicked(Object item, int position) {
        return;
    }

    @Override
    protected void onLoginValidated() {
        requestPage();
    }

    @Override
    protected CommentsPage createPage(JSONObject obj) throws JSONException, ParseException {
        try {
            final Dao<StatusMessage, String> dao = getHelper().getStatusMessageDao();
            final StatusMessage parent = dao.queryForId(threadID);
            return CommentsPage.fromJSONObject(obj, threadID, parent);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getGraphPath() {
        return threadID;
    }

    @Override
    protected String getPostGraphPath() {
        return threadID + "/comments";
    }

    @Override
    protected ItemAdapter<Comment, CommentsPage> getItemAdapter() {
        return adapter;
    }

    @Override
    protected Dao<Comment, String> getDao() throws SQLException {
        return getHelper().getCommentDao();
    }

    @Override
    protected List<Comment> getPersistedItems() throws SQLException {
        final Dao<Comment, String> dao = getHelper().getCommentDao();
        return dao.queryForEq(Comment.PARENT_ID_FIELD_NAME, threadID);
    }
}
