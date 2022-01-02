package fr.aytronn.storyhard.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * @author aytronn
 * @created 02/01/2022
 * @project StoryHard
 */
public class PlayerListener implements Listener {

    /**
     * When player take damage from entity he dies
     *
     * @param event EntityDamageByEntityEvent
     */
    @EventHandler
    public void playerTakeDamageFromEntity(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        final var player = (Player) event.getEntity();
        event.setDamage(player.getHealth());
    }
}
