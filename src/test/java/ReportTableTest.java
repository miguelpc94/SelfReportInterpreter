import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class ReportTableTest {
    private String testFile = "src/test/resources/report.txt";
    private String configFile = "src/test/resources/config.json";
    private String outputFile = "src/test/resources/output.csv";

    private ReportTable generateReportTableForTest() throws IOException {

        ReportInterpreter interpreter = new ReportInterpreter();
        interpreter.withConfigurationFile(configFile);

        SelfReportParser selfReport = new SelfReportParser(testFile, interpreter);

        return(selfReport.generateReportTable());
    }

    @Test
    public void testGetCsvLines() throws IOException {
        ReportTable table = generateReportTableForTest();

        List<String> csvLines = table.getCsvLines();

        assert(csvLines.get(5).equals("\"Working at office\",\"Career development\",\"0.12500000000000006\",\"8\",\"43844.395833333336\",\"43844.520833333336\",\"43844.0\""));
        assert(csvLines.get(11).equals("\"Programming Java\",\"Programming\",\"0.125\",\"9\",\"43845.8125\",\"43845.9375\",\"43845.0\""));
        assert(csvLines.get(42).equals("\"Reading a book\",\"Reading\",\"0.02083333333333326\",\"10\",\"43858.520833333336\",\"43858.541666666664\",\"43858.0\""));
        assert(csvLines.get(56).equals("\"Meditation\",\"Mindfulness\",\"0.031249999999999944\",\"10\",\"43865.322916666664\",\"43865.354166666664\",\"43865.0\""));
        assert(csvLines.get(66).equals("\"\",\"General\",\"0.0\",\"0\",\"43869.0\",\"43869.0\",\"43869.0\""));
    }

    private boolean outputFileExists() {
        File output = new File(outputFile);
        return(output.exists());
    }

    private void deleteOutputFile() {
        File output = new File(outputFile);
        output.delete();
    }

    @Test
    public void testWriteToFile() throws IOException {
        deleteOutputFile();
        assert !outputFileExists();

        ReportTable table = generateReportTableForTest();
        assert table != null;
        table.writeToFile(outputFile);

        assert outputFileExists();
        deleteOutputFile();
        assert !outputFileExists();
    }
}
