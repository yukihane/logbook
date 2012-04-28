package yukihane.logbook.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Feed {

    public static Feed fromJSONObject(JSONObject obj) throws JSONException {
        final JSONArray data = obj.getJSONArray("data");
        return new Feed();
    }

    public List<Drawable> getDrawables() {
        List<Drawable> list = new ArrayList<Drawable>(2);
        list.add(new Drawable());
        list.add(new Drawable());
        return list;
    }

}
