import org.junit.Test;

import java.util.ArrayList;


public class AppTest
{
    private String[] testFile = new String[] {"src/test/resources/report.txt"};

    @Test
    public void shouldPrintFileContent() throws Exception
    {
        App.main(testFile);
        assert(true);
    }

}