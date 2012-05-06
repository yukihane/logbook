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

    public static class Builder {
        private final String id;
        private final String type;
        private String userID;
        private Date createdTime;
        private String message;
        private String userName;
        private Date updatedTime;
        private int commentsCount;

        public Builder(String id, String type) {
            this.id = id;
            this.type = type;
        }

        public Item build() {
            return new Item(id, type, message, userID, userName, createdTime, updatedTime, commentsCount);
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder userID(String userID) {
            this.userID = userID;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder createdTime(Date createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder updatedTime(Date updatedTime) {
            this.updatedTime = updatedTime;
            return this;
        }

        public Builder commentCount(int commentCount) {
            this.commentsCount = commentCount;
            return this;
        }

    }
}
