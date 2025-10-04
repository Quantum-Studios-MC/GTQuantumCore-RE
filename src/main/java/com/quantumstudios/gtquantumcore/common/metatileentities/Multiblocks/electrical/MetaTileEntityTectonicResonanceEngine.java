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
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.ore.StoneType;
import gregtech.api.worldgen.config.OreConfigUtils;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.common.blocks.BlockWireCoil;
import gregtech.common.blocks.MetaBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nullable;
import java.util.*;

public class MetaTileEntityTectonicResonanceEngine extends RecipeMapMultiblockController {

    // Resonance System State
    private int resonanceFrequency = 0;
    private int geologicalStability = 100;
    private int seismicAmplitude = 0;
    private int tickCounter = 0;
    private int operationCooldown = 0;
    private Random random = new Random();

    // Ore Generation Tracking
    private Map<ChunkPos, Integer> processedChunks = new HashMap<>();

    // Performance optimizations - Caches and queues
    private Map<Material, Map<StoneType, IBlockState>> oreMapCache = new HashMap<>();
    private Map<String, Material[]> materialArrayCache = new HashMap<>();
    private Queue<OreGenerationTask> oreGenerationQueue = new ArrayDeque<>();
    private long lastCacheClean = 0;

    // Configuration constants - INCREASED ORE GENERATION
    private static final int MAX_CHUNK_OPERATIONS = 5; // Increased from 3
    private static final int OPERATION_COOLDOWN_TICKS = 4800; // Reduced from 6000 (4 minutes)
    private static final int RESONANCE_BUILD_RATE = 1;
    private static final int STABILITY_DECAY_RATE = 2;
    private static final int DRILLING_MUD_CONSUMPTION = 200;
    private static final int SEISMIC_CHARGES_CONSUMPTION = 1000;
    private static final int MIN_FREQUENCY_FOR_OPERATION = 50;
    private static final int MIN_STABILITY_FOR_OPERATION = 30;
    private static final int CACHE_CLEAN_INTERVAL = 6000;
    private static final int MAX_ORES_PER_TICK = 3; // Reduced to spread load better

    // Inner class for queued ore generation tasks
    private static class OreGenerationTask {
        final BlockPos pos;
        final Material material;

        OreGenerationTask(BlockPos pos, Material material) {
            this.pos = pos;
            this.material = material;
        }
    }

    public MetaTileEntityTectonicResonanceEngine(ResourceLocation id) {
        super(id, RecipeMaps.COMPRESSOR_RECIPES);
        this.recipeMapWorkable.setParallelLimit(1);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tile) {
        return new MetaTileEntityTectonicResonanceEngine(metaTileEntityId);
    }

    @Override
    protected BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("CCCCC", "CCCCC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "CCCCC")
                .aisle("CCCCC", "CRRRC", "CCCCC")
                .aisle("CCCCC", "CCSCC", "CCCCC")
                .where('S', selfPredicate())
                .where('C', states(getCasingState())
                        .setMinGlobalLimited(50)
                        .or(autoAbilities())
                        .or(abilities(MultiblockAbility.IMPORT_FLUIDS))
                        .or(abilities(MultiblockAbility.IMPORT_ITEMS))
                        .or(abilities(MultiblockAbility.EXPORT_ITEMS))
                        .or(abilities(MultiblockAbility.INPUT_ENERGY))
                        .or(abilities(MultiblockAbility.MAINTENANCE_HATCH)))
                .where('R', states(getResonatorState()))
                .build();
    }

    private static IBlockState getCasingState() {
        return MetaBlocksHandler.MULTIBLOCK_CASING.getState(GTQUMultiblockCasing.CasingType.SEISMIC_CASING);
    }

    private static IBlockState getResonatorState() {
        return MetaBlocks.WIRE_COIL.getState(BlockWireCoil.CoilType.CUPRONICKEL);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart part) {
        return TexturesHandler.SEISMIC_CASING;
    }

    @Override
    protected ICubeRenderer getFrontOverlay() {
        return TexturesHandler.GENERAL_ELECTRIC_OVERLAY_3;
    }

    // Main Machine Logic - Optimized with safety checks
    @Override
    protected void updateFormedValid() {
        super.updateFormedValid();
        if (getWorld().isRemote) return;

        tickCounter = (tickCounter + 1) % 20;
        if (operationCooldown > 0) operationCooldown--;

        // Process queued ore generation tasks to spread workload
        processOreGenerationQueue();

        // Clean cache periodically to prevent memory leaks
        if (tickCounter == 0 && getOffsetTimer() - lastCacheClean > CACHE_CLEAN_INTERVAL) {
            cleanCaches();
            lastCacheClean = getOffsetTimer();
        }

        // Check system resources (only when needed)
        boolean powered = energyContainer != null && energyContainer.getEnergyStored() > 0;
        boolean hasDrillingMud = false;

        // Only check drilling mud if powered (early exit optimization)
        if (powered) {
            hasDrillingMud = drainFluid(Materials.Concrete.getFluid(DRILLING_MUD_CONSUMPTION));
        }

        // Update system state once per second only
        if (tickCounter == 0) {
            updateResonanceSystem(powered, hasDrillingMud);

            if (canPerformOperation()) {
                performTectonicOperation();
            }
        }

        recipeMapWorkable.setWorkingEnabled(false);
    }

    // Process a limited number of ore generation tasks per tick
    private void processOreGenerationQueue() {
        int processed = 0;
        while (!oreGenerationQueue.isEmpty() && processed < MAX_ORES_PER_TICK) {
            OreGenerationTask task = oreGenerationQueue.poll();
            if (task != null) {
                placeGTOreBlockOptimized(getWorld(), task.pos, task.material);
                processed++;
            }
        }
    }

    // Clean caches to prevent memory leaks
    private void cleanCaches() {
        if (oreMapCache.size() > 50) {
            oreMapCache.clear();
        }
        if (materialArrayCache.size() > 20) {
            materialArrayCache.clear();
        }
    }

    private void updateResonanceSystem(boolean powered, boolean hasDrillingMud) {
        if (powered && hasDrillingMud && resonanceFrequency < 100) {
            resonanceFrequency = Math.min(100, resonanceFrequency + RESONANCE_BUILD_RATE);
        } else if (!powered || !hasDrillingMud) {
            resonanceFrequency = Math.max(0, resonanceFrequency - (RESONANCE_BUILD_RATE * 2));
        }

        int targetAmplitude = (int)(resonanceFrequency * 0.8);
        if (seismicAmplitude < targetAmplitude) {
            seismicAmplitude = Math.min(100, seismicAmplitude + 2);
        } else if (seismicAmplitude > targetAmplitude) {
            seismicAmplitude = Math.max(0, seismicAmplitude - 1);
        }

        if (operationCooldown == 0 && geologicalStability < 100) {
            geologicalStability = Math.min(100, geologicalStability + 1);
        }
    }

    private boolean canPerformOperation() {
        return operationCooldown == 0 &&
                resonanceFrequency >= MIN_FREQUENCY_FOR_OPERATION &&
                geologicalStability >= MIN_STABILITY_FOR_OPERATION &&
                hasSeismicCharges();
    }

    private void performTectonicOperation() {
        ChunkPos currentChunk = new ChunkPos(getPos());
        int chunkOperations = processedChunks.getOrDefault(currentChunk, 0);

        if (chunkOperations >= MAX_CHUNK_OPERATIONS) {
            return;
        }

        if (consumeSeismicCharges()) {
            generateOreDeposits(currentChunk);

            processedChunks.put(currentChunk, chunkOperations + 1);
            geologicalStability = Math.max(0, geologicalStability - STABILITY_DECAY_RATE);
            operationCooldown = OPERATION_COOLDOWN_TICKS;

            resonanceFrequency = Math.max(0, resonanceFrequency - 30);
            seismicAmplitude = Math.max(0, seismicAmplitude - 20);
        }
    }

    private boolean hasSeismicCharges() {
        for (IFluidTank tank : getAbilities(MultiblockAbility.IMPORT_FLUIDS)) {
            FluidStack fluid = tank.getFluid();
            if (fluid != null && fluid.getFluid() == Materials.Toluene.getFluid() &&
                    fluid.amount >= SEISMIC_CHARGES_CONSUMPTION) {
                return true;
            }
        }
        return false;
    }

    private boolean consumeSeismicCharges() {
        FluidStack charges = Materials.Toluene.getFluid(SEISMIC_CHARGES_CONSUMPTION);
        return drainFluid(charges);
    }

    // Cached ore map retrieval
    private Map<StoneType, IBlockState> getOreMapCached(Material material) {
        if (material == null) return Collections.emptyMap();

        if (oreMapCache.containsKey(material)) {
            return oreMapCache.get(material);
        }

        Map<StoneType, IBlockState> oreMap = OreConfigUtils.getOreForMaterial(material);
        if (oreMap == null) {
            oreMap = Collections.emptyMap();
        }

        oreMapCache.put(material, oreMap);
        return oreMap;
    }

    // Cached material arrays for ore selection - EXPANDED ORE VARIETY
    private Material[] getMaterialArrayCached(String type) {
        if (materialArrayCache.containsKey(type)) {
            return materialArrayCache.get(type);
        }

        Material[] materials;
        switch (type) {
            case "deep":
                materials = new Material[]{Materials.Diamond, Materials.Bauxite, Materials.Cooperite,
                        Materials.Uraninite, Materials.Pitchblende, Materials.Thorium};
                break;
            case "mid":
                materials = new Material[]{Materials.Iron, Materials.Copper, Materials.Gold, Materials.Silver,
                        Materials.Nickel, Materials.Garnierite, Materials.Galena, Materials.Bauxite};
                break;
            case "shallow":
                materials = new Material[]{Materials.Coal, Materials.CassiteriteSand, Materials.Galena, Materials.Sphalerite,
                        Materials.GreenSapphire, Materials.Sulfur, Materials.Saltpeter};
                break;
            default:
                materials = new Material[]{Materials.Magnetite};
        }

        materialArrayCache.put(type, materials);
        return materials;
    }

    private Material selectOreMaterial(int y) {
        Material[] materials;

        if (y < 30 && resonanceFrequency >= 75) {
            materials = getMaterialArrayCached("deep");
        } else if (y >= 30 && y < 60 && resonanceFrequency >= 50) {
            materials = getMaterialArrayCached("mid");
        } else if (y >= 60) {
            materials = getMaterialArrayCached("shallow");
        } else {
            return Materials.Magnetite;
        }

        return materials[random.nextInt(materials.length)];
    }

    private void generateOreDeposits(ChunkPos chunk) {
        int oreVeins = calculateOreVeins(); // Now generates vein count instead of ore count

        for (int i = 0; i < oreVeins; i++) {
            // Spread ore veins across wider area (not just one chunk)
            int x = chunk.x * 16 + random.nextInt(32) - 8; // Can extend into neighboring chunks
            int z = chunk.z * 16 + random.nextInt(32) - 8;
            int y = 5 + random.nextInt(60);

            BlockPos veinCenter = new BlockPos(x, y, z);

            if (isValidOrePosition(getWorld(), veinCenter)) {
                Material oreMaterial = selectOreMaterial(y);
                if (oreMaterial != null) {
                    queueLargerOreVeinGeneration(veinCenter, oreMaterial);
                }
            }
        }
    }

    private int calculateOreVeins() {
        int baseVeins = 8;
        if (resonanceFrequency >= 90) baseVeins += 8;
        else if (resonanceFrequency >= 80) baseVeins += 6;
        else if (resonanceFrequency >= 65) baseVeins += 4;
        else if (resonanceFrequency >= 50) baseVeins += 2;

        if (seismicAmplitude >= 80) baseVeins += 6;
        else if (seismicAmplitude >= 70) baseVeins += 4;
        else if (seismicAmplitude >= 50) baseVeins += 2;

        // Less penalty for low geological stability
        if (geologicalStability < 40) baseVeins = Math.max(3, baseVeins - 3);

        return baseVeins + random.nextInt(5); // More randomness
    }

    // LARGER ore vein generation to spread across multiple ticks
    private void queueLargerOreVeinGeneration(BlockPos center, Material material) {
        // Create larger, more realistic ore veins
        int veinSize = 3 + random.nextInt(3); // Vein size 3-5 blocks radius

        for (int dx = -veinSize; dx <= veinSize; dx++) {
            for (int dy = -veinSize; dy <= veinSize; dy++) {
                for (int dz = -veinSize; dz <= veinSize; dz++) {
                    // Distance-based probability for more realistic vein shape
                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double probability = Math.max(0.1, 0.8 - (distance / veinSize * 0.6));

                    if (random.nextDouble() < probability) {
                        BlockPos veinPos = center.add(dx, dy, dz);
                        if (isValidOrePosition(getWorld(), veinPos)) {
                            oreGenerationQueue.offer(new OreGenerationTask(veinPos, material));
                        }
                    }
                }
            }
        }
    }

    // Optimized ore block placement with caching and safety checks
    private void placeGTOreBlockOptimized(World world, BlockPos pos, Material material) {
        if (world == null || pos == null || material == null) {
            return;
        }

        try {
            Map<StoneType, IBlockState> oreMap = getOreMapCached(material);

            if (oreMap.isEmpty()) {
                return;
            }

            IBlockState currentState = world.getBlockState(pos);
            if (currentState == null) return;

            Block currentBlock = currentState.getBlock();
            if (currentBlock == null) return;

            StoneType matchingStoneType = null;
            for (StoneType stoneType : oreMap.keySet()) {
                try {
                    IBlockState stoneState = stoneType.stone.get();
                    if (stoneState != null && stoneState.getBlock() == currentBlock) {
                        matchingStoneType = stoneType;
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            IBlockState oreState = null;
            if (matchingStoneType != null) {
                oreState = oreMap.get(matchingStoneType);
            } else {
                for (StoneType stoneType : oreMap.keySet()) {
                    try {
                        IBlockState stoneState = stoneType.stone.get();
                        if (stoneState != null && stoneState.getBlock() == Blocks.STONE) {
                            oreState = oreMap.get(stoneType);
                            break;
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }

                if (oreState == null && !oreMap.isEmpty()) {
                    oreState = oreMap.values().iterator().next();
                }
            }

            if (oreState != null && !oreState.equals(currentState) && isValidOrePosition(world, pos)) {
                world.setBlockState(pos, oreState, 2);
            }
        } catch (Exception e) {
            // Silent error handling
        }
    }

    // FIXED: Added proper null checks and predicate function
    private boolean isValidOrePosition(World world, BlockPos pos) {
        if (world == null || pos == null) {
            return false;
        }

        IBlockState state = world.getBlockState(pos);
        if (state == null) return false;

        Block block = state.getBlock();
        if (block == null) return false;

        // FIXED: Use proper predicate instead of null - THIS FIXES THE CRASH
        return block.isReplaceableOreGen(state, world, pos,
                blockState -> blockState.getBlock() == Blocks.STONE);
    }

    private boolean drainFluid(FluidStack required) {
        if (required == null) return false;

        for (IFluidTank tank : getAbilities(MultiblockAbility.IMPORT_FLUIDS)) {
            FluidStack fluid = tank.getFluid();
            if (fluid != null && fluid.getFluid() == required.getFluid() &&
                    fluid.amount >= required.amount) {

                if (tank instanceof FluidTank) {
                    ((FluidTank) tank).drain(required.amount, true);
                } else {
                    fluid.amount -= required.amount;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    protected void addDisplayText(List<ITextComponent> list) {
        super.addDisplayText(list);

        TextFormatting freqColor = resonanceFrequency >= MIN_FREQUENCY_FOR_OPERATION ?
                TextFormatting.GREEN : TextFormatting.RED;
        list.add(new TextComponentString(freqColor + "Resonance: " + resonanceFrequency + " Hz"));

        TextFormatting stabColor = geologicalStability >= 70 ? TextFormatting.GREEN :
                geologicalStability >= MIN_STABILITY_FOR_OPERATION ? TextFormatting.YELLOW : TextFormatting.RED;
        list.add(new TextComponentString(stabColor + "Geological Stability: " + geologicalStability + "%"));

        TextFormatting ampColor = seismicAmplitude >= 60 ? TextFormatting.AQUA : TextFormatting.BLUE;
        list.add(new TextComponentString(ampColor + "Seismic Amplitude: " + seismicAmplitude + "%"));

        ChunkPos currentChunk = new ChunkPos(getPos());
        int operations = processedChunks.getOrDefault(currentChunk, 0);
        TextFormatting chunkColor = operations >= MAX_CHUNK_OPERATIONS ? TextFormatting.RED :
                operations >= 3 ? TextFormatting.YELLOW : TextFormatting.GREEN;
        list.add(new TextComponentString(chunkColor + "Chunk Operations: " + operations + "/" + MAX_CHUNK_OPERATIONS));

        if (operationCooldown > 0) {
            int minutes = operationCooldown / 1200;
            int seconds = (operationCooldown % 1200) / 20;
            list.add(new TextComponentString(TextFormatting.GOLD + "Cooldown: " + minutes + "m " + seconds + "s"));
        } else if (canPerformOperation()) {
            list.add(new TextComponentString(TextFormatting.GREEN + "Ready for Operation"));
        } else {
            list.add(new TextComponentString(TextFormatting.RED + "Cannot Operate"));
        }

        if (!oreGenerationQueue.isEmpty()) {
            list.add(new TextComponentString(TextFormatting.YELLOW + "Ore Queue: " + oreGenerationQueue.size()));
        }

        list.add(new TextComponentString(TextFormatting.GRAY + "Requires: Drilling Mud, Seismic Charges"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("ResonanceFreq", resonanceFrequency);
        nbt.setInteger("GeologicalStab", geologicalStability);
        nbt.setInteger("SeismicAmp", seismicAmplitude);
        nbt.setInteger("OperationCooldown", operationCooldown);

        NBTTagCompound chunksNBT = new NBTTagCompound();
        for (Map.Entry<ChunkPos, Integer> entry : processedChunks.entrySet()) {
            chunksNBT.setInteger(entry.getKey().x + "," + entry.getKey().z, entry.getValue());
        }
        nbt.setTag("ProcessedChunks", chunksNBT);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        resonanceFrequency = Math.min(100, Math.max(0, nbt.getInteger("ResonanceFreq")));
        geologicalStability = Math.min(100, Math.max(0, nbt.getInteger("GeologicalStab")));
        seismicAmplitude = Math.min(100, Math.max(0, nbt.getInteger("SeismicAmp")));
        operationCooldown = Math.max(0, nbt.getInteger("OperationCooldown"));

        processedChunks.clear();
        NBTTagCompound chunksNBT = nbt.getCompoundTag("ProcessedChunks");
        for (String key : chunksNBT.getKeySet()) {
            String[] coords = key.split(",");
            if (coords.length == 2) {
                try {
                    ChunkPos pos = new ChunkPos(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                    processedChunks.put(pos, chunksNBT.getInteger(key));
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World w, List<String> tip, boolean adv) {
        super.addInformation(stack, w, tip, adv);
        tip.add(TextFormatting.GRAY + I18n.format("gtquantumcore.machine.tectonic_resonance_engine.desc"));
        tip.add(TextFormatting.YELLOW + "Uses seismic resonance to stimulate ore formation");
        tip.add(TextFormatting.GREEN + "Requires drilling mud and seismic charges");
        tip.add(TextFormatting.AQUA + "Limited operations per chunk area");
        tip.add("");
        tip.add(TextFormatting.GOLD + "Enhanced Operation Specs:");
        tip.add(TextFormatting.WHITE + "• Max " + MAX_CHUNK_OPERATIONS + " operations per chunk");
        tip.add(TextFormatting.WHITE + "• 4 minute cooldown between operations");
        tip.add(TextFormatting.WHITE + "• Generates large, realistic ore veins");
        tip.add(TextFormatting.WHITE + "• Higher ore variety and quantity");
        tip.add("");
        tip.add(TextFormatting.RED + "⚠ High power consumption during operation");
    }
}
