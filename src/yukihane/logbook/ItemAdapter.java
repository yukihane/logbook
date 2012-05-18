package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

import yukihane.logbook.entity.Listable;
import yukihane.logbook.structure.Page;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemAdapter<E extends Listable<E>, P extends Page<E>> extends BaseAdapter {
    private final Context context;
    private final ReachLastItemListener listner;
    private final Collection<E> items;
    private Object[] itemArrayCache;
    private Bundle nextParam;
    private boolean fired = false;

    public ItemAdapter(Context context, ReachLastItemListener listener) {
        this.context = context;
        this.listner = listener;
        this.items = new TreeSet<E>(getComparator());
    }

    public void addPage(P feed2) {
        Log.i(TAG,
                "item added. cur:" + items.size() + ", new:" + feed2.getItems().size() + ", next:"
                        + feed2.getNextParam());
        fired = false;
        nextParam = feed2.getNextParam();

        addItems(feed2.getItems());
    }

    public final void addItems(Collection<E> it) {
        items.removeAll(it);
        final boolean modified = items.addAll(it);
        if (modified) {
            notifyDataSetChanged();
        }
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
        if (itemArrayCache == null) {
            itemArrayCache = items.toArray();
        }
        return itemArrayCache[position];
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
            v = inflater.inflate(R.layout.item_display, null);
        }

        final E item = (E) getItem(position);
        if (item != null) {
            final TextView header = (TextView) v.findViewById(R.id.rowheader);
            header.setText(item.getHeader());
            final TextView textView = (TextView) v.findViewById(R.id.rowitem);
            textView.setText(item.getBody());
        }

        if (!fired && position >= getCount() - 1) {
            Log.i(TAG, "fire next page request");
            listner.fire(nextParam);
            fired = true;
        }

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        itemArrayCache = null;
        super.notifyDataSetChanged();
    }

    protected final Context getContext(){
        return context;
    }
    protected Comparator<E> getComparator() {
        return new Comparator<E>() {
            @Override
            public int compare(E lhs, E rhs) {
                return lhs.compareTo(rhs);
            }
        };
    }

    public interface ReachLastItemListener {

        void fire(Bundle nextParam);
    }
}