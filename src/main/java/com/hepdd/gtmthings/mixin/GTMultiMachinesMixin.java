package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.machines.GTAEMachines;
import com.gregtechceu.gtceu.common.data.machines.GTMultiMachines;

import net.minecraft.world.level.block.Block;

import com.hepdd.gtmthings.yiran.data.Multiblocks;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.stream.Stream;

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

    @WrapOperation(method = "lambda$static$52", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/machine/multiblock/PartAbility;getBlockRange(II)Ljava/util/Collection;"))
    private static Collection<Block> FusionUseAllEnergyHatches(PartAbility instance, int from, int to, Operation<Collection<Block>> original) {
        return Stream.concat(Stream.concat(original.call(instance, from, to).stream(),
                PartAbility.SUBSTATION_INPUT_ENERGY.getBlockRange(from, to).stream()),
                PartAbility.INPUT_LASER.getBlockRange(from, to).stream()).toList();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void onClinit(CallbackInfo ci) {
        Multiblocks.init();
    }
}
