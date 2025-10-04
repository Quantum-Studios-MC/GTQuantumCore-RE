package com.quantumstudios.gtquantumcore.render;
import com.quantumstudios.gtquantumcore.Tags;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Tags.MODID, value = Side.CLIENT)
public final class TexturesHandler 
{

	
	public static SimpleOverlayRenderer SEALED_CASING;
	public static SimpleOverlayRenderer NICKEL_STEEL_CASING;
	public static SimpleOverlayRenderer CAST_IRON_CASING;
	public static SimpleOverlayRenderer SEISMIC_CASING;

	public static OrientedOverlayRenderer ELECTRICAL_SPRENGEL_PUMP_OVERLAY;
	public static OrientedOverlayRenderer STEAM_ABF_OVERLAY;
	public static OrientedOverlayRenderer GENERAL_SMELTER_OVERLAY;
	public static OrientedOverlayRenderer GENERAL_ELECTRIC_OVERLAY;
	public static OrientedOverlayRenderer GENERAL_SMELTER_OVERLAY_2;
	public static OrientedOverlayRenderer GENERAL_ELECTRIC_OVERLAY_2;
	public static OrientedOverlayRenderer GENERAL_ELECTRIC_OVERLAY_3;
	private TexturesHandler() {}
	
	public static void init() 
	{
		SEALED_CASING = new SimpleOverlayRenderer("casings/gtqu_multiblock_casing/sealed_casing");
		NICKEL_STEEL_CASING = new SimpleOverlayRenderer("casings/gtqu_multiblock_casing/nickel_steel_casing");
		CAST_IRON_CASING = new SimpleOverlayRenderer("casings/gtqu_multiblock_casing/cast_iron_casing");
		SEISMIC_CASING = new SimpleOverlayRenderer("casings/gtqu_multiblock_casing/seismic_casing");

		ELECTRICAL_SPRENGEL_PUMP_OVERLAY = new OrientedOverlayRenderer("machines/esp");
		STEAM_ABF_OVERLAY = new OrientedOverlayRenderer("machines/sabf");
		GENERAL_ELECTRIC_OVERLAY = new OrientedOverlayRenderer("machines/general/general_electric");
		GENERAL_SMELTER_OVERLAY = new OrientedOverlayRenderer("machines/general/general_smelter");
		GENERAL_SMELTER_OVERLAY_2 = new OrientedOverlayRenderer("machines/general/general_smelter_2");
		GENERAL_ELECTRIC_OVERLAY_2 = new OrientedOverlayRenderer("machines/general/general_electric_2");
		GENERAL_ELECTRIC_OVERLAY_3 = new OrientedOverlayRenderer("machines/general/general_electric_3");
	}
}
