
public class InterpreterNotification {

    private String message;
    private NotificationTypes type;
    InterpreterNotification(NotificationTypes type, String message) {
        this.message = message;
        this.type = type;
    }

    @Override
    public String toString() {
        return "<"+type.toString()+"> "+message;
    }
}
