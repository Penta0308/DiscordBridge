package com.nguyenquyhy.discordbridge.commands;

import com.nguyenquyhy.discordbridge.logics.LoginHandler;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

/**
 * Created by Hy on 1/4/2016.
 */
public class LogoutCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        return LoginHandler.logout(commandSource, false);
    }
}
