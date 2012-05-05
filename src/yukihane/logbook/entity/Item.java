package yukihane.logbook.entity;

import java.util.Date;
import java.util.List;

import org.w3c.dom.Comment;

/**
 * ページに表示するひとつひとつの項目.
 * @author yuki
 *
 */
public class Item {

    private final String id;
    private final String type;
    private final String message;
    private final String userID;
    private String userName;
    private final Date createdTime;
    private Date updatedTime;
    private List<Comment> comments;
    private int commentsCount;

    public static class ItemBuilder {
        private final String id;
        private final String type;
        private String userID;
        private Date createdTime;
        private String message;
        private String userName;
        private Date updatedTime;
        private int commentsCount;

        public ItemBuilder(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public Item build() {
            return new Item(id, type, message, userID, userName, createdTime, updatedTime, commentsCount);
        }

        public ItemBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ItemBuilder setUserID(String userID) {
            this.userID = userID;
            return this;
        }

        public ItemBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public ItemBuilder setCreatedTime(Date createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public ItemBuilder setUpdatedTime(Date updatedTime) {
            this.updatedTime = updatedTime;
            return this;
        }

        public ItemBuilder setCommentCount(int commentCount) {
            this.commentsCount = commentCount;
            return this;
        }

    }

    private Item(String id, String type, String message, String userID, String userName, Date createdTime,
            Date updatedTime, int commentsCount) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.userID = userID;
        this.userName = userName;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.commentsCount = commentsCount;
    }

    public String getId() {
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
}
