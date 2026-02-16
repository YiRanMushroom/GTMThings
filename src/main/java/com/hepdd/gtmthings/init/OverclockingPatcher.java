package com.hepdd.gtmthings.init;

import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;

import com.hepdd.gtmthings.GTMThings;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * Patches OverclockingLogic interface static fields after mixin stage completes.
 * This is necessary because interface mixins cannot modify static final fields during bytecode injection.
 *
 * Inspired by: https://github.com/GregTech-Intergalactical/Gregfluxology
 */
public class OverclockingPatcher {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            UNSAFE = (Unsafe) unsafeField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }

    public static void init() {
        try {
            GTMThings.LOGGER.info("Patching OverclockingLogic fields to enable sub-tick parallel and modify duration factors...");

            setStaticDoubleField(OverclockingLogic.class, "STD_DURATION_FACTOR", 0.25 / 2.0);
            setStaticDoubleField(OverclockingLogic.class, "STD_DURATION_FACTOR_INV", 4.0 * 2.0);

            setStaticDoubleField(OverclockingLogic.class, "PERFECT_DURATION_FACTOR", 0.125 / 2.0);
            setStaticDoubleField(OverclockingLogic.class, "PERFECT_DURATION_FACTOR_INV", 8.0 * 2.0);

            setStaticDoubleField(OverclockingLogic.class, "PERFECT_HALF_DURATION_FACTOR", 0.25 / 2.0);
            setStaticDoubleField(OverclockingLogic.class, "PERFECT_HALF_DURATION_FACTOR_INV", 4.0 * 2.0);

            setStaticDoubleField(OverclockingLogic.class, "STD_VOLTAGE_FACTOR", 4.0);
            setStaticDoubleField(OverclockingLogic.class, "PERFECT_HALF_VOLTAGE_FACTOR", 2.0);

            double newStdDuration = 0.25 / 2.0;
            double newPerfectDuration = 0.125 / 2.0;
            double stdVoltage = 4.0;

            setStaticField(OverclockingLogic.class, "PERFECT_OVERCLOCK",
                    OverclockingLogic.create(newPerfectDuration, stdVoltage, true));

            setStaticField(OverclockingLogic.class, "NON_PERFECT_OVERCLOCK",
                    OverclockingLogic.create(newStdDuration, stdVoltage, true));

            setStaticField(OverclockingLogic.class, "PERFECT_OVERCLOCK_SUBTICK",
                    OverclockingLogic.create(newPerfectDuration, stdVoltage, true));

            setStaticField(OverclockingLogic.class, "NON_PERFECT_OVERCLOCK_SUBTICK",
                    OverclockingLogic.create(newStdDuration, stdVoltage, true));

            GTMThings.LOGGER.info("Successfully patched OverclockingLogic fields!");
            GTMThings.LOGGER.info("New duration factors: STD={}, PERFECT={}", newStdDuration, newPerfectDuration);
        } catch (Exception e) {
            GTMThings.LOGGER.error("Failed to patch OverclockingLogic fields", e);
            throw new RuntimeException("Critical: OverclockingLogic patching failed", e);
        }
    }

    private static void setStaticField(Class<?> clazz, String fieldName, Object newValue) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);

        Object base = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);

        UNSAFE.putObject(base, offset, newValue);

        GTMThings.LOGGER.info("Set {} = {}", fieldName, newValue);
    }

    private static void setStaticDoubleField(Class<?> clazz, String fieldName, double newValue) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);

        Object base = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);

        UNSAFE.putDouble(base, offset, newValue);

        GTMThings.LOGGER.debug("Set {} = {}", fieldName, newValue);
    }
}
