package com.hepdd.gtmthings.forge;

// Inspired by Gregfluxology (DBot) - https://github.com/AmpAutomation/Gregfluxology
// MIT License

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.compat.CapabilityCompatProvider;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.common.pipelike.cable.EnergyNetHandler;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import com.hepdd.gtmthings.config.ConfigHolder;
import com.hepdd.gtmthings.utils.GTMTUtil;
import org.jetbrains.annotations.NotNull;

public class FEToEUProvider extends CapabilityCompatProvider {

    public FEToEUProvider(ICapabilityProvider upValue) {
        super(upValue);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        if (capability != ForgeCapabilities.ENERGY) {
            return LazyOptional.empty();
        }

        LazyOptional<IEnergyContainer> energyContainer = getUpvalueCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER,
                facing);
        return energyContainer.isPresent() ?
                ForgeCapabilities.ENERGY.orEmpty(capability,
                        LazyOptional.of(() -> new FEEnergyWrapper(energyContainer.resolve().get(), facing))) :
                LazyOptional.empty();
    }

    public static class FEEnergyWrapper implements IEnergyStorage {

        private final IEnergyContainer energyContainer;
        private final Direction facing;

        public FEEnergyWrapper(IEnergyContainer energyContainer, Direction facing) {
            this.energyContainer = energyContainer;
            this.facing = facing;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (maxReceive == 1 && simulate) {
                return energyContainer.getEnergyCanBeInserted() > 0L ? 1 : 0;
            }

            long maxIn = maxReceive / FeCompat.ratio(true);
            long missing = energyContainer.getEnergyCanBeInserted();

            if (missing <= 0) return 0;

            long voltage = energyContainer.getInputVoltage();
            if (voltage <= 0) return 0;

            maxIn = Math.min(missing, maxIn);
            long maxAmp = Math.min(energyContainer.getInputAmperage(), maxIn / voltage);

            if (ConfigHolder.INSTANCE.ignoreCableCapacity && energyContainer instanceof EnergyNetHandler) {
                maxIn = maxReceive / FeCompat.ratio(true);
                maxAmp = maxIn / voltage;
            }

            if (maxAmp < 1L) {
                if (maxIn <= 0) return 0;

                if (!simulate) {
                    long inserted = energyContainer.acceptEnergyFromNetwork(facing, maxIn, 1);
                    if (inserted <= 0) return 0;
                    return GTMTUtil.safeConvertEUToFE(maxIn);
                } else {
                    if (!energyContainer.inputsEnergy(facing)) return 0;
                    return GTMTUtil.safeConvertEUToFE(maxIn);
                }
            }

            if (!simulate) {
                maxAmp = energyContainer.acceptEnergyFromNetwork(facing, voltage, maxAmp);
            }

            return GTMTUtil.safeConvertEUToFE(maxAmp * voltage);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return GTMTUtil.safeConvertEUToFE(energyContainer.getEnergyStored());
        }

        @Override
        public int getMaxEnergyStored() {
            return GTMTUtil.safeConvertEUToFE(energyContainer.getEnergyCapacity());
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return energyContainer.inputsEnergy(this.facing);
        }
    }
}
