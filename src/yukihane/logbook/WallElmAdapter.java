package yukihane.logbook;

import java.util.ArrayList;
import java.util.List;

import yukihane.logbook.R.layout;
import yukihane.logbook.entity.WallElement;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.android.R.id;

public class WallElmAdapter extends BaseAdapter {
    private final Context context;
    private final List<WallElement> wallElements;

    public WallElmAdapter(Context context, List<WallElement> wallElements) {
        this.context = context;
        this.wallElements = new ArrayList<WallElement>(wallElements);
    }

    @Override
    public int getCount() {
        return wallElements.size();
    }

    @Override
    public Object getItem(int position) {
        return wallElements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            final LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(layout.row, null);
        }

        final WallElement item = (WallElement) getItem(position);
        if (item != null) {
            final TextView header = (TextView) v.findViewById(id.rowheader);
            header.setText(item.getHeader());
            final TextView textView = (TextView) v.findViewById(id.rowitem);
            textView.setText(item.getBody());
        }

        return v;
    }

}