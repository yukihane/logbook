package yukihane.logbook.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Feed {
    private final List<WallElement> drawables;

    private Feed(List<WallElement> drawables) {
        this.drawables = drawables;
    }

    public static Feed fromJSONObject(JSONObject obj) throws JSONException {
        final JSONArray data = obj.getJSONArray("data");
        final int length = data.length();
        final List<WallElement> dr = new ArrayList<WallElement>(length);
        for (int i = 0; i < length; i++) {
            final JSONObject m = data.getJSONObject(i);
            final String id = m.optString("id");
            final String type = m.optString("type");
            final String message = m.optString("message");
            dr.add(new WallElement(id, type, message));
        }

        // TODO
        final JSONObject paging = obj.getJSONObject("paging");

        return new Feed(dr);
    }

    public List<WallElement> getWallElements() {
        return drawables;
    }

}
