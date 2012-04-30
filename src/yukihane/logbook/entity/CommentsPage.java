package yukihane.logbook.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CommentsPage extends Page {

    private CommentsPage(List<Item> items) {
        super(items, null);
    }

    public static Page fromJSONObject(JSONObject obj) throws JSONException, ParseException {
        final JSONObject commentsObj = obj.getJSONObject("comments");
        final JSONArray data = commentsObj.getJSONArray("data");
        final int length = data.length();
        final List<Item> it = new ArrayList<Item>(length);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (int i = 0; i < length; i++) {
            final JSONObject m = data.getJSONObject(i);
            final String id = m.getString("id");
            final String message = m.optString("message");
            final String createdTimeStr = m.getString("created_time");
            final Date createdTime = sdf.parse(createdTimeStr);
            final Date updatedTime = createdTime;

            final JSONObject fromObj = m.getJSONObject("from");
            final String userName = fromObj.getString("name");
            final String userID = fromObj.getString("id");

            it.add(new Item(id, "comment", message, userID, userName, createdTime, updatedTime, 0));
        }

        return new Page(it, null);
    }
}
