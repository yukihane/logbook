package yukihane.logbook.entity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * コメントエンティティ.
 * @author yuki
 *
 */
public class Comment implements Listable<Comment> {

    private String id;
    private String type;
    private String message;
    private String userName;

    private String userID;
    private final Date createdTime;

    private URL picture;
    private URL link;
    private String linkName;

    public static Builder<?> builder(String id, String type) {
        return new Builder2(id, type);
    }

    public String getID() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public URL getPicture() {
        return picture;
    }

    public URL getLink() {
        return link;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getHeader() {
        return "" + userName + "  " + createdTime + " " + type;
    }

    public String getBody() {
        return "" + message;
    }

    @Override
    public String toString() {
        return "id:" + id + ", type:" + type + ", \nmessage:" + message + ", \nuser name: " + userName
                + ", create time: " + createdTime;
    }

    @Override
    public int compareTo(Comment another) {
        final Date me = createdTime;
        final Date you = another.createdTime;
        return me.getTime() == you.getTime() ? 0 : me.getTime() > you.getTime() ? 1 : -1;
    }

    protected Comment(Builder<?> b) {
        this.id = b.id;
        this.type = b.type;
        this.message = b.message;
        this.userID = b.userID;
        this.userName = b.userName;
        this.createdTime = b.createdTime;

        this.picture = b.picture;
        this.link = b.link;
        this.linkName = b.linkName;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private final String id;
        private final String type;
        private String message;
        private String userName;

        private String userID;
        private Date createdTime;

        private URL picture;
        private URL link;
        private String linkName;

        protected abstract T self();

        public Builder(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public Comment build() {
            return new Comment(this);
        }

        public T message(String message) {
            this.message = message;
            return self();
        }

        public T userID(String userID) {
            this.userID = userID;
            return self();
        }

        public T userName(String userName) {
            this.userName = userName;
            return self();
        }

        public T createdTime(Date createdTime) {
            this.createdTime = createdTime;
            return self();
        }

        public T picture(String picture) {
            try {
                this.picture = (picture != null && picture.length() > 0) ? new URL(picture) : null;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("illegal picture url: " + picture + ",id:" + id + ",type:" + type, e);
            }
            return self();
        }

        public T link(String link) {
            try {
                this.link = (link != null && link.length() > 0) ? new URL(link) : null;
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException("illegal link url: " + picture + ",id:" + id + ",type:" + type, e);
            }
            return self();
        }

        public T linkName(String name) {
            this.linkName = (name != null && name.length() > 0) ? name : null;
            return self();
        }
    }

    private static final class Builder2 extends Builder<Builder2> {

        public Builder2(String id, String type) {
            super(id, type);
        }

        @Override
        protected Builder2 self() {
            return this;
        }
    }
}
