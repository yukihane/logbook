package yukihane.logbook;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.CommentsPage;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentAdapter extends ItemAdapter<Comment, CommentsPage> {

    private StatusMessage parent;

    public CommentAdapter(Context context, ReachLastItemListener listener) {
        super(context, listener);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position == 0) {
            return parent;
        }

        return super.getItem(position - 1);
    }

    @Override
    public void addPage(CommentsPage feed2) {
        if (feed2.getParent() != null) {
            this.parent = feed2.getParent();
        }
        super.addPage(feed2);
    }

    @Override
    public void clear() {
        parent = null;
        super.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position != 0) {
            return super.getView(position - 1, convertView, parent);
        }

        View v = convertView;
        if (v == null) {
            final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_display, null);
        }

        final StatusMessage item = (StatusMessage) getItem(position);
        if (item != null) {
            final TextView header = (TextView) v.findViewById(R.id.rowheader);
            header.setText(item.getHeader());
            final TextView textView = (TextView) v.findViewById(R.id.rowitem);
            textView.setText(item.getBody());

            final String picture = item.getPicture();
            final ImageView iv = (ImageView) v.findViewById(R.id.rowpicture);

            iv.setImageBitmap(null);
            if (picture != null) {
                new StatusMessageAdapter.DownloadImageTask(iv).execute(picture.toString());
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

        } else {
            final TextView textView = (TextView) v.findViewById(R.id.rowitem);
            textView.setText("Parent text is not exists.");
        }
    

        return v;
    }

}
