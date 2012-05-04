package yukihane.logbook;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.CommentsPage;
import yukihane.logbook.entity.Item;
import yukihane.logbook.entity.Page;
import android.os.Bundle;

public class CommentActivity extends FacebookListActivity {
    private String threadID;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        threadID = getIntent().getStringExtra("id");
    }

    @Override
    protected void onListItemClicked(Item item) {
        return;
    }

    @Override
    protected void onLoginValidated() {
        adapter.clear();
        requestPage();
    }

    @Override
    protected Page createPage(JSONObject obj) throws JSONException, ParseException {
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
}
