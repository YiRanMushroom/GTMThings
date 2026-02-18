package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.GTUtil;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Inspired by: https://github.com/GregTech-Intergalactical/Gregfluxology
 */
@Mixin(OverclockingLogic.class)
public interface OverclockingLogicMixin {

    @Shadow(remap = false)
    OverclockingLogic.OCResult runOverclockingLogic(OverclockingLogic.OCParams params, long maxVoltage);

    // @Shadow(remap = false)
    // static OverclockingLogic.OCResult subTickParallelOC(OverclockingLogic.@NotNull OCParams params,
    // long maxV, double durationFactor, double voltageFactor) {
    // return null;
    // }

    // /**
    // * @author Yiran
    // * @reason Always allow sub-tick parallel
    // */
    // @Overwrite(remap = false)
    // static OverclockingLogic create(double durationFactor, double voltageFactor, boolean subtick) {
    // return (params, maxV) -> subTickParallelOC(params, maxV, durationFactor, voltageFactor);
    // }

    /**
     * @author Yiran
     * @reason Always allow sub-tick parallel
     */
    @Overwrite(remap = false)
    @NotNull
    default ModifierFunction getModifier(MetaMachine machine, GTRecipe recipe,
                                         long maxVoltage, boolean shouldParallel) {
        // LOGGER.info("Calculating overclocking for recipe with max voltage {} and shouldParallel {}", maxVoltage,
        // shouldParallel);

        shouldParallel = true; // Force enable sub-tick parallel for all recipes

        long EUt = Math.abs(RecipeHelper.getRealEUt(recipe));

        int recipeTier = GTUtil.getTierByVoltage(EUt);
        int maximumTier = GTUtil.getOCTierByVoltage(maxVoltage);
        int OCs = maximumTier - recipeTier;

        if (OCs == 0) return ModifierFunction.IDENTITY;

        int maxParallels;
        if (!shouldParallel) {
            maxParallels = 1;
        } else if ((Math.pow(0.125, OCs) * recipe.duration) > 1) {
            maxParallels = 512;
        } else {
            maxParallels = ParallelLogic.getParallelAmount(machine, recipe, Integer.MAX_VALUE);
        }

        OverclockingLogic.OCParams params = new OverclockingLogic.OCParams(EUt, recipe.duration, OCs, maxParallels);
        OverclockingLogic.OCResult result = runOverclockingLogic(params, maxVoltage);
        return result.toModifier();
    }

    /**
     * @author Yiran
     * @reason change perfect OC amount logic
     */
    @Overwrite(remap = false)
    @NotNull
    static OverclockingLogic.OCResult heatingCoilOC(OverclockingLogic.OCParams params, long maxVoltage, int recipeTemp, int machineTemp) {
        double duration = params.duration();
        double eut = params.eut();
        int ocAmount = params.ocAmount();
        int maxParallels = params.maxParallels();

        // LOGGER.info("Starting heating coil OC calculation with params: " +
        // "EUt={}, duration={}, OC amount={}, max parallels={}, recipeTemp={}, machineTemp={}",
        // eut, duration, ocAmount, maxParallels, recipeTemp, machineTemp);

        double parallel = 1;
        boolean shouldParallel = false;
        int ocLevel = 0;
        double durationMultiplier = 1;

        while (ocAmount-- > 0) {
            // Check if EUt can be multiplied again without going over the max
            double potentialEUt = eut * 4.0;
            if (potentialEUt > maxVoltage) break;

            // If we're already doing parallels or our duration would go below 1, try parallels
            double dFactor = 0.125;
            if (shouldParallel || duration * dFactor < 1) {
                double pFactor = 8.0;
                double potentialParallel = parallel * pFactor;
                if (potentialParallel > maxParallels) break;
                parallel = potentialParallel;
                shouldParallel = true;
            } else {
                duration *= dFactor;
                durationMultiplier *= dFactor;
            }

            // Only set EUt after checking parallels - no need to OC if parallels would be too high
            eut = potentialEUt;
            ocLevel++;
        }

        return new OverclockingLogic.OCResult(Math.pow(4.0, ocLevel), durationMultiplier, ocLevel, (int) parallel);
    }
}
