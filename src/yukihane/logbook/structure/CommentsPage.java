package yukihane.logbook.structure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.Comment;
import android.os.Bundle;

public class CommentsPage implements Page<Comment> {

    private final List<Comment> items;
    private Bundle nextParam;

    protected CommentsPage(List<Comment> items, Bundle nextParam) {
        this.items = items;
        this.nextParam = nextParam;
    }

    public static CommentsPage fromJSONObject(JSONObject obj) throws JSONException, ParseException {
        final JSONObject commentsObj = obj.getJSONObject("comments");
        final JSONArray data = commentsObj.optJSONArray("data");
        final List<Comment> it;
        if (data != null) {
            final int length = data.length();
            it = new ArrayList<Comment>(length);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            for (int i = 0; i < length; i++) {
                final JSONObject m = data.getJSONObject(i);
                final String id = m.getString("id");
                final String message = m.optString("message");
                final String createdTimeStr = m.getString("created_time");
                final Date createdTime = sdf.parse(createdTimeStr);

                final JSONObject fromObj = m.getJSONObject("from");
                final String userName = fromObj.getString("name");
                final String userID = fromObj.getString("id");

                final Comment item = Comment.builder(id, "comment").message(message).userID(userID).userName(userName)
                        .createdTime(createdTime).build();
                it.add(item);
            }
        } else {
            it = new ArrayList<Comment>(0);
        }

        return new CommentsPage(it, null);
    }

    @Override
    public List<Comment> getItems() {
        return items;
    }

    @Override
    public Bundle getNextParam() {
        return nextParam;
    }
}
