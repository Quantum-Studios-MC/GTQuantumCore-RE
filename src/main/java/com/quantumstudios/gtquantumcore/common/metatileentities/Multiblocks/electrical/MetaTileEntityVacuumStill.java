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

public class MetaTileEntityVacuumStill extends RecipeMapMultiblockController {

    /* ------------------------- vacuum state ------------------------- */
    private int vacuumPercent = 0;      // 0-100 %
    private int tickCounter  = 0;

    /* --------------------------- tuning ----------------------------- */
    private static final int VACUUM_SPINUP_PER_SEC      = 2;  // +2 % / s
    private static final int VACUUM_DECAY_NO_LUBE_PS    = 1;  // −1 % / s
    private static final int VACUUM_DECAY_NO_POWER_PS   = 2;  // −2 % / s
    private static final int LUBE_SPIN_MB_T             = 1;  // 1 mB / tick
    private static final int LUBE_HOLD_MB_T             = 2;  // 2 mB / tick
    private static final int MAX_PARALLEL               = 4;  // at 100 % vacuum
    private static final int MIN_VACUUM_FOR_OPERATION   = 25; // below ⇒ machine idles

    /* ---------------------------------------------------------------- */
    public MetaTileEntityVacuumStill(ResourceLocation id) {
        super(id, RecipeMaps.EXTRACTOR_RECIPES);
        /* leave GregTech’s own energy logic untouched – no manual scaling needed */
        this.recipeMapWorkable.setParallelLimit(1);          // default
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tile) {
        return new MetaTileEntityVacuumStill(metaTileEntityId);
    }

    /* -------------------- multiblock shape & visuals ---------------- */
    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCC", "CCC", "CCC")
                .aisle("CCC", "C C", "CCC")
                .aisle("CCC", "C C", "CCC")
                .aisle("CCC", "CSC", "CCC")
                .where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .setMinGlobalLimited(24)
                        .or(autoAbilities())
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY))
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)))
                .build();
    }

    private static IBlockState getCasingState() {
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.SEALED_CASING);
    }

    @Override public ICubeRenderer getBaseTexture(IMultiblockPart part) { return TexturesHandler.SEALED_CASING; }
    @Override protected ICubeRenderer getFrontOverlay()                { return TexturesHandler.ELECTRICAL_SPRENGEL_PUMP_OVERLAY; }

    /* --------------------------- runtime ---------------------------- */
    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();
        if (getWorld().isRemote) return;

        /* --- 20 t clock ------------------------------------------------ */
        tickCounter = (tickCounter + 1) % 20;

        boolean powered = energyContainer != null && energyContainer.getEnergyStored() > 0;
        boolean lubeForSpin = false, lubeForHold = false;

        if (powered && vacuumPercent < 100)        lubeForSpin = drainLubricant(LUBE_SPIN_MB_T);
        else if (powered && vacuumPercent >= 100)  lubeForHold = drainLubricant(LUBE_HOLD_MB_T);

        if (tickCounter == 0) updateVacuum(powered, lubeForSpin, lubeForHold);

        int targetParallel = getCurrentParallelism();
        recipeMapWorkable.setParallelLimit(targetParallel);
        recipeMapWorkable.setWorkingEnabled(canOperate());
    }

    private void updateVacuum(boolean powered, boolean lubeSpin, boolean lubeHold) {
        if (powered && vacuumPercent < 100 && lubeSpin)               vacuumPercent = Math.min(100, vacuumPercent + VACUUM_SPINUP_PER_SEC);
        else if (!powered)                                            vacuumPercent = Math.max(0,    vacuumPercent - VACUUM_DECAY_NO_POWER_PS);
        else if (powered && vacuumPercent < 100 && !lubeSpin)         vacuumPercent = Math.max(0,    vacuumPercent - VACUUM_DECAY_NO_LUBE_PS);
        else if (powered && vacuumPercent >= 100 && !lubeHold)        vacuumPercent = Math.max(0,    vacuumPercent - VACUUM_DECAY_NO_LUBE_PS);
        /* powered + 100 % + lube ⇒ stable vacuum */
    }

    private boolean drainLubricant(int mB) {
        if (mB <= 0) return true;
        FluidStack want = Materials.Lubricant.getFluid(mB);
        if (want == null) return false;

        int need = mB;
        for (IFluidTank tank : getAbilities(MultiblockAbility.IMPORT_FLUIDS)) {
            FluidStack fs = tank.getFluid();
            if (fs != null && fs.getFluid() == want.getFluid() && fs.amount > 0) {
                int take = Math.min(need, fs.amount);
                if (tank instanceof FluidTank) ((FluidTank) tank).drain(take, true);
                else                           fs.amount -= take;
                need -= take;
                if (need <= 0) return true;
            }
        }
        return false;
    }

    /* ------------------------- GUI / HUD ---------------------------- */
    @Override
    protected void addDisplayText(List<ITextComponent> list) {
        super.addDisplayText(list);

        TextFormatting col = vacuumPercent >= 100 ? TextFormatting.GREEN :
                vacuumPercent >= MIN_VACUUM_FOR_OPERATION ? TextFormatting.YELLOW : TextFormatting.RED;
        String state = vacuumPercent >= 100 ? " (OPTIMAL)" :
                vacuumPercent >= MIN_VACUUM_FOR_OPERATION ? " (FUNCTIONAL)" : " (INSUFFICIENT)";

        list.add(new TextComponentString(col + "Vacuum: " + vacuumPercent + "%" + state));

        int par = getCurrentParallelism();
        list.add(new TextComponentString(TextFormatting.AQUA + "Parallel: " + par + "×" +
                (par == MAX_PARALLEL ? " (MAXIMUM)" : par == 1 ? " (BASIC)" : "")));

        if (!canOperate()) list.add(new TextComponentString(TextFormatting.RED + "⚠ Insufficient vacuum for operation"));

        if (energyContainer != null && energyContainer.getEnergyStored() > 0) {
            int rate = vacuumPercent >= 100 ? LUBE_HOLD_MB_T : LUBE_SPIN_MB_T;
            list.add(new TextComponentString(TextFormatting.GRAY + "Lubricant: " + rate + " mB/t"));
        } else {
            list.add(new TextComponentString(TextFormatting.RED + "No power - vacuum decaying"));
        }
    }

    /* ---------------------- helpers / persistence ------------------- */
    private int getCurrentParallelism() { return vacuumPercent >= 100 ? MAX_PARALLEL :
            vacuumPercent >= MIN_VACUUM_FOR_OPERATION ? 1 : 0; }

    private boolean canOperate()        { return vacuumPercent >= MIN_VACUUM_FOR_OPERATION; }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { super.writeToNBT(nbt); nbt.setInteger("Vacuum", vacuumPercent); return nbt; }
    @Override public void readFromNBT(NBTTagCompound nbt)          { super.readFromNBT(nbt); vacuumPercent = Math.min(100, Math.max(0, nbt.getInteger("Vacuum"))); }

    @Override
    public void addInformation(ItemStack stack, @Nullable World w, List<String> tip, boolean adv) {
        super.addInformation(stack, w, tip, adv);
        tip.add(TextFormatting.GRAY   + I18n.format("gtquantumcore.machine.vacuum_still_lv.desc"));
        tip.add(TextFormatting.YELLOW + I18n.format("gtquantumcore.machine.vacuum_still_lv.tooltip.1"));
        tip.add(TextFormatting.GREEN  + I18n.format("gtquantumcore.machine.vacuum_still_lv.tooltip.2"));
        tip.add(TextFormatting.AQUA   + I18n.format("gtquantumcore.machine.vacuum_still_lv.tooltip.3"));
        tip.add("");
        tip.add(TextFormatting.GOLD   + "Parallel Processing:");
        tip.add(TextFormatting.WHITE  + "• 100 % Vacuum: " + TextFormatting.GREEN  + MAX_PARALLEL + "×");
        tip.add(TextFormatting.WHITE  + "• 25-99 % Vacuum: " + TextFormatting.YELLOW + "1×");
        tip.add(TextFormatting.WHITE  + "• <25 % Vacuum: "  + TextFormatting.RED   + "disabled");
    }
}
