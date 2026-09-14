package net.hilman.hilmanrpg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.hilman.hilmanrpg.attribute.ModAttributes;
import net.hilman.hilmanrpg.stat.StatEngine;

import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HillSRPG implements ModInitializer {
	public static final String MOD_ID = "hills-rpg";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Sinkronisasi ulang stat setiap berapa tick (20 tick = 1 detik).
	private static final int SYNC_INTERVAL_TICKS = 20;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Bisa anjay bangke woooooooooooooooo!!!!");

		// Daftarkan 5 stat inti (STR/VIT/DEX/INT/LCK) sedini mungkin.
		ModAttributes.bootstrap();

		// Hitung ulang & terapkan semua modifier turunan tiap kali pemain join,
		// dan berkala tiap SYNC_INTERVAL_TICKS supaya perubahan stat (dari item,
		// skill, command, dsb) ikut ke-refresh tanpa perlu re-join.
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				StatEngine.apply(handler.getPlayer()));

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % SYNC_INTERVAL_TICKS == 0) {
				server.getPlayerManager().getPlayerList().forEach(StatEngine::apply);
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}