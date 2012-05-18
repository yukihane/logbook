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
            final String picture = item.getPicture();
            final ImageView iv = (ImageView) v.findViewById(R.id.rowpicture);

            iv.setImageBitmap(null);
            if (picture != null) {
                new DownloadImageTask(iv).execute(picture.toString());
            }

            final String linkName = item.getLinkName();
            final TextView linkTV = (TextView) v.findViewById(R.id.rowlinkname);
            if (linkName != null) {
                linkTV.setText(linkName);
            } else {
                linkTV.setText("");
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

        return v;
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

    static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private final ImageView iv;

        DownloadImageTask(ImageView iv) {
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