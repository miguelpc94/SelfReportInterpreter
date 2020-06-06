import java.io.*;
import java.util.List;

public class App {
    static public void main(String[] args) throws IOException {


        String dataFilePath;
        try {
            dataFilePath = args[0];
        }
        catch(Exception e) {
            System.out.println("Give file path as an argument");
            throw new Error("No file given as argument");
        }

        runWithFiles(dataFilePath,"config.json", "output.csv");
    }

    static public void runWithFiles(String dataFilePath, String configFilePath, String outputFilePath) throws IOException {
        System.out.println("Reading self report...");

        ReportInterpreter interpreter = new ReportInterpreter();
        interpreter.withConfigurationFile(configFilePath);
        SelfReportParser selfReport = new SelfReportParser(dataFilePath, interpreter);
        List<InterpreterNotification> notifications = interpreter.getNotifications();

        System.out.println("\nThe self report was read\n");
        if (notifications.size()>0) {
            System.out.println("Could not write output file due to the following notifications:\n");
            for (InterpreterNotification notificaion : notifications) {
                System.out.println(notificaion.toString()+"\n");
            }
        } else {
            System.out.println("\nGenerating report table...\n");
            ReportTable reportTable = selfReport.generateReportTable();
            System.out.println("\nWriting report table to output file...\n");
            reportTable.writeToFile(outputFilePath);
            // TODO: reportTable.writeToCsv <========================================== 12 (11 done)
        }
    }

}