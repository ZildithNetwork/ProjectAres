package tc.oc.lobby.bukkit.gizmos;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import tc.oc.lobby.bukkit.LobbyTranslations;
import tc.oc.lobby.bukkit.gizmos.chicken.ChickenGizmo;
import tc.oc.lobby.bukkit.gizmos.empty.EmptyGizmo;
import tc.oc.lobby.bukkit.gizmos.gun.GunGizmo;
import tc.oc.lobby.bukkit.gizmos.launcher.UnitedStatesGizmo;
import tc.oc.lobby.bukkit.gizmos.launcher.MexicoGizmo;
import tc.oc.lobby.bukkit.gizmos.popper.PopperGizmo;
import tc.oc.lobby.bukkit.gizmos.rocket.RocketGizmo;

public class Gizmos implements Listener {
    public static EmptyGizmo emptyGizmo = new EmptyGizmo("Empty", ChatColor.GRAY.toString(), "Unequip current gizmo", Material.THIN_GLASS, 0);
    public static PopperGizmo popperGizmo = new PopperGizmo("Popper", ChatColor.WHITE.toString(), "Clear players with a satisfying pop", Material.FIREWORK_CHARGE, 100);
    public static RocketGizmo rocketGizmo = new RocketGizmo("Player Rocketer", ChatColor.RED.toString(), "Hide players by launching them into colorful fireworks", Material.FIREWORK, 5000);
    public static GunGizmo gunGizmo = new GunGizmo("Raindrop Gun", ChatColor.AQUA.toString(), "Gift raindrops with a punch :D", Material.IRON_HOE, 7500);
    public static ChickenGizmo chickenGizmo = new ChickenGizmo("Chickenifier5000", ChatColor.YELLOW.toString(), "bok B'GAWK", Material.EGG, 10000);
    public static UnitedStatesGizmo UnitedStatesGizmo = new UnitedStatesGizmo("United States", ChatColor.RED.toString(), "Fireworks to the flag of the United States", Material.WOOL, 17760);
    public static MexicoGizmo MexicoGizmo = new MexicoGizmo("Mexico", ChatColor.RED.toString(), "Fireworks to the flag of Mexico", Material.WOOL, 17300);

    public static final List<Gizmo> gizmos = Lists.newArrayList(emptyGizmo, popperGizmo, rocketGizmo, gunGizmo, chickenGizmo, UnitedStatesGizmo, MexicoGizmo);
    public static Map<Player, Gizmo> gizmoMap = Maps.newHashMap();
    public static Map<Player, Gizmo> purchasingMap = Maps.newHashMap();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void openMenu(final PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL) return;

        Player player = event.getPlayer();
        if(player.getItemInHand().getType() == Material.GOLD_NUGGET) {
            GizmoUtils.openMenu(event.getPlayer());
            purchasingMap.put(event.getPlayer(), null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void click(final InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        if(!purchasingMap.containsKey(player)) return;

        Material clicked = event.getCurrentItem().getType();

        for(Gizmo gizmo : gizmos) {
            if(clicked == gizmo.getIcon()) {
                player.closeInventory();
                if(gizmo.ownsGizmo(player)) {
                    GizmoUtils.setGizmo(player, gizmo, false);
                } else if(purchasingMap.containsKey(player)) {
                    player.sendMessage(ChatColor.GOLD + LobbyTranslations.get().t("gizmo.currentlyPurchasing", player));
                } else if(gizmo.canPurchase(player)) {
                    GizmoUtils.openShop(player, gizmo);
                    purchasingMap.put(player, gizmo);
                } else {
                    player.sendMessage(gizmo.getNoPurchaseMessage(player));
                }

                break;
            }
        }

        if(clicked == Material.DIAMOND) {
            GizmoUtils.purchaseGizmo(player, purchasingMap.get(player));
        } else if(clicked == Material.REDSTONE_BLOCK) {
            GizmoUtils.cancelPurchase(player);
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void close(final InventoryCloseEvent event) {
        purchasingMap.remove(event.getPlayer());
    }

    @EventHandler
    public void logout(final PlayerQuitEvent event) {
        gizmoMap.remove(event.getPlayer());
        purchasingMap.remove(event.getPlayer());
    }
}
