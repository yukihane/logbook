package yukihane.logbook;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.structure.CommentsPage;
import android.os.Bundle;

public class CommentActivity extends FacebookListActivity<Comment, CommentsPage> {

    private final ItemAdapter<Comment, CommentsPage> adapter = new ItemAdapter<Comment, CommentsPage>(this,
            new RequestNextPage());
    private String threadID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        threadID = getIntent().getStringExtra("id");
    }

    @Override
    protected void onListItemClicked(Comment item) {
        return;
    }

    @Override
    protected void onLoginValidated() {
        getItemAdapter().clear();
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
}
