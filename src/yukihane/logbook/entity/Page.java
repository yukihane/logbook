package yukihane.logbook.entity;

import static yukihane.logbook.LogbookActivity.TAG;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

public class Page {
    private final List<Item> items;
    private Bundle nextParam;

    protected Page(List<Item> items, Bundle nextParam) {
        this.items = items;
        this.nextParam = nextParam;
    }

    public static Page fromJSONObject(JSONObject obj) throws JSONException, ParseException {
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

        final JSONObject paging = obj.optJSONObject("paging");
        if (paging != null) {
            final String nextURL = paging.getString("next");
            Bundle bundle = getParameter(nextURL);
            Log.v(TAG, "NEXT URL:" + nextURL);
            return new Page(it, bundle);
        } else {
            Log.v(TAG, "NO NEXT PAGE");
            return new Page(it, null);
        }
    }

    public static Bundle getParameter(String url) {
        final String[] text = url.replaceAll("^.*?\\?", "").split("&");
        final Bundle b = new Bundle();
        for (String p : text) {
            final String[] kv = p.split("=");
            b.putString(kv[0], kv[1]);
        }
        return b;
    }

    public List<Item> getItems() {
        return items;
    }

    public Bundle getNextParam() {
        return nextParam;
    }
}