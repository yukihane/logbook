package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.CommentsPage;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;

public class CommentActivity extends FacebookListActivity<Comment, CommentsPage> {

    private final ItemAdapter<Comment, CommentsPage> adapter = new ItemAdapter<Comment, CommentsPage>(this,
            new RequestNextPage());
    private String threadID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.clear();
        threadID = getIntent().getStringExtra("id");
        Log.i(TAG, "threadID: " + threadID);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        Log.v(TAG, "onCreateContextMenu " + getClass().getSimpleName());
        super.onCreateContextMenu(menu, v, menuInfo);

        final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
        final ListView lv = (ListView) v;
        final Comment e = (Comment) lv.getItemAtPosition(acmi.position);

        menu.setHeaderTitle("Action");
        addTextLinkToContextMenu(menu, e.getMessage(), 0);
    }

    @Override
    protected void onListItemClicked(Comment item) {
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
            final StatusMessage parent = dao.queryForId(getParentID());
            return CommentsPage.fromJSONObject(obj, threadID, parent);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getParentID() {
        return threadID.split("_")[1];
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
        return dao.queryForAll();
    }
}
