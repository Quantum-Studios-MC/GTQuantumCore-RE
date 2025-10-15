package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks;

import com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.electrical.*;
import com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.steam.*;
import com.quantumstudios.gtquantumcore.common.metatileentities.multiblockpart.MetaTileEntityBellowHatch;

import static gregtech.api.util.GTUtility.gregtechId;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

public class MultiblockHandler
{
	// Multiblock Parts
	public static MetaTileEntityBellowHatch BELLOW_HATCH;

	// Steam Multiblocks
	public static SteamAlloyBlastFurnace STEAM_ALLOY_BLAST_FURNACE;
	public static SteamSprengelPump STEAM_SPRENGEL_PUMP;
	public static SteamWiremill STEAM_WIREMILL;
	public static SteamStrandCaster STEAM_STRAND_CASTER;
	public static SteamVulcanisationVat STEAM_VULCANISATION_VAT;
	public static SteamLatexExtractor LATEX_EXTRACTOR;
	public static SteamConstructionChamber CONSTRUCTION_CHAMBER;
	public static SteamRoaster STEAM_ROASTER;
	public static SteamVaporCondensator STEAM_VAPOR_CONDENSATOR;
	public static SteamSluice STEAM_SLUICE;
	public static SteamGlassFurnace GLASS_FURNACE;
	public static SteamOreWasher STEAM_ORE_WASHER;
	public static SteamMoltenSteelFurnace MOLTEN_STEEL_FURNACE;
	public static SteamWoodVarnisher STEAM_WOOD_VARNISHER;
	public static SteamPaperMill STEAM_PAPER_MILL;


	// Electrical Multiblocks
	public static ElectricalSprengelPump ELECTRICAL_SPRENGEL_PUMP;
	public static ElectricalLoom ELECTRICAL_LOOM;
	public static ElectricalComponentFactory ELECTRICAL_COMPONENT_FACTORY;
	public static MetaTileEntitySeawaterProcessor SEAWATER_PROCESSOR;
	public static MetaTileEntitySteamVortexReactor STEAM_VORTEX_REACTOR;
	public static MetaTileEntityVacuumStill VACUUM_STILL;
	public static MetaTileEntityThermalCrystallizer THERMAL_CRYSTALLIZER;
	public static MetaTileEntityTectonicResonanceEngine TECTONIC_RESONANCE_ENGINE;

	public static void init() {
		// Register Multiblock Parts First
		BELLOW_HATCH = registerMetaTileEntity(11000,
				new MetaTileEntityBellowHatch(gregtechId("bellow_hatch")));

		// Steam Multiblocks - ID Range: 11012-11024
		STEAM_ALLOY_BLAST_FURNACE = registerMetaTileEntity(11012,
				new SteamAlloyBlastFurnace(gregtechId("steam_alloy_blast_furnace")));

		STEAM_SPRENGEL_PUMP = registerMetaTileEntity(11013,
				new SteamSprengelPump(gregtechId("steam_sprengel_pump")));

		STEAM_STRAND_CASTER = registerMetaTileEntity(11015,
				new SteamStrandCaster(gregtechId("steam_strand_caster")));

		STEAM_WIREMILL = registerMetaTileEntity(11016,
				new SteamWiremill(gregtechId("steam_wiremill")));

		STEAM_VULCANISATION_VAT = registerMetaTileEntity(11018,
				new SteamVulcanisationVat(gregtechId("steam_vulcanisation_vat")));

		LATEX_EXTRACTOR = registerMetaTileEntity(11020,
				new SteamLatexExtractor(gregtechId("latex_extractor")));

		CONSTRUCTION_CHAMBER = registerMetaTileEntity(11021,
				new SteamConstructionChamber(gregtechId("construction_chamber")));

		STEAM_ROASTER = registerMetaTileEntity(11022,
				new SteamRoaster(gregtechId("steam_roaster")));

		STEAM_VAPOR_CONDENSATOR = registerMetaTileEntity(11023,
				new SteamVaporCondensator(gregtechId("steam_vapor_condensator")));

		STEAM_SLUICE = registerMetaTileEntity(11024,
				new SteamSluice(gregtechId("steam_sluice")));

		// Electrical Multiblocks - ID Range: 11011, 11019, 11025-11026
		ELECTRICAL_SPRENGEL_PUMP = registerMetaTileEntity(11011,
				new ElectricalSprengelPump(gregtechId("electrical_sprengel_pump")));

		ELECTRICAL_LOOM = registerMetaTileEntity(11019,
				new ElectricalLoom(gregtechId("electrical_loom")));

		ELECTRICAL_COMPONENT_FACTORY = registerMetaTileEntity(11025,
				new ElectricalComponentFactory(gregtechId("electrical_component_factory")));

		GLASS_FURNACE = registerMetaTileEntity(11029,
				new SteamGlassFurnace(gregtechId("glass_furnace")));

		STEAM_ORE_WASHER = registerMetaTileEntity(11030,
				new SteamOreWasher(gregtechId("steam_ore_washer")));

		MOLTEN_STEEL_FURNACE = registerMetaTileEntity(11031,
				new SteamMoltenSteelFurnace(gregtechId("molten_steel_furnace")));

		SEAWATER_PROCESSOR = registerMetaTileEntity(11033,
				new MetaTileEntitySeawaterProcessor(gregtechId("seawater_processor")));

		STEAM_VORTEX_REACTOR = registerMetaTileEntity(11034,
				new MetaTileEntitySteamVortexReactor(gregtechId("steam_vortex_reactor")));

		VACUUM_STILL = registerMetaTileEntity(11036,
				new MetaTileEntityVacuumStill(gregtechId("vacuum_still")));

		THERMAL_CRYSTALLIZER = registerMetaTileEntity(11038,
				new MetaTileEntityThermalCrystallizer(gregtechId("thermal_crystallizer")));

		TECTONIC_RESONANCE_ENGINE = registerMetaTileEntity(11039,
				new MetaTileEntityTectonicResonanceEngine(gregtechId("tectonic_resonance_engine")));

		STEAM_WOOD_VARNISHER = registerMetaTileEntity(31000,
				new SteamWoodVarnisher(gregtechId("steam_wood_varnisher")));

		STEAM_PAPER_MILL = registerMetaTileEntity(31001,
				new SteamPaperMill(gregtechId("steam_paper_mill")));

	}
}
