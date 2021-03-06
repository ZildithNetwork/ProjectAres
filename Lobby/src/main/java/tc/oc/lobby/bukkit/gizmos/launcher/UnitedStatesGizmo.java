package tc.oc.lobby.bukkit.gizmos.launcher;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;
import tc.oc.commons.bukkit.util.OnlinePlayerMapAdapter;
import tc.oc.lobby.bukkit.Lobby;
import tc.oc.lobby.bukkit.gizmos.Gizmo;
import tc.oc.lobby.bukkit.gizmos.Gizmos;

public class UnitedStatesGizmo extends Gizmo implements Listener {
    private final OnlinePlayerMapAdapter<Firework> launchedPlayers = new OnlinePlayerMapAdapter<>(Lobby.get());
    private static final Color[] UNITED_STATES_COLORS = {Color.RED, Color.WHITE, Color.BLUE};

    public UnitedStatesGizmo(String name, String prefix, String description, Material icon, int cost) {
        super(name, prefix, description, icon, cost);
        this.launchedPlayers.enable();
    }

    @Override
    protected void initialize() {
        Bukkit.getPluginManager().registerEvents(this, Lobby.get());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!(Gizmos.gizmoMap.get(player) instanceof UnitedStatesGizmo)) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (player.getItemInHand().getType() != this.getIcon()) return;

        Firework oldFirework = this.launchedPlayers.get(player);
        if (oldFirework == null || oldFirework.isDead()) {
            Firework firework = this.buildFirework(player.getLocation());
            firework.setPassenger(player);
            this.launchedPlayers.put(player, firework);
        }
    }

    private Firework buildFirework(Location loc) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();
        fwm.addEffect(FireworkEffect.builder()
                      .withColor(UNITED_STATES_COLORS)
                      .with(FireworkEffect.Type.STAR)
                      .withTrail()
                      .withFade(UNITED_STATES_COLORS)
                      .build());
        firework.setFireworkMeta(fwm);

        firework.setVelocity(loc.getDirection().divide(new Vector(2, 1, 2)));

        return firework;
    }
}
