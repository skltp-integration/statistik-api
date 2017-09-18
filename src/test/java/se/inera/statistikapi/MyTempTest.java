package se.inera.statistikapi;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

/**
 * Created by gorerk on 9/13/2017.
 */
public class MyTempTest {
    @Test
    public void testIsSunday() {
//        Assert.assertFalse(isSunday("2017-09-13"));
//        Assert.assertTrue(isSunday("2017-09-17"));
//        Assert.assertTrue(isSunday("2017-09-242342342"));
    }

    @Test
    public void testPercent() {
        Long totalAntal = 9L;
        Long antalError = 3L;
        BigDecimal antalFelAvTotal  = getPercent(antalError, totalAntal );
        System.out.println(antalFelAvTotal.toString());
        System.out.println(antalFelAvTotal.intValue());
    }

    BigDecimal getPercent(Long n, Long v){
        Float percent = 0f;
        if (v > 0) {
            percent = (n * 100f) / v;
        }
        return new BigDecimal(percent).setScale(2, RoundingMode.HALF_UP);
    }


}
