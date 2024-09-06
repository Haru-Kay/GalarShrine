package com.haru.galarshrine.mixin;

import com.haru.galarshrine.GalarShrine;
import com.haru.galarshrine.util.OrbAccessor;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.client.render.item.ItemRendererShrineOrb;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.data.IModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(ItemRendererShrineOrb.class)
public class OrbRendererMixin implements OrbAccessor {

    @Shadow(remap = false)
    Item orbType;
    @Shadow(remap = false)
    private float height;

    @Unique
    private boolean galarshrine$galarian;

    @Inject(
            method = "Lcom/pixelmonmod/pixelmon/client/render/item/ItemRendererShrineOrb;getQuads(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Ljava/util/Random;Lnet/minecraftforge/client/model/data/IModelData;)Ljava/util/List;",
            at = @At("TAIL"),
            remap = false,
            cancellable = true
    )
    private void modifyTextureAtlas(BlockState state, Direction side, Random rand, IModelData extraData, CallbackInfoReturnable<List<BakedQuad>> cir){
        if(this.galarshrine$galarian == true){
            List<BakedQuad> quadList = new ArrayList(6);
            TextureAtlasSprite orbTexture = (TextureAtlasSprite) Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(ResourceLocationHelper.of("pixelmon:items/back"));
            quadList.addAll(ItemLayerModel.getQuadsForSprite(0, orbTexture, TransformationMatrix.identity()));
            if (this.orbType == PixelmonItems.uno_orb.getItem()) {
                orbTexture = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(new ResourceLocation(GalarShrine.MOD_ID, "textures/items/galaruno_orb.png"));
            } else if (this.orbType == PixelmonItems.dos_orb.getItem()) {
                orbTexture = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(new ResourceLocation(GalarShrine.MOD_ID, "textures/items/galardos_orb.png"));
            } else if (this.orbType == PixelmonItems.tres_orb.getItem()) {
                orbTexture = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(new ResourceLocation(GalarShrine.MOD_ID, "textures/items/galartres_orb.png"));
            }


            if (this.height >= 1.0F) {
                quadList.addAll(ItemLayerModel.getQuadsForSprite(0, orbTexture, TransformationMatrix.identity()));
            }

            orbTexture = (TextureAtlasSprite)Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(ResourceLocationHelper.of("pixelmon:items/front"));
            quadList.addAll(ItemLayerModel.getQuadsForSprite(0, orbTexture, TransformationMatrix.identity()));
            cir.setReturnValue(quadList);
        }
    }

    @Override
    public void setGalarian(boolean bool) {
        this.galarshrine$galarian = bool;
    }

    @Override
    public boolean getGalarian() {
        return this.galarshrine$galarian;
    }
}
