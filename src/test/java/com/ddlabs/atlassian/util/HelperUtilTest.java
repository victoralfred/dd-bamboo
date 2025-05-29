package com.ddlabs.atlassian.util;

import static org.junit.jupiter.api.Assertions.*;

import com.ddlabs.atlassian.util.exceptions.NullOrEmptyFieldsException;
import junit.framework.TestCase;

public class HelperUtilTest extends TestCase {
    public void testCheckNotNullOrEmptyStrings_withValidStrings_doesNotThrow() {
        assertDoesNotThrow(() -> HelperUtil.checkNotNullOrEmptyStrings("alpha", "beta", "gamma"));
    }
    public void testCheckNotNullOrEmptyStrings_withNull_throwsException() {
        Exception exception = assertThrows(NullOrEmptyFieldsException.class, () ->
                HelperUtil.checkNotNullOrEmptyStrings("alpha", null, "gamma")
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("is null or empty"));
    }
    public void testCheckNotNullOrEmptyStrings_withEmptyString_throwsException() {
        Exception exception = assertThrows(NullOrEmptyFieldsException.class, () ->
                HelperUtil.checkNotNullOrEmptyStrings("alpha", "", "gamma")
        );
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }
    public void testCheckNotNullOrEmptyStrings_withArrayNull_throwsException() {
        assertThrows(NullOrEmptyFieldsException.class, () ->
                HelperUtil.checkNotNullOrEmptyStrings((String[]) null)
        );
    }
    public void testCheckNotNull_withValidObjects_doesNotThrow() {
        Object obj1 = new Object();
        Object obj2 = "string";
        assertDoesNotThrow(() -> HelperUtil.checkNotNull(obj1, obj2));
    }
    public void testCheckNotNull_withNullValue_throwsException() {
        Object obj1 = new Object();
        Object obj2 = null;
        Exception exception = assertThrows(NullOrEmptyFieldsException.class, () ->
                HelperUtil.checkNotNull(obj1, obj2)
        );
        assertInstanceOf(NullPointerException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("is null"));
    }
    public void testCheckNotNull_withNullArray_throwsException() {
        assertThrows(NullOrEmptyFieldsException.class, () ->
                HelperUtil.checkNotNull((Object[]) null)
        );
    }
}
