package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GTMultiMachines.class)
public class GTMultiMachinesMixin {

    @Definition(id = "where", method = "Lcom/gregtechceu/gtceu/api/pattern/FactoryBlockPattern;where(CLcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;)Lcom/gregtechceu/gtceu/api/pattern/FactoryBlockPattern;")
    @Expression("?.where('I', @(?))")
    @ModifyExpressionValue(method = "lambda$static$43", at = @At(value = "MIXINEXTRAS:EXPRESSION"), remap = false)
    private static TraceabilityPredicate addAEBusPattern(TraceabilityPredicate original) {
        return original.or(Predicates.blocks(GTAEMachines.STOCKING_IMPORT_BUS_ME.getBlock(),
                GTAEMachines.ITEM_IMPORT_BUS_ME.getBlock()));
    }

    @Definition(id = "where", method = "Lcom/gregtechceu/gtceu/api/pattern/FactoryBlockPattern;where(CLcom/gregtechceu/gtceu/api/pattern/TraceabilityPredicate;)Lcom/gregtechceu/gtceu/api/pattern/FactoryBlockPattern;")
    @Expression("?.where('F', @(?))")
    @ModifyExpressionValue(method = "lambda$static$43", at = @At(value = "MIXINEXTRAS:EXPRESSION"), remap = false)
    private static TraceabilityPredicate addAEHatchPattern(TraceabilityPredicate original) {
        return original.or(Predicates.blocks(GTAEMachines.STOCKING_IMPORT_HATCH_ME.getBlock(),
                GTAEMachines.FLUID_IMPORT_HATCH_ME.getBlock()));
    }
}
