import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


// Translate a raw string into a interpreted row content depending on the ReportLine type
public class ReportInterpreter {

    private List<InterpreterNotification> notifications;
    private List<String> columnNames;
    private Map<String,Integer> monthCodeToIntMap;
    private Map<String,String> activityCodeToActivityValueMap;
    private Map<String,String> activityCodeToCategoryValueMap;
    private int yearValue;
    public static String activity = "Activity";
    public static String category = "Category";
    public static String timeAllocated = "Time allocated";
    public static String efficiency = "Efficiency";
    public static String start = "Start";
    public static String end = "End";
    public static String day = "Day";

    private char dashChar = '-';

    public ReportInterpreter() {
        notifications = new ArrayList<>();
        activityCodeToActivityValueMap = new HashMap<>();
        activityCodeToCategoryValueMap = new HashMap<>();
        monthCodeToIntMap = new HashMap<>();
        columnNames = new ArrayList<>(Arrays.asList(
                activity,
                category,
                timeAllocated,
                efficiency,
                start,
                end,
                day
        ));
    }

    public ReportInterpreter withConfigurationFile(String fileName) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(fileName))
        {
            Object configFile = jsonParser.parse(reader);
            JSONObject configurations = (JSONObject) configFile;

            Long year = (Long) configurations.get("year");
            this.withYear(year.intValue());

            JSONObject months = (JSONObject) configurations.get("months");
            for (Object month :  months.keySet()) {
                Long monthNumber = (Long) months.get(month);
                this.withMonthCodeFor((String) month, monthNumber.intValue());
            }

            JSONObject activities = (JSONObject) configurations.get("activities");
            for (Object activityCode :  activities.keySet()) {
                JSONArray categoryAndValue = (JSONArray) activities.get(activityCode);
                this.withActivity((String) activityCode, (String)categoryAndValue.get(0), (String) categoryAndValue.get(1));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ReportInterpreter withYear(int yearValue) {
        this.yearValue = yearValue;
        return this;
    }

    public ReportInterpreter withMonthCodeFor(String monthCode, int monthInt) {
        this.monthCodeToIntMap.put( monthCode, monthInt);
        return this;
    }

    public ReportInterpreter withActivity(String activityCode, String categoryValue, String activityValue) {
        this.activityCodeToActivityValueMap.put(activityCode,activityValue);
        activityCodeToCategoryValueMap.put(activityCode,categoryValue);
        return this;
    }

    public List<String> getColumnNames() { return columnNames; }

    public ReportLineTypes discoverLineType(String rawLine) {
        if (isActivityLine(rawLine)) return ReportLineTypes.ACTIVITY_LINE;
        if (isDateLine(rawLine)) return ReportLineTypes.DATE_LINE;
        if (isNoActivityLine(rawLine)) return ReportLineTypes.NO_ACTIVITY_LINE;
        if (isEmpyLine(rawLine)) return ReportLineTypes.EMPTY_LINE;
        return ReportLineTypes.INVALID_LINE;
    }

    public boolean isActivityLine(String rawLine) {
        if (rawLine.length() < 13) return false;
        if (isDateLine(rawLine)) return false;
        if (isNoActivityLine(rawLine)) return false;
        return true;
    }

    public boolean isDateLine(String rawLine) {
        if (rawLine.length()<3) return false;
        if (countDashes(rawLine) != 1) return false;
        return true;
    }

    public boolean isEmpyLine(String rawLine) {
        if (rawLine.length()==0) return true;
        String cleanLine = cleanRawDateLine(rawLine);
        return cleanLine.length()==0;
    }

    public boolean isNoActivityLine(String rawLine) {
        return countDashes(rawLine) >= 2;
    }

    public int countDashes(String rawLine) {
        int dashCount = 0;
        for (char ch:rawLine.toCharArray()) {
            if (ch == dashChar) dashCount++;
        }
        return dashCount;
    }

    public Map<String,String> interpretLine(String rawLine, ReportLineTypes lineType) {
        switch(lineType) {
            case ACTIVITY_LINE:
                return interpretActivityLine(rawLine);
            case DATE_LINE:
                return interpretDateLine(rawLine);
            case NO_ACTIVITY_LINE:
                return interpretNoActivityLine();
            case EMPTY_LINE:
                return null;
            default:
                notify(NotificationTypes.ERROR, "Invalid line: "+rawLine);
                return null;
        }
    }

    public boolean areThereNotifications() { return notifications.size() > 0; }

    public void cleanNotifications() { notifications = new ArrayList<>(); }

    public List<InterpreterNotification> getNotifications() { return notifications; }

    private void notify(NotificationTypes type, String message) {
        notifications.add(
                new InterpreterNotification(type, message)
        );
    }

    private String cleanRawDateLine(String rawLine) {
        return rawLine.replaceAll("\n", "").replaceAll(" ", "");
    }

    private int mapMonthCodeToInt(String monthCode) { return monthCodeToIntMap.get(monthCode); }

    private Map<String,String> interpretDateLine(String rawLine) {
        String[] dayAndMonth = cleanRawDateLine(rawLine).split("-");

        int dayValue;
        int monthValue;
        LocalDate date;
        try {
            dayValue = Integer.parseInt(dayAndMonth[0]);
            monthValue = mapMonthCodeToInt(dayAndMonth[1]);
            date = LocalDate.of(yearValue, monthValue, dayValue);
        } catch(Exception e) {
            notify(NotificationTypes.ERROR, "Invalid date line: "+rawLine);
            return null;
        }

        String excelDate = Double.toString(DateUtil.getExcelDate(date));
        Map<String,String> rowContent = new HashMap<>();
        rowContent.put(activity, "");
        rowContent.put(category, "");
        rowContent.put(timeAllocated, "");
        rowContent.put(efficiency, "");
        rowContent.put(start, excelDate);
        rowContent.put(end, excelDate);
        rowContent.put(day, excelDate);
        return rowContent;
    }

    private String cleanRawActivityLine(String rawLine) {
        return rawLine.replaceAll("\n", "");
    }

    List<String> parseRawActivityLine(String rawLine) {
        List<String> activityLineValues = new ArrayList<>();
        String[] splitRawLine = cleanRawActivityLine(rawLine).split(" ");
        for (String splitValue:splitRawLine) {
            if (!splitValue.contentEquals("")) activityLineValues.add(splitValue);
        }
        return activityLineValues;
    }

    private String mapActivityCodeToActivityValue(String activityCode) {
        String activityValue = activityCodeToActivityValueMap.get(activityCode);
        if (activityValue==null) {
            notify(NotificationTypes.WARNING, "Activity code does not have a value: "+activityCode);
        }
        return activityValue;
    }

    private String mapActivityCodeToCategoryValue(String activityCode) {
        String categoryValue = activityCodeToCategoryValueMap.get(activityCode);
        if (categoryValue==null) {
            notify(NotificationTypes.WARNING, "Activity code does not have a category: "+activityCode);
        }
        return categoryValue;
    }

    private double activityLineHourToExcelHour(String activityLineHour) {
        if (activityLineHour.length() != 4) {
            notify(NotificationTypes.ERROR, "Invalid activity hour: "+activityLineHour);
            return 0.0;
        }
        double hour = Double.parseDouble(activityLineHour.substring(0,2));
        double minute = Double.parseDouble(activityLineHour.substring(2,4));
        if (hour<0 || hour>23 || minute<0 || minute>59) {
            notify(NotificationTypes.ERROR, "Invalid activity hour: "+activityLineHour);
            return 0.0;
        }
        double excelHour = hour / DateUtil.HOURS_PER_DAY;
        double excelMinute = minute / ( DateUtil.MINUTES_PER_HOUR * DateUtil.HOURS_PER_DAY);
        return excelHour + excelMinute;
    }

    private Map<String,String> interpretActivityLine(String rawLine) {
        List<String> activityLineValues =  parseRawActivityLine(rawLine);

        String activityValue;
        String categoryValue;
        int efficiencyValue;
        Double startValue;
        Double endValue;
        double timeAllocatedValue;
        try {
            activityValue = mapActivityCodeToActivityValue(activityLineValues.get(0));
            categoryValue = mapActivityCodeToCategoryValue(activityLineValues.get(0));
            efficiencyValue = Integer.parseInt(activityLineValues.get(1));
            startValue = activityLineHourToExcelHour(activityLineValues.get(2));
            endValue = activityLineHourToExcelHour(activityLineValues.get(3));
            timeAllocatedValue = endValue - startValue;
        } catch(Exception e) {
            notify(NotificationTypes.ERROR, "Invalid activity line: "+rawLine);
            return null;
        }

        Map<String,String> rowContent = new HashMap<>();
        rowContent.put(activity, activityValue);
        rowContent.put(category, categoryValue);
        rowContent.put(timeAllocated, Double.toString(timeAllocatedValue));
        rowContent.put(efficiency, Integer.toString(efficiencyValue));
        rowContent.put(start, startValue.toString());
        rowContent.put(end, endValue.toString());
        rowContent.put(day, "0.0");
        return rowContent;
    }

    private Map<String,String> interpretNoActivityLine() {
        Map<String,String> rowContent = new HashMap<>();
        rowContent.put(activity, "");
        rowContent.put(category, "General");
        rowContent.put(timeAllocated, "0.0");
        rowContent.put(efficiency, "0");
        rowContent.put(start, "0.0");
        rowContent.put(end, "0.0");
        rowContent.put(day, "0.0");
        return rowContent;
    }
}
