package yukihane.logbook;

import java.util.Comparator;

import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.FeedPage;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fedorvlasov.lazylist.ImageLoader;

public class StatusMessageAdapter extends ItemAdapter<StatusMessage, FeedPage> {

    public StatusMessageAdapter(Context context, ReachLastItemListener listener) {
        super(context, listener);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View v = super.getView(position, convertView, parent);

        final StatusMessage item = (StatusMessage) getItem(position);
        if (item != null) {
            inflateStatusMessage(v, item, getImageLoader());
        }

        return v;
    }

    static void inflateStatusMessage(final View v, final StatusMessage item, final ImageLoader imLoader) {
        final String picture = item.getPicture();
        final ViewHolder holder = (ViewHolder) v.getTag();

        holder.picture.setImageBitmap(null);
        if (picture != null) {
            imLoader.displayImage(picture, holder.picture);
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
}
