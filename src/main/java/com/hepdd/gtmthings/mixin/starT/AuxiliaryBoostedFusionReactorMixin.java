package com.hepdd.gtmthings.mixin.starT;

import com.startechnology.start_core.machine.fusion.AuxiliaryBoostedFusionReactor;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;

@Restriction(
             require = {
                     @Condition(type = Condition.Type.MOD, value = "start_core")
             })
@Mixin(AuxiliaryBoostedFusionReactor.class)
public class AuxiliaryBoostedFusionReactorMixin {
    // @Definition(id = "FUSION_OC", field =
    // "Lcom/gregtechceu/gtceu/common/machine/multiblock/electric/FusionReactorMachine;FUSION_OC:Lcom/gregtechceu/gtceu/api/recipe/OverclockingLogic;")
    // @Definition(id = "getModifier", method =
    // "Lcom/gregtechceu/gtceu/api/recipe/OverclockingLogic;getModifier(Lcom/gregtechceu/gtceu/api/machine/MetaMachine;Lcom/gregtechceu/gtceu/api/recipe/GTRecipe;JZ)Lcom/gregtechceu/gtceu/api/recipe/modifier/ModifierFunction;")
    // @Expression("FUSION_OC.getModifier(?, ?, ?, ?)")
    // @WrapOperation(method = "recipeModifier", at = @At(value = "MIXINEXTRAS:EXPRESSION"), remap = false)
    // private static ModifierFunction fusionUseOverclockingLogic(OverclockingLogic instance, MetaMachine metaMachine,
    // GTRecipe gtRecipe, long voltage, boolean parallel, Operation<ModifierFunction> original) {
    // return original.call(
    // instance, metaMachine, gtRecipe,
    // ((FusionReactorMachine) metaMachine).getOverclockVoltage(), true);
    // }
    //
    // @Definition(id = "FUSION_OC", field =
    // "Lcom/gregtechceu/gtceu/common/machine/multiblock/electric/FusionReactorMachine;FUSION_OC:Lcom/gregtechceu/gtceu/api/recipe/OverclockingLogic;")
    // @Expression("FUSION_OC = @(?)")
    // @WrapOperation(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
    // private static OverclockingLogic modifyFusionOverclockingLogic(double durationFactor, double voltageFactor,
    // boolean subtick, Operation<OverclockingLogic> original) {
    // return OverclockingLogic.PERFECT_OVERCLOCK_SUBTICK;
    // }
}
