package com.haru.galarshrine.util;

import com.haru.galarshrine.GalarShrine;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class GalarOrbChecker {


    public static boolean hasGalarTag(ItemStack item) {
        CompoundNBT nbt = item.getTag();
        String galar = GalarShrine.getConfig().getGalarNBT();
        return nbt.contains(galar) && nbt.getBoolean(galar) == true;
    }

    public static boolean isValidGalarItem(ItemStack item) {
        String orbString = GalarShrine.getConfig().getOrbItem();
        ResourceLocation orbItem = ResourceLocationHelper.of(orbString);
        return ResourceLocationHelper.of(String.valueOf(item.getItem().getRegistryName())).equals(orbItem) && hasGalarTag(item);
    }
}
