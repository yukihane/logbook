package yukihane.logbook;

import java.util.Comparator;

import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.FeedPage;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusMessageAdapter extends ItemAdapter<StatusMessage, FeedPage> {

    public StatusMessageAdapter(Context context, ReachLastItemListener listener) {
        super(context, listener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);

        final StatusMessage item = (StatusMessage) getItem(position);
        if (item != null) {
            inflateStatusMessage(v, item);

        }

        return v;
    }

    static void inflateStatusMessage(final View v, final StatusMessage item) {
        final String picture = item.getPicture();
        final ViewHolder holder = (ViewHolder) v.getTag();

        holder.picture.setImageBitmap(null);
        if (picture != null) {
            new DownloadImageTask(holder.picture).execute(picture.toString());
        }

        final String linkName = item.getLinkName();
        if (linkName != null) {
            holder.link.setText(linkName);
        } else {
            holder.link.setText("");
        }

        final String link = item.getLink();
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
                holder.picture.setOnClickListener(listener);
            }

            if (linkName == null) {
                holder.link.setText("link");
            }
            holder.link.setOnClickListener(listener);
        } else {
            holder.picture.setOnClickListener(null);
            holder.link.setOnClickListener(null);
        }
    }

    @Override
    protected Comparator<StatusMessage> getComparator() {
        return new Comparator<StatusMessage>() {
            @Override
            public int compare(StatusMessage lhs, StatusMessage rhs) {
                return -1 * lhs.compareTo(rhs);
            }
        };
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