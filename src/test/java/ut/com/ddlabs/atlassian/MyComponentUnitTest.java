package ut.com.ddlabs.atlassian;

import org.junit.Test;
import com.ddlabs.atlassian.api.MyPluginComponent;
import com.ddlabs.atlassian.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest {
    @Test
    public void testMyName() {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent", component.getName());
    }
}