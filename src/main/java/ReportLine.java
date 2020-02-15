import java.util.HashMap;
import java.util.Map;

public class ReportLine {
    private String rawLine;
    Enum<ReportLineTypes> lineType;
    Map<String,String> rowContent;

    ReportLine(String line) {
        this.rawLine = line;
        setLineType();
        if (discoverLineType(line) == lineType) lineType = ReportLineTypes.DATE_LINE;
        else throw new Error("Raw line content could not be casted into " + lineType);
        populateRowContent();
    }

    public String getRaw() { return this.rawLine; }

    public Enum<ReportLineTypes> getLineType() { return lineType; }

    public String getRowContent(String columnName) { return rowContent.get(columnName); }

    // Develop this to determine line type
    static public Enum<ReportLineTypes> discoverLineType(String line) { return ReportLineTypes.EMPTY_LINE; }

    // Override this
    // Develop this to populate all existing columns with null
    void populateRowContent() { rowContent = new HashMap<>(); }

    // Override this
    void setLineType() { lineType = ReportLineTypes.EMPTY_LINE; }
}