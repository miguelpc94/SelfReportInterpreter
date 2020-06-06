import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ReportInterpreterTest {

    private String testFile = "src/test/resources/report.txt";
    private String configFile = "src/test/resources/config.json";

    @Test
    public void loadingConfigurationsFromFileTest() throws IOException {
        ReportInterpreter interpreter = new ReportInterpreter();
        interpreter.withConfigurationFile(configFile);
        SelfReportParser selfReport = new SelfReportParser(testFile, interpreter);
        List<ReportLine> interpretedLines = selfReport.getInterpretedLines();
        assert(interpreter.getNotifications().size() == 0);
        assert(interpretedLines.size() == 129 );
    }

    @Test
    public void dashCounterTest() {
        List<String> dashes = Arrays.asList(
                "-- - - nope -",
                "ab-cd\n",
                "123-456-790-123",
                "1994\n",
                "randomText-42-",
                "-dash-test-\n"
        );

        ReportInterpreter interpreter = new ReportInterpreter();

        assert(interpreter.countDashes(dashes.get(0)) == 5);
        assert(interpreter.countDashes(dashes.get(1)) == 1);
        assert(interpreter.countDashes(dashes.get(2)) == 3);
        assert(interpreter.countDashes(dashes.get(3)) == 0);
        assert(interpreter.countDashes(dashes.get(4)) == 2);
        assert(interpreter.countDashes(dashes.get(5)) == 3);
    }

    @Test
    public void emptyLineDetectionTest() {
        List<String> rawLines = Arrays.asList(
                "ab-cd\n",
                "  \n",
                "-\n ",
                "",
                "\n   ",
                "test 10 1890 2142"
        );

        ReportInterpreter interpreter = new ReportInterpreter();

        assert(!interpreter.isEmpyLine(rawLines.get(0)));
        assert(interpreter.isEmpyLine(rawLines.get(1)));
        assert(!interpreter.isEmpyLine(rawLines.get(2)));
        assert(interpreter.isEmpyLine(rawLines.get(3)));
        assert(interpreter.isEmpyLine(rawLines.get(4)));
        assert(!interpreter.isEmpyLine(rawLines.get(5)));
    }

    @Test
    public void noActivityLineDetectionTest() {
        List<String> rawLines = Arrays.asList(
                "ab-cd\n",
                "   -----   \n",
                "--\n",
                "-",
                "---  \n",
                "----"
        );

        ReportInterpreter interpreter = new ReportInterpreter();

        assert(!interpreter.isNoActivityLine(rawLines.get(0)));
        assert(interpreter.isNoActivityLine(rawLines.get(1)));
        assert(interpreter.isNoActivityLine(rawLines.get(2)));
        assert(!interpreter.isNoActivityLine(rawLines.get(3)));
        assert(interpreter.isNoActivityLine(rawLines.get(4)));
        assert(interpreter.isNoActivityLine(rawLines.get(5)));
    }

    @Test
    public void dateLineDetectionTest() {
        List<String> rawLines = Arrays.asList(
                "ab-cd\n",
                "----\n",
                "--\n",
                "-",
                "11-fb   \n",
                "15-dc",
                "  9-ag \n"
        );

        ReportInterpreter interpreter = new ReportInterpreter();

        assert(interpreter.isDateLine(rawLines.get(0)));
        assert(!interpreter.isDateLine(rawLines.get(1)));
        assert(!interpreter.isDateLine(rawLines.get(2)));
        assert(!interpreter.isDateLine(rawLines.get(3)));
        assert(interpreter.isDateLine(rawLines.get(4)));
        assert(interpreter.isDateLine(rawLines.get(5)));
        assert(interpreter.isDateLine(rawLines.get(6)));
    }

    @Test
    public void activityLineDetectionTest() {
        List<String> rawLines = Arrays.asList(
                "abcd                   8    0900    1820 \n",
                "ab8 1234 5678     \n",
                "----\n",
                "--\n",
                "-",
                "11-fb   \n",
                "abcd         8    0900 1820     \n",
                "a 10 0810 0910\n",
                "a 9 0810 0910"
        );

        ReportInterpreter interpreter = new ReportInterpreter();

        assert(interpreter.isActivityLine(rawLines.get(0)));
        assert(interpreter.isActivityLine(rawLines.get(1)));
        assert(!interpreter.isActivityLine(rawLines.get(2)));
        assert(!interpreter.isActivityLine(rawLines.get(3)));
        assert(!interpreter.isActivityLine(rawLines.get(4)));
        assert(!interpreter.isActivityLine(rawLines.get(5)));
        assert(interpreter.isActivityLine(rawLines.get(6)));
        assert(interpreter.isActivityLine(rawLines.get(7)));
        assert(interpreter.isActivityLine(rawLines.get(8)));
    }

    private boolean isRowContentExpected(Map<String,String> actualRowContent, Map<String,String> expectedRowContent) {
        for (String column:expectedRowContent.keySet()) {
            if (!actualRowContent.containsKey(column)) return false;
             if (!expectedRowContent.get(column).contentEquals(actualRowContent.get(column))) return false;
        }
        return true;
    }

    @Test
    public void interpretDateLineTest() {
        String rawLine;
        Map<String,String> expectedRowContent, actualRowContent;
        ReportInterpreter interpreter = new ReportInterpreter();
        interpreter.withYear(2020)
                    .withMonthCodeFor("xx", 2)
                    .withMonthCodeFor("zz", 10);


        expectedRowContent = new HashMap<>();
        expectedRowContent.put("Activity", "");
        expectedRowContent.put("Category", "");
        expectedRowContent.put("Time allocated", "");
        expectedRowContent.put("Efficiency", "");
        expectedRowContent.put("Start", "43870.0");
        expectedRowContent.put("End", "43870.0");
        expectedRowContent.put("Day", "43870.0");
        rawLine = " 9 - xx\n";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.DATE_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));

        expectedRowContent = new HashMap<>();
        expectedRowContent.put("Start", "44130.0");
        expectedRowContent.put("End", "44130.0");
        expectedRowContent.put("Day", "44130.0");
        rawLine = " 26-  zz    ";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.DATE_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));
    }

    @Test
    public void interpretActivityLineTest() {
        ReportInterpreter interpreter = new ReportInterpreter();
        String rawLine;
        Map<String,String> expectedRowContent, actualRowContent;

        interpreter.withYear(2020)
                    .withActivity("tst", "Testing", "Testing the code")
                    .withActivity("Greene", "Reading", "Reading Until The End Of Time");

        expectedRowContent = new HashMap<>();
        expectedRowContent.put("Activity", "Reading Until The End Of Time");
        expectedRowContent.put("Category", "Reading");
        expectedRowContent.put("Time allocated", "0.3645833333333333");
        expectedRowContent.put("Efficiency", "10");
        expectedRowContent.put("Start", "0.3854166666666667");
        expectedRowContent.put("End", "0.75");
        expectedRowContent.put("Day", "0.0");
        rawLine = "Greene     10  0915  1800\n\n";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.ACTIVITY_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));

        expectedRowContent = new HashMap<>();
        expectedRowContent.put("Activity", "Testing the code");
        expectedRowContent.put("Category", "Testing");
        expectedRowContent.put("Time allocated", "0.06250000000000011");
        expectedRowContent.put("Efficiency", "5");
        expectedRowContent.put("Start", "0.9166666666666666");
        expectedRowContent.put("End", "0.9791666666666667");
        expectedRowContent.put("Day", "0.0");
        rawLine = " tst     5  2200  2330  \n";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.ACTIVITY_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));
    }

    @Test
    public void interpretNoActivityLineTest() {
        ReportInterpreter interpreter = new ReportInterpreter();
        String rawLine;
        Map<String,String> expectedRowContent, actualRowContent;

        expectedRowContent = new HashMap<>();
        expectedRowContent.put("Activity", "");
        expectedRowContent.put("Category", "General");
        expectedRowContent.put("Time allocated", "0.0");
        expectedRowContent.put("Efficiency", "0");
        expectedRowContent.put("Start", "0.0");
        expectedRowContent.put("End", "0.0");
        expectedRowContent.put("Day", "0.0");
        rawLine = "---";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.NO_ACTIVITY_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));
        rawLine = "- - -\n";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.NO_ACTIVITY_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));
        rawLine = "--";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.NO_ACTIVITY_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));
        rawLine = "--- -";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.NO_ACTIVITY_LINE);
        assert(isRowContentExpected( actualRowContent, expectedRowContent));
    }

    @Test
    public void interpretEmptyLineTest() {
        ReportInterpreter interpreter = new ReportInterpreter();
        String rawLine;
        Map<String,String> actualRowContent;

        rawLine = "\n\n  ";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.EMPTY_LINE);
        assert(actualRowContent==null);

        rawLine = "";
        actualRowContent = interpreter.interpretLine(rawLine, ReportLineTypes.EMPTY_LINE);
        assert(actualRowContent==null);
    }

    private class NotificationTestStore {
        String rawLine;
        ReportLineTypes lineType;
        List<String> notifications;
        NotificationTestStore(String rawLine, ReportLineTypes lineType, List<String> notifications) {
            this.rawLine = rawLine;
            this.lineType = lineType;
            this.notifications = notifications;
        }
    }

    @Test
    public void notificationTest() {
        ReportInterpreter interpreter = new ReportInterpreter();
        interpreter.withYear(2020)
                    .withMonthCodeFor("xx",1)
                    .withActivity("test", "Testing", "Testing interpreter");
        List<NotificationTestStore> tests = new ArrayList<>();

        tests.add(new NotificationTestStore("15-ha", ReportLineTypes.DATE_LINE,
                Arrays.asList("<ERROR> Invalid date line: 15-ha")
        ));
        tests.add(new NotificationTestStore("35-xx", ReportLineTypes.DATE_LINE,
                Arrays.asList("<ERROR> Invalid date line: 35-xx")
        ));
        tests.add(new NotificationTestStore("1-hh", ReportLineTypes.DATE_LINE,
                Arrays.asList("<ERROR> Invalid date line: 1-hh")
        ));
        tests.add(new NotificationTestStore("GG  10 1900 2100", ReportLineTypes.ACTIVITY_LINE,
                Arrays.asList(
                        "<WARNING> Activity code does not have a value: GG",
                        "<WARNING> Activity code does not have a category: GG"
                )
        ));
        tests.add(new NotificationTestStore("AB    10     1900    2165", ReportLineTypes.ACTIVITY_LINE,
                Arrays.asList(
                        "<WARNING> Activity code does not have a value: AB",
                        "<WARNING> Activity code does not have a category: AB",
                        "<ERROR> Invalid activity hour: 2165"
                )
        ));
        tests.add(new NotificationTestStore("test    10     1900    4200", ReportLineTypes.ACTIVITY_LINE,
                Arrays.asList("<ERROR> Invalid activity hour: 4200")
        ));

        for (NotificationTestStore test:tests) {
            interpreter.cleanNotifications();
            Map<String,String> deleteMePlease = interpreter.interpretLine(test.rawLine, test.lineType);
            assert(interpreter.areThereNotifications());
            for (int i=0; i<test.notifications.size(); i++ ) {
                assert(interpreter.getNotifications().get(i).toString().contentEquals(test.notifications.get(i)));
            }
        }
    }
}
