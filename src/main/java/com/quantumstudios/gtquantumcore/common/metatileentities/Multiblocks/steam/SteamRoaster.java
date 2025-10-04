package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.steam;

import com.quantumstudios.gtquantumcore.GTQuantumCoreValues;
import com.quantumstudios.gtquantumcore.api.IBellowHatch;
import com.quantumstudios.gtquantumcore.api.capability.impl.MultiblockAbilities;
import com.quantumstudios.gtquantumcore.blocks.GTQUMultiblockCasing;
import com.quantumstudios.gtquantumcore.blocks.MetaBlocksHandler;
import com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler;
import com.quantumstudios.gtquantumcore.render.TexturesHandler;
import com.quantumstudios.gtquantumcore.utils.GTQuantumCoreTooltipHelper;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.util.TextComponentUtil;
import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SteamRoaster extends GTQuantumCoreRecipeMapSteamMultiblockController {

    private int parallelLimit = 1;

    public SteamRoaster(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMapsHandler.ROASTER, CONVERSION_RATE);
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
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("FFF", "FFF", "FFF")
                .aisle("FFF", "F#F", "FFF")
                .aisle("FSF", "FFF", "FFF")
                .where('S', selfPredicate())
                .where('F', states(getCasingState()).setMinGlobalLimited(12).or(autoAbilities())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS))
                        .or(abilities(MultiblockAbilities.BELLOW_HATCH))) // Add bellow hatch support
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
        return 3;
    }

    @Override
    public int getFluidOutputLimit() {
        return 1;
    }

    private static IBlockState getCasingState() {
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.CAST_IRON_CASING);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return TexturesHandler.CAST_IRON_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return TexturesHandler.GENERAL_SMELTER_OVERLAY_2;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SteamRoaster(metaTileEntityId);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        GTQuantumCoreTooltipHelper.addGTQuantumCoreInformation(tooltip);
        tooltip.add(I18n.format("gtquantumcore.multiblock.bellow_support"));
        tooltip.add(I18n.format("gtquantumcore.multiblock.bellow_bonus"));
        tooltip.add(I18n.format("gtquantumcore.machine.author") + " " +
                GTQuantumCoreValues.FORMAT_IRIS_1 + I18n.format("gtquantumcore.machine.author.iris.1") +
                GTQuantumCoreValues.FORMAT_IRIS_2 + I18n.format("gtquantumcore.machine.author.iris.2") +
                GTQuantumCoreValues.FORMAT_IRIS_3 + I18n.format("gtquantumcore.machine.author.iris.3")
        );
    }
}
