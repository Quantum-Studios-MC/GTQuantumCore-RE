package com.quantumstudios.gtquantumcore.common;


import com.quantumstudios.gtquantumcore.api.unification.material.modifications.GTQuantumCoreExtraFlags;
import com.quantumstudios.gtquantumcore.api.unification.material.ore.GTQuantumCoreOrePrefix;
import com.quantumstudios.gtquantumcore.api.unification.material.ore.GTQuantumCoreRecipeHandler;
import com.quantumstudios.gtquantumcore.recipes.GTQuantumCoreToolsRecipeHandler;
import gregtech.api.unification.material.event.PostMaterialEvent;
import gregtech.api.unification.material.properties.PropertyKey;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.common.items.MetaItems;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventHandlers {

    @SubscribeEvent
    public static void materialChanges(PostMaterialEvent event) {
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.mesh);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.billet);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.ntmpipe);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.wiredense);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.shell);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.plateTriple);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.plateSextuple);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.piston);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.hook);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.tablet);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.curvedplate);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.thread);
        MetaItems.addOrePrefix(GTQuantumCoreOrePrefix.yarn);
        GTQuantumCoreExtraFlags.register();
    }


    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {

        OrePrefix.ingot.addProcessingHandler(
                PropertyKey.DUST, GTQuantumCoreToolsRecipeHandler::processSpindle);

        OrePrefix.ingot.addProcessingHandler(
                PropertyKey.DUST, GTQuantumCoreToolsRecipeHandler::processNeedle);

        OrePrefix.ingot.addProcessingHandler(
                PropertyKey.DUST, GTQuantumCoreToolsRecipeHandler::processPowerFist);

        OrePrefix.ingot.addProcessingHandler(
                PropertyKey.DUST, GTQuantumCoreToolsRecipeHandler::processSledgehammer);

        OrePrefix.ingot.addProcessingHandler(
                PropertyKey.DUST, GTQuantumCoreToolsRecipeHandler::processShockProd);

        GTQuantumCoreRecipeHandler.init();
    }

}