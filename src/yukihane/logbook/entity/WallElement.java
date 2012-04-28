package yukihane.logbook.entity;

public class WallElement {

    private final String id;
    private final String type;
    private final String message;

    public WallElement(String id, String type, String message) {
        this.id = id;
        this.type = type;
        this.message = message;
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

    @Override
    public String toString() {
        return "id:" + id + ", type:" + type + ", message:" + message;
    }
}
