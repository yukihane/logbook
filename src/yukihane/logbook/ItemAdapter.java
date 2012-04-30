package yukihane.logbook;

import java.util.ArrayList;
import java.util.List;

import yukihane.logbook.R.layout;
import yukihane.logbook.entity.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.android.R.id;

public class ItemAdapter extends BaseAdapter {
    private final Context context;
    private final List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        this.context = context;
        this.items = new ArrayList<Item>(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
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

        final Item item = (Item) getItem(position);
        if (item != null) {
            final TextView header = (TextView) v.findViewById(id.rowheader);
            header.setText(item.getHeader());
            final TextView textView = (TextView) v.findViewById(id.rowitem);
            textView.setText(item.getBody());
        }

        return v;
    }

}