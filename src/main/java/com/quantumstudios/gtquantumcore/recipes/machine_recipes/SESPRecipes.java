package com.quantumstudios.gtquantumcore.recipes.machine_recipes;




import com.quantumstudios.gtquantumcore.api.unification.material.ore.GTQuantumCoreOrePrefix;

import static com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler.STEAM_SPRENGEL_PUMP;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.wireFine;
import static gregtech.common.items.MetaItems.GLASS_TUBE;
import static gregtech.common.items.MetaItems.VACUUM_TUBE;

public class SESPRecipes {

    public static void init() {
        STEAM_SPRENGEL_PUMP.recipeBuilder()
                .input(GTQuantumCoreOrePrefix.mesh, RedAlloy, 3)
                .input(GLASS_TUBE)
                .input(wireFine, Copper, 4)
                .fluidInputs(Mercury.getFluid(144))
                .output(VACUUM_TUBE, 2)
                .fluidOutputs(Air.getFluid(100))
                .duration(120).EUt(16).buildAndRegister();
    }

}