package com.booksaw.betterTeams.events;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.message.MessageManager;

public class ChestManagement implements Listener {

	@EventHandler
	public void onOpen(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.CHEST
				|| e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Team team = Team.getTeam(e.getPlayer());

		Team claimedBy = Team.getClaimingTeam(e.getClickedBlock());
		if (claimedBy != null && team != claimedBy && (team == null || !claimedBy.isAlly(team.getID()))) {
			cancelChestEvent(e, claimedBy);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Team claimedBy = Team.getClaimingTeam(e.getBlock());

		if (claimedBy != null) {
			cancelChestEvent(e, claimedBy);
		}

	}

	@EventHandler
	public void onHopper(InventoryMoveItemEvent e) {
		Team claimedBy = Team.getClaimingTeam(e.getSource().getHolder());

		if (claimedBy != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplode(BlockExplodeEvent e) {
		Iterator<Block> iter = e.blockList().iterator();
		while (iter.hasNext()) {
			Block b = iter.next();
			Team claimedBy = Team.getClaimingTeam(b);

			if (claimedBy != null) {
				iter.remove();
			}
		}

	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		Iterator<Block> iter = e.blockList().iterator();
		while (iter.hasNext()) {
			Block b = iter.next();
			if (b.getType() != Material.CHEST) {
				continue;
			}
			Team claimedBy = Team.getClaimingTeam(b);

			if (claimedBy != null) {
				iter.remove();
			}
		}
	}

	private void cancelChestEvent(PlayerInteractEvent e, Team claimedBy) {
		MessageManager.sendMessageF(e.getPlayer(), "chest.claimed", claimedBy.getName());
		((Cancellable) e).setCancelled(true);
	}

	private void cancelChestEvent(BlockBreakEvent e, Team claimedBy) {
		MessageManager.sendMessageF(e.getPlayer(), "chest.claimed", claimedBy.getName());
		((Cancellable) e).setCancelled(true);
	}

	public static Location getLocation(Chest chest) {
		return new Location(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ());
	}
}