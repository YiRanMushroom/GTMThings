package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ParallelLogic.class)
public class ParallelLogicMixin {

    /**
     * @author Yiran
     * @reason Disable this check so we always use sub-tick parallel
     */
    @Overwrite(remap = false)
    public static int getParallelAmount(MetaMachine machine, GTRecipe recipe, int parallelLimit) {
        return Math.min(parallelLimit, 1);
    }
}
