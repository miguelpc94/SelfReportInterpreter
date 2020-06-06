public enum NotificationTypes {
    WARNING("WARNING"),
    ERROR("ERROR");

    private String type;
    NotificationTypes(String type) {
        this.type = type;
    }
    @Override
    public String toString() {
        return type;
    }

}