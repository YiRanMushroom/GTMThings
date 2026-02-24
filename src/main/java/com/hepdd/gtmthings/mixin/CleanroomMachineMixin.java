package com.hepdd.gtmthings.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MufflerPartMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CleanroomMachine.class)
public class CleanroomMachineMixin {

    @WrapMethod(method = "isMachineBanned", remap = false)
    private boolean gtmthings$allowCleanroomMachine(MetaMachine machine, Operation<Boolean> original) {
        if (machine instanceof MufflerPartMachine) {
            return false;
        } else {
            return original.call(machine);
        }
    }
}
