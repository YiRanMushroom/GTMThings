package com.hepdd.gtmthings.forge;

// Inspired by Gregfluxology (DBot) - https://github.com/AmpAutomation/Gregfluxology
// MIT License

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import com.hepdd.gtmthings.GTMThings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * FE -> EU capability bridge.
 * Attach to GT machine BlockEntities to expose IEnergyStorage (FE) capability
 * that proxies to GT's IEnergyContainer (EU).
 */
public class FEToEUProvider implements ICapabilityProvider {

    public static final ResourceLocation CAP_ID = GTMThings.id("fecapability");

    private final BlockEntity blockEntity;

    public FEToEUProvider(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap != ForgeCapabilities.ENERGY) {
            return LazyOptional.empty();
        }

        // Dynamically get the GT energy container at query time (not at attach time)
        LazyOptional<IEnergyContainer> gtCap = blockEntity.getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, side);

        return gtCap.map(container -> {
            // Only provide FE cap if GT container can input energy on this side
            if (container.inputsEnergy(side)) {
                return ForgeCapabilities.ENERGY.<T>orEmpty(cap, LazyOptional.of(() -> new FEEnergyWrapper(container, side)));
            }
            return LazyOptional.<T>empty();
        }).orElse(LazyOptional.empty());
    }

    /**
     * Wrapper to expose GT's IEnergyContainer as Forge's IEnergyStorage
     */
    private static class FEEnergyWrapper implements IEnergyStorage {

        private final IEnergyContainer energyContainer;
        @Nullable
        private final Direction facing;

        private FEEnergyWrapper(IEnergyContainer energyContainer, @Nullable Direction facing) {
            this.energyContainer = energyContainer;
            this.facing = facing;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!canReceive()) {
                return 0;
            }

            // Mekanism compatibility: probe with 1 FE
            if (maxReceive == 1 && simulate) {
                return energyContainer.getEnergyCanBeInserted() > 0L ? 1 : 0;
            }

            // Convert FE to EU
            long maxInEu = FeCompat.toEu(maxReceive, FeCompat.ratio(true));
            long missing = energyContainer.getEnergyCanBeInserted();
            long voltage = energyContainer.getInputVoltage();
            if (voltage <= 0) {
                return 0;
            }

            maxInEu = Math.min(missing, maxInEu);
            long maxAmp = Math.min(energyContainer.getInputAmperage(), maxInEu / voltage);
            if (maxAmp < 1L) {
                return 0;
            }

            if (!simulate) {
                // Use acceptEnergyFromNetwork to properly go through GT's energy network logic
                maxAmp = energyContainer.acceptEnergyFromNetwork(facing, voltage, maxAmp);
            }

            // Convert EU back to FE for return value
            return FeCompat.toFe(maxAmp * voltage, FeCompat.ratio(false));
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            // FE extraction is handled by GT itself, we only handle FE input
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return FeCompat.toFe(energyContainer.getEnergyStored(), FeCompat.ratio(false));
        }

        @Override
        public int getMaxEnergyStored() {
            return FeCompat.toFe(energyContainer.getEnergyCapacity(), FeCompat.ratio(false));
        }

        @Override
        public boolean canExtract() {
            // FE extraction handled by GT itself
            return false;
        }

        @Override
        public boolean canReceive() {
            return energyContainer.inputsEnergy(facing);
        }
    }
}
