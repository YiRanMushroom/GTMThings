package com.hepdd.gtmthings.init;

import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static com.hepdd.gtmthings.GTMThings.LOGGER;

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
            LOGGER.info("Patching OverclockingLogic fields to enable sub-tick parallel and modify duration factors...");

            // double fields are already inlined, cannot modify them so fuck that.

            // setStaticDoubleField(OverclockingLogic.class, "STD_DURATION_FACTOR", 0.25);
            // setStaticDoubleField(OverclockingLogic.class, "STD_DURATION_FACTOR_INV", 4.0);
            //
            // setStaticDoubleField(OverclockingLogic.class, "PERFECT_DURATION_FACTOR", 0.125);
            // setStaticDoubleField(OverclockingLogic.class, "PERFECT_DURATION_FACTOR_INV", 8.0);
            //
            // setStaticDoubleField(OverclockingLogic.class, "PERFECT_HALF_DURATION_FACTOR", 0.25);
            // setStaticDoubleField(OverclockingLogic.class, "PERFECT_HALF_DURATION_FACTOR_INV", 4.0);
            //
            // setStaticDoubleField(OverclockingLogic.class, "STD_VOLTAGE_FACTOR", 4.0);
            // setStaticDoubleField(OverclockingLogic.class, "PERFECT_HALF_VOLTAGE_FACTOR", 2.0);
            //
            // LOGGER.info("Double field STD_DURATION_FACTOR is now {}", OverclockingLogic.STD_DURATION_FACTOR);
            // LOGGER.info("Double field PERFECT_DURATION_FACTOR is now {}", OverclockingLogic.PERFECT_DURATION_FACTOR);
            // LOGGER.info("Double field STD_VOLTAGE_FACTOR is now {}", OverclockingLogic.STD_VOLTAGE_FACTOR);
            // LOGGER.info("Double field PERFECT_HALF_VOLTAGE_FACTOR is now {}",
            // OverclockingLogic.PERFECT_HALF_VOLTAGE_FACTOR);

            double newStdDuration = 0.25;
            double newPerfectDuration = 0.125;
            double stdVoltage = 4.0;

            setStaticField(OverclockingLogic.class, "PERFECT_OVERCLOCK",
                    OverclockingLogic.create(newPerfectDuration, stdVoltage, false));

            setStaticField(OverclockingLogic.class, "NON_PERFECT_OVERCLOCK",
                    OverclockingLogic.create(newStdDuration, stdVoltage, false));

            setStaticField(OverclockingLogic.class, "PERFECT_OVERCLOCK_SUBTICK",
                    OverclockingLogic.create(newPerfectDuration, stdVoltage, true));

            setStaticField(OverclockingLogic.class, "NON_PERFECT_OVERCLOCK_SUBTICK",
                    OverclockingLogic.create(newStdDuration, stdVoltage, true));

            LOGGER.info("Successfully patched OverclockingLogic fields!");
            LOGGER.info("New duration factors: STD={}, PERFECT={}", newStdDuration, newPerfectDuration);
        } catch (Exception e) {
            LOGGER.error("Failed to patch OverclockingLogic fields", e);
            throw new RuntimeException("Critical: OverclockingLogic patching failed", e);
        }
    }

    private static void setStaticField(Class<?> clazz, String fieldName, Object newValue) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);

        Object base = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);

        UNSAFE.putObject(base, offset, newValue);

        LOGGER.info("Set {} = {}", fieldName, newValue);
    }

    private static void setStaticDoubleField(Class<?> clazz, String fieldName, double newValue) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);

        Object base = UNSAFE.staticFieldBase(field);
        long offset = UNSAFE.staticFieldOffset(field);

        UNSAFE.putDouble(base, offset, newValue);

        LOGGER.info("Set {} = {}", fieldName, newValue);
    }
}
