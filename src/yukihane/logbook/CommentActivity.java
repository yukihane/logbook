package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.CommentsPage;
import android.os.Bundle;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

public class CommentActivity extends FacebookListActivity<Comment, CommentsPage> {

    private final ItemAdapter<Comment, CommentsPage> adapter = new ItemAdapter<Comment, CommentsPage>(this,
            new RequestNextPage());
    private String threadID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        threadID = getIntent().getStringExtra("id");
        Log.i(TAG, "threadID: " + threadID);
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
        return CommentsPage.fromJSONObject(obj);
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
