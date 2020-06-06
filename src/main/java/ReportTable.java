import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// The interpreted lines are a set of instructions to assemble lines in the self-report table
public class ReportTable {
    private List<String> csvLines;

    public ReportTable(List<ReportLine> interpretedLines) {
        List<Map<String,String>> enrichedRowContents = enrichInterpretedLinesLines(removeEmptyLines(interpretedLines));
        convertReportLinesToCsvLines(enrichedRowContents);
    }

    private List<ReportLine> removeEmptyLines(List<ReportLine> interpretedLines) {
        List<ReportLine> nonEmptyLines = new ArrayList<>();

        for (ReportLine interpretedLine : interpretedLines) {
            if (interpretedLine.getLineType() != ReportLineTypes.EMPTY_LINE || interpretedLine.getLineType() != ReportLineTypes.INVALID_LINE) nonEmptyLines.add(interpretedLine);
        }

        return interpretedLines;
    }

    private List<Map<String,String>> enrichInterpretedLinesLines(List<ReportLine> interpretedLines) {
        List<Map<String,String>>  enrichedRowContents = new ArrayList<>();
        double day = 0;

        for (ReportLine interpretedLine:interpretedLines) {
            switch (interpretedLine.getLineType()) {
                case DATE_LINE:
                    day = Double.parseDouble(interpretedLine.getRowContent(ReportInterpreter.day));
                    break;
                case ACTIVITY_LINE:
                case NO_ACTIVITY_LINE:
                    Map<String,String> enrichedRowContent = new HashMap<>();
                    enrichedRowContent.put(ReportInterpreter.activity, interpretedLine.getRowContent(ReportInterpreter.activity));
                    enrichedRowContent.put(ReportInterpreter.category, interpretedLine.getRowContent(ReportInterpreter.category));
                    enrichedRowContent.put(ReportInterpreter.timeAllocated, interpretedLine.getRowContent(ReportInterpreter.timeAllocated));
                    enrichedRowContent.put(ReportInterpreter.efficiency, interpretedLine.getRowContent(ReportInterpreter.efficiency));
                    enrichedRowContent.put(ReportInterpreter.day, Double.toString(day));
                    double newStart = Double.parseDouble(interpretedLine.getRowContent(ReportInterpreter.start)) + day;
                    double newEnd = Double.parseDouble(interpretedLine.getRowContent(ReportInterpreter.end)) + day;
                    enrichedRowContent.put(ReportInterpreter.start, Double.toString(newStart));
                    enrichedRowContent.put(ReportInterpreter.end, Double.toString(newEnd));
                    enrichedRowContents.add(enrichedRowContent);
            }
        }

        return enrichedRowContents;
    }

    private void convertReportLinesToCsvLines(List<Map<String,String>>  rowContents) {
        csvLines = new ArrayList<>();
        String header = "";
        header += "\""+ReportInterpreter.activity+"\""+",";
        header += "\""+ReportInterpreter.category+"\""+",";
        header += "\""+ReportInterpreter.timeAllocated+"\""+",";
        header += "\""+ReportInterpreter.efficiency+"\""+",";
        header += "\""+ReportInterpreter.start+"\""+",";
        header += "\""+ReportInterpreter.end+"\""+",";
        header += "\""+ReportInterpreter.day+"\"";
        csvLines.add(header);

        for (Map<String,String> rowContent : rowContents) {
            String line = "";
            line += "\""+rowContent.get(ReportInterpreter.activity)+"\""+",";
            line += "\""+rowContent.get(ReportInterpreter.category)+"\""+",";
            line += "\""+rowContent.get(ReportInterpreter.timeAllocated)+"\""+",";
            line += "\""+rowContent.get(ReportInterpreter.efficiency)+"\""+",";
            line += "\""+rowContent.get(ReportInterpreter.start)+"\""+",";
            line += "\""+rowContent.get(ReportInterpreter.end)+"\""+",";
            line += "\""+rowContent.get(ReportInterpreter.day)+"\"";
            csvLines.add(line);
        }
    }

    public List<String> getCsvLines() { return csvLines; }

    public void writeToFile(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        for (String csvLine:csvLines) {
            writer.write(csvLine+"\n");
        }
        writer.close();
    }
}
