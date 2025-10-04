package com.quantumstudios.gtquantumcore;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID, name = Tags.MODNAME, version = Tags.VERSION)
public class GTQuantumCore {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODNAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MODNAME);
    }
}
