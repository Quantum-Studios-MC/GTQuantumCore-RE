package com.quantumstudios.gtquantumcore.blocks;

import org.jetbrains.annotations.NotNull;

import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MetaBlocksHandler
{
	private MetaBlocksHandler() {}

	public static BlockSpecialMultiblockPart SPECIAL_PART;
	public static GTQUMultiblockCasing MULTIBLOCK_CASING;

	static {
		SPECIAL_PART = new BlockSpecialMultiblockPart();
		SPECIAL_PART.setRegistryName("gtqu_special_part");

		MULTIBLOCK_CASING = new GTQUMultiblockCasing();
		MULTIBLOCK_CASING.setRegistryName("gtqu_multiblock_casing");
	}

	public static void init() {
	}

	@SideOnly(Side.CLIENT)
	public static void registerItemModels() {
		SPECIAL_PART.onModelRegister();
		registerItemModel(MULTIBLOCK_CASING);
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemModel(@NotNull Block block) {
		for (IBlockState state : block.getBlockState().getValidStates()) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
					block.getMetaFromState(state),
					new ModelResourceLocation(block.getRegistryName(),
							MetaBlocks.statePropertiesToString(state.getProperties())));
		}
	}
}
