package net.hilman.hilmanrpg.item.custom.classrpg;

import net.minecraft.util.Identifier;

/**
 * Ditandai ke item (armor, dll) yang cuma boleh dipakai oleh player yang
 * sudah punya advancement class tertentu (di-grant otomatis oleh
 * Command Reward node Pufferfish's Skills saat node itu unlock).
 */
public interface ClassRestrictedItem {
	/**
	 * @return Identifier advancement (mis. "hills-rpg:class/warrior_tank")
	 *         yang wajib dimiliki player supaya boleh memakai item ini.
	 */
	Identifier getRequiredClassAdvancement();
}
