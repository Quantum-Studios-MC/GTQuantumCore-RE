package com.quantumstudios.gtquantumcore;

import com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.MultiblockHandler;
import com.quantumstudios.gtquantumcore.integration.GTCEuTConIntegration;
import com.quantumstudios.gtquantumcore.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION)
public class GTQuantumCore {

    @SidedProxy(clientSide = "com.quantumstudios.gtquantumcore.proxy.ClientProxy", serverSide = "com.quantumstudios.gtquantumcore.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODNAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        LOGGER.info("Hello From {}!", Tags.MODNAME);
    }

    @Mod.EventHandler
    public void preInit(FMLInitializationEvent event) {
        proxy.init(event);
        GTCEuTConIntegration.registerAllMaterials();
        MultiblockHandler.init();
    }
}
