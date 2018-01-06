package tc.oc.commons.bukkit.experience;

import javax.annotation.Nullable;
import javax.inject.Inject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventBus;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import java.time.Duration;
import tc.oc.api.bukkit.users.BukkitUserStore;
import tc.oc.api.docs.PlayerId;
import tc.oc.api.users.UserService;
import tc.oc.commons.bukkit.chat.Audiences;
import tc.oc.commons.bukkit.util.NMSHacks;
import tc.oc.commons.bukkit.util.SyncPlayerExecutorFactory;
import tc.oc.commons.core.chat.Audience;
import tc.oc.commons.core.chat.Component;

public class ExperienceUtil {

    @Inject private static Plugin plugin;
    @Inject private static BukkitUserStore userStore;
    @Inject private static UserService userService;
    @Inject private static SyncPlayerExecutorFactory playerExecutorFactory;
    @Inject private static EventBus eventBus;
    @Inject private static Audiences audiences;

    public static int useMultiplier(int count, int multiplier) {
        return (int) (count * multiplier / 100f);
    }

    public static int calculateMultiplier(PlayerId playerId) {
        final Player player = userStore.find(playerId);
        int multiplier = ExperienceConstants.MULTIPLIER_BASE;
        if(player != null) {
            for(int i = ExperienceConstants.MULTIPLIER_MAX; i > 0; i = i - ExperienceConstants.MULTIPLIER_INCREMENT) {
                if(player.hasPermission("experience.multiplier." + i)) {
                    multiplier = i;
                    break;
                }
            }
        }
        return multiplier;
    }

    public static int calculateExperience(PlayerId playerId, int delta, boolean useMultiplier) {
        if(delta == 0) return 0;
        if(useMultiplier) {
            delta = useMultiplier(delta, calculateMultiplier(playerId));
        }
        return delta;
    }

    public static void giveExperience(PlayerId playerId, int count, @Nullable ExperienceResult result) {
        giveExperience(playerId, count, result, null);
    }

    public static void giveExperience(PlayerId playerId, int count, @Nullable ExperienceResult result, @Nullable BaseComponent reason) {
        giveExperience(playerId, count, result, reason, true);
    }

    public static void giveExperience(PlayerId playerId, int delta, @Nullable ExperienceResult result, @Nullable BaseComponent reason, boolean useMultiplier) {
        giveExperience(playerId, delta, result, reason, useMultiplier, true);
    }

    public static void giveExperience(PlayerId playerId, int delta, @Nullable ExperienceResult result, @Nullable BaseComponent reason, boolean useMultiplier, boolean save) {
        giveExperience(playerId, delta, result, reason, useMultiplier, save, true);
    }

    public static void giveExperience(PlayerId playerId, int delta, @Nullable ExperienceResult result, @Nullable BaseComponent reason, boolean useMultiplier, boolean save, boolean show) {
        if(delta == 0) return;

        final int multiplier;
        if(useMultiplier) {
            multiplier = calculateMultiplier(playerId);
            delta = useMultiplier(delta, multiplier);
        } else {
            multiplier = ExperienceConstants.MULTIPLIER_BASE;
        }

        giveExperience(playerId, delta, multiplier, result, reason, save, show);
    }

    public static void giveExperience(PlayerId playerId, int delta, int multiplier, @Nullable ExperienceResult result, @Nullable BaseComponent reason, boolean save) {
        giveExperience(playerId, delta, multiplier, result, reason, save, true);
    }

    public static void giveExperience(PlayerId playerId, int delta, int multiplier, @Nullable ExperienceResult result, @Nullable BaseComponent reason, boolean save, boolean show) {
        final int countBefore = userStore.getUser(playerId).experience();

        if(countBefore + delta < 0) {
            if(result != null) {
                result.setSuccess(false);
            }
            return;
        }

        if(save) {
            final int finalDelta = delta;
            playerExecutorFactory.queued(playerId).callback(
                userService.creditExperience(playerId, finalDelta),
                (player, update) -> {
                    if(update.success()) {
                        showExperience(player, finalDelta, multiplier, reason, show);
                    }
                    if(result != null) {
                        result.setSuccess(update.success());
                    }
                }
            );
        } else {
            final Player player = userStore.find(playerId);
            if(player != null) {
                showExperience(player, delta, multiplier, reason, show);
            }
            if(result != null) {
                result.setSuccess(true);
            }
        }
    }

    public static void showExperience(Player player, int delta, int multiplier, @Nullable BaseComponent reason) {
        showExperience(player, delta, multiplier, reason, true);
    }

    public static void showExperience(Player player, int delta, int multiplier, @Nullable BaseComponent reason, boolean show) {
        eventBus.callEvent(new PlayerRecieveExperienceEvent(player, delta, multiplier, reason));
        if (show) {
            final Audience audience = audiences.get(player);
            audience.sendMessage(experienceMessage(delta, multiplier, reason));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);
            experienceDisplay(player, delta);
        }
    }

    public static void experienceDisplay(final Player player, int count) {
        if(count == 1) {
            count = 2;
        } else if(count > 12) {
            count = 12;
        }

        NMSHacks.showFakeItems(plugin,
                               player,
                               player.getLocation().add(0, 2, 0),
                               new ItemStack(Material.GHAST_TEAR),
                               count,
                               Duration.ofSeconds(3));
    }

    private static BaseComponent experienceMessage(int count, int multiplier, @Nullable BaseComponent reason) {
        Component message = new Component(ChatColor.GRAY);
        message.extra(new Component((count > 0 ? "+" : "") + count, ChatColor.GREEN, ChatColor.BOLD),
                      new Component(" Experience", ChatColor.AQUA));
        if(multiplier != 100) {
            message.extra(new Component(" | ", ChatColor.DARK_PURPLE),
                          new Component((multiplier / 100f) + "x", ChatColor.GOLD, ChatColor.ITALIC));
        }
        if(reason != null) {
            message.extra(new Component(" | ", ChatColor.DARK_PURPLE),
                          reason);
        }
        return message;
    }
}
