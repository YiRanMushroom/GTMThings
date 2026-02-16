package com.hepdd.gtmthings.utils;

import com.gregtechceu.gtceu.api.capability.compat.FeCompat;

public class GTMTUtil {

    public static int safeCastLongToInt(long v) {
        return v > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) v;
    }

    public static int safeConvertEUToFE(long eu) {
        return safeCastLongToInt(eu * FeCompat.ratio(false));
    }
}
