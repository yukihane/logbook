package yukihane.logbook.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Feed {
    private final List<Item> items;

    private Feed(List<Item> items) {
        this.items = items;
    }

    public static Feed fromJSONObject(JSONObject obj) throws JSONException, ParseException {
        final JSONArray data = obj.getJSONArray("data");
        final int length = data.length();
        final List<Item> it = new ArrayList<Item>(length);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (int i = 0; i < length; i++) {
            final JSONObject m = data.getJSONObject(i);
            final String id = m.getString("id");
            final String type = m.getString("type");
            final String message = m.optString("message");
            final String createdTimeStr = m.getString("created_time");
            final Date createdTime = sdf.parse(createdTimeStr);
            final String updatedTimeStr = m.getString("updated_time");
            final Date updatedTime = sdf.parse(updatedTimeStr);

            final JSONObject fromObj = m.getJSONObject("from");
            final String userName = fromObj.getString("name");
            final String userID = fromObj.getString("id");

            final JSONObject commentsObj = m.getJSONObject("comments");
            int commentCount = commentsObj.getInt("count");

            it.add(new Item(id, type, message, userID, userName, createdTime, updatedTime, commentCount));
        }

        // TODO
        final JSONObject paging = obj.getJSONObject("paging");

        return new Feed(it);
    }

    public List<Item> getItems() {
        return items;
    }

}
