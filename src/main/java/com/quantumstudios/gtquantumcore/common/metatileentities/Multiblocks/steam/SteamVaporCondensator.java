package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.steam;

import com.quantumstudios.gtquantumcore.GTQuantumCoreValues;
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
import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SteamVaporCondensator extends GTQuantumCoreRecipeMapSteamMultiblockController {

    public SteamVaporCondensator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMapsHandler.CONDENSATION_CHAMBER, CONVERSION_RATE);
        // Initialize the custom recipe logic with parallel processing
        this.recipeMapWorkable = new GTQuantumCoreSteamMultiblockRecipeLogic(this, CONVERSION_RATE);
        // Set parallel limit (adjust as needed for balance)
        this.recipeMapWorkable.setParallelLimit(1);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#FFF#", "#FFF#", "#FFF#", "#FFF#", "#FFF#")
                .aisle("FFFFF", "FF#FF", "FF#FF", "FF#FF", "FFFFF")
                .aisle("#FSF#", "#FFF#", "#FFF#", "#FFF#", "#FFF#")
                .where('S', selfPredicate())
                .where('F', states(getCasingState()).setMinGlobalLimited(48).or(autoAbilities())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS)))
                .where('#', any())
                .build();
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
        return TexturesHandler.GENERAL_SMELTER_OVERLAY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SteamVaporCondensator(metaTileEntityId);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        GTQuantumCoreTooltipHelper.addGTQuantumCoreInformation(tooltip);
        tooltip.add(I18n.format("gtquantumcore.machine.author") + " " +
                GTQuantumCoreValues.FORMAT_IRIS_1 + I18n.format("gtquantumcore.machine.author.iris.1") +
                GTQuantumCoreValues.FORMAT_IRIS_2 + I18n.format("gtquantumcore.machine.author.iris.2") +
                GTQuantumCoreValues.FORMAT_IRIS_3 + I18n.format("gtquantumcore.machine.author.iris.3")
        );
    }
}