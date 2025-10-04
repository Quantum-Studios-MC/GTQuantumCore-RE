package com.quantumstudios.gtquantumcore.common.metatileentities.Multiblocks.steam;

import gregtech.api.capability.IControllable;
import gregtech.api.capability.impl.FluidTankList;
import gregtech.api.capability.impl.ItemHandlerList;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import gregtech.core.sound.GTSoundEvents;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MetaTileEntitySteamVortexReactor extends MultiblockWithDisplayBase implements IControllable {

    // Vortex mechanics
    private float vortexIntensity = 0.0f; // 0-100 scale
    private int vortexLevel = 0; // 0-10 scale for efficiency tiers
    private long lastVortexUpdate = 0;

    // Breakdown mechanics
    private boolean isBrokenDown = false;
    private long breakdownCooldown = 0;
    private int maintenanceProblems = 0; // Accumulates over time at high intensity

    // Steam generation
    private boolean isOperating = false;
    private int fuelBurnTime = 0;
    private int maxFuelBurnTime = 0;
    private boolean workingEnabled = true;

    // Inventories
    private FluidTankList fluidImportInventory;
    private ItemHandlerList itemImportInventory;
    private FluidTankList steamOutputTank;

    // Constants
    private static final float MAX_VORTEX_INTENSITY = 100.0f;
    private static final float VORTEX_INCREASE_RATE = 2.5f; // per second when operating
    private static final float VORTEX_DECAY_RATE = 1.0f; // per second when idle
    private static final float BREAKDOWN_THRESHOLD = 100.0f; // Breakdown at 100%
    private static final int BREAKDOWN_COOLDOWN_TICKS = 2400; // 2 minutes repair time
    private static final int BASE_STEAM_PRODUCTION = 800; // mB per tick - same as Large Bronze Boiler
    private static final int BASE_WATER_CONSUMPTION = 800; // mB per tick
    private static final int EXPLOSION_CHANCE = 100; // 1% chance per second at 100%

    public MetaTileEntitySteamVortexReactor(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        resetTileAbilities();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntitySteamVortexReactor(metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCCCC", "CSSSSSC", "CSFFFSC", "CSFFFSC", "CSFFFSC", "CSSSSSC", "CCCCCCC")
                .aisle("CFFCFFC", "SFFFFFS", "FVVVVVF", "FVVVVVF", "FVVVVVF", "SFFFFFS", "CFFCFFC")
                .aisle("CFCFCFC", "SFVVVFS", "FVVVVVF", "FVVVVVF", "FVVVVVF", "SFVVVFS", "CFCFCFC")
                .aisle("CFFCFFC", "SFFFFFS", "FVVVVVF", "FVVVVVF", "FVVVVVF", "SFFFFFS", "CFFCFFC")
                .aisle("CCCCCCC", "CSSSSSC", "CSFFFSC", "CSFKFSC", "CSFFFSC", "CSSSSSC", "CCCCCCC")
                .where('K', selfPredicate())
                .where('C', states(getCasingState()).setMinGlobalLimited(20)
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS).setMinGlobalLimited(1))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS).setMaxGlobalLimited(1))
                        .or(abilities(MultiblockAbility.EXPORT_FLUIDS).setMinGlobalLimited(1))
                        .or(autoAbilities()))
                .where('S', states(getSteamCasingState()))
                .where('F', frames(Materials.Steel))
                .where('V', states(getVortexCoreState()))
                .build();
    }

    private IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.BRONZE_BRICKS);
    }

    private IBlockState getSteamCasingState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.BRONZE_PIPE);
    }

    private IBlockState getVortexCoreState() {
        return MetaBlocks.TRANSPARENT_CASING.getDefaultState();
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        initializeAbilities();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetTileAbilities();
        this.vortexIntensity = 0.0f;
        this.isOperating = false;
        this.isBrokenDown = false;
    }

    private void initializeAbilities() {
        this.fluidImportInventory = new FluidTankList(true, getAbilities(MultiblockAbility.IMPORT_FLUIDS));
        this.itemImportInventory = new ItemHandlerList(getAbilities(MultiblockAbility.IMPORT_ITEMS));
        this.steamOutputTank = new FluidTankList(true, getAbilities(MultiblockAbility.EXPORT_FLUIDS));
    }

    private void resetTileAbilities() {
        this.fluidImportInventory = new FluidTankList(true);
        this.itemImportInventory = new ItemHandlerList(Collections.emptyList());
        this.steamOutputTank = new FluidTankList(true);
    }

    @Override
    protected void updateFormedValid() {
        if (!getWorld().isRemote) {
            long currentTime = getWorld().getTotalWorldTime();

            // Update vortex mechanics every second
            if (currentTime - lastVortexUpdate >= 20) {
                lastVortexUpdate = currentTime;
                updateVortexMechanics();
            }

            // Handle breakdown cooldown
            if (isBrokenDown && breakdownCooldown > 0) {
                breakdownCooldown--;
                if (breakdownCooldown <= 0) {
                    isBrokenDown = false;
                    maintenanceProblems = 0;
                }
            }

            // Generate steam if we can operate and not broken down
            if (workingEnabled && !isBrokenDown && canOperate()) {
                generateSteam();
                isOperating = true;
            } else {
                isOperating = false;
            }
        }
    }

    private void updateVortexMechanics() {
        if (isBrokenDown) {
            // Force decay when broken down
            vortexIntensity = Math.max(0, vortexIntensity - (VORTEX_DECAY_RATE * 2));
            return;
        }

        if (isOperating && canOperate()) {
            // Increase vortex intensity when operating
            float increaseRate = VORTEX_INCREASE_RATE;

            // Check for cooling water at high intensity
            boolean hasCooling = hasCoolingWater();
            if (!hasCooling && vortexIntensity > 70) {
                increaseRate *= 0.5f; // Slower buildup without cooling
            }

            vortexIntensity = Math.min(MAX_VORTEX_INTENSITY, vortexIntensity + increaseRate);

            // Accumulate maintenance problems at high intensity
            if (vortexIntensity >= 90) {
                maintenanceProblems++;

                // Check for breakdown at 100% intensity
                if (vortexIntensity >= BREAKDOWN_THRESHOLD) {
                    checkForBreakdown();
                }
            }
        } else {
            // Decay when idle
            float decayRate = hasCoolingWater() ? VORTEX_DECAY_RATE * 2 : VORTEX_DECAY_RATE;
            vortexIntensity = Math.max(0, vortexIntensity - decayRate);

            // Maintenance problems slowly decay when not at dangerous levels
            if (vortexIntensity < 80 && maintenanceProblems > 0) {
                if (getWorld().rand.nextInt(20) == 0) { // 5% chance per second
                    maintenanceProblems--;
                }
            }
        }

        // Calculate vortex level (0-10 for efficiency tiers)
        vortexLevel = (int)(vortexIntensity / 10);

        markDirty();
    }

    private void checkForBreakdown() {
        // Higher maintenance problems = higher breakdown chance
        int breakdownChance = Math.min(500, 50 + maintenanceProblems); // 0.5% to 5% per second

        if (getWorld().rand.nextInt(10000) < breakdownChance) {
            triggerBreakdown();
        }

        // Small explosion chance at maximum intensity
        if (getWorld().rand.nextInt(EXPLOSION_CHANCE * 100) == 0) { // 0.01% chance per second
            triggerExplosion();
        }
    }

    private void triggerBreakdown() {
        isBrokenDown = true;
        breakdownCooldown = BREAKDOWN_COOLDOWN_TICKS;
        vortexIntensity = 0; // Emergency shutdown

        // Create explosion effect without damage
        getWorld().createExplosion(null, getPos().getX() + 0.5, getPos().getY() + 2, getPos().getZ() + 0.5,
                2.0f, false);
    }

    private void triggerExplosion() {
        // Small chance of actual explosion that breaks some blocks
        getWorld().createExplosion(null, getPos().getX() + 0.5, getPos().getY() + 2, getPos().getZ() + 0.5,
                4.0f, true);

        // Force structure to break
        invalidateStructure();
    }

    private boolean canOperate() {
        return hasFuel() && hasWater();
    }

    private boolean hasFuel() {
        if (fuelBurnTime > 0) return true;

        if (itemImportInventory != null && itemImportInventory.getSlots() > 0) {
            ItemStack fuelStack = itemImportInventory.getStackInSlot(0);
            return !fuelStack.isEmpty() && getBurnTime(fuelStack) > 0;
        }
        return false;
    }

    private boolean hasWater() {
        if (fluidImportInventory != null) {
            int waterNeeded = getWaterConsumption();
            FluidStack water = new FluidStack(FluidRegistry.getFluid("water"), waterNeeded);
            FluidStack available = fluidImportInventory.drain(water, false);
            return available != null && available.amount >= waterNeeded;
        }
        return false;
    }

    private boolean hasCoolingWater() {
        if (fluidImportInventory != null) {
            int totalWater = getWaterConsumption();
            FluidStack water = new FluidStack(FluidRegistry.getFluid("water"), totalWater);
            FluidStack available = fluidImportInventory.drain(water, false);
            return available != null && available.amount >= totalWater;
        }
        return false;
    }

    private int getWaterConsumption() {
        int baseWater = BASE_WATER_CONSUMPTION;

        // Extra water needed for cooling at high intensity
        if (vortexIntensity > 70) {
            float waterMultiplier = 1.0f + ((vortexIntensity - 70) / 30f); // Up to 2x water at 100%
            baseWater = (int)(baseWater * waterMultiplier);
        }

        return baseWater;
    }

    private void generateSteam() {
        // Handle fuel consumption
        if (fuelBurnTime <= 0) {
            if (itemImportInventory != null && itemImportInventory.getSlots() > 0) {
                ItemStack fuelStack = itemImportInventory.getStackInSlot(0);
                if (!fuelStack.isEmpty()) {
                    int burnTime = getBurnTime(fuelStack);
                    if (burnTime > 0) {
                        fuelBurnTime = maxFuelBurnTime = burnTime;
                        fuelStack.shrink(1);
                        if (itemImportInventory instanceof IItemHandlerModifiable) {
                            ((IItemHandlerModifiable) itemImportInventory).setStackInSlot(0, fuelStack);
                        }
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        fuelBurnTime--;

        // Consume water
        int waterNeeded = getWaterConsumption();
        FluidStack water = new FluidStack(FluidRegistry.getFluid("water"), waterNeeded);
        FluidStack drainedWater = fluidImportInventory.drain(water, true);

        if (drainedWater == null || drainedWater.amount < BASE_WATER_CONSUMPTION) {
            return;
        }

        // Calculate steam output - simple multiplier based on vortex
        float steamMultiplier = 1.0f + (vortexIntensity / 100f); // 1.0x to 2.0x steam

        int steamAmount = (int)(BASE_STEAM_PRODUCTION * steamMultiplier);
        FluidStack steam = new FluidStack(FluidRegistry.getFluid("steam"), steamAmount);

        if (steamOutputTank != null) {
            steamOutputTank.fill(steam, true);
        }
    }

    private int getBurnTime(ItemStack stack) {
        if (stack.getItem() == net.minecraft.init.Items.COAL) return 1600;
        if (stack.getItem() == net.minecraft.init.Items.BLAZE_ROD) return 2400;
        if (stack.getItem() == net.minecraft.init.Blocks.COAL_BLOCK.getItemDropped(null, null, 0)) return 16000;
        if (stack.getItem() == net.minecraft.init.Items.COAL && stack.getMetadata() == 1) return 1600; // Charcoal
        return 0;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        super.addDisplayText(textList);

        textList.add(new TextComponentString(TextFormatting.GOLD + "Steam Vortex Reactor"));

        textList.add(new TextComponentString(
                TextFormatting.YELLOW + "Vortex: " + String.format("%.1f", vortexIntensity) + "%" +
                        TextFormatting.GRAY + "   Level: " + vortexLevel + "/10"
        ));

        float steamMultiplier = 1.0f + (vortexIntensity / 100f);
        textList.add(new TextComponentString(
                TextFormatting.AQUA + "Steam: " +
                        String.format("%.1fx (%d mB/t)", steamMultiplier, (int)(BASE_STEAM_PRODUCTION * steamMultiplier))
        ));

        if (fuelBurnTime > 0) {
            textList.add(new TextComponentString(
                    TextFormatting.GRAY + "Fuel: " + String.format("%.0f", (float)fuelBurnTime / maxFuelBurnTime * 100) + "%"));
        }

        // Status display
        if (isBrokenDown) {
            int minutes = (int)(breakdownCooldown / 1200);
            int seconds = (int)((breakdownCooldown % 1200) / 20);
            textList.add(new TextComponentString(
                    TextFormatting.DARK_RED + "§lBROKEN DOWN: Repairing " + minutes + ":" + String.format("%02d", seconds)
            ));
        } else if (isOperating) {
            textList.add(new TextComponentString(TextFormatting.GREEN + "Active"));

            if (vortexIntensity >= 95) {
                textList.add(new TextComponentString(TextFormatting.DARK_RED + "§l⚠ CRITICAL: Explosion risk!"));
            } else if (vortexIntensity >= 85) {
                textList.add(new TextComponentString(TextFormatting.RED + "§lDANGER: High breakdown risk!"));
            }

            if (maintenanceProblems > 50) {
                textList.add(new TextComponentString(TextFormatting.YELLOW + "Maintenance needed"));
            }
        } else {
            textList.add(new TextComponentString(TextFormatting.DARK_GRAY + "Idle"));
        }
    }

    @Override
    public boolean isActive() {
        return super.isActive() && isOperating && workingEnabled && !isBrokenDown;
    }

    @Override
    public String[] getDescription() {
        return new String[] { I18n.format("gtquantumcore.multiblock.steam_vortex_reactor.description") };
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, @NotNull List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        tooltip.add(I18n.format("§7Advanced steam generator with vortex technology"));
        tooltip.add(I18n.format("§7Up to 2x steam output, but high intensity risks breakdown"));
        tooltip.add(I18n.format("§7§c⚠ Can break down or explode at 100% vortex intensity"));
        tooltip.add(I18n.format("§7Structure: 7×5×7 vortex chamber complex"));

        if (advanced) {
            tooltip.add(I18n.format("§7Base Production: " + BASE_STEAM_PRODUCTION + " mB/t steam"));
            tooltip.add(I18n.format("§7Optimal operation: 80-90% vortex intensity"));
            tooltip.add(I18n.format("§7Cooling water reduces breakdown risk"));
            tooltip.add(I18n.format("§7Breakdown repair time: 2 minutes"));
        }
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    protected ICubeRenderer getFrontOverlay() {
        return Textures.ASSEMBLER_OVERLAY;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart sourcePart) {
        return Textures.BRONZE_PLATED_BRICKS;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return true;
    }

    @Override
    public SoundEvent getSound() {
        return GTSoundEvents.BOILER;
    }

    @Override
    protected boolean shouldUpdate(MTETrait trait) {
        return true;
    }

    @Override
    public IItemHandlerModifiable getImportItems() {
        return itemImportInventory;
    }

    @Override
    public FluidTankList getImportFluids() {
        return fluidImportInventory;
    }

    @Override
    public FluidTankList getExportFluids() {
        return steamOutputTank;
    }

    // IControllable implementation
    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        this.workingEnabled = isWorkingAllowed;
    }

    // NBT and sync data
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setFloat("vortexIntensity", vortexIntensity);
        data.setInteger("vortexLevel", vortexLevel);
        data.setBoolean("isOperating", isOperating);
        data.setBoolean("workingEnabled", workingEnabled);
        data.setBoolean("isBrokenDown", isBrokenDown);
        data.setLong("breakdownCooldown", breakdownCooldown);
        data.setInteger("maintenanceProblems", maintenanceProblems);
        data.setInteger("fuelBurnTime", fuelBurnTime);
        data.setInteger("maxFuelBurnTime", maxFuelBurnTime);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        vortexIntensity = data.getFloat("vortexIntensity");
        vortexLevel = data.getInteger("vortexLevel");
        isOperating = data.getBoolean("isOperating");
        workingEnabled = data.getBoolean("workingEnabled");
        isBrokenDown = data.getBoolean("isBrokenDown");
        breakdownCooldown = data.getLong("breakdownCooldown");
        maintenanceProblems = data.getInteger("maintenanceProblems");
        fuelBurnTime = data.getInteger("fuelBurnTime");
        maxFuelBurnTime = data.getInteger("maxFuelBurnTime");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeFloat(vortexIntensity);
        buf.writeInt(vortexLevel);
        buf.writeBoolean(isOperating);
        buf.writeBoolean(isBrokenDown);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        vortexIntensity = buf.readFloat();
        vortexLevel = buf.readInt();
        isOperating = buf.readBoolean();
        isBrokenDown = buf.readBoolean();
    }
}
