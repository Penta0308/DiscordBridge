package com.nguyenquyhy.discordbridge.commands;

import com.nguyenquyhy.discordbridge.logics.LoginHandler;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Hy on 1/4/2016.
 */
public class LoginConfirmCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        String email = commandContext.<String>getOne("email").get();
        String password = commandContext.<String>getOne("password").get();

        // Sign in to Discord
        commandSource.sendMessage(Text.of(TextColors.GRAY, "Logging in to Discord..."));
        return LoginHandler.login(commandSource, email, password);
    }
}
