package com.haru.galarshrine.mixin;

import com.haru.galarshrine.GalarShrine;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.enums.ShrineType;
import com.pixelmonmod.pixelmon.api.events.PlayerActivateShrineEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.api.util.EncounterData;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.blocks.machines.ShrineBlock;
import com.pixelmonmod.pixelmon.blocks.tileentity.BirdShrineTileEntity;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.enums.EnumEncounterMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BirdShrineTileEntity.class)
public class ShrineBlockMixin extends TileEntity {

    @Shadow(remap = false)
    public EncounterData encounters;

    public ShrineBlockMixin(TileEntityType<?> p_i48289_1_) {
        super(p_i48289_1_);
    }

    @Inject(
            remap = false,
            method = "Lcom/pixelmonmod/pixelmon/blocks/tileentity/BirdShrineTileEntity;activate(Lnet/minecraft/entity/player/PlayerEntity;Lcom/pixelmonmod/pixelmon/blocks/machines/ShrineBlock;Lnet/minecraft/block/BlockState;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void galarOrbActivate(PlayerEntity player, ShrineBlock block, BlockState state, ItemStack item, CallbackInfo ci){
        System.out.println("injected");
        if(isValidGalarItem(item)) {
            if (!player.level.isClientSide) {
                Species species;
                int bird = item.getTag().getInt("CustomModelData");


                if (bird == 1 && block.getRockType() == ShrineType.ARTICUNO) {
                    species = (Species) PixelmonSpecies.ARTICUNO.getValueUnsafe();
                } else if (bird == 2 && block.getRockType() == ShrineType.ZAPDOS) {
                    species = (Species)PixelmonSpecies.ZAPDOS.getValueUnsafe();
                } else {
                    if (bird != 3 || block.getRockType() != ShrineType.MOLTRES) {
                        ChatHandler.sendChat(player, "pixelmon.blocks.orbwrong", new Object[0]);
                        return;
                    }

                    species = (Species)PixelmonSpecies.MOLTRES.getValueUnsafe();
                }

                PlayerPartyStorage party = StorageProxy.getParty((ServerPlayerEntity)player);
                boolean canEncounter = this.encounters.canEncounter(player);
                PixelmonEntity pixelmonEntity = PokemonSpecificationProxy.create(new String[]{species.getName()}).create(player.level);
                boolean shiny = PixelmonConfigProxy.getSpawning().getShinyRate(this.level.dimension()) > 0.0F && RandomHelper.getRandomChance(1.0F / PixelmonConfigProxy.getSpawning().getShinyRate(this.level.dimension()));
                pixelmonEntity.setForm("galarian");
                pixelmonEntity.getPokemon().setShiny(shiny);
                pixelmonEntity.setPos((double)this.worldPosition.getX(), (double)(this.worldPosition.getY() + 2), (double)this.worldPosition.getZ());
                PixelmonEntity startingPixelmon = party.getAndSendOutFirstAblePokemon(player);
                boolean hasParty = startingPixelmon != null && BattleRegistry.getBattle(player) == null;
                if (!hasParty) {
                    ChatHandler.sendChat(player, "pixelmon.blocks.partyfainted", new Object[0]);
                    return;
                }

                PlayerActivateShrineEvent.Pre event = new PlayerActivateShrineEvent.Pre(this.getBlockPos(), (ServerPlayerEntity)player, block, block.getRockType(), (BirdShrineTileEntity) (Object) this, canEncounter, pixelmonEntity);
                if (Pixelmon.EVENT_BUS.post(event)) {
                    return;
                }

                if (event.canEncounter()) {
                    if (this.encounters.getMode() == EnumEncounterMode.Once) {
                        player.level.setBlock(this.worldPosition, (BlockState)state.setValue(ShrineBlock.USED, true), 2);
                    }

                    this.encounters.registerEncounter(player);
                    item.shrink(1);
                    player.level.addFreshEntity(event.getPixelmonEntity());
                    PlayerParticipant playerParticipant = new PlayerParticipant((ServerPlayerEntity)player, new PixelmonEntity[]{startingPixelmon});
                    WildPixelmonParticipant wildPixelmonParticipant = new WildPixelmonParticipant(false, new PixelmonEntity[]{event.getPixelmonEntity()});
                    wildPixelmonParticipant.startedBattle = true;
                    BattleRegistry.startBattle(playerParticipant, wildPixelmonParticipant);
                    Pixelmon.EVENT_BUS.post(new PlayerActivateShrineEvent.Post(this.getBlockPos(), (ServerPlayerEntity)player, block, block.getRockType(), (BirdShrineTileEntity) (Object) this, canEncounter, pixelmonEntity));
                } else if (this.encounters.getMode().isTimedAccess()) {
                    ChatHandler.sendChat(player, "pixelmon.blocks.shrine.today", new Object[0]);
                } else {
                    ChatHandler.sendChat(player, "pixelmon.blocks.shrine.encountered", new Object[0]);
                }
            }

            ci.cancel();
        }
    }

    private boolean hasGalarTag(ItemStack item) {
        CompoundNBT nbt = item.getTag();
        String galar = GalarShrine.getConfig().getGalarNBT();
        return nbt.contains(galar) && nbt.getBoolean(galar) == true;
    }

    private boolean isValidGalarItem(ItemStack item) {
        String orbString = GalarShrine.getConfig().getOrbItem();
        ResourceLocation orbItem = new ResourceLocation(orbString);
        System.out.println(orbItem);
        System.out.println(item.getItem().getRegistryName());
        return item.getItem().getRegistryName().equals(orbItem) && hasGalarTag(item);
    }


}
