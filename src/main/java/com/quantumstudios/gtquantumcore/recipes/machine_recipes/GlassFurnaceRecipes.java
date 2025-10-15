package com.quantumstudios.gtquantumcore.recipes.machine_recipes;

import net.minecraft.init.Blocks;

import static com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler.GLASS_FURNACE;
import static gregtech.api.unification.material.Materials.Glass;

public class GlassFurnaceRecipes {


    public static void init() {

        GLASS_FURNACE.recipeBuilder()
                .input(Blocks.SAND, 64)
                .fluidOutputs(Glass.getFluid(9216))
                .duration(120).EUt(1).buildAndRegister();
    }



}
