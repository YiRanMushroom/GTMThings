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

import static com.hepdd.gtmthings.GTMThings.LOGGER;

/**
 * Inspired by: https://github.com/GregTech-Intergalactical/Gregfluxology
 */
@Mixin(OverclockingLogic.class)
public interface OverclockingLogicMixin {

    @Shadow(remap = false)
    OverclockingLogic.OCResult runOverclockingLogic(OverclockingLogic.OCParams params, long maxVoltage);

    @Shadow(remap = false)
    static OverclockingLogic.OCResult subTickParallelOC(OverclockingLogic.@NotNull OCParams params,
                                                        long maxV, double durationFactor, double voltageFactor) {
        return null;
    }

    /**
     * @author Yiran
     * @reason Always allow sub-tick parallel
     */
    @Overwrite(remap = false)
    static OverclockingLogic create(double durationFactor, double voltageFactor, boolean subtick) {
        return (params, maxV) -> subTickParallelOC(params, maxV, durationFactor, voltageFactor);
    }

    /**
     * @author Yiran
     * @reason Always allow sub-tick parallel
     */
    @Overwrite(remap = false)
    @NotNull
    default ModifierFunction getModifier(MetaMachine machine, GTRecipe recipe,
                                         long maxVoltage, boolean shouldParallel) {
        LOGGER.info("Calculating overclocking for recipe with max voltage {} and shouldParallel {}", maxVoltage, shouldParallel);

        long EUt = Math.abs(RecipeHelper.getRealEUt(recipe));

        int recipeTier = GTUtil.getTierByVoltage(EUt);
        int maximumTier = GTUtil.getOCTierByVoltage(maxVoltage);
        int OCs = maximumTier - recipeTier;

        if (OCs == 0) return ModifierFunction.IDENTITY;

        int maxParallels;
        if (!shouldParallel) {
            maxParallels = 1;
        } else if ((Math.pow(OverclockingLogic.PERFECT_DURATION_FACTOR, OCs) * recipe.duration) > 1) {
            maxParallels = 512;
        } else {
            maxParallels = ParallelLogic.getParallelAmount(machine, recipe, Integer.MAX_VALUE);
        }

        OverclockingLogic.OCParams params = new OverclockingLogic.OCParams(EUt, recipe.duration, OCs, maxParallels);
        OverclockingLogic.OCResult result = runOverclockingLogic(params, maxVoltage);
        return result.toModifier();
    }
}
