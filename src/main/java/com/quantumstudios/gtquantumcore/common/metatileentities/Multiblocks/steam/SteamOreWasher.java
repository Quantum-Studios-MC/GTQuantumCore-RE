package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.steam;

import com.quantumstudios.gtquantumcore.GTQuantumCoreValues;
import com.quantumstudios.gtquantumcore.blocks.GTQUMultiblockCasing;
import com.quantumstudios.gtquantumcore.blocks.MetaBlocksHandler;
import com.quantumstudios.gtquantumcore.render.TexturesHandler;
import com.quantumstudios.gtquantumcore.utils.GTQuantumCoreTooltipHelper;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class SteamOreWasher extends GTQuantumCoreRecipeMapSteamMultiblockController {

    public SteamOreWasher(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.ORE_WASHER_RECIPES, CONVERSION_RATE);
        this.recipeMapWorkable = new GTQuantumCoreSteamMultiblockRecipeLogic(this, CONVERSION_RATE);
        this.recipeMapWorkable.setParallelLimit(2);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("FFFF", "FFFF", "FFFF")
                .aisle("F##F", "F##F", "F##F")  // Washing basins need space
                .aisle("F##F", "F##F", "F##F") 
                .aisle("FSFF", "FFFF", "FFFF")
                .where('S', selfPredicate())
                .where('F', states(getCasingState()).setMinGlobalLimited(28).or(autoAbilities())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS)))
                .where('#', any())
                .build();
    }

    @Override
    public int getItemOutputLimit() {
        return 4; // Main output + 3 byproducts
    }

    @Override
    public int getFluidOutputLimit() {
        return 1; // Dirty water output
    }

    private static IBlockState getCasingState() {
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.SEALED_CASING);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return TexturesHandler.SEALED_CASING;
    }

    @Override
    protected @NotNull ICubeRenderer getFrontOverlay() {
        return Textures.ORE_WASHER_OVERLAY;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SteamOreWasher(metaTileEntityId);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        GTQuantumCoreTooltipHelper.addGTQuantumCoreInformation(tooltip);
        tooltip.add(I18n.format("gtquantumcore.multiblock.steam_ore_washer.description"));
        tooltip.add(I18n.format("gtquantumcore.multiblock.steam_ore_washer.usage"));
        tooltip.add(I18n.format("gtquantumcore.multiblock.steam_ore_washer.structure"));
        tooltip.add(I18n.format("gtquantumcore.multiblock.steam_ore_washer.water_requirement"));
        tooltip.add(I18n.format("gtquantumcore.machine.author") + " " +
                GTQuantumCoreValues.FORMAT_IRIS_1 + I18n.format("gtquantumcore.machine.author.iris.1") +
                GTQuantumCoreValues.FORMAT_IRIS_2 + I18n.format("gtquantumcore.machine.author.iris.2") +
                GTQuantumCoreValues.FORMAT_IRIS_3 + I18n.format("gtquantumcore.machine.author.iris.3")
        );
    }
}
