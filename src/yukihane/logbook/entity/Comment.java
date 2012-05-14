package yukihane.logbook.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * コメントエンティティ.
 * @author yuki
 *
 */
public class Comment implements Listable<Comment> {

    @DatabaseField(id = true)
    private String id;
    @DatabaseField(foreign = true)
    private String statusMessageID;
    @DatabaseField
    private String message;
    private String userName;

    @DatabaseField
    private String userID;
    @DatabaseField
    private final Date createdTime;

    @DatabaseField
    private String picture;
    @DatabaseField
    private String link;
    @DatabaseField
    private String linkName;

    public static Builder<?> builder(String id) {
        return new Builder2(id);
    }

    public String getID() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getPicture() {
        return picture;
    }

    public String getLink() {
        return link;
    }

    public String getLinkName() {
        return linkName;
    }

    public String getHeader() {
        return "" + userName + "  " + createdTime;
    }

    public String getBody() {
        return "" + message;
    }

    @Override
    public String toString() {
        return "id:" + id + ", type:comment, \nmessage:" + message + ", \nuser name: " + userName + ", create time: "
                + createdTime;
    }

    @Override
    public int compareTo(Comment another) {
        final Date me = createdTime;
        final Date you = another.createdTime;
        return me.getTime() == you.getTime() ? 0 : me.getTime() > you.getTime() ? 1 : -1;
    }

    protected Comment(Builder<?> b) {
        this.id = b.id;
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
        private String message;
        private String userName;

        private String userID;
        private Date createdTime;

        private String picture;
        private String link;
        private String linkName;

        protected abstract T self();

        public Builder(String id) {
            this.id = id;
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
            this.picture = (picture != null && picture.length() > 0) ? picture : null;
            return self();
        }

        public T link(String link) {
            this.link = (link != null && link.length() > 0) ? link : null;
            return self();
        }

        public T linkName(String name) {
            this.linkName = (name != null && name.length() > 0) ? name : null;
            return self();
        }
    }

    private static final class Builder2 extends Builder<Builder2> {

        public Builder2(String id) {
            super(id);
        }

        @Override
        protected Builder2 self() {
            return this;
        }
    }
}
