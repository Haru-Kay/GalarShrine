package com.haru.galarshrine.listener;

import com.haru.galarshrine.GalarShrine;
import com.pixelmonmod.api.pokemon.PokemonSpecificationProxy;
import com.pixelmonmod.pixelmon.api.config.PixelmonConfigProxy;
import com.pixelmonmod.pixelmon.api.enums.ShrineType;
import com.pixelmonmod.pixelmon.api.events.PlayerActivateShrineEvent;
import com.pixelmonmod.pixelmon.api.pokemon.species.Species;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.util.helpers.RandomHelper;
import com.pixelmonmod.pixelmon.blocks.tileentity.BirdShrineTileEntity;
import com.pixelmonmod.pixelmon.comm.ChatHandler;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.items.ShrineOrbItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GalarShrine.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BirdShrineListener {
    @SubscribeEvent
    public void onUseBirdShrine(PlayerActivateShrineEvent.Pre event){
        PlayerEntity player = event.getPlayer();
        ITextComponent playerName = player.getName();
        ShrineType shrineType = event.getShrineType();
        //ITextComponent text = new StringTextComponent(playerName.getString() + " just tried to use " + shrineType + " shrine.");
        //player.sendMessage(text, Util.NIL_UUID);

        ItemStack item = player.getItemInHand(Hand.MAIN_HAND);
        /*
        protecting against the use case of changing main hand item before event is run. we do not have access to the event's item.
         */
        if(item == null || !(item.getItem() instanceof ShrineOrbItem)){
            event.setCanEncounter(false);
            return;
        }

        if (!player.level.isClientSide) {
            Species species;
            if (item.getItem() == PixelmonItems.uno_orb && shrineType == ShrineType.ARTICUNO) {
                species = (Species)PixelmonSpecies.ARTICUNO.getValueUnsafe();
            } else if (item.getItem() == PixelmonItems.dos_orb && shrineType == ShrineType.ZAPDOS) {
                species = (Species)PixelmonSpecies.ZAPDOS.getValueUnsafe();
            } else {
                if (item.getItem() != PixelmonItems.tres_orb || shrineType != ShrineType.MOLTRES) {
                    ChatHandler.sendChat(player, "pixelmon.blocks.orbwrong", new Object[0]);
                    return;
                }
                species = (Species)PixelmonSpecies.MOLTRES.getValueUnsafe();
            }

            if (item.getTag().contains(GalarShrine.getConfig().getGalarNBT()) && item.getTag().getBoolean(GalarShrine.getConfig().getGalarNBT()) == true) {
                BirdShrineTileEntity shrine = event.getShrine();
                PixelmonEntity pixelmonEntity = PokemonSpecificationProxy.create(new String[]{species.getName()}).create(player.level);
                boolean shiny = PixelmonConfigProxy.getSpawning().getShinyRate(shrine.getLevel().dimension()) > 0.0F && RandomHelper.getRandomChance(1.0F / PixelmonConfigProxy.getSpawning().getShinyRate(shrine.getLevel().dimension()));
                pixelmonEntity.getPokemon().setShiny(shiny);
                pixelmonEntity.setPos((double)shrine.getBlockPos().getX(), (double)(shrine.getBlockPos().getY() + 2), (double)shrine.getBlockPos().getZ());
                //player.sendMessage(new StringTextComponent("Galarian tag detected. Changing to Galar bird."), Util.NIL_UUID);
                pixelmonEntity.setForm("galarian");

                event.setPixelmonEntity(pixelmonEntity);
                //player.sendMessage(new StringTextComponent("Changing encounter to " + pixelmonEntity.getSpecies().getName() + "-" + pixelmonEntity.getForm().getName()), Util.NIL_UUID);
            }

        }
    }
}
