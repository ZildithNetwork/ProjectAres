package tc.oc.commons.bukkit.channels;

import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.rmsy.channels.Channel;
import com.github.rmsy.channels.ChannelsPlugin;
import com.github.rmsy.channels.PlayerManager;
import com.github.rmsy.channels.event.ChannelMessageEvent;
import com.github.rmsy.channels.impl.SimpleChannel;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.Console;
import me.anxuiz.settings.Setting;
import me.anxuiz.settings.SettingBuilder;
import me.anxuiz.settings.types.BooleanType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import tc.oc.api.bukkit.users.OnlinePlayers;
import tc.oc.commons.bukkit.settings.SettingManagerProvider;
import tc.oc.commons.core.commands.Commands;

@Singleton
public class DeveloperChannel extends SimpleChannel implements Commands {

    static final Setting SETTING = new SettingBuilder()
        .name("DeveloperChat").alias("dc")
        .summary("Show developer chat")
        .type(new BooleanType())
        .defaultValue(true).get();

    public static final String PERM_NODE = "chat.developer";
    public static final String PERM_SEND = PERM_NODE + ".send";
    public static final String PERM_RECEIVE = PERM_NODE + ".receive";

    public static final String PREFIX = ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "D" + ChatColor.WHITE + "]";
    public static final String BROADCAST_FORMAT = PREFIX + " {2}";
    public static final String FORMAT =  PREFIX + " {1}" + ChatColor.WHITE + ": {2}";

    private final ConsoleCommandSender console;
    private final OnlinePlayers players;
    private final SettingManagerProvider settings;

    @Inject DeveloperChannel(ConsoleCommandSender console, OnlinePlayers players, SettingManagerProvider settings) {
        super(FORMAT, BROADCAST_FORMAT, new Permission(PERM_RECEIVE, PermissionDefault.OP));
        this.players = players;
        this.settings = settings;
        this.console = console;
    }

    @Command(aliases = "d",
             desc = "Sends a message to the staff channel (or sets the staff channel to your default channel).",
             max = -1,
             min = 0,
             anyFlags = true,
             usage = "[message...]")
    @Console
    @CommandPermissions({PERM_SEND, PERM_RECEIVE})
    public void onDeveloperChatCommand(final CommandContext arguments, final CommandSender sender) throws CommandException {
        if(arguments.argsLength() == 0) {
            if (sender.hasPermission(PERM_RECEIVE)) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerManager playerManager = ChannelsPlugin.get().getPlayerManager();
                    Channel oldChannel = playerManager.getMembershipChannel(player);
                    playerManager.setMembershipChannel(player, this);
                    if (!oldChannel.equals(this)) {
                        sender.sendMessage(org.bukkit.ChatColor.YELLOW + "Changed default channel to developer chat");
                    } else {
                        throw new CommandException("Developer chat is already your default channel");
                    }
                } else {
                    throw new CommandUsageException("You must provide a message.", "/dc <message...>");
                }
            } else {
                throw new CommandPermissionsException();
            }
        } else if (sender.hasPermission(PERM_SEND)) {
            Player sendingPlayer = null;
            if (sender instanceof Player) {
                sendingPlayer = (Player) sender;
            }
            this.sendMessage(arguments.getJoinedStrings(0), sendingPlayer);
            if (!sender.hasPermission(PERM_RECEIVE)) {
                sender.sendMessage(org.bukkit.ChatColor.YELLOW + "Message sent");
            }
        } else {
            throw new CommandPermissionsException();
        }
    }

    @Override
    public void sendMessageToViewer(Player sender, CommandSender viewer, String sanitizedMessage, ChannelMessageEvent event) {
        if(viewer != null && !isEnabled(viewer)) {
            return;
        }

        super.sendMessageToViewer(sender, viewer, sanitizedMessage, event);
    }

    public boolean isEnabled(Player viewer) {
        return (boolean) settings.getManager(viewer)
                                 .getValue(SETTING);
    }

    public boolean isEnabled(CommandSender viewer) {
        return !(viewer instanceof Player) || isEnabled((Player) viewer);
    }

    public boolean isVisible(CommandSender viewer) {
        return viewer.hasPermission(getListeningPermission()) &&
               isEnabled(viewer);
    }

    public Stream<CommandSender> viewers() {
        return Stream.<CommandSender>concat(Stream.of(console),
                                            players.all().stream())
            .filter(this::isVisible);
    }
}
