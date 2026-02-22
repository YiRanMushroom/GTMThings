package com.hepdd.gtmthings.yiran.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.LARGE_CHEMICAL_RECIPES;

public class Recipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        // add recipe for smart assembly factory
        LARGE_CHEMICAL_RECIPES.recipeBuilder("smart_assembly_factory")
                .inputItems(new ItemStack(GTMultiMachines.ASSEMBLY_LINE.getItem(), 64))
                .inputFluids(GTMaterials.Polybenzimidazole.getFluid(144 * 64))
                .outputItems(Multiblocks.SMART_ASSEMBLY_FACTORY.getItem())
                .circuitMeta(24)
                .duration(20 * 60)
                .inputEU(GTValues.VA[GTValues.UV])
                .save(provider);
    }
}
