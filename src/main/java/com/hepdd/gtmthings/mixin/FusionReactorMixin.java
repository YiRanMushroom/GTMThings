package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FusionReactorMachine.class)
public class FusionReactorMixin {

    @Definition(id = "getMaxVoltage", method = "Lcom/gregtechceu/gtceu/api/machine/multiblock/WorkableElectricMultiblockMachine;getMaxVoltage()J")
    @Definition(id = "min", method = "Ljava/lang/Math;min(JJ)J")
    @Expression("@(min(?, super.getMaxVoltage()))")
    @WrapOperation(method = "getMaxVoltage", at = @At("MIXINEXTRAS:EXPRESSION"), remap = false)
    private long getMaxVoltage(long a, long b, Operation<Long> original) {
        return b;
    }
}
