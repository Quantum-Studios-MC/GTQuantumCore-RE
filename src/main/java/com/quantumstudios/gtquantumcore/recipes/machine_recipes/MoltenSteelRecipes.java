package com.quantumstudios.gtquantumcore.recipes.machine_recipes;

import static com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler.MOLTEN_STEEL_FURNACE;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.dust;

public class MoltenSteelRecipes {


    public static void init() {


        MOLTEN_STEEL_FURNACE.recipeBuilder()
                .input(dust, Iron, 1)
                .input(dust, Coke, 1)
                .fluidOutputs(Steel.getFluid(144))
                .duration(100)
                .EUt(8)
                .buildAndRegister();

    }



}
