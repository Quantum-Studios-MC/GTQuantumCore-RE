package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.electrical;

import com.quantumstudios.gtquantumcore.blocks.GTQUMultiblockCasing;
import com.quantumstudios.gtquantumcore.blocks.MetaBlocksHandler;
import com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler;
import com.quantumstudios.gtquantumcore.render.TexturesHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ElectricalComponentFactory extends RecipeMapMultiblockController
{
	public ElectricalComponentFactory(ResourceLocation metaTileEntityId)
	{
		super(metaTileEntityId, RecipeMapsHandler.COMPONENT_FACTORY);
	}

	@Override
	public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity)
	{
		return new ElectricalComponentFactory(metaTileEntityId);
	}

	@Override
	protected BlockPattern createStructurePattern() {
		return FactoryBlockPattern.start()
				.aisle("FFF", "FFF", "FFF")
				.aisle("FFF", "FFF", "FFF")
				.aisle("FFF", "FFF", "FFF")
				.aisle("FFF", "FFF", "FFF")
				.aisle("FFF", "FSF", "FFF")
				.where('S', selfPredicate())
				.where('F', states(getCasingState()).setMinGlobalLimited(45).or(autoAbilities())
						.or(abilities(MultiblockAbility.IMPORT_ITEMS))
						.or(abilities(MultiblockAbility.EXPORT_ITEMS))
						.or(abilities(MultiblockAbility.IMPORT_FLUIDS))
						.or(abilities(MultiblockAbility.EXPORT_FLUIDS)))
				.where('#', any())
				.build();
	}


	
	private static IBlockState getCasingState() 
	{
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.CAST_IRON_CASING);
    }
	
	//@SideOnly(Side.CLIENT)
	@Override
	public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) 
	{
		return TexturesHandler.CAST_IRON_CASING;
	}

	//@SideOnly(Side.CLIENT)
	@Override
	protected @NotNull ICubeRenderer getFrontOverlay()
	{
		return TexturesHandler.GENERAL_ELECTRIC_OVERLAY_2;
	}
}