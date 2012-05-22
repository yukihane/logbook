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
import android.widget.ImageView;
import android.widget.TextView;

import com.fedorvlasov.lazylist.ImageLoader;

public abstract class ItemAdapter<E extends Listable<E>, P extends Page<E>> extends BaseAdapter {
    private final Context context;
    private final ReachLastItemListener listner;
    private final ImageLoader imageLoader;
    private final Collection<E> items;
    private Object[] itemArrayCache;
    private Bundle nextParam;
    private boolean fired = false;

    public ItemAdapter(Context context, ReachLastItemListener listener) {
        this.context = context;
        this.listner = listener;
        imageLoader = new ImageLoader(context.getApplicationContext(), "logbook_cache", com.fedorvlasov.lazylist.R.drawable.stub);
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

    protected static final class ViewHolder {
        TextView header;
        TextView body;
        ImageView picture;
        TextView link;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v;
        final ViewHolder holder;
        if (convertView == null) {
            final LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_display, null);

            holder = new ViewHolder();
            holder.header = (TextView) v.findViewById(R.id.rowheader);
            holder.body = (TextView) v.findViewById(R.id.rowitem);
            holder.picture = (ImageView) v.findViewById(R.id.rowpicture);
            holder.link = (TextView) v.findViewById(R.id.rowlinkname);

            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }

        final E item = (E) getItem(position);
        if (item != null) {
            holder.header.setText(item.getHeader());
            holder.body.setText(item.getBody());
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

    protected final Context getContext() {
        return context;
    }
    
    protected final ImageLoader getImageLoader(){
        return imageLoader;
    }

    protected abstract Comparator<E> getComparator();

    public interface ReachLastItemListener {

        void fire(Bundle nextParam);
    }
}