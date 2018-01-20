package tc.oc.commons.bukkit.prestiges;

import javax.inject.Inject;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tc.oc.minecraft.scheduler.SyncExecutor;
import tc.oc.commons.bukkit.chat.Audiences;
import tc.oc.commons.bukkit.chat.PlayerComponent;
import tc.oc.commons.bukkit.commands.UserFinder;
import tc.oc.commons.bukkit.nick.IdentityProvider;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.commands.CommandFutureCallback;
import tc.oc.commons.core.commands.Commands;
import tc.oc.commons.bukkit.prestiges.PrestigeUtil;

public class PrestigeCommands implements Commands {


    private final UserFinder userFinder;
    private final SyncExecutor executor;
    private final Audiences audiences;
    private final IdentityProvider identityProvider;

    @Inject PrestigeCommands(UserFinder userFinder, SyncExecutor executor, Audiences audiences, IdentityProvider identityProvider) {
        this.userFinder = userFinder;
        this.executor = executor;
        this.audiences = audiences;
        this.identityProvider = identityProvider;
    }

    @Command(
        aliases = {"prestige"},
                usage = "",
                desc = "Prestige once you have at least ???????",
                min = 0,
                max = 0
    )
    public void prestige(final CommandContext args, final CommandSender sender) throws CommandException {
        executor.callback(
                userFinder.findUser(sender, args, 0, UserFinder.Default.SENDER),
                CommandFutureCallback.onSuccess(sender, args -> {
                    final boolean self = sender instanceof Player && ((Player) sender).getUniqueId().equals(result.user.uuid());
                    final int raindrops;

                    raindrops = sender.user.raindrops();


                }
        );
    }
}