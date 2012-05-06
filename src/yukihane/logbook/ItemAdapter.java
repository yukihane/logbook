package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import yukihane.logbook.entity.Item;
import yukihane.logbook.entity.Page;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
            v = inflater.inflate(R.layout.item_display, null);
        }

        final Item item = (Item) getItem(position);
        if (item != null) {
            final TextView header = (TextView) v.findViewById(R.id.rowheader);
            header.setText(item.getHeader());
            final TextView textView = (TextView) v.findViewById(R.id.rowitem);
            textView.setText(item.getBody());

            final URL picture = item.getPicture();
            final ImageView iv = (ImageView) v.findViewById(R.id.rowpicture);
            if (picture != null) {
                new DownloadImageTask(iv).execute(picture.toString());
            } else {
                iv.setImageBitmap(null);
            }

            final String linkName = item.getLinkName();
            final TextView linkTV = (TextView) v.findViewById(R.id.rowlinkname);
            if (linkName != null) {
                linkTV.setText(linkName);
            } else {
                linkTV.setText("");
            }

            final URL link = item.getLink();
            if (link != null) {
                final OnClickListener listener = new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        final Uri uri = Uri.parse(link.toString());
                        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        v.getContext().startActivity(intent);
                    }
                };

                if (picture != null) {
                    iv.setOnClickListener(listener);
                }

                if (linkName == null) {
                    linkTV.setTag("link");
                }
                linkTV.setOnClickListener(listener);
            } else {
                iv.setOnClickListener(null);
                linkTV.setOnClickListener(null);
            }

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

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final ImageView iv;

        private DownloadImageTask(ImageView iv) {
            super();
            this.iv = iv;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return LogbookApplication.getBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            iv.setImageBitmap(result);
        }
    }
}