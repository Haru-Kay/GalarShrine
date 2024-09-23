package com.haru.galarshrine.mixin;

import com.haru.galarshrine.GalarShrine;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.items.ShrineOrbItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.haru.galarshrine.util.GalarOrbChecker.hasGalarTag;
import static com.haru.galarshrine.util.GalarOrbChecker.isValidGalarItem;

@Mixin(ShrineOrbItem.class)
public class OrbMixin {

    @Shadow(remap = false)
    public static int full;

    @Inject(
            method = "Lcom/pixelmonmod/pixelmon/items/ShrineOrbItem;inventoryTick(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;IZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void fillGalarOrb(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5, CallbackInfo ci){
        if (hasGalarTag(par1ItemStack) && par3Entity instanceof PlayerEntity) {
            int damage = par1ItemStack.getDamageValue();
            if (damage >= full) {
                PlayerEntity player = (PlayerEntity) par3Entity;
                ResourceLocation orbRL = ResourceLocationHelper.of(GalarShrine.getConfig().getOrbItem());
                ItemStack galarOrb = new ItemStack(Registry.ITEM.get(orbRL));
                CompoundNBT nbt = new CompoundNBT();
                nbt.putBoolean(GalarShrine.getConfig().getGalarNBT(), true);
                CompoundNBT nameNBT = new CompoundNBT();
                String name;
                if (par1ItemStack.getItem() == PixelmonItems.uno_orb) {
                    nbt.putInt("CustomModelData", 1);
                    name = GalarShrine.getConfig().getUnoName();
                } else if (par1ItemStack.getItem() == PixelmonItems.dos_orb) {
                    nbt.putInt("CustomModelData", 2);
                    name = GalarShrine.getConfig().getDosName();
                } else {
                    nbt.putInt("CustomModelData", 3);
                    name = GalarShrine.getConfig().getTresName();
                }
                nameNBT.putString("Name", "[\"\", {\"text\":\"" + name + "\",\"italic\":false}]");
                nbt.put("display", nameNBT);
                galarOrb.setTag(nbt);
                par1ItemStack.shrink(1);
                player.inventory.add(galarOrb);
                ci.cancel();
            }
        }
    }
}
