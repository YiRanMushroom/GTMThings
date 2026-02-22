package com.hepdd.gtmthings.yiran.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.hepdd.gtmthings.common.registry.GTMTRegistration;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;
import static com.gregtechceu.gtceu.api.pattern.Predicates.dataHatchPredicate;
import static com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.*;
import static com.gregtechceu.gtceu.common.data.GTBlocks.CASING_GRATE;

public class Multiblocks {

    public static MultiblockMachineDefinition SMART_ASSEMBLY_FACTORY;

    public static void init() {
        SMART_ASSEMBLY_FACTORY = GTMTRegistration.GTMTHINGS_REGISTRATE.multiblock("smart_assembly_factory", WorkableElectricMultiblockMachine::new)
                .rotationState(RotationState.ALL)
                .recipeType(GTRecipeTypes.ASSEMBLY_LINE_RECIPES)
                .recipeModifiers(GTRecipeModifiers.OC_PERFECT_SUBTICK)
                .appearanceBlock(CASING_STEEL_SOLID)
                .pattern(definition -> FactoryBlockPattern.start(BACK, UP, RIGHT) // 7 * 7 * 11
                        .aisle("XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX") // first
                                                                                                            // layer
                        .aisle("XXXXXXX", "RTTTTTR", "RAAAAAR", "GTTTTTG", "RAAAAAR", "RTTTTTR", "XXXXXXX").setRepeatable(4)
                        // middle:
                        .aisle("SXXXXXX", "RTTTTTR", "RAAAAAR", "GTTTTTG", "RAAAAAR", "RTTTTTR", "XXXXXXX")
                        .aisle("XXXXXXX", "RTTTTTR", "RAAAAAR", "GTTTTTG", "RAAAAAR", "RTTTTTR", "XXXXXXX").setRepeatable(4)
                        .aisle("XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX", "XXXXXXX") // last
                                                                                                            // layer
                        .where('S', Predicates.controller(blocks(definition.getBlock())))
                        .where('X', blocks(CASING_STEEL_SOLID.get())
                                .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                .or(Predicates.autoAbilities(true, false, true))
                                .or(dataHatchPredicate(blocks(CASING_GRATE.get()))))
                        .where('G', blocks(CASING_GRATE.get()))
                        .where('A', blocks(CASING_ASSEMBLY_CONTROL.get()))
                        .where('R', blocks(CASING_LAMINATED_GLASS.get()))
                        .where('T', blocks(CASING_ASSEMBLY_LINE.get()))
                        .where('#', Predicates.any())
                        .build())
                .workableCasingRenderer(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                        GTCEu.id("block/multiblock/assembly_line"))
                .register();
    }
}
