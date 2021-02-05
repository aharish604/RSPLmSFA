package com.rspl.sf.msfa.common;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Created by e10769 on 10-02-2017.
 */

public class CustomComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {
        BigInteger i1 = null;
        BigInteger i2 = null;
        try {
            i1 = new BigInteger(s1);
        } catch (NumberFormatException e) {
        }

        try {
            i2 = new BigInteger(s2);
        } catch (NumberFormatException e) {
        }

        if (i1 != null && i2 != null) {
            return i1.compareTo(i2);
        } else {
            return s1.compareTo(s2);
        }
    }

}
