import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: This class will have the translation maps that in the future will be loaded from a file
//TODO: It will bee used to translate a raw string into a row content depending on the ReportLine type
public class ReportInterpreter {

    private String activity = "Activity";
    private String category = "Category";
    private String efficiency = "Efficiency";
    private String start = "Start";
    private String end = "End";
    private String day = "Day";
    private List<String> columnNames;


    public ReportInterpreter() {
        columnNames = new ArrayList<>(Arrays.asList(
                activity,
                category,
                efficiency,
                start,
                end,
                day
        ));
    }

    public List<String> getColumnNames() { return columnNames; }

    //TODO: Implement code to determine report line type
    public ReportLineTypes discoverLineType(String rawLine) {
        return ReportLineTypes.EMPTY_LINE;
    }

    public Map<String,String> interpretLine(String rawLine, ReportLineTypes lineType) {
        switch(lineType) {
            case ACTIVITY_LINE:
                return interpreteActivityLine(rawLine, lineType);
            case DATE_LINE:
                return interpreteDateLine(rawLine, lineType);
            case EMPTY_LINE:
                return interpreteEmptyLine(rawLine, lineType);
            default:
                throw new Error("Invalid report line: " + rawLine);
        }
    }

    //TODO: Method to interpret date lines
    private Map<String,String> interpreteDateLine(String rawLine, ReportLineTypes lineType) { return new HashMap<>(); }

    //TODO: Method to interpret activity line
    private Map<String,String> interpreteActivityLine(String rawLine, ReportLineTypes lineType) { return new HashMap<>(); }

    //TODO: Method to interpret empty line
    private Map<String,String> interpreteEmptyLine(String rawLine, ReportLineTypes lineType) { return new HashMap<>(); }
}
