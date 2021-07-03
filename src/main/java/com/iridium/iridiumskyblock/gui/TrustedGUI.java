package com.iridium.iridiumskyblock.gui;

import com.iridium.iridiumcore.utils.InventoryUtils;
import com.iridium.iridiumcore.utils.ItemStackUtils;
import com.iridium.iridiumcore.utils.Placeholder;
import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.PlaceholderBuilder;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.IslandTrusted;
import com.iridium.iridiumskyblock.database.User;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * GUI which displays all trusted users of an Island.
 */
public class TrustedGUI extends GUI {

    private final HashMap<Integer, User> members;

    /**
     * The default constructor.
     *
     * @param island The Island this GUI belongs to
     */
    public TrustedGUI(@NotNull Island island) {
        super(IridiumSkyblock.getInstance().getInventories().trustedGUI, island);
        this.members = new HashMap<>();
    }

    @Override
    public void addContent(Inventory inventory) {
        CompletableFuture.supplyAsync(() -> {
            List<IslandTrusted> islandTrustedList = IridiumSkyblock.getInstance().getDatabaseManager().getIslandTrustedTableManager().getEntries(getIsland());
            AtomicInteger atomicInteger = new AtomicInteger(0);
            return islandTrustedList.stream()
                    .map(islandTrusted -> {
                        List<Placeholder> placeholderList = new PlaceholderBuilder().applyPlayerPlaceholders(islandTrusted.getUser()).applyIslandPlaceholders(getIsland()).build();
                        placeholderList.add(new Placeholder("trustee", islandTrusted.getTruster().getName()));
                        members.put(atomicInteger.getAndIncrement(), islandTrusted.getUser());
                        return ItemStackUtils.makeItem(IridiumSkyblock.getInstance().getInventories().trustedGUI.item, placeholderList);
                    })
                    .collect(Collectors.toList());
        }).thenAccept(itemStacks -> {
            int i = 0;
            for (ItemStack itemStack : itemStacks) {
                inventory.setItem(i, itemStack);
                i++;
            }
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    /**
     * Called when there is a click in this GUI.
     * Cancelled automatically.
     *
     * @param event The InventoryClickEvent provided by Bukkit
     */
    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        if (members.containsKey(event.getSlot())) {
            User user = members.get(event.getSlot());
            String command = IridiumSkyblock.getInstance().getCommands().unTrustCommand.aliases.get(0);
            Bukkit.getServer().dispatchCommand(event.getWhoClicked(), "is " + command + " " + user.getName());
            addContent(event.getInventory());
        }
    }

}
