package com.mffs.common.items.modules.interdiction;

import com.mffs.SettingConfiguration;
import com.mffs.api.security.IBiometricIdentifier;
import com.mffs.api.security.IInterdictionMatrix;
import com.mffs.api.security.Permission;
import com.mffs.common.items.modules.MatrixModule;
import com.mffs.common.items.modules.projector.ItemModuleShock;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.util.Set;

/**
 * @author Calclavia
 */
public class ItemModuleAntiPersonnel extends MatrixModule {

    @Override
    public boolean onDefend(IInterdictionMatrix matri, EntityLivingBase paramEntityLivingBase) {
        if (paramEntityLivingBase instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) paramEntityLivingBase;
            IBiometricIdentifier bio = matri.getBiometricIdentifier();
            if (!pl.capabilities.isCreativeMode && !pl.isEntityInvulnerable() && bio.isAccessGranted(pl.getGameProfile().getName(), Permission.BYPASS_DEFENSE)) {
                if (SettingConfiguration.COLLECT_ON_PERSONELL_KILL) {
                    Set<Item> safe_items = matri.getFilteredItems();
                    for (int slot = 0; slot < pl.inventory.getSizeInventory(); slot++) {
                        ItemStack stack = pl.inventory.getStackInSlot(slot);
                        if (stack == null || safe_items.contains(stack.getItem())) {
                            continue;
                        }
                        matri.mergeIntoInventory(stack);
                        pl.inventory.setInventorySlotContents(slot, null);
                    }
                }
                pl.setHealth(1F);
                pl.attackEntityFrom(ItemModuleShock.SHOCK_SOURCE, 100F);
                matri.requestFortron(SettingConfiguration.INTERDICTION_MURDER_ENERGY, false);
                pl.addChatMessage(new ChatComponentText("[" + matri.getInventoryName() + "] " + LanguageRegistry.instance().getStringLocalization("message.moduleAntiPersonnel.death")));
            }
        }
        return super.onDefend(matri, paramEntityLivingBase);
    }
}
