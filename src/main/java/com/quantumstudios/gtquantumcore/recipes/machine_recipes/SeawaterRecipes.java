package com.quantumstudios.gtquantumcore.recipes.machine_recipes;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.OrePrefix;

import static com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler.SEAWATER_PROCESSING_RECIPES;
import static gregtech.api.unification.material.Materials.SodiumHydroxide;

public class SeawaterRecipes {

    public static void init() {

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(50000))
                .fluidOutputs(Materials.DistilledWater.getFluid(45000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Salt, 128), 8000, 500)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Magnesium, 64), 3000, 200)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Potassium, 32), 1500, 0)
                .duration(1200).EUt(16)
                .circuitMeta(1)
                .buildAndRegister();

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(100000))
                .fluidInputs(Materials.SulfuricAcid.getFluid(1000))
                .fluidOutputs(Materials.DistilledWater.getFluid(80000))
                .fluidOutputs(Materials.Hydrogen.getFluid(2000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Lithium, 16), 800, 100)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Sodium, 128), 6000, 400)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Potassium, 16), 4500, 250)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Magnesium, 16), 5000, 300)
                .duration(1800).EUt(32)
                .circuitMeta(2)
                .buildAndRegister();

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(150000))
                .input(OrePrefix.dust, SodiumHydroxide, 16)
                .fluidOutputs(Materials.DistilledWater.getFluid(120000))
                .fluidOutputs(Materials.Chlorine.getFluid(5000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Salt, 128), 7500, 600)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Bromine, 64), 2000, 150)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Iodine, 16), 400, 50)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Fluorine, 16), 200, 0)
                .duration(2400).EUt(512)
                .circuitMeta(3)
                .buildAndRegister();

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(200000))
                .fluidInputs(Materials.HydrochloricAcid.getFluid(3000))
                .fluidOutputs(Materials.DistilledWater.getFluid(150000))
                .fluidOutputs(Materials.HydrogenSulfide.getFluid(1000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Salt, 64), 8500, 700)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Calcium, 12), 3500, 300)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Strontium, 1), 1000, 100)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Boron, 4), 600, 75)
                .duration(3600).EUt(1024)
                .circuitMeta(4)
                .buildAndRegister();

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(300000))
                .fluidInputs(Materials.NitricAcid.getFluid(2000))
                .fluidOutputs(Materials.DistilledWater.getFluid(200000))
                .fluidOutputs(Materials.Oxygen.getFluid(3000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Salt, 96), 9000, 800)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Sulfur, 16), 4000, 400)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Silicon, 8), 1200, 150)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Aluminium, 4), 800, 100)
                .duration(4800).EUt(2048)
                .circuitMeta(5)
                .buildAndRegister();

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(100000))
                .fluidInputs(Materials.DistilledWater.getFluid(50000))
                .fluidOutputs(Materials.DistilledWater.getFluid(120000))
                .fluidOutputs(Materials.Hydrogen.getFluid(15000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Salt, 96), 6000, 400)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Carbon, 16), 1500, 200)
                .duration(2700).EUt(1024)
                .circuitMeta(6)
                .buildAndRegister();

        SEAWATER_PROCESSING_RECIPES.recipeBuilder()
                .fluidInputs(Materials.SaltWater.getFluid(500000))
                .fluidInputs(Materials.SulfuricAcid.getFluid(5000))
                .input(OrePrefix.dust, SodiumHydroxide, 16)
                .fluidOutputs(Materials.DistilledWater.getFluid(400000))
                .fluidOutputs(Materials.HydrogenSulfide.getFluid(2000))
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Salt, 256), 9500, 1000)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Magnesium, 32), 7000, 800)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Lithium, 32), 3500, 400)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Potassium, 32), 5000, 500)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Calcium, 32), 4500, 600)
                .chancedOutput(OreDictUnifier.get(OrePrefix.dust, Materials.Bromine, 16), 2500, 300)
                .duration(7200).EUt(4096)
                .circuitMeta(7)
                .buildAndRegister();


    }



}



