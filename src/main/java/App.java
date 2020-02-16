import java.io.*;

public class App {
    static public void main(String[] args) throws IOException {
        System.out.println("Reading self report...");
        //SelfReport report = new SelfReport("")
        String filePath;
        try {
            filePath = args[0];
        }
        catch(Exception e) {
            System.out.println("Give file path as an argument");
            throw new Error("No file given as argument");
        }

        BufferedReader reader;
        try {
            File file = new File(filePath);
            reader = new BufferedReader(new FileReader(file));
        }
        catch(Exception e) {
            throw new Error("Could not read file: " + e.getMessage(), e);
        }

        // TODO: There should be a SelfReportBuilder, that takes a reader for a report file and another for the interpreter config
        // TODO: A instance of the interpreter is created and passed to the SelfReportParser's constructor, just as seen bellow
        ReportInterpreter interpreter = new ReportInterpreter();
        SelfReportParser selfReport = new SelfReportParser(reader, interpreter);
        ReportTable reportTable = selfReport.generateReportTable();
        // TODO: reportTable.writeToCsv ?
    }
}