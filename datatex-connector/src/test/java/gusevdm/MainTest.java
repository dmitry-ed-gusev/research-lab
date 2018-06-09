package gusevdm;

import gusevdm.helpers.ExitStatus;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gusevdm.helpers.CommandLineOption.OPTION_HELP;
import static gusevdm.helpers.CommandLineOption.OPTION_LOG_LEVEL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.inOrder;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Unit tests for Main class.
 * @author Serhii Hapii
 */

public class MainTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OptionParser optionParser;
    @Mock
    private Runtime      runtime;
    @Mock
    private OptionSet    optionSet;
    private Main         main;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        main = spy(new Main(optionParser, runtime));
        when(optionParser.parse(any())).thenReturn(optionSet);
        doNothing().when(main).run(optionSet);
        when(optionSet.valueOf(Matchers.<OptionSpec<String>>any())).thenReturn("test.dataset-1");
    }

    @Test
    public void run() throws Exception {
        main.run("arg");

        InOrder inOrder = inOrder(main);
        inOrder.verify(main).run(optionSet);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void runHelp() throws Exception {
        when(optionSet.has(OPTION_HELP.getName())).thenReturn(true);

        main.run("arg");

        verify(runtime).exit(ExitStatus.OK.getValue());
    }

    @Test
    public void runInvalidLogLevels() {

        // test preparation
        List<String> logLevels = Arrays.asList("trace", "TRACE", "error", "ERROR", "fatal", "FATAL", "123", "fff");
        ArgumentCaptor<Integer> exitStatusCaptor = ArgumentCaptor.forClass(Integer.class);
        when(optionSet.has(OPTION_LOG_LEVEL.getName())).thenReturn(true);

        // test executing
        logLevels.forEach(level -> {
            when(optionSet.valueOf(OPTION_LOG_LEVEL.getName())).thenReturn(level);
            main.run("arg"); // execute
        });

        // test verification
        verify(runtime, times(logLevels.size())).exit(exitStatusCaptor.capture()); // verify
        exitStatusCaptor.getAllValues().forEach(value -> assertEquals(ExitStatus.MISUSE.getValue(), (int) value));
    }

    @Test
    public void runVerifyParserArgs() throws Exception {
        main.run("arg");

        verify(optionParser).parse("arg");
    }

    @Test
    public void runOptionException() throws Exception {
        when(optionParser.parse(anyVararg())).thenThrow(new TestOptionException());

        main.run("arg");

        verify(runtime).exit(ExitStatus.MISUSE.getValue());
    }

    @Test
    public void runIllegalArgumentException() throws Exception {
        when(optionParser.parse(anyVararg())).thenThrow(new IllegalArgumentException());

        main.run("arg");

        verify(runtime).exit(ExitStatus.MISUSE.getValue());
    }

    @Test
    public void runRuntimeException() throws Exception {
        doThrow(new RuntimeException()).when(main).run(optionSet);

        main.run("arg");

        verify(runtime).exit(ExitStatus.GENERAL_ERROR.getValue());
    }

    class TestOptionException extends OptionException {
        TestOptionException() {
            super(Collections.emptyList());
        }
    }
}
