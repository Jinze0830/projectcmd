package backend.test.com.projectcmd.csvprocessor;

import backend.main.com.projectcmd.csvprocessor.CommandFactor;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class CommandFactorTest {

    @Test
    public void getHexStrTest() throws UnsupportedEncodingException {
        assertEquals("42482c322c414243440d",
                CommandFactor.getHexStr("BH", "2", "ABCD"));
    }
}
