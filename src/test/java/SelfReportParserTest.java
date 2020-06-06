import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class SelfReportParserTest {

    private String testFile = "src/test/resources/report.txt";
    private String configFile = "src/test/resources/config.json";

    private SelfReportParser generateSelfReportParserForTest() throws IOException {

        ReportInterpreter interpreter = new ReportInterpreter();
        interpreter.withConfigurationFile(configFile);

        SelfReportParser selfReport = new SelfReportParser(testFile, interpreter);

        return(selfReport);
    }

    @Test
    public void testInterpretedLines() throws IOException {

        SelfReportParser selfReport = generateSelfReportParserForTest();
        List<ReportLine> interpretedLines = selfReport.getInterpretedLines();

        assert(interpretedLines.size() == 129 );
    }

    @Test
    public void testReportTableGeneration() throws IOException {
        SelfReportParser selfReport = generateSelfReportParserForTest();
        ReportTable table = selfReport.generateReportTable();
        assert(table.getCsvLines().size() == 70);
    }
}
