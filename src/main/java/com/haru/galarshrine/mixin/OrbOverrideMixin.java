package com.haru.galarshrine.mixin;

import com.haru.galarshrine.GalarShrine;
import com.haru.galarshrine.util.OrbAccessor;
import com.pixelmonmod.pixelmon.client.render.item.ItemRendererShrineOrb;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.pixelmonmod.pixelmon.client.render.item.ItemRendererShrineOrb$OverrideList")
public class OrbOverrideMixin {
    @Inject(
            method = "Lcom/pixelmonmod/pixelmon/client/render/item/ItemRendererShrineOrb$OverrideList;resolve(Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/client/renderer/model/IBakedModel;",
            at = @At("TAIL"),
            remap = false
    )
    private void checkGalarian(IBakedModel originalModel, ItemStack stack, ClientWorld world, LivingEntity entity, CallbackInfoReturnable<IBakedModel> cir){

        if (originalModel instanceof ItemRendererShrineOrb) {
            ItemRendererShrineOrb orb = (ItemRendererShrineOrb)originalModel;
            if (orb instanceof OrbAccessor){
                CompoundNBT nbt = stack.getTag();
                if(nbt.getBoolean(GalarShrine.getConfig().getGalarNBT())) {
                    ((OrbAccessor) orb).setGalarian(true);
                } else {
                    ((OrbAccessor) orb).setGalarian(false);
                }
            }
        }
    }
}
