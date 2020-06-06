import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelfReportParser {

    private ReportInterpreter interpreter;
    private List<String> rawLines;
    private List<ReportLine> interpretedLines;

    public SelfReportParser(String filePath, ReportInterpreter interpreter) throws IOException {
        BufferedReader reader;
        try {
            File file = new File(filePath);
            reader = new BufferedReader(new FileReader(file));
        }
        catch(Exception e) {
            throw new Error("Could not read data file: " + filePath, e);
        }

        this.interpreter = interpreter;
        readReportLines(reader);
        interpretedLines = new ArrayList<>();
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

    public List<ReportLine> getInterpretedLines() { return interpretedLines; }

    public ReportTable generateReportTable() {
        return new ReportTable(interpretedLines);
    }
}
