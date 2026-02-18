package com.hepdd.gtmthings.utils;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

public class Logics {

    /**
     * Calculates the maximum parallel amount that can be done for the given machine and recipe, up to the passed limit
     *
     * @param machine       machine to test against
     * @param recipe        recipe to test with
     * @param parallelLimit hard upper limit of parallels that can be done
     * @return The number of possible parallels, 0 if the recipe cannot be done
     */
    public static int originalGetParallelAmount(MetaMachine machine, GTRecipe recipe, int parallelLimit) {
        if (parallelLimit <= 1) return parallelLimit;
        if (!(machine instanceof IRecipeLogicMachine rlm)) return 1;
        // First check if we are limited by recipe inputs. This can short circuit a lot of consecutive checking
        int maxInputMultiplier = ParallelLogic.limitByInput(rlm, recipe, parallelLimit);
        if (maxInputMultiplier == 0) return 0;

        // Simulate the merging of the maximum amount of recipes that can be run with these items
        // and limit by the amount we can successfully merge
        return ParallelLogic.limitByOutputMerging(rlm, recipe, maxInputMultiplier, rlm::canVoidRecipeOutputs);
    }
}
