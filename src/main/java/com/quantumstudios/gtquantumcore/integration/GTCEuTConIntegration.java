package com.quantumstudios.gtquantumcore.integration;

import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.material.properties.ToolProperty;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.core.unification.material.internal.MaterialRegistryManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.MaterialRenderInfo;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;

public class GTCEuTConIntegration {

    public static void registerAllMaterials() {
        int registered = 0;

        for (Material material : MaterialRegistryManager.getInstance().getRegisteredMaterials()) {
            if (material.hasProperty(PropertyKey.TOOL)) {
                try {
                    registerTiConMaterial(material);
                    registered++;
                } catch (Exception e) {
                    System.err.println("Failed to register TiCon material for: " + material.toString());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("GTCEu-TiCon Integration: Registered " + registered + " materials");
    }

    @SideOnly(Side.CLIENT)
    public static void registerMaterialRenderInfo() {
        for (Material material : MaterialRegistryManager.getInstance().getRegisteredMaterials()) {
            if (material.hasProperty(PropertyKey.TOOL)) {
                String materialId = "gtceu_" + material.toString().toLowerCase();
                slimeknights.tconstruct.library.materials.Material tconMat = TinkerRegistry.getMaterial(materialId);

                if (tconMat != null && !tconMat.equals(slimeknights.tconstruct.library.materials.Material.UNKNOWN)) {
                    // Set render info with the material's color
                    tconMat.setRenderInfo(material.getMaterialRGB());
                }
            }
        }
    }

    private static void registerTiConMaterial(Material gtMaterial) {
        ToolProperty toolProp = gtMaterial.getProperty(PropertyKey.TOOL);

        String materialId = "gtceu_" + gtMaterial.toString().toLowerCase();

        // Check if already registered
        if (!TinkerRegistry.getMaterial(materialId).equals(slimeknights.tconstruct.library.materials.Material.UNKNOWN)) {
            return;
        }

        // Create material with color
        slimeknights.tconstruct.library.materials.Material tconMat =
                new slimeknights.tconstruct.library.materials.Material(materialId,
                        gtMaterial.getMaterialRGB());

        // Get representative item
        ItemStack representativeItem = getRepresentativeItem(gtMaterial);

        if (!representativeItem.isEmpty()) {
            tconMat.setRepresentativeItem(representativeItem);
            tconMat.addItem(representativeItem, 1, slimeknights.tconstruct.library.materials.Material.VALUE_Ingot);
        }

        // Set craftable/castable
        if (gtMaterial.hasProperty(PropertyKey.FLUID)) {
            tconMat.setFluid(gtMaterial.getFluid());
            tconMat.setCastable(true);
            tconMat.setCraftable(false);
        } else {
            tconMat.setCraftable(true);
            tconMat.setCastable(false);
        }

        // Create and add stats
        HeadMaterialStats headStats = createHeadStats(toolProp);
        HandleMaterialStats handleStats = createHandleStats(toolProp);
        ExtraMaterialStats extraStats = createExtraStats(toolProp);

        // Register material first
        TinkerRegistry.addMaterial(tconMat);

        // Then add stats
        TinkerRegistry.addMaterialStats(tconMat, headStats, handleStats, extraStats);

        // Finally integrate
        TinkerRegistry.integrate(new MaterialIntegration(tconMat,
                gtMaterial.hasProperty(PropertyKey.FLUID) ? gtMaterial.getFluid() : null,
                gtMaterial.toCamelCaseString()));
    }

    private static HeadMaterialStats createHeadStats(ToolProperty toolProp) {
        int durability = toolProp.getToolDurability();
        float miningSpeed = toolProp.getToolSpeed();
        float attackDamage = toolProp.getToolAttackDamage();
        int harvestLevel = toolProp.getToolHarvestLevel();

        return new HeadMaterialStats(durability, miningSpeed, attackDamage, harvestLevel);
    }

    private static HandleMaterialStats createHandleStats(ToolProperty toolProp) {
        float modifier = 1.0f;
        int handleDurability = (int) (toolProp.getToolDurability() * 0.5f);

        return new HandleMaterialStats(modifier, handleDurability);
    }

    private static ExtraMaterialStats createExtraStats(ToolProperty toolProp) {
        int extraDurability = (int) (toolProp.getToolDurability() * 0.3f);

        return new ExtraMaterialStats(extraDurability);
    }

    private static ItemStack getRepresentativeItem(Material material) {
        if (material.hasProperty(PropertyKey.INGOT)) {
            return OreDictUnifier.get(OrePrefix.ingot, material);
        } else if (material.hasProperty(PropertyKey.GEM)) {
            return OreDictUnifier.get(OrePrefix.gem, material);
        } else if (material.hasProperty(PropertyKey.DUST)) {
            return OreDictUnifier.get(OrePrefix.dust, material);
        }

        return ItemStack.EMPTY;
    }
}
