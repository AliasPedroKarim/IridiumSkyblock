package com.iridium.iridiumskyblock.listeners;

import com.iridium.iridiumcore.utils.StringUtils;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.database.User;
import com.iridium.iridiumskyblock.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = IridiumSkyblock.getInstance().getUserManager().getUser(player);
        user.setBypass(false);

        // Update the internal username in case of name change
        user.setName(event.getPlayer().getName());

        // Send their island border
        IridiumSkyblock.getInstance().getIslandManager().getIslandViaLocation(player.getLocation()).ifPresent(island ->
                PlayerUtils.sendBorder(player, island)
        );

        if (IridiumSkyblock.getInstance().getConfiguration().messageWelcome) {
            player.sendMessage(StringUtils.color(IridiumSkyblock.getInstance().getMessages().messageWecome.replace("%prefix%", IridiumSkyblock.getInstance().getConfiguration().prefix)));
        }
    }

}
