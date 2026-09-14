package net.hilman.hilmanrpg.stat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.hilman.hilmanrpg.HillSRPG;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Nyalain "aliran" dari poin STR/INT/VIT/DEX/LCK ke attribute asli (Puffish
 * Attributes dkk), berdasarkan konfigurasi di {@link StatAttributeMapping}.
 *
 * Cara kerja: tiap tick server, tiap player online dicek — kalau nilai salah
 * satu dari 5 stat mentahnya BERUBAH dari tick sebelumnya (misal abis buka
 * skill node lain / kena command / abis pindah class), semua attribute target
 * yang terhubung ke stat itu langsung dihitung ulang dan ditimpa modifier-nya.
 * Kalau nilainya sama kayak tick sebelumnya, tidak ada yang dikerjakan sama
 * sekali (murah, aman dipanggil tiap tick).
 *
 * Modifier yang dipasang punya id TETAP (dari StatLink#modifierId), jadi setiap
 * sinkronisasi cukup replace, tidak pernah numpuk modifier lama.
 */
public class StatAttributeSync {

	// nilai stat terakhir yang sudah disinkronkan, per player, per stat mentah.
	// dipakai supaya tidak kerja ulang tiap tick kalau nilainya tidak berubah.
	private static final Map<UUID, Map<RegistryEntry<EntityAttribute>, Double>> LAST_SYNCED = new HashMap<>();

	/** Panggil sekali dari HillSRPG#onInitialize(). */
	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				syncPlayer(player);
			}
		});

		// bersihin cache pas player logout, biar tidak numpuk di memori server
		// yang sudah lama online dengan banyak player keluar-masuk.
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
				LAST_SYNCED.remove(handler.getPlayer().getUuid()));
	}

	private static void syncPlayer(ServerPlayerEntity player) {
		Map<RegistryEntry<EntityAttribute>, Double> cache =
				LAST_SYNCED.computeIfAbsent(player.getUuid(), uuid -> new HashMap<>());

		for (Map.Entry<RegistryEntry<EntityAttribute>, List<StatLink>> entry : StatAttributeMapping.all().entrySet()) {
			RegistryEntry<EntityAttribute> rawStat = entry.getKey();

			EntityAttributeInstance rawInstance = player.getAttributeInstance(rawStat);
			if (rawInstance == null) {
				// harusnya tidak pernah kejadian untuk player (PlayerAttributesMixin
				// selalu nambahin ke-5 stat ini), tapi tetap dijaga biar tidak NPE.
				continue;
			}

			double currentValue = rawInstance.getValue();
			Double lastValue = cache.get(rawStat);

			if (lastValue != null && lastValue.doubleValue() == currentValue) {
				continue; // tidak berubah, tidak usah kerja
			}

			cache.put(rawStat, currentValue);
			applyLinks(player, currentValue, entry.getValue());
		}
	}

	private static void applyLinks(ServerPlayerEntity player, double statValue, List<StatLink> links) {
		for (StatLink link : links) {
			Registries.ATTRIBUTE.getEntry(link.targetAttributeId()).ifPresentOrElse(targetEntry -> {
				EntityAttributeInstance instance = player.getAttributeInstance(targetEntry);
				if (instance == null) {
					// attribute target ada di registry tapi tidak dipasang ke player
					// (jarang, tapi bisa kejadian kalau mod pemiliknya cuma nempelin
					// attribute-nya ke entity tertentu, bukan ke semua LivingEntity).
					return;
				}

				instance.removeModifier(link.modifierId());

				double amount = statValue * link.amountPerPoint();
				if (amount != 0.0) {
					instance.addPersistentModifier(
							new EntityAttributeModifier(link.modifierId(), amount, link.operation()));
				}
			}, () -> {
				// mod pemilik attribute ini (mis. puffish_attributes / staminaattributes)
				// belum/tidak terpasang. Diamkan saja -> fitur itu otomatis nonaktif,
				// bukan crash. Kalau mau tahu kenapa suatu bonus tidak jalan, cek log
				// startup Fabric buat mod yang missing.
			});
		}
	}
}