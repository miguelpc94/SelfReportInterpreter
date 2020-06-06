import org.junit.Test;

public class ReportLineTest {

    @Test
    public void testGetRaw() {

        ReportInterpreter interpreter = new ReportInterpreter();
        ReportLine interpretedLine;
        String rawLine;

        interpreter.withYear(2020)
                .withActivity("tst", "Testing", "Testing the code")
                .withActivity("Greene", "Reading", "Reading Until The End Of Time");

        rawLine = "answer 10 2142 2242";
        interpretedLine = new ReportLine(rawLine, interpreter);
        assert(interpretedLine.getRaw().equals("answer 10 2142 2242"));

        rawLine = "- - - - \n";
        interpretedLine = new ReportLine(rawLine, interpreter);
        assert(interpretedLine.getRaw().equals("- - - - \n"));

    }

    @Test
    public void testGetLineType(){

        ReportInterpreter interpreter = new ReportInterpreter();
        ReportLine interpretedLine;
        String rawLine;

        rawLine = "test 10 0900 2142";
        interpretedLine = new ReportLine(rawLine, interpreter);
        assert(interpretedLine.getLineType() == ReportLineTypes.ACTIVITY_LINE);

        rawLine = "ac-dc";
        interpretedLine = new ReportLine(rawLine, interpreter);
        assert(interpretedLine.getLineType() == ReportLineTypes.DATE_LINE);

    }

    @Test
    public void testGetRowContent() {

        ReportInterpreter interpreter = new ReportInterpreter();
        ReportLine interpretedLine;
        String rawLine;

        interpreter.withYear(2020)
                .withActivity("tst", "Testing", "Testing the code")
                .withActivity("Greene", "Reading", "Reading Until The End Of Time");

        rawLine = "tst 10 0900 2142";
        interpretedLine = new ReportLine(rawLine, interpreter);
        assert(interpretedLine.getRowContent("Activity").equals("Testing the code"));
        assert(interpretedLine.getRowContent("Category").equals("Testing"));
        assert(interpretedLine.getRowContent("Time allocated").equals("0.5291666666666667"));
        assert(interpretedLine.getRowContent("Efficiency").equals("10"));
        assert(interpretedLine.getRowContent("Start").equals("0.375"));
        assert(interpretedLine.getRowContent("End").equals("0.9041666666666667"));
        assert(interpretedLine.getRowContent("Day").equals("0.0"));

        rawLine = " ";
        interpretedLine = new ReportLine(rawLine, interpreter);
        assert(interpretedLine.getRowContent("Activity") == null);
        assert(interpretedLine.getRowContent("Category") == null);
        assert(interpretedLine.getRowContent("Time allocated") == null);
        assert(interpretedLine.getRowContent("Efficiency") == null);
        assert(interpretedLine.getRowContent("Start") == null);
        assert(interpretedLine.getRowContent("End") == null);
        assert(interpretedLine.getRowContent("Day") == null);
    }
}
