package com.hepdd.gtmthings.forge;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.hepdd.gtmthings.GTMThings;
import com.hepdd.gtmthings.common.block.machine.trait.UniversalEnergyContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Forge event handler to attach FE/RF energy capability to GT machines
 */
@Mod.EventBusSubscriber(modid = GTMThings.MOD_ID)
public class UniversalEnergyCapabilityProvider {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity be = event.getObject();
        Level level = be.getLevel();

        if (level == null) {
            return;
        }

        // Try to get GTCEu machine from the block entity
        MetaMachine machine = MetaMachine.getMachine(level, be.getBlockPos());

        if (machine != null) {
            // Try to get the energy container trait
            machine.getTraits().stream()
                    .filter(trait -> trait instanceof NotifiableEnergyContainer)
                    .map(trait -> (NotifiableEnergyContainer) trait)
                    .findFirst()
                    .ifPresent(energyContainer -> {
                        // Attach FE capability
                        event.addCapability(
                                GTMThings.id("universal_energy"),
                                new UniversalEnergyProvider(machine, energyContainer));
                    });
        }
    }

    /**
     * Capability provider that exposes FE energy on appropriate sides
     */
    private static class UniversalEnergyProvider implements ICapabilityProvider {

        private final MetaMachine machine;
        private final NotifiableEnergyContainer energyContainer;
        private UniversalEnergyContainer universalWrapper;

        public UniversalEnergyProvider(MetaMachine machine, NotifiableEnergyContainer energyContainer) {
            this.machine = machine;
            this.energyContainer = energyContainer;
        }

        @NotNull
        @Override
        public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ENERGY) {
                // Check if this side can input or output energy
                if (side == null || energyContainer.inputsEnergy(side) || energyContainer.outputsEnergy(side)) {
                    // Lazy initialize the universal wrapper
                    if (universalWrapper == null) {
                        universalWrapper = new UniversalEnergyContainer(
                                machine,
                                energyContainer.getEnergyCapacity(),
                                energyContainer.getInputVoltage(),
                                energyContainer.getInputAmperage(),
                                energyContainer.getOutputVoltage(),
                                energyContainer.getOutputAmperage());
                    }

                    // Sync energy before returning
                    universalWrapper.setEnergyStored(energyContainer.getEnergyStored());

                    // Return the FE capability
                    return universalWrapper.getForgeEnergyCapability(side).cast();
                }
            }
            return LazyOptional.empty();
        }
    }
}
