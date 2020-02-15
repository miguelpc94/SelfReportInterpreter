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

        SelfReport report = new SelfReport(reader);
    }
}
