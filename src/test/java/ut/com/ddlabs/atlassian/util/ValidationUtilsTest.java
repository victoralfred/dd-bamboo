package ut.com.ddlabs.atlassian.util;

import static org.junit.jupiter.api.Assertions.*;

import com.ddlabs.atlassian.impl.exception.NullOrEmptyFieldsException;
import com.ddlabs.atlassian.util.ValidationUtils;
import junit.framework.TestCase;

public class ValidationUtilsTest extends TestCase {
    public void testCheckNotNullOrEmptyStrings_withValidStrings_doesNotThrow() {
        assertDoesNotThrow(() -> ValidationUtils.checkNotNullOrEmptyStrings("alpha", "beta", "gamma"));
    }
    public void testCheckNotNullOrEmptyStrings_withNull_throwsException() {
        Exception exception = assertThrows(NullOrEmptyFieldsException.class, () ->
                ValidationUtils.checkNotNullOrEmptyStrings("alpha", null, "gamma")
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("is null or empty"));
    }
    public void testCheckNotNullOrEmptyStrings_withEmptyString_throwsException() {
        Exception exception = assertThrows(NullOrEmptyFieldsException.class, () ->
                ValidationUtils.checkNotNullOrEmptyStrings("alpha", "", "gamma")
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }
    public void testCheckNotNullOrEmptyStrings_withArrayNull_throwsException() {
        assertThrows(NullOrEmptyFieldsException.class, () ->
                ValidationUtils.checkNotNullOrEmptyStrings((String[]) null)
        );
    }
    public void testCheckNotNull_withValidObjects_doesNotThrow() {
        Object obj1 = new Object();
        Object obj2 = "string";
        assertDoesNotThrow(() -> ValidationUtils.checkNotNull(obj1, obj2));
    }
    public void testCheckNotNull_withNullValue_throwsException() {
        Object obj1 = new Object();
        Object obj2 = null;
        Exception exception = assertThrows(NullOrEmptyFieldsException.class, () ->
                ValidationUtils.checkNotNull(obj1, obj2)
        );
        assertInstanceOf(NullPointerException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("is null"));
    }
    public void testCheckNotNull_withNullArray_throwsException() {
        assertThrows(NullOrEmptyFieldsException.class, () ->
                ValidationUtils.checkNotNull((Object[]) null)
        );
    }
}
