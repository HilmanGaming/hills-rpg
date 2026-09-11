package net.hilman.hilmanrpg;

import net.fabricmc.api.ModInitializer;

import net.hilman.hilmanrpg.item.ModItems;
import net.hilman.hilmanrpg.rpgclass.ClassArmorLockHandler;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.theredbrain.rpgclassselection.registry.RPGClassSelectionConfigs;

public class HillSRPG implements ModInitializer {
	public static final String MOD_ID = "hills-rpg";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.register();
		ClassArmorLockHandler.register();

		LOGGER.info("Bisa anjay bangke woooooooooooooooo!!!!");
		RPGClassSelectionConfigs.SERVER_CONFIG.firstJoinScreenSettings.enable_first_join_class_selection = false;
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
