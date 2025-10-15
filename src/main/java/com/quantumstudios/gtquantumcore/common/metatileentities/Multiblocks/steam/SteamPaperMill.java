package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.steam;

import com.quantumstudios.gtquantumcore.api.IBellowHatch;
import com.quantumstudios.gtquantumcore.api.capability.impl.MultiblockAbilities;
import com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler;
import com.quantumstudios.gtquantumcore.render.TexturesHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockFireboxCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SteamPaperMill extends GTQuantumCoreRecipeMapSteamMultiblockController {

    private int parallelLimit = 1;

    public SteamPaperMill(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMapsHandler.PAPER_MILLING, CONVERSION_RATE);
        // Initialize the custom recipe logic with parallel processing
        this.recipeMapWorkable = new GTQuantumCoreSteamMultiblockRecipeLogic(this, CONVERSION_RATE);
        // Set parallel limit (will be updated by bellows)
        this.recipeMapWorkable.setParallelLimit(parallelLimit);
    }

    @Override
    protected void updateFormedValid() {
        if (!getWorld().isRemote) {
            updateBellowEffects();
        }
        super.updateFormedValid();
    }

    private void updateBellowEffects() {
        List<IBellowHatch> bellowHatches = getAbilities(MultiblockAbilities.BELLOW_HATCH);

        parallelLimit = 1; // Base parallel limit

        for (IBellowHatch bellow : bellowHatches) {
            if (bellow.isActive()) {
                parallelLimit += bellow.getParallelBonus(); // Add 4 for each active bellow
            }
        }

        // Update the recipe workable's parallel limit
        if (this.recipeMapWorkable != null) {
            this.recipeMapWorkable.setParallelLimit(parallelLimit);
        }
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("FFF", "FFF", "CCC", "CCC")
                .aisle("FFF", "FFF", "CCC", "C#C")
                .aisle("FFF", "FFF", "CSC", "CCC")

                .where('F', states(getFireboxCasing()).setMinGlobalLimited(1))
                .where('C', states(getCasing()).setMinGlobalLimited(13)
                        .or(autoAbilities(true, true, true, true, true))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS))
                        .or(abilities(MultiblockAbilities.BELLOW_HATCH))) // Add bellow hatch support
                .where('S', selfPredicate())
                .where('#', any())
                .build();
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);

        if (isStructureFormed() && parallelLimit > 1) {
            textList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.GREEN,
                    "gtquantumcore.multiblock.parallel_bonus",
                    parallelLimit
            ));
        }

        // Show active bellow count
        List<IBellowHatch> bellows = getAbilities(MultiblockAbilities.BELLOW_HATCH);
        int activeBellows = 0;
        for (IBellowHatch bellow : bellows) {
            if (bellow.isActive()) {
                activeBellows++;
            }
        }

        if (activeBellows > 0) {
            textList.add(TextComponentUtil.translationWithColor(
                    TextFormatting.YELLOW,
                    "gtquantumcore.multiblock.active_bellows",
                    activeBellows
            ));
        }
    }


    @Override
    public int getItemOutputLimit() {
        return 1;
    }

    @Override
    public int getFluidOutputLimit() {
        return 1;
    }

    private static IBlockState getCasing() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    private static IBlockState getFireboxCasing() {
        return MetaBlocks.BOILER_FIREBOX_CASING.getState(BlockFireboxCasing.FireboxCasingType.BRONZE_FIREBOX);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        if (sourcePart != null && isFireboxPart(sourcePart)) {
            return lastActive ? Textures.BRONZE_FIREBOX_ACTIVE : Textures.BRONZE_FIREBOX;
        }
        return Textures.BRONZE_PLATED_BRICKS;
    }

    private boolean isFireboxPart(IMultiblockPart sourcePart) {
        return isStructureFormed() && (((MetaTileEntity) sourcePart).getPos().getY() < getPos().getY());
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return TexturesHandler.GENERAL_SMELTER_OVERLAY_2;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SteamPaperMill(metaTileEntityId);
    }

}
