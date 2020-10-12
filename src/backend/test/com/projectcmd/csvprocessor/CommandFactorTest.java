package backend.test.com.projectcmd.csvprocessor;

import backend.main.com.projectcmd.csvprocessor.CommandFactor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandFactorTest {

    @Test
    public void getHexStrTest() {
        assertEquals("42482c20312c2048656c6c6f20576f726c64",
                CommandFactor.getHexStr("BH", "1", "Hello World", null));
    }
}
