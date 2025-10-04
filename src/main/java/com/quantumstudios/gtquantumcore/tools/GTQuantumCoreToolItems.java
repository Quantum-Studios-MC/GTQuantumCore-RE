package com.quantumstudios.gtquantumcore.tools;


import com.quantumstudios.gtquantumcore.Tags;
import com.quantumstudios.gtquantumcore.item.GTQuantumCoreMetaItem;
import com.quantumstudios.gtquantumcore.tools.behavior.ShockProdBehavior;
import gregtech.api.GTValues;
import gregtech.api.items.toolitem.ItemGTTool;
import gregtech.api.items.toolitem.ToolClasses;
import gregtech.api.items.toolitem.ToolHelper;
import gregtech.api.items.toolitem.ToolOreDict;
import gregtech.common.items.ToolItems;
import gregtech.core.sound.GTSoundEvents;

public class GTQuantumCoreToolItems {
    public static String SPINDLE_CLASS = "spindle";
    public static String NEEDLE_CLASS = "needle";
    public static String POT_CLASS = "pot";
    public static String POWER_FIST = "power_fist";
    public static String SLEDGEHAMMER = "sledgehammer";
    public static String SHOCK_PROD_CLASS = "shock_prod";




    public static void init() {
        GTQuantumCoreMetaItem.SPINDLE = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "spindle")
                .toolStats(b -> b.crafting())
                        .toolClasses(SPINDLE_CLASS));

        GTQuantumCoreMetaItem.NEEDLE = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "needle")
                .toolStats(b -> b.crafting())
                .oreDict("craftingToolNeedle")
                .toolClasses(NEEDLE_CLASS));

        GTQuantumCoreMetaItem.POT = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "pot")
                .toolStats(b -> b.attacking())
                .oreDict("toolThisItemIsDeprecated")
                .toolClasses(POT_CLASS));

        GTQuantumCoreMetaItem.POWER_FIST = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "power_fist")
                .toolStats(b -> b.attacking()
                        .attackDamage(20.0F)
                        .attackSpeed(-5.0F)
                        .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_LV))
                .oreDict(ToolOreDict.toolHammer)
                .sound(GTSoundEvents.FORGE_HAMMER, true)
                .toolClasses(ToolClasses.HARD_HAMMER, ToolClasses.SWORD)
                .electric(GTValues.LV)
                .build());

        GTQuantumCoreMetaItem.POWER_FIST_MV = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "power_fist_mv")
                .toolStats(b -> b.attacking().attackDamage(30.0F)
                        .attackDamage(30.0F)
                        .attackSpeed(-3.6F)
                        .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV))
                .oreDict("craftingToolHammer")
                .sound(GTSoundEvents.FORGE_HAMMER, true)
                .toolClasses(ToolClasses.HARD_HAMMER, ToolClasses.SWORD)
                .electric(GTValues.MV)
                .build());

// HV Power Fist
        GTQuantumCoreMetaItem.POWER_FIST_HV = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "power_fist_hv")
                .toolStats(b -> b.attacking()
                        .attackDamage(40.0F)
                        .attackSpeed(-2.4F)
                        .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_HV))
                .oreDict("craftingToolHammer")
                .sound(GTSoundEvents.FORGE_HAMMER, true)
                .toolClasses(ToolClasses.HARD_HAMMER, ToolClasses.SWORD)
                .electric(GTValues.HV)
                .build());

        GTQuantumCoreMetaItem.SLEDGEHAMMER = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "sledgehammer")
                .toolStats(b -> b.attacking()
                        .attackDamage(14)
                        .attackSpeed(-4.2F))
                .oreDict("craftingToolHammer")
                .sound(GTSoundEvents.FORGE_HAMMER, true)
                .toolClasses(ToolClasses.HARD_HAMMER)
                .build());

        GTQuantumCoreMetaItem.SHOCK_PROD = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "shock_prod")
                .toolStats(b -> b.attacking()
                        .attackDamage(8.0F)
                        .attackSpeed(-2.0F)
                        .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_LV)
                        .behaviors(new ShockProdBehavior()))
                .oreDict("toolShockProd")
                .sound(GTSoundEvents.ELECTROLYZER, true)
                .toolClasses(SHOCK_PROD_CLASS)
                .electric(GTValues.LV)
                .build());

        GTQuantumCoreMetaItem.SHOCK_PROD_MV = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "shock_prod_mv")
                .toolStats(b -> b.attacking()
                        .attackDamage(12.0F)
                        .attackSpeed(-1.8F)
                        .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_MV)
                        .behaviors(new ShockProdBehavior()))
                .oreDict("toolShockProd")
                .sound(GTSoundEvents.ELECTROLYZER, true)
                .toolClasses(SHOCK_PROD_CLASS)
                .electric(GTValues.MV)
                .build());

        // HV Shock Prod
        GTQuantumCoreMetaItem.SHOCK_PROD_HV = ToolItems.register(ItemGTTool.Builder.of(Tags.MODID, "shock_prod_hv")
                .toolStats(b -> b.attacking()
                        .attackDamage(16.0F)
                        .attackSpeed(-1.6F)
                        .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_HV)
                        .behaviors(new ShockProdBehavior()))
                .oreDict("toolShockProd")
                .sound(GTSoundEvents.ELECTROLYZER, true)
                .toolClasses(SHOCK_PROD_CLASS)
                .electric(GTValues.LV)
                .build());
    }



}



