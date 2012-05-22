package yukihane.logbook;

import java.util.Comparator;

import yukihane.logbook.entity.Comment;
import yukihane.logbook.entity.StatusMessage;
import yukihane.logbook.structure.CommentsPage;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
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
        final View v = super.getView(position, convertView, parent);
        if (position != 0) {
            return v;
        }

        final StatusMessage item = (StatusMessage) getItem(position);
        if (item != null) {
            StatusMessageAdapter.inflateStatusMessage(v, item, getImageLoader());
        } else {
            final TextView textView = (TextView) v.findViewById(R.id.rowitem);
            textView.setText("Parent text is not exists(Menu -> Original to brows).");
        }

        return v;
    }

    @Override
    protected Comparator<Comment> getComparator() {
        return new Comparator<Comment>() {
            @Override
            public int compare(Comment lhs, Comment rhs) {
                return lhs.compareTo(rhs);
            }
        };
    }

}
