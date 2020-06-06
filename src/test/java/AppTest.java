import org.junit.Test;

import java.io.File;


public class AppTest
{
    private String testFile = "src/test/resources/report.txt";
    private String configFile = "src/test/resources/config.json";
    private String outputFile = "src/test/resources/output.csv";

    private boolean outputFileExists() {
        File output = new File(outputFile);
        return(output.exists());
    }

    private void deleteOutputFile() {
        File output = new File(outputFile);
        output.delete();
    }

    @Test
    public void shouldNotThrowException() throws Exception
    {
        deleteOutputFile();
        assert !outputFileExists();

        // If App throw an exception the test fails
        try {
            App.runWithFiles(testFile,configFile, outputFile);
        } catch (Exception e) {
            assert(false);
        }

        assert outputFileExists();
        deleteOutputFile();
        assert !outputFileExists();
    }

}