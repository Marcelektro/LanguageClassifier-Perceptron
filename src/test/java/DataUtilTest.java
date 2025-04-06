import com.github.marcelektro.langdetect.DataUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataUtilTest {

    @Test
    public void test_getCharFrequency_HelloWorld1() {

        // Hello, World!
        double[] helloFreq = new double[]{
                0.0, 0.0, 0.0, 0.1, 0.1, 0.0, 0.0, 0.1, 0.0, 0.0,
                0.0, 0.3, 0.0, 0.0, 0.2, 0.0, 0.0, 0.1, 0.0, 0.0,
                0.0, 0.0, 0.1, 0.0, 0.0, 0.0
        };

        assertArrayEquals(helloFreq, DataUtil.getCharFrequency("Hello, World!"));
        assertArrayEquals(helloFreq, DataUtil.getCharFrequency("hello, world!")); // same freq, should be case-insensitive
        assertArrayEquals(helloFreq, DataUtil.getCharFrequency("hello--world!")); // should ignore special chars
        assertArrayEquals(helloFreq, DataUtil.getCharFrequency("hello--&-??world!")); // should ignore special chars, so length won't matter as well

    }

    @Test
    public void test_getCharFrequency_singleChar() {

        // A
        double[] aFreq = new double[]{
                1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        };

        assertArrayEquals(aFreq, DataUtil.getCharFrequency("A"));
        assertArrayEquals(aFreq, DataUtil.getCharFrequency("a"));
        assertArrayEquals(aFreq, DataUtil.getCharFrequency("AA"));
        assertArrayEquals(aFreq, DataUtil.getCharFrequency("aa"));
        assertArrayEquals(aFreq, DataUtil.getCharFrequency("aA"));
        assertArrayEquals(aFreq, DataUtil.getCharFrequency("a????????"));
        assertArrayEquals(aFreq, DataUtil.getCharFrequency("😄a😄"));

    }

}
