import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelfReportParser {

    ReportInterpreter interpreter;
    List<String> rawLines;
    List<ReportLine> interpretedLines;

    public SelfReportParser(BufferedReader reader, ReportInterpreter interpreter) throws IOException {
        this.interpreter = interpreter;
        readReportLines(reader);
        interpretReportLines(rawLines);
    }

    private void readReportLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null)
            lines.add(line);
        rawLines = lines;
    }

    private void interpretReportLines(List<String> lines) {
        for(String line: lines) {
            interpretedLines.add(new ReportLine(line, interpreter));
        }
    }

    public ReportTable generateReportTable() {
        return new ReportTable(interpretedLines);
    }
}
