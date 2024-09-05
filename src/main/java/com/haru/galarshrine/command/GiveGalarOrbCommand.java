package com.haru.galarshrine.command;

import com.haru.galarshrine.GalarShrine;
import com.haru.galarshrine.config.GalarShrineConfig;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pixelmonmod.pixelmon.api.command.PixelmonCommandUtils;
import com.pixelmonmod.pixelmon.api.registries.PixelmonItems;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.command.PixelCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GiveGalarOrbCommand extends PixelCommand {

    public GiveGalarOrbCommand(CommandDispatcher<CommandSource> dispatcher) {
        super(dispatcher, "giveOrb", "/giveOrb <player> <Articuno|Zapdos|Moltres> <empty|full|value> [galar]", 2);
    }

    @Override
    public void execute(CommandSource sender, String[] args) throws CommandException, CommandSyntaxException {
        if (args.length >= 1) {
            args = PixelmonCommandUtils.setupCommandTargets(this, sender, args, 0);
            GameProfile profile;
            if (PixelmonSpecies.has(args[0].toLowerCase(Locale.ROOT)) && ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(args[0]) == null) {
                profile = PixelmonCommandUtils.requireEntityPlayer(sender).getGameProfile();
            } else {
                profile = PixelmonCommandUtils.findProfile(args[0]);
            }

            if (profile == null) {
                PixelmonCommandUtils.endCommand("argument.entity.notfound.player", new Object[]{args[0]});
            }

            ServerPlayerEntity player = PixelmonCommandUtils.getEntityPlayer(profile.getId());

            if (profile.getName().equalsIgnoreCase(args[0])) {
                args = (String[])Arrays.copyOfRange(args, 1, args.length);
            }

            ItemStack orb;
            switch (args[0]) {
                case "Articuno":
                    orb = new ItemStack(PixelmonItems.uno_orb);
                    break;
                case "Zapdos":
                    orb = new ItemStack(PixelmonItems.dos_orb);
                    break;
                case "Moltres":
                    orb = new ItemStack(PixelmonItems.tres_orb);
                    break;
                default:
                    PixelmonCommandUtils.endCommand("Argument " + args[0] + " not valid.", new Object[]{args[0]});
                    return;
            }

            CompoundNBT nbt = orb.getTag();
            if(args[1].equals("full")) {
                nbt.putInt("Damage", 375);
            } else if (args[1] == "value") {
                System.out.println("value");
                PixelmonCommandUtils.endCommand("Please replace <value> with an integer.", new Object[]{args[0]});
                return;
            } else {
                try {
                    int damage = Integer.parseInt(args[1]);
                    nbt.putInt("Damage", damage);
                } catch (NumberFormatException e) {}
            }
            orb.setTag(nbt);

            boolean galar = false;
            if(args.length > 2 && args[2].equals("galar")) {
                nbt.putBoolean(GalarShrine.getConfig().getGalarNBT(), true);
                galar = true;
            }
            orb.setTag(nbt);

            String text = " a " + (galar ? "Galarian " : "") + args[0] + " orb.";
            sender.sendSuccess(new StringTextComponent("Gave " + player.getName().getString() + text), false);
            player.inventory.add(orb);
        } else {
            sender.sendSuccess(PixelmonCommandUtils.format(TextFormatting.RED, "pixelmon.command.general.invalid", new Object[0]), false);
            PixelmonCommandUtils.endCommand(this.getUsage(sender), new Object[0]);
        }
    }

    public List<String> getTabCompletions(MinecraftServer server, CommandSource sender, String[] args, BlockPos pos) throws CommandSyntaxException {
        List<String> pokemon = Arrays.asList("Articuno", "Zapdos", "Moltres");
        switch (args.length) {
            case 0:
                return super.getTabCompletions(server, sender, args, pos);
            case 1:
                return PixelmonCommandUtils.tabCompleteUsernames();
            case 2:
                return pokemon;
            case 3:
                return Arrays.asList("full", "empty", "value");
            default:
                return Arrays.asList("");
        }
    }
}
