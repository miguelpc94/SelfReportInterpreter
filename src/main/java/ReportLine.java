import java.util.Map;

public class ReportLine {
    private ReportInterpreter interpreter;
    private String rawLine = "";
    private ReportLineTypes lineType;
    private Map<String,String> rowContent;

    ReportLine(String line, ReportInterpreter interpreter) {
        this.interpreter = interpreter;
        this.rawLine = line;
        lineType = interpreter.discoverLineType(rawLine);
        populateRowContent();
    }

    public String getRaw() { return this.rawLine; }

    public ReportLineTypes getLineType() { return lineType; }

    public String getRowContent(String columnName) { return rowContent == null ? null : rowContent.get(columnName); }

    private void populateRowContent() { rowContent = interpreter.interpretLine(rawLine, lineType); }
}