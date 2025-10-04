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

public class SteamSluice extends GTQuantumCoreRecipeMapSteamMultiblockController {

    public SteamSluice(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMapsHandler.SLUICE, CONVERSION_RATE);
        this.recipeMapWorkable = new GTQuantumCoreSteamMultiblockRecipeLogic(this, CONVERSION_RATE);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("FFFFF", "#FFF#", "#FFF#", "#FFF#", "#FFF#")
                .aisle("FFFFF", "FF#FF", "FF#FF", "FF#FF", "FFFFF")
                .aisle("FFSFF", "#FFF#", "#FFF#", "#FFF#", "#FFF#")
                .where('S', selfPredicate())
                .where('F', states(getCasingState()).setMinGlobalLimited(50).or(autoAbilities())
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
        return TexturesHandler.GENERAL_ELECTRIC_OVERLAY_2;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SteamSluice(metaTileEntityId);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        GTQuantumCoreTooltipHelper.addGTQuantumCoreInformation(tooltip);

        tooltip.add(I18n.format("§6Steam Sluice"));
        tooltip.add(I18n.format("§7Steam-powered ore washing and separation"));
        tooltip.add(I18n.format("§7processes materials through fluid separation."));
        tooltip.add("");

        tooltip.add(I18n.format("§e§lOperation Requirements:"));
        tooltip.add(I18n.format("§7• §fSteam Supply: §bConstant steam flow required"));
        tooltip.add(I18n.format("§7• §fWater Input: §9Large quantities needed"));
        tooltip.add(I18n.format("§7• §fRaw Materials: §aOres and crushed materials"));
        tooltip.add("");

        tooltip.add(I18n.format("§b§lProcessing Capabilities:"));
        tooltip.add(I18n.format("§7• §aUp to 3 item outputs §7simultaneously"));
        tooltip.add(I18n.format("§7• §91 fluid output §7for byproducts"));
        tooltip.add(I18n.format("§7• §6Parallel processing §7for efficiency"));
        tooltip.add("");

        tooltip.add(I18n.format("§d§lStructure: §75×3×5 Washing Plant"));
        tooltip.add(I18n.format("§7• §8Cast Iron Casings §7for structure"));
        tooltip.add(I18n.format("§7• §3Multiple hatches §7for inputs/outputs"));
        tooltip.add(I18n.format("§7• §eHollow interior §7for water flow"));
        tooltip.add("");

        if (advanced) {
            tooltip.add(I18n.format("§8§lTechnical Details:"));
            tooltip.add(I18n.format("§8• Steam conversion rate: 1:1 with steam machines"));
            tooltip.add(I18n.format("§8• Minimum 50 casing blocks required"));
            tooltip.add(I18n.format("§8• Supports multiple input/output hatches"));
            tooltip.add(I18n.format("§8• Automatic recipe detection and processing"));
            tooltip.add("");

            tooltip.add(I18n.format("§8§lOptimization Tips:"));
            tooltip.add(I18n.format("§8• Use multiple input hatches for faster feeding"));
            tooltip.add(I18n.format("§8• Ensure adequate water supply for continuous operation"));
            tooltip.add(I18n.format("§8• Monitor output hatches to prevent backing up"));
            tooltip.add("");
        }

        tooltip.add(I18n.format("§8§oSteam-powered ore processing"));
        tooltip.add(I18n.format("gtquantumcore.machine.author") + " " +
                GTQuantumCoreValues.FORMAT_IRIS_1 + I18n.format("gtquantumcore.machine.author.iris.1") +
                GTQuantumCoreValues.FORMAT_IRIS_2 + I18n.format("gtquantumcore.machine.author.iris.2") +
                GTQuantumCoreValues.FORMAT_IRIS_3 + I18n.format("gtquantumcore.machine.author.iris.3")
        );
    }
}
