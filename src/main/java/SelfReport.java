import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelfReport {

    public SelfReport(BufferedReader reader) throws IOException {
        List<String> lines = readReportLines(reader);
        for (String line : lines) {
            System.out.println(line);
        }
        List<ReportLine> interpretedLines = interpretReportLines(lines);
    }

    private List<String> readReportLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null)
            lines.add(line);
        return lines;
    }

    ReportLine castToCorrectLineType(String line) {
        Enum<ReportLineTypes> lineType = ReportLine.discoverLineType(line);
        if (lineType==ReportLineTypes.DATE_LINE) return new ReportDateLine(line);
        // Implement for activity line type too

        // Implement empty line as default
        return new ReportLine(line);
    }

    private List<ReportLine> interpretReportLines(List<String> lines) {
        List<ReportLine> interpretedLines = new ArrayList<>();
        for(String line: lines) {
            interpretedLines.add(castToCorrectLineType(line));
        }
        return interpretedLines;
    }


}
