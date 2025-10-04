package com.quantumstudios.gtquantumcore.recipes;

import com.quantumstudios.gtquantumcore.api.unification.material.info.GTQuantumCoreMaterialFlags;
import com.quantumstudios.gtquantumcore.api.unification.material.ore.GTQuantumCoreOrePrefix;
import com.quantumstudios.gtquantumcore.item.GTQuantumCoreMetaItem;
import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.DustProperty;
import gregtech.api.unification.material.properties.ToolProperty;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import gregtech.loaders.recipe.handlers.ToolRecipeHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import static gregtech.api.unification.material.properties.PropertyKey.DUST;
import static gregtech.api.unification.material.properties.PropertyKey.TOOL;
import static gregtech.common.items.MetaItems.*;

public class GTQuantumCoreToolsRecipeHandler {

    public static void init() {
        OrePrefix.stick.addProcessingHandler(DUST, GTQuantumCoreToolsRecipeHandler::processSpindle);
        OrePrefix.bolt.addProcessingHandler(DUST, GTQuantumCoreToolsRecipeHandler::processNeedle);
        OrePrefix.ingot.addProcessingHandler(DUST, GTQuantumCoreToolsRecipeHandler::processPowerFist);
        OrePrefix.ingot.addProcessingHandler(DUST, GTQuantumCoreToolsRecipeHandler::processSledgehammer);
        OrePrefix.ingot.addProcessingHandler(DUST, GTQuantumCoreToolsRecipeHandler::processShockProd);
    }

    public static void processSpindle(OrePrefix prefix, Material material, DustProperty property) {
        if (material == null) return;
        if (!material.hasFlag(MaterialFlags.GENERATE_PLATE)) return;
        if (!material.hasFlag(MaterialFlags.GENERATE_ROD)) return;
        ToolProperty toolProp = material.getProperty(TOOL);
        if (toolProp == null) return;
        ToolRecipeHandler.addToolRecipe(
                material,
                GTQuantumCoreMetaItem.SPINDLE,
                true,
                " IE",
                " EI",
                "E  ",
                'I', new UnificationEntry(OrePrefix.plate, material),
                'E', new UnificationEntry(OrePrefix.stick, material)
        );
    }

    public static void processNeedle(OrePrefix prefix, Material material, DustProperty property) {
        if (material == null) return;
        if (!material.hasFlag(MaterialFlags.GENERATE_BOLT_SCREW)) return;
        ToolProperty toolProp = material.getProperty(TOOL);
        if (toolProp == null) return;
        ToolRecipeHandler.addToolRecipe(
                material,
                GTQuantumCoreMetaItem.NEEDLE,
                true,
                "   ",
                "fI ",
                "   ",
                'I', new UnificationEntry(OrePrefix.bolt, material)
        );
    }

    public static void processPowerFist(OrePrefix prefix, Material material, DustProperty property) {
        if (material == null) return;
        if (!material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_PISTON)) return;
        ToolProperty toolProp = material.getProperty(TOOL);
        if (toolProp == null) return;

        if (toolProp.getToolDurability() > 0) {
            ItemStack[] powerUnitsLV = {
                    POWER_UNIT_LV.getMaxChargeOverrideStack(32000L),
            };

            for (int i = 0; i < powerUnitsLV.length; i++) {
                IElectricItem powerUnit = powerUnitsLV[i].getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (powerUnit == null) continue;
                ItemStack toolItem = GTQuantumCoreMetaItem.POWER_FIST.get(material, 0, powerUnit.getMaxCharge());

                ModHandler.addShapedEnergyTransferRecipe(
                        String.format("%s_%s_%s", "power_fist", material, i),
                        toolItem,
                        Ingredient.fromStacks(powerUnitsLV[i]),
                        true,
                        true,
                        "PCP",
                        "PUP",
                        "fSh",
                        'P', new UnificationEntry(GTQuantumCoreOrePrefix.piston, material),
                        'C', MetaItems.ELECTRIC_MOTOR_LV,
                        'U', powerUnitsLV[i],
                        'S', new UnificationEntry(OrePrefix.stick, Materials.Steel)
                );
            }

            ItemStack[] powerUnitsMV = {
                    POWER_UNIT_MV.getMaxChargeOverrideStack(128000L),
            };

            for (int i = 0; i < powerUnitsMV.length; i++) {
                IElectricItem powerUnit = powerUnitsMV[i].getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (powerUnit == null) continue;
                ItemStack toolItem = GTQuantumCoreMetaItem.POWER_FIST_MV.get(material, 0, powerUnit.getMaxCharge());

                ModHandler.addShapedEnergyTransferRecipe(
                        String.format("%s_%s_%s", "power_fist_mv", material, i),
                        toolItem,
                        Ingredient.fromStacks(powerUnitsMV[i]),
                        true,
                        true,
                        "PCP",
                        "PUP",
                        "fSh",
                        'P', new UnificationEntry(GTQuantumCoreOrePrefix.piston, material),
                        'C', MetaItems.ELECTRIC_MOTOR_MV,
                        'U', powerUnitsMV[i],
                        'S', new UnificationEntry(OrePrefix.stick, Materials.Steel)
                );
            }

            ItemStack[] powerUnitsHV = {
                    POWER_UNIT_HV.getMaxChargeOverrideStack(512000L),
            };

            for (int i = 0; i < powerUnitsHV.length; i++) {
                IElectricItem powerUnit = powerUnitsHV[i].getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (powerUnit == null) continue;
                ItemStack toolItem = GTQuantumCoreMetaItem.POWER_FIST_HV.get(material, 0, powerUnit.getMaxCharge());

                ModHandler.addShapedEnergyTransferRecipe(
                        String.format("%s_%s_%s", "power_fist_hv", material, i),
                        toolItem,
                        Ingredient.fromStacks(powerUnitsHV[i]),
                        true,
                        true,
                        "PCP",
                        "PUP",
                        "fSh",
                        'P', new UnificationEntry(GTQuantumCoreOrePrefix.piston, material),
                        'C', MetaItems.ELECTRIC_MOTOR_HV,
                        'U', powerUnitsHV[i],
                        'S', new UnificationEntry(OrePrefix.stick, Materials.Steel)
                );
            }
        }
    }

    public static void processSledgehammer(OrePrefix prefix, Material material, DustProperty property) {
        if (material == null) return;
        if (!material.hasFlag(MaterialFlags.GENERATE_PLATE)) return;
        ToolProperty toolProp = material.getProperty(TOOL);
        if (toolProp == null) return;
        if (!material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_PISTON) && !material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_HOOK) && !material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_SHELL) && !material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_CURVED_PLATE)) return;
        ToolRecipeHandler.addToolRecipe(
                material,
                GTQuantumCoreMetaItem.SLEDGEHAMMER,
                true,
                "GGG",
                "GGG",
                "hIf",
                'I', new UnificationEntry(OrePrefix.stick, material),
                'G', new UnificationEntry(OrePrefix.plateDouble, material)
        );
    }

    public static void processShockProd(OrePrefix prefix, Material material, DustProperty property) {
        if (material == null) return;
        if (!material.hasFlag(MaterialFlags.GENERATE_ROD)) return;
        if (!material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_PISTON) && !material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_HOOK) && !material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_SHELL) && !material.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_CURVED_PLATE)) return;
        ToolProperty toolProp = material.getProperty(TOOL);
        if (toolProp == null) return;
        if (toolProp.getToolDurability() > 0) {
            ItemStack[] powerUnitsLV = {
                    POWER_UNIT_LV.getMaxChargeOverrideStack(16000L),
            };
            for (int i = 0; i < powerUnitsLV.length; i++) {
                IElectricItem powerUnit = powerUnitsLV[i].getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (powerUnit == null) continue;
                ItemStack toolItem = GTQuantumCoreMetaItem.SHOCK_PROD.get(material, 0, powerUnit.getMaxCharge());
                ModHandler.addShapedEnergyTransferRecipe(String.format("%s_%s_%s", "shock_prod", material, i), toolItem, Ingredient.fromStacks(powerUnitsLV[i]), true, true, " RC", "hIR", "Uf ", 'R', new UnificationEntry(OrePrefix.stick, material), 'C', MetaItems.ELECTRIC_MOTOR_LV, 'U', powerUnitsLV[i], 'I', new UnificationEntry(OrePrefix.wireGtSingle, Materials.Copper));
            }
            ItemStack[] powerUnitsMV = {
                    POWER_UNIT_MV.getMaxChargeOverrideStack(64000L),
            };
            for (int i = 0; i < powerUnitsMV.length; i++) {
                IElectricItem powerUnit = powerUnitsMV[i].getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (powerUnit == null) continue;
                ItemStack toolItem = GTQuantumCoreMetaItem.SHOCK_PROD_MV.get(material, 0, powerUnit.getMaxCharge());
                ModHandler.addShapedEnergyTransferRecipe(String.format("%s_%s_%s", "shock_prod_mv", material, i), toolItem, Ingredient.fromStacks(powerUnitsMV[i]), true, true, " RC", "hIR", "Uf ", 'R', new UnificationEntry(OrePrefix.stick, material), 'C', MetaItems.ELECTRIC_MOTOR_MV, 'U', powerUnitsMV[i], 'I', new UnificationEntry(OrePrefix.wireGtSingle, Materials.Silver));
            }
            ItemStack[] powerUnitsHV = {
                    POWER_UNIT_HV.getMaxChargeOverrideStack(256000L),
            };
            for (int i = 0; i < powerUnitsHV.length; i++) {
                IElectricItem powerUnit = powerUnitsHV[i].getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
                if (powerUnit == null) continue;
                ItemStack toolItem = GTQuantumCoreMetaItem.SHOCK_PROD_HV.get(material, 0, powerUnit.getMaxCharge());
                ModHandler.addShapedEnergyTransferRecipe(String.format("%s_%s_%s", "shock_prod_hv", material, i), toolItem, Ingredient.fromStacks(powerUnitsHV[i]), true, true, " RC", "hIR", "Uf ", 'R', new UnificationEntry(OrePrefix.stick, material), 'C', MetaItems.ELECTRIC_MOTOR_HV, 'U', powerUnitsHV[i], 'I', new UnificationEntry(OrePrefix.wireGtSingle, Materials.Electrum));
            }
        }
    }
}
