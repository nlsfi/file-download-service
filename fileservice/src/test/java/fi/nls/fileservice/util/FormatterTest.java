package fi.nls.fileservice.util;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.junit.BeforeClass;
import org.junit.Test;

public class FormatterTest {

    static char decimalSymbol;

    @BeforeClass
    public static void initFormatSymbol() {
        // acquire locale specific decimal symbol so that test passes
        // in different environments
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance();
        decimalSymbol = formatter.getDecimalFormatSymbols()
                .getDecimalSeparator();
    }

    @Test
    public void testGetPrettySizeT() {
        assertEquals("363 t", Formatter.formatLength(363L));
    }

    @Test
    public void testGetPrettySizeKt() {
        String expected = "173" + decimalSymbol + "1 Kt";
        assertEquals(expected, Formatter.formatLength(177285L));
    }

    @Test
    public void testGetPrettySizeMt() {
        String expected = "11" + decimalSymbol + "9 Mt";
        assertEquals(expected, Formatter.formatLength(12510123L));
    }

}
