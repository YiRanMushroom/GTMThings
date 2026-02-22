package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FusionReactorMachine.class)
public abstract class FusionReactorMixin extends WorkableElectricMultiblockMachine {

    public FusionReactorMixin(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Definition(id = "FUSION_OC", field = "Lcom/gregtechceu/gtceu/common/machine/multiblock/electric/FusionReactorMachine;FUSION_OC:Lcom/gregtechceu/gtceu/api/recipe/OverclockingLogic;")
    @Definition(id = "getModifier", method = "Lcom/gregtechceu/gtceu/api/recipe/OverclockingLogic;getModifier(Lcom/gregtechceu/gtceu/api/machine/MetaMachine;Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;JZ)Lcom/gregtechceu/gtceu/api/recipe/modifier/ModifierFunction;")
    @Expression("FUSION_OC.getModifier(?, ?, ?, ?)")
    @WrapOperation(method = "recipeModifier", at = @At(value = "MIXINEXTRAS:EXPRESSION"), remap = false)
    private static ModifierFunction fusionUseOverclockingLogic(OverclockingLogic instance, MetaMachine metaMachine, GTRecipe gtRecipe, long voltage, boolean parallel, Operation<ModifierFunction> original) {
        return original.call(
                instance, metaMachine, gtRecipe,
                ((FusionReactorMachine) metaMachine).getOverclockVoltage(), true);
    }
}
