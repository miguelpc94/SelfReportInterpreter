import java.util.HashMap;

public class ReportDateLine extends ReportLine {

    ReportDateLine(String line) {
        super(line);
    }

    // Develop to ready raw line and populate the row content
    @Override
    void populateRowContent() { rowContent = new HashMap<>(); }

    @Override
    void setLineType() { lineType = ReportLineTypes.DATE_LINE; }

}
