package com.nguyenquyhy.discordbridge.commands;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;
import org.spongepowered.api.profile.GameProfileManager;

public class TempBanCommand implements CommandExecutor {
    BanService service = Sponge.getServiceManager().provide(BanService.class).get();
    GameProfileManager profileManager = Sponge.getServer().getGameProfileManager();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        DiscordBridge mod = DiscordBridge.getInstance();

        GameProfile gp = args.<User>getOne(Text.of("player")).get().getProfile();

        Ban ban = Ban.builder().type(BanTypes.PROFILE).profile(gp)
                .reason(Text.of("The Sponge Council has Spoken!")).build();
        service.addBan(ban);

        src.sendMessage(Text.of(TextColors.GREEN, "Bot account:"));
        return CommandResult.success();
    }
}
