package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.electrical;

import com.quantumstudios.gtquantumcore.recipes.RecipeMapsHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.Recipe;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntitySeawaterProcessor extends RecipeMapMultiblockController {

    // Calcification system
    private int calcificationLevel = 0; // 0-100%
    private boolean isCalcificationCritical = false;
    private long lastCalcificationUpdate = 0;

    // Cleaning system
    private boolean cleaningMode = false;
    private static final int NITRIC_ACID_CONSUMPTION = 25; // mB per cleaning cycle (increased from 10)
    private static final int CLEANING_RATE = 5; // % reduction per cleaning cycle (increased from 2)

    // Calcification parameters
    private static final int BASE_CALCIFICATION_RATE = 1; // % per interval during operation
    private static final int CRITICAL_CALCIFICATION_THRESHOLD = 100;
    private static final int EFFICIENCY_PENALTY_START = 25; // % calcification where efficiency starts dropping

    public MetaTileEntitySeawaterProcessor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMapsHandler.SEAWATER_PROCESSING_RECIPES);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySeawaterProcessor(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CFCCCFC", "FCCFCCF", "CFCCCFC", "FCCFCCF", "CFCCCFC")
                .aisle("CFFCFFC", "FCCFCCF", "CFFCFFC", "FCCFCCF", "CFFCFFC")
                .aisle("CFCFCFC", "FCCCCFC", "CCCCCCC", "FCCCCFC", "CFCFCFC")
                .aisle("FCCFCCF", "CFFCFFC", "FCCCCFC", "CFFCFFC", "FCCFCCF")
                .aisle("CFCSCFC", "FCCFCCF", "CFCCCFC", "FCCFCCF", "CFCCCFC")
                .where('S', selfPredicate())
                .where('C', getCasingPredicate().or(autoAbilities(true, true, true, true, true, true, false)))
                .where('F', frames(Materials.Steel))
                .build();
    }

    private TraceabilityPredicate getCasingPredicate() {
        return states(getCasingState());
    }

    private IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();

        long currentTime = getWorld().getTotalWorldTime();

        // Handle calcification and cleaning every second
        if (currentTime - lastCalcificationUpdate >= 20) {
            lastCalcificationUpdate = currentTime;

            // Always check for cleaning first - works whether running or not
            if (hasNitricAcid() && calcificationLevel > 0) {
                performCleaning();
            }

            // Only increase calcification if actively running and not cleaning
            if (isActive() && (!hasNitricAcid() || cleaningMode)) {
                increaseCalcification();
            }
        }
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe, boolean consumeIfSuccess) {
        // Allow operation even with high calcification if actively cleaning
        if (calcificationLevel >= CRITICAL_CALCIFICATION_THRESHOLD && !hasNitricAcid()) {
            return false;
        }
        return super.checkRecipe(recipe, consumeIfSuccess);
    }

    private void increaseCalcification() {
        if (calcificationLevel >= CRITICAL_CALCIFICATION_THRESHOLD) return;

        // Base calcification rate during operation
        int increase = BASE_CALCIFICATION_RATE;

        // Get current recipe to determine calcification rate
        if (getRecipeMapWorkable().getPreviousRecipe() != null) {
            int recipeEUt = getRecipeMapWorkable().getPreviousRecipe().getEUt();
            if (recipeEUt > 2048) increase *= 3; // EV+ recipes
            else if (recipeEUt > 512) increase *= 2; // HV recipes
        }

        calcificationLevel = Math.min(CRITICAL_CALCIFICATION_THRESHOLD, calcificationLevel + increase);

        // Force shutdown at 100% calcification ONLY if no nitric acid available
        if (calcificationLevel >= CRITICAL_CALCIFICATION_THRESHOLD && !hasNitricAcid()) {
            isCalcificationCritical = true;
            getRecipeMapWorkable().setWorkingEnabled(false);
        }

        markDirty();
    }

    private boolean hasNitricAcid() {
        IFluidHandler fluidHandler = getInputFluidInventory();
        if (fluidHandler != null) {
            FluidStack nitricAcid = new FluidStack(FluidRegistry.getFluid("nitric_acid"), NITRIC_ACID_CONSUMPTION);
            FluidStack available = fluidHandler.drain(nitricAcid, false);
            return available != null && available.amount >= NITRIC_ACID_CONSUMPTION;
        }
        return false;
    }

    private void performCleaning() {
        if (calcificationLevel <= 0) return;

        IFluidHandler fluidHandler = getInputFluidInventory();
        if (fluidHandler != null) {
            // Consume nitric acid (increased consumption for better cleaning)
            FluidStack nitricAcid = new FluidStack(FluidRegistry.getFluid("nitric_acid"), NITRIC_ACID_CONSUMPTION);
            FluidStack drained = fluidHandler.drain(nitricAcid, true);

            if (drained != null && drained.amount >= NITRIC_ACID_CONSUMPTION) {
                // Reduce calcification (5% per second, much faster than buildup)
                int oldCalcification = calcificationLevel;
                calcificationLevel = Math.max(0, calcificationLevel - CLEANING_RATE);

                // Produce waste acid as byproduct (more waste due to higher consumption)
                FluidStack wasteAcid = new FluidStack(FluidRegistry.getFluid("diluted_sulfuric_acid"), 40);
                getOutputFluidInventory().fill(wasteAcid, true);

                // Reset critical status and re-enable working when cleaning makes progress
                if (calcificationLevel < CRITICAL_CALCIFICATION_THRESHOLD) {
                    isCalcificationCritical = false;
                    getRecipeMapWorkable().setWorkingEnabled(true);
                }

                cleaningMode = true;
                markDirty();

                // Debug logging for cleaning effectiveness
                if (oldCalcification != calcificationLevel) {
                    System.out.println("[Seawater Processor] Cleaning: " + oldCalcification + "% -> " + calcificationLevel + "%");
                }
            }
        }
    }

    private int getEfficiencyPenalty() {
        if (calcificationLevel <= EFFICIENCY_PENALTY_START) return 0;

        // Reduce penalty when actively cleaning
        int basePenalty = Math.min(90, calcificationLevel - EFFICIENCY_PENALTY_START);
        if (hasNitricAcid() && cleaningMode) {
            return basePenalty / 2; // 50% less penalty when cleaning
        }
        return basePenalty;
    }

    private TextFormatting getCalcificationColor() {
        // Show better colors when actively cleaning
        if (hasNitricAcid() && cleaningMode) {
            if (calcificationLevel >= 80) return TextFormatting.YELLOW; // Better than red when cleaning
            if (calcificationLevel >= 50) return TextFormatting.GREEN; // Good when cleaning
        }

        if (calcificationLevel >= CRITICAL_CALCIFICATION_THRESHOLD) return TextFormatting.DARK_RED;
        if (calcificationLevel >= 80) return TextFormatting.RED;
        if (calcificationLevel >= 50) return TextFormatting.YELLOW;
        if (calcificationLevel >= 25) return TextFormatting.GOLD;
        return TextFormatting.GREEN;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);

        // Multiblock status header
        textList.add(new TextComponentString("§6Seawater Processing Status:"));

        // Calcification level with color coding
        TextFormatting calcColor = getCalcificationColor();
        String cleaningIndicator = (hasNitricAcid() && cleaningMode) ? " §b[CLEANING]" : "";
        textList.add(new TextComponentString(calcColor + "Calcification Level: " + calcificationLevel + "%" + cleaningIndicator));

        // Detailed status based on calcification level
        if (calcificationLevel >= CRITICAL_CALCIFICATION_THRESHOLD) {
            if (hasNitricAcid()) {
                textList.add(new TextComponentString(TextFormatting.YELLOW + "HIGH CALCIFICATION: Cleaning in progress..."));
                textList.add(new TextComponentString(TextFormatting.BLUE + "Processing continues while cleaning"));
            } else {
                textList.add(new TextComponentString(TextFormatting.DARK_RED + "§lBLOCKED: Cannot process until cleaned"));
                textList.add(new TextComponentString(TextFormatting.GRAY + "Feed nitric acid to restore operation"));
            }
        } else if (calcificationLevel >= 80) {
            textList.add(new TextComponentString(TextFormatting.RED + "CRITICAL: Heavy calcium buildup!"));
            if (!hasNitricAcid()) {
                textList.add(new TextComponentString(TextFormatting.GRAY + "Feed nitric acid to prevent shutdown"));
            }
        } else if (calcificationLevel >= 50) {
            textList.add(new TextComponentString(TextFormatting.YELLOW + "WARNING: Calcification reducing efficiency"));
            textList.add(new TextComponentString(TextFormatting.GRAY + "Processing efficiency reduced by " + getEfficiencyPenalty() + "%"));
        }

        // Operating status with cleaning integration
        if (isActive()) {
            if (hasNitricAcid() && cleaningMode) {
                textList.add(new TextComponentString(TextFormatting.AQUA + "Processing seawater while cleaning..."));
            } else {
                textList.add(new TextComponentString(TextFormatting.GREEN + "Processing seawater..."));
            }
        } else if (hasNitricAcid() && calcificationLevel > 0) {
            textList.add(new TextComponentString(TextFormatting.BLUE + "Cleaning with nitric acid..."));
        }

        // Cleaning information
        if (hasNitricAcid() && calcificationLevel > 0) {
            textList.add(new TextComponentString(TextFormatting.AQUA + "Active cleaning: -" + CLEANING_RATE + "%/second"));
            textList.add(new TextComponentString(TextFormatting.GRAY + "Consuming: " + NITRIC_ACID_CONSUMPTION + "mB/s nitric acid"));
        } else if (!hasNitricAcid() && calcificationLevel > 25) {
            textList.add(new TextComponentString(TextFormatting.GRAY + "Feed nitric acid for cleaning (" + NITRIC_ACID_CONSUMPTION + "mB/s)"));
        }

        // Reset cleaning mode flag after display
        cleaningMode = false;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.CHEMICAL_REACTOR_OVERLAY;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return true;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        tooltip.add(I18n.format("§7Processes infinite seawater into trace elements"));
        tooltip.add(I18n.format("§7Large-scale industrial mineral extraction facility"));
        tooltip.add(I18n.format("§7Requires nitric acid maintenance to prevent calcification"));
        tooltip.add(I18n.format("§7Structure: 7×5×5 steel and frame complex"));

        if (advanced) {
            tooltip.add(I18n.format("§7Calcification builds up during operation"));
            tooltip.add(I18n.format("§7Nitric acid cleaning: " + CLEANING_RATE + "%/second"));
            tooltip.add(I18n.format("§7Cleaning works whether multiblock is active or not"));
            tooltip.add(I18n.format("§7Can continue processing while cleaning if acid available"));
            tooltip.add(I18n.format("§7Consumption: " + NITRIC_ACID_CONSUMPTION + "mB/s when cleaning"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("calcificationLevel", calcificationLevel);
        data.setBoolean("isCalcificationCritical", isCalcificationCritical);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        calcificationLevel = data.getInteger("calcificationLevel");
        isCalcificationCritical = data.getBoolean("isCalcificationCritical");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(calcificationLevel);
        buf.writeBoolean(isCalcificationCritical);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        calcificationLevel = buf.readInt();
        isCalcificationCritical = buf.readBoolean();
    }

    @Override
    public boolean isMultiblockPartWeatherResistant(@Nonnull IMultiblockPart part) {
        return true;
    }
}
