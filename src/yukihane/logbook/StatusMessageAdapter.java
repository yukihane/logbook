package yukihane.logbook;

import static yukihane.logbook.LogbookApplication.TAG;

import java.util.Comparator;

import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.FeedPage;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fedorvlasov.lazylist.ImageLoader;

public class StatusMessageAdapter extends ItemAdapter<StatusMessage, FeedPage> {
    private final ImageLoader imageLoader;

    public StatusMessageAdapter(Context context, ReachLastItemListener listener) {
        super(context, listener);
        imageLoader = new ImageLoader(context.getApplicationContext(), "logbook_cache", com.fedorvlasov.lazylist.R.drawable.stub);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);

        final StatusMessage item = (StatusMessage) getItem(position);
        if (item != null) {
            inflateStatusMessage(v, item, imageLoader);

        }

        return v;
    }

    static void inflateStatusMessage(final View v, final StatusMessage item, final ImageLoader imLoader) {
        final String picture = item.getPicture();
        final ViewHolder holder = (ViewHolder) v.getTag();

        holder.picture.setImageBitmap(null);
        if (picture != null) {
            if (imLoader == null) {
                new DownloadImageTask(holder.picture).execute(picture.toString());
            } else {
                imLoader.displayImage(picture, holder.picture);
            }
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
            final Bitmap bm = LogbookApplication.getBitmap(params[0]);
            if (bm == null) {
                Log.i(TAG, "Image not found: " + params[0]);
                return null;
            }

            final int sizeMax = 128;
            int width = bm.getWidth();
            int height = bm.getHeight();
            if (width >= height) {
                if (width > sizeMax) {
                    height = height * sizeMax / width;
                    width = sizeMax;
                }
            } else {
                if (height > sizeMax) {
                    width = width * sizeMax / height;
                    height = sizeMax;
                }
            }
            return Bitmap.createScaledBitmap(bm, width, height, true);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            iv.setImageBitmap(result);
        }
    }
}