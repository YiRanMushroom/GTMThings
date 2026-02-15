package com.hepdd.gtmthings.common.block.machine.trait;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import org.jetbrains.annotations.Nullable;

/**
 * Universal Energy Container that supports both EU (GTCEu) and FE/RF (Forge Energy)
 * This allows all machines to accept/output both energy types on their energy faces.
 */
public class UniversalEnergyContainer extends NotifiableEnergyContainer {

    private final LazyOptional<IEnergyStorage> forgeEnergyCapability;

    public UniversalEnergyContainer(MetaMachine machine, long maxCapacity, long maxInputVoltage,
                                    long maxInputAmperage, long maxOutputVoltage, long maxOutputAmperage) {
        super(machine, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
        this.forgeEnergyCapability = LazyOptional.of(() -> new ForgeEnergyWrapper(this));
    }

    public LazyOptional<IEnergyStorage> getForgeEnergyCapability(@Nullable Direction facing) {
        // Check if this side can input or output energy
        if (facing == null || inputsEnergy(facing) || outputsEnergy(facing)) {
            return forgeEnergyCapability;
        }
        return LazyOptional.empty();
    }

    /**
     * Wrapper to expose GTCEu's EU container as Forge Energy (FE/RF)
     */
    private static class ForgeEnergyWrapper implements IEnergyStorage {

        private final UniversalEnergyContainer container;
        // EU to FE conversion ratio (from ConverterMachine: uses FeCompat.toFeLong)
        // Typically 1 EU = 4 FE in most modpacks
        private static final int EU_TO_FE_RATIO = 4;

        public ForgeEnergyWrapper(UniversalEnergyContainer container) {
            this.container = container;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!container.inputsEnergy(null) || maxReceive <= 0) {
                return 0;
            }

            // Convert FE to EU
            long euToReceive = maxReceive / EU_TO_FE_RATIO;

            // Calculate how many amps we can accept
            long voltage = container.getInputVoltage();
            if (voltage <= 0) {
                return 0;
            }

            long maxAmperage = container.getInputAmperage();
            long currentStored = container.getEnergyStored();
            long capacity = container.getEnergyCapacity();
            long availableSpace = capacity - currentStored;

            // Limit by available space and amp limit
            long maxEuAccept = Math.min(voltage * maxAmperage, availableSpace);
            long actualEuReceived = Math.min(euToReceive, maxEuAccept);

            if (!simulate && actualEuReceived > 0) {
                container.setEnergyStored(currentStored + actualEuReceived);
            }

            // Convert back to FE
            return (int) (actualEuReceived * EU_TO_FE_RATIO);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!container.outputsEnergy(null) || maxExtract <= 0) {
                return 0;
            }

            // Convert FE to EU
            long euToExtract = maxExtract / EU_TO_FE_RATIO;

            long voltage = container.getOutputVoltage();
            if (voltage <= 0) {
                return 0;
            }

            long maxAmperage = container.getOutputAmperage();
            long currentStored = container.getEnergyStored();

            // Limit by available energy and amp limit
            long maxEuExtract = Math.min(voltage * maxAmperage, currentStored);
            long actualEuExtracted = Math.min(euToExtract, maxEuExtract);

            if (!simulate && actualEuExtracted > 0) {
                container.setEnergyStored(currentStored - actualEuExtracted);
            }

            // Convert back to FE
            return (int) (actualEuExtracted * EU_TO_FE_RATIO);
        }

        @Override
        public int getEnergyStored() {
            long euStored = container.getEnergyStored();
            return (int) Math.min(Integer.MAX_VALUE, euStored * EU_TO_FE_RATIO);
        }

        @Override
        public int getMaxEnergyStored() {
            long euCapacity = container.getEnergyCapacity();
            return (int) Math.min(Integer.MAX_VALUE, euCapacity * EU_TO_FE_RATIO);
        }

        @Override
        public boolean canExtract() {
            return container.outputsEnergy(null) && container.getEnergyStored() > 0;
        }

        @Override
        public boolean canReceive() {
            return container.inputsEnergy(null) && container.getEnergyStored() < container.getEnergyCapacity();
        }
    }
}
