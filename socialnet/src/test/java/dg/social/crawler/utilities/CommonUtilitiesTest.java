package dg.social.crawler.utilities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link CommonUtilities} class.
 * Created by gusevdm on 3/3/2017.
 */

public class CommonUtilitiesTest {

    @Mock
    CmdLine cmdLine;

    @Before
    public void beforeTest() {
        initMocks(this);
    }

    @Test
    public void test() {
        when(this.cmdLine.hasOption("-help")).thenReturn(true);

        System.out.println(this.cmdLine.hasOption("-help"));
    }

}
