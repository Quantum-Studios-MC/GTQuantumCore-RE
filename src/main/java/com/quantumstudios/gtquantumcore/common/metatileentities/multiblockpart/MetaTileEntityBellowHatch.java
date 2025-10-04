package com.quantumstudios.gtquantumcore.common.metatileentities.multiblockpart;


import com.quantumstudios.gtquantumcore.api.IBellowHatch;
import com.quantumstudios.gtquantumcore.api.capability.impl.MultiblockAbilities;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.LabelWidget;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockAbilityPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockControllerBase;
import gregtech.common.metatileentities.multi.multiblockpart.MetaTileEntityMultiblockPart;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Matrix4;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class MetaTileEntityBellowHatch extends MetaTileEntityMultiblockPart
        implements IMultiblockAbilityPart<IBellowHatch> {

    public MetaTileEntityBellowHatch(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, 0);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityBellowHatch(metaTileEntityId);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        return ModularUI.builder(GuiTextures.BACKGROUND, 176, 166)
                .label(6, 6, getMetaFullName())
                .widget(new LabelWidget(6, 20, "Bellow Hatch"))
                .bindPlayerInventory(entityPlayer.inventory, GuiTextures.SLOT, 7, 84)
                .build(getHolder(), entityPlayer);
    }

    @Override
    public MultiblockAbility<IBellowHatch> getAbility() {
        return MultiblockAbilities.BELLOW_HATCH;
    }

    @Override
    public void registerAbilities(List<IBellowHatch> list) {
        list.add(new IBellowHatch() {
            @Override
            public int getParallelBonus() {
                return isAttachedToMultiBlock() ? 4 : 0;
            }

            @Override
            public boolean isActive() {
                return isAttachedToMultiBlock();
            }
        });
    }

    @Override
    public boolean isAttachedToMultiBlock() {
        return getController() != null && getController().isStructureFormed();
    }

    @Override
    public MultiblockControllerBase getController() {
        return super.getController();
    }


    @Override
    public ICubeRenderer getBaseTexture() {
        MultiblockControllerBase controller = getController();
        if (controller != null) {
            return controller.getBaseTexture(this);
        }
        return Textures.BRONZE_PLATED_BRICKS;
    }


    // Overlay renderer with SimpleOverlayRenderer
    @Override
    @SideOnly(Side.CLIENT)
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        super.renderMetaTileEntity(renderState, translation, pipeline);

        // Choose overlay based on state
        SimpleOverlayRenderer overlay;
        if (isAttachedToMultiBlock()) {
            overlay = Textures.STEAM_VENT_OVERLAY; // Active - steam/air flow
        } else {
            overlay = Textures.PIPE_OUT_OVERLAY;   // Inactive - simple pipe
        }

        // Render the overlay on the front face
        overlay.renderSided(getFrontFacing(), renderState, translation, pipeline);
    }
}
