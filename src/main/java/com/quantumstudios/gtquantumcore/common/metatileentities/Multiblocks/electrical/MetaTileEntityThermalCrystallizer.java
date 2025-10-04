package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.electrical;

import com.quantumstudios.gtquantumcore.blocks.GTQUMultiblockCasing;
import com.quantumstudios.gtquantumcore.blocks.MetaBlocksHandler;
import com.quantumstudios.gtquantumcore.render.TexturesHandler;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityThermalCrystallizer extends RecipeMapMultiblockController {

    /* ----------------------- crystallization state -------------------- */
    private int heatLevel = 0;              // 0-100°C above ambient
    private int crystallizationProgress = 0; // 0-100%
    private int seedDensity = 0;            // 0-100%
    private int tickCounter = 0;

    /* --------------------------- tuning ----------------------------- */
    private static final int HEAT_BUILDUP_RATE = 2;        // +2°C / s with lava
    private static final int HEAT_LOSS_RATE = 1;           // -1°C / s natural cooling
    private static final int LAVA_CONSUMPTION = 50;        // mB/t
    private static final int WATER_CONSUMPTION = 100;      // mB/t for cooling control
    private static final int OPTIMAL_HEAT = 75;            // °C for best crystallization
    private static final int MIN_HEAT_FOR_OPERATION = 25;  // minimum heat needed
    private static final int MAX_PARALLEL = 6;             // at optimal conditions

    /* ---------------------------------------------------------------- */
    public MetaTileEntityThermalCrystallizer(ResourceLocation id) {
        super(id, RecipeMaps.AUTOCLAVE_RECIPES);
        this.recipeMapWorkable.setParallelLimit(1);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tile) {
        return new MetaTileEntityThermalCrystallizer(metaTileEntityId);
    }

    /* -------------------- multiblock shape & visuals ---------------- */
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCCCC", "CCCCC")
                .aisle("CCCCC", "CHHHC", "CCCCC")
                .aisle("CCCCC", "CHCHC", "CCCCC")
                .aisle("CCCCC", "CHHHC", "CCCCC")
                .aisle("CCCCC", "CCSCC", "CCCCC")
                .where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .setMinGlobalLimited(50)
                        .or(autoAbilities())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)))
                .where('H', states(getHeatingChamberState()))
                .build();
    }

    private static IBlockState getCasingState() {
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.CAST_IRON_CASING);
    }

    private static IBlockState getHeatingChamberState() {
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.CAST_IRON_CASING);
    }

    @Override public ICubeRenderer getBaseTexture(IMultiblockPart part) { return TexturesHandler.CAST_IRON_CASING; }
    @Override protected ICubeRenderer getFrontOverlay()                { return Textures.AUTOCLAVE_OVERLAY; }

    /* --------------------------- runtime ---------------------------- */
    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();
        if (getWorld().isRemote) return;

        tickCounter = (tickCounter + 1) % 20;

        boolean powered = energyContainer != null && energyContainer.getEnergyStored() > 0;
        boolean hasLava = false, hasWater = false;

        if (powered) {
            hasLava = drainFluid(Materials.Lava.getFluid(LAVA_CONSUMPTION));
            hasWater = drainFluid(Materials.Water.getFluid(WATER_CONSUMPTION));
        }

        if (tickCounter == 0) {
            updateThermalState(powered, hasLava, hasWater);
            updateCrystallization();
        }

        updateParallelProcessing();
        recipeMapWorkable.setWorkingEnabled(canOperate());
    }

    private void updateThermalState(boolean powered, boolean lava, boolean water) {
        // Heat management
        if (powered && lava && heatLevel < 100) {
            heatLevel = Math.min(100, heatLevel + HEAT_BUILDUP_RATE);
        } else if (!powered || (!lava && heatLevel > 0)) {
            int coolingRate = water ? HEAT_LOSS_RATE * 2 : HEAT_LOSS_RATE;
            heatLevel = Math.max(0, heatLevel - coolingRate);
        }

        // Seed density affects crystallization quality
        if (heatLevel >= MIN_HEAT_FOR_OPERATION) {
            int targetDensity = Math.abs(heatLevel - OPTIMAL_HEAT) < 10 ? 100 : 50;
            if (seedDensity < targetDensity) seedDensity = Math.min(100, seedDensity + 1);
            else if (seedDensity > targetDensity) seedDensity = Math.max(0, seedDensity - 1);
        } else {
            seedDensity = Math.max(0, seedDensity - 2);
        }
    }

    private void updateCrystallization() {
        if (heatLevel >= MIN_HEAT_FOR_OPERATION && seedDensity > 20) {
            int efficiency = calculateCrystallizationEfficiency();
            crystallizationProgress = Math.min(100, crystallizationProgress + (efficiency / 10));
        } else {
            crystallizationProgress = Math.max(0, crystallizationProgress - 1);
        }
    }

    private int calculateCrystallizationEfficiency() {
        int heatEfficiency = 100 - Math.abs(heatLevel - OPTIMAL_HEAT);
        int seedEfficiency = seedDensity;
        return (heatEfficiency + seedEfficiency) / 2;
    }

    private boolean drainFluid(FluidStack want) {
        if (want == null) return false;
        
        for (IFluidTank tank : getAbilities(MultiblockAbility.IMPORT_FLUIDS)) {
            FluidStack fs = tank.getFluid();
            if (fs != null && fs.getFluid() == want.getFluid() && fs.amount >= want.amount) {
                if (tank instanceof FluidTank) {
                    ((FluidTank) tank).drain(want.amount, true);
                } else {
                    fs.amount -= want.amount;
                }
                return true;
            }
        }
        return false;
    }

    private void updateParallelProcessing() {
        int efficiency = calculateCrystallizationEfficiency();
        int parallel;
        
        if (efficiency >= 90) parallel = MAX_PARALLEL;
        else if (efficiency >= 75) parallel = 4;
        else if (efficiency >= 50) parallel = 2;
        else if (efficiency >= 25) parallel = 1;
        else parallel = 0;

        recipeMapWorkable.setParallelLimit(parallel);
    }

    /* ------------------------- GUI / HUD ---------------------------- */
    @Override
    protected void addDisplayText(List<ITextComponent> list) {
        super.addDisplayText(list);

        // Heat Level
        TextFormatting heatColor = Math.abs(heatLevel - OPTIMAL_HEAT) < 10 ? TextFormatting.GREEN :
                heatLevel >= MIN_HEAT_FOR_OPERATION ? TextFormatting.YELLOW : TextFormatting.RED;
        String heatState = Math.abs(heatLevel - OPTIMAL_HEAT) < 10 ? " (OPTIMAL)" :
                heatLevel >= MIN_HEAT_FOR_OPERATION ? " (FUNCTIONAL)" : " (TOO COLD)";
        
        list.add(new TextComponentString(heatColor + "Heat Level: " + heatLevel + "°C" + heatState));

        // Seed Density
        TextFormatting seedColor = seedDensity >= 80 ? TextFormatting.AQUA :
                seedDensity >= 40 ? TextFormatting.BLUE : TextFormatting.GRAY;
        list.add(new TextComponentString(seedColor + "Seed Density: " + seedDensity + "%"));

        // Crystallization Progress
        TextFormatting progColor = crystallizationProgress >= 80 ? TextFormatting.LIGHT_PURPLE :
                crystallizationProgress >= 40 ? TextFormatting.DARK_PURPLE : TextFormatting.GRAY;
        list.add(new TextComponentString(progColor + "Crystallization: " + crystallizationProgress + "%"));

        // Parallel Processing
        int parallel = recipeMapWorkable.getParallelLimit();
        TextFormatting parColor = parallel == MAX_PARALLEL ? TextFormatting.GOLD : 
                parallel >= 2 ? TextFormatting.GREEN : TextFormatting.WHITE;
        list.add(new TextComponentString(parColor + "Parallel: " + parallel + "×"));

        // Efficiency
        int efficiency = calculateCrystallizationEfficiency();
        TextFormatting effColor = efficiency >= 90 ? TextFormatting.GOLD :
                efficiency >= 60 ? TextFormatting.GREEN : TextFormatting.YELLOW;
        list.add(new TextComponentString(effColor + "Efficiency: " + efficiency + "%"));

        // Resource consumption
        if (energyContainer != null && energyContainer.getEnergyStored() > 0) {
            list.add(new TextComponentString(TextFormatting.GRAY + "Lava: " + LAVA_CONSUMPTION + " mB/t"));
            list.add(new TextComponentString(TextFormatting.BLUE + "Water: " + WATER_CONSUMPTION + " mB/t (cooling)"));
        } else {
            list.add(new TextComponentString(TextFormatting.RED + "No power - heat dissipating"));
        }
    }

    /* ---------------------- helpers / persistence ------------------- */
    private boolean canOperate() { 
        return heatLevel >= MIN_HEAT_FOR_OPERATION && seedDensity > 20; 
    }

    @Override 
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) { 
        super.writeToNBT(nbt); 
        nbt.setInteger("HeatLevel", heatLevel);
        nbt.setInteger("CrystallizationProgress", crystallizationProgress);
        nbt.setInteger("SeedDensity", seedDensity);
        return nbt; 
    }

    @Override 
    public void readFromNBT(NBTTagCompound nbt) { 
        super.readFromNBT(nbt); 
        heatLevel = Math.min(100, Math.max(0, nbt.getInteger("HeatLevel")));
        crystallizationProgress = Math.min(100, Math.max(0, nbt.getInteger("CrystallizationProgress")));
        seedDensity = Math.min(100, Math.max(0, nbt.getInteger("SeedDensity")));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World w, List<String> tip, boolean adv) {
        super.addInformation(stack, w, tip, adv);
        tip.add(TextFormatting.GRAY   + I18n.format("gtquantumcore.machine.thermal_crystallizer.desc"));
        tip.add(TextFormatting.YELLOW + "Uses controlled heat to grow crystals from seed materials");
        tip.add(TextFormatting.GREEN  + "Requires lava for heating and water for temperature control");
        tip.add(TextFormatting.AQUA   + "Optimal temperature: " + OPTIMAL_HEAT + "°C");
        tip.add("");
        tip.add(TextFormatting.GOLD   + "Parallel Processing:");
        tip.add(TextFormatting.WHITE  + "• 90%+ Efficiency: " + TextFormatting.GREEN  + MAX_PARALLEL + "×");
        tip.add(TextFormatting.WHITE  + "• 75-89% Efficiency: " + TextFormatting.YELLOW + "4×");
        tip.add(TextFormatting.WHITE  + "• 50-74% Efficiency: " + TextFormatting.GOLD + "2×");
        tip.add(TextFormatting.WHITE  + "• <50% Efficiency: " + TextFormatting.RED + "1× or disabled");
    }
}
