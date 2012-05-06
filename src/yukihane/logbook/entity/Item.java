package yukihane.logbook.entity;

import java.util.Date;

/**
 * ページに表示するひとつひとつの項目.
 * @author yuki
 *
 */
public class Item {

    private String id;
    private String type;
    private String message;
    private String userID;
    private String userName;
    private final Date createdTime;
    private Date updatedTime;
    private int commentsCount;

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

    public String getHeader() {
        return "" + userName + "  " + updatedTime + "(" + commentsCount + ")" + " " + type;
    }

    public String getBody() {
        return "" + message;
    }

    @Override
    public String toString() {
        return "(" + commentsCount + ")" + "id:" + id + ", type:" + type + ", \nmessage:" + message + ", \nuser name: "
                + userName + ", update time: " + updatedTime;
    }

    protected Item(Builder<?> b) {
        this.id = b.id;
        this.type = b.type;
        this.message = b.message;
        this.userID = b.userID;
        this.userName = b.userName;
        this.createdTime = b.createdTime;
        this.updatedTime = b.updatedTime;
        this.commentsCount = b.commentsCount;
    }

    public static abstract class Builder<T extends Builder<T>> {
        private final String id;
        private final String type;
        private String userID;
        private Date createdTime;
        private String message;
        private String userName;
        private Date updatedTime;
        private int commentsCount;

        protected abstract T self();

        public Builder(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public Item build() {
            return new Item(this);
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

        public T updatedTime(Date updatedTime) {
            this.updatedTime = updatedTime;
            return self();
        }

        public T commentCount(int commentCount) {
            this.commentsCount = commentCount;
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
