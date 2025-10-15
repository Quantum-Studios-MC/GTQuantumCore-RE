package com.quantumstudios.gtquantumcore.recipes;

import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.widgets.ProgressWidget;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.builders.SimpleRecipeBuilder;

public class RecipeMapsHandler
{
	public static final RecipeMap<SimpleRecipeBuilder> ELECTRICAL_SPRENGEL_PUMP_MAP = new RecipeMap<>("electrical_sprengel_pump", 3, 1, 0, 0,
			new SimpleRecipeBuilder().duration(10).EUt(8), false);
	//    .setSlotOverlay(false, false, false, gregtech.client.renderer.texture.Textures.)
	//    .setSlotOverlay(true, true, true, gregtech.client.renderer.texture.Textures.SIFTER_OVERLAY)
	//    .setProgressBar()

	public static final RecipeMap<SimpleRecipeBuilder> STEAM_ALLOY_BLAST_FURNACE = new RecipeMap<>("steam_alloy_furnace", 4, 0, 1, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> VULCANISATION_VAT = new RecipeMap<>("vulcanisation_vat", 2, 1, 1, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> STEAM_SPRENGEL_PUMP = new RecipeMap<>("steam_sprengel_pump", 3, 1, 1, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> ELECTRICAL_LOOM = new RecipeMap<>("electrical_loom", 3, 1, 0, 0,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> LATEX_EXTRACTOR = new RecipeMap<>("latex_extractor", 1, 1, 0, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> CONSTRUCTION_CHAMBER = new RecipeMap<>("construction_chamber", 9, 1, 1, 0,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> ROASTER = new RecipeMap<>("roaster", 1, 4, 1, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> CONDENSATION_CHAMBER = new RecipeMap<>("condensation_chamber", 0, 0, 1, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> SLUICE = new RecipeMap<>("sluice", 1, 4, 0, 0,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> COMPONENT_FACTORY = new RecipeMap<>("component_factory", 4, 1, 2, 0,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> GLASS_FURNACE = new RecipeMap<>("glass_furnace", 1, 0, 0, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);


	public static final RecipeMap<SimpleRecipeBuilder> MOLTEN_STEEL_FURNACE = new RecipeMap<>("molten_steel_furnace", 2, 0, 0, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> SEAWATER_PROCESSING_RECIPES = new RecipeMap<>("seawater_processor", 2, 9, 3, 2,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> WOOD_VARNISHING = new RecipeMap<>("wood_varnishing", 1, 1, 1, 0,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);

	public static final RecipeMap<SimpleRecipeBuilder> PAPER_MILLING = new RecipeMap<>("paper_milling", 1, 2, 1, 1,
			new SimpleRecipeBuilder().duration(10).EUt(6), false);
}
