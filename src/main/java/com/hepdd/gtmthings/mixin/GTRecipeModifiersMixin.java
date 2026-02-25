package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GTRecipeModifiers.class)
public class GTRecipeModifiersMixin {

    // @Inject(method = "multiSmelterParallel", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    // @Definition(id = "builderFunction", method =
    // "Lcom/gregtechceu/gtceu/api/recipe/modifier/ModifierFunction;builder()Lcom/gregtechceu/gtceu/api/recipe/modifier/ModifierFunction$FunctionBuilder;")
    // @Expression("@(builderFunction())")
    // private static void multiSmelterParallel(MetaMachine machine, GTRecipe recipe,
    // CallbackInfoReturnable<ModifierFunction> cir,
    // @Local(name = "parallels") LocalIntRef parallels,
    // @Local(name = "baseModifier") ModifierFunction baseModifier,
    // @Local(name = "ocModifier") ModifierFunction ocModifier) {
    // parallels.set(parallels.get() / baseModifier.andThen(ocModifier).apply(recipe).parallels);
    // }

    @Definition(id = "apply", method = "Lcom/gregtechceu/gtceu/api/recipe/modifier/ModifierFunction;apply(Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;)Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;")
    @Definition(id = "recipe", local = @Local(name = "recipe", type = GTRecipe.class))
    @Definition(id = "baseModifier", local = @Local(name = "baseModifier", type = ModifierFunction.class))
    @Expression("baseModifier.apply(recipe)")
    @Inject(method = "multiSmelterParallel", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false, cancellable = true)
    private static void multiSmelterParallel(MetaMachine machine, GTRecipe recipe,
                                             CallbackInfoReturnable<ModifierFunction> cir,
                                             @Local(name = "baseModifier") ModifierFunction baseModifier) {
        cir.setReturnValue(baseModifier.andThen(OverclockingLogic.PERFECT_OVERCLOCK_SUBTICK
                .getModifier(machine, recipe, ((CoilWorkableElectricMultiblockMachine) machine).getOverclockVoltage())));
    }
}
