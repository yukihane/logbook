package yukihane.logbook.structure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yukihane.logbook.entity.StatusMessage;

public class CommentsPage extends FeedPage {

    private CommentsPage(List<StatusMessage> items) {
        super(items, null);
    }

    public static FeedPage fromJSONObject(JSONObject obj) throws JSONException, ParseException {
        final JSONObject commentsObj = obj.getJSONObject("comments");
        final JSONArray data = commentsObj.optJSONArray("data");
        final List<StatusMessage> it;
        if (data != null) {
            final int length = data.length();
            it = new ArrayList<StatusMessage>(length);
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

                final StatusMessage item = StatusMessage.builder(id, "comment").message(message).userID(userID).userName(userName)
                        .createdTime(createdTime).updatedTime(updatedTime).commentCount(0).build();
                it.add(item);
            }
        } else {
            it = new ArrayList<StatusMessage>(0);
        }

        return new FeedPage(it, null);
    }
}
