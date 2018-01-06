package tc.oc.commons.bukkit.experience;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerRecieveExperienceEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    protected final int experience;
    protected final int multiplier;
    protected final BaseComponent reason;

    public PlayerRecieveExperienceEvent(Player who, int experience, int multiplier, BaseComponent reason) {
        super(who);
        this.experience = experience;
        this.multiplier = multiplier;
        this.reason = reason;
    }

    public int getExperience() {
        return this.experience;
    }

    public int getMultiplier() {
        return this.multiplier;
    }

    public BaseComponent getReason() {
        return this.reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
