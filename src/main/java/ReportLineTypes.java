public enum ReportLineTypes {
    DATE_LINE("DATE_LINE_TYPE"),
    ACTIVITY_LINE("ACTIVITY_LINE_TYPE"),
    EMPTY_LINE("EMPTY_LINE_TYPE");

    private String lineType;

    ReportLineTypes(String lineType) {
        this.lineType = lineType;
    }

    @Override
    public String toString() {
        return lineType;
    }
}
