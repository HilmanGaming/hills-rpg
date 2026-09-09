package net.hilman.hilmanrpg.rpgclass;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.hilman.hilmanrpg.HillSRPG;
import net.hilman.hilmanrpg.item.custom.classrpg.ClassRestrictedItem;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ClassArmorLockHandler {
	private static final int CHECK_INTERVAL_TICKS = 20;

	private ClassArmorLockHandler() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % CHECK_INTERVAL_TICKS != 0) return;
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				checkPlayerArmor(server, player);
			}
		});
	}

	private static void checkPlayerArmor(MinecraftServer server, ServerPlayerEntity player) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.HUMANOID_ARMOR) continue;
			ItemStack stack = player.getEquippedStack(slot);
			if (stack.isEmpty() || !(stack.getItem() instanceof ClassRestrictedItem restricted)) continue;
			if (!hasClassAdvancement(server, player, restricted.getRequiredClassAdvancement())) {
				unequipAndDrop(player, slot, stack);
			}
		}
	}

	private static boolean hasClassAdvancement(MinecraftServer server, ServerPlayerEntity player, Identifier id) {
		AdvancementEntry entry = server.getAdvancementLoader().get(id);
		if (entry == null) {
			HillSRPG.LOGGER.warn("Advancement class tidak ditemukan: {}", id);
			return false;
		}
		return player.getAdvancementTracker().getProgress(entry).isDone();
	}

	private static void unequipAndDrop(ServerPlayerEntity player, EquipmentSlot slot, ItemStack stack) {
		player.equipStack(slot, ItemStack.EMPTY);
		player.dropItem(stack, false, false);
		player.sendMessage(Text.translatable("hills-rpg.class_lock.armor_removed", stack.getName()), true);
	}
}