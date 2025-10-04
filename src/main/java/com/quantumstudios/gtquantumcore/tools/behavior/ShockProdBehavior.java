package com.quantumstudios.gtquantumcore.tools.behavior;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.api.items.toolitem.behavior.IToolBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class ShockProdBehavior implements IToolBehavior {

    private static final long ENERGY_PER_USE = 100L;

    @Override
    public void hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (!(attacker instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) attacker;

        if (player.world.isRemote) return;

        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        if (electricItem == null) return;

        // Check if we have enough power
        if (electricItem.getCharge() < ENERGY_PER_USE) {
            player.sendStatusMessage(
                    new TextComponentTranslation("com.quantumstudios.gtquantumcore.tool.shock_prod.no_power")
                            .setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.RED)),
                    true
            );
            return;
        }

        electricItem.discharge(ENERGY_PER_USE, electricItem.getTier(), true, false, false);

        target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 5)); // 100 ticks = 5 seconds, level 2

        target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, 10)); // 60 ticks = 3 seconds, level 1

        target.world.playSound(null, target.getPosition(),
                net.minecraft.init.SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT,
                net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 4.0F);

        if (target.world.isRemote) {
            for (int i = 0; i < 8; i++) {
                target.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.REDSTONE,
                        target.posX + (player.world.rand.nextDouble() - 0.5) * target.width,
                        target.posY + target.height * 0.5 + (player.world.rand.nextDouble() - 0.5) * target.height,
                        target.posZ + (player.world.rand.nextDouble() - 0.5) * target.width,
                        0, 0, 1 // Blue-ish particles
                );
            }
        }
    }
}