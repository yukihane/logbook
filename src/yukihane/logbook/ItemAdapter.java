package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.util.ArrayList;
import java.util.List;

import yukihane.logbook.R.id;
import yukihane.logbook.R.layout;
import yukihane.logbook.entity.Item;
import yukihane.logbook.entity.Page;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter extends BaseAdapter {
    private final Context context;
    private final ReachLastItemListener listner;
    private final List<Item> items = new ArrayList<Item>();
    private Bundle nextParam;
    private boolean fired = false;

    public ItemAdapter(Context context, ReachLastItemListener listener) {
        this.context = context;
        this.listner = listener;
    }

    public void addPage(Page feed2) {
        Log.i(TAG,
                "item added. cur:" + items.size() + ", new:" + feed2.getItems().size() + ", next:"
                        + feed2.getNextParam());
        fired = false;
        nextParam = feed2.getNextParam();
        items.addAll(feed2.getItems());
        notifyDataSetChanged();
    }

    public void clear() {
        fired = false;
        nextParam = new Bundle();
        items.clear();
        notifyDataSetChanged();
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
            v = inflater.inflate(layout.item_display, null);
        }

        final Item item = (Item) getItem(position);
        if (item != null) {
            final TextView header = (TextView) v.findViewById(id.rowheader);
            header.setText(item.getHeader());
            final TextView textView = (TextView) v.findViewById(id.rowitem);
            textView.setText(item.getBody());
        }

        if (!fired && position >= getCount() - 1) {
            Log.v(TAG, "fire next page request");
            listner.fire(nextParam);
            fired = true;
        }

        return v;
    }

    public interface ReachLastItemListener {

        void fire(Bundle nextParam);
    }
}