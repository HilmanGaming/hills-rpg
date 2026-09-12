package net.hilman.hilmanrpg.attribute;

import net.hilman.hilmanrpg.HillSRPG;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

/**
 * Atribut custom untuk sistem stat Hill's RPG: STR / INT / VIT / DEX / LCK.
 *
 * Nilai atribut ini sendiri TIDAK melakukan apa-apa terhadap gameplay.
 * Ini cuma "wadah angka" yang bisa dibaca/diubah oleh:
 *  - Pufferfish's Skills (node skill tree ngasih modifier ke atribut ini)
 *  - RPG Class Selection (item upgrade dari pilihan class bisa juga nge-modify ini)
 *  - Logic custom kita sendiri (mis. mapping STR -> Melee Damage dari Puffish Attributes,
 *    lihat Daftar_Atribut_Mod_Update.md untuk pemetaan lengkapnya)
 *
 * Base value (fallback) sengaja 0.0 karena ini murni stat tambahan di luar stat vanilla,
 * bukan pengganti Max Health / Attack Damage dsb.
 */
public class ModAttributes {

	// Batas atas 1000 itu perkiraan aman untuk sekarang (mob late-game / 12 Advanced Class).
	// Gampang dinaikkan belakangan kalau progression system-nya ternyata butuh lebih tinggi.
	private static final double MIN = 0.0;
	private static final double MAX = 1000.0;

	public static final RegistryEntry<EntityAttribute> STRENGTH =
			register("strength");

	public static final RegistryEntry<EntityAttribute> INTELLIGENCE =
			register("intelligence");

	public static final RegistryEntry<EntityAttribute> VITALITY =
			register("vitality");

	public static final RegistryEntry<EntityAttribute> DEXTERITY =
			register("dexterity");

	public static final RegistryEntry<EntityAttribute> LUCK =
			register("luck");

	private static RegistryEntry<EntityAttribute> register(String path) {
		Identifier id = HillSRPG.id(path);
		RegistryKey<EntityAttribute> key = RegistryKey.of(RegistryKeys.ATTRIBUTE, id);

		String translationKey = "attribute.name." + id.getNamespace() + "." + id.getPath();
		EntityAttribute attribute = new ClampedEntityAttribute(translationKey, 0.0, MIN, MAX)
				.setTracked(true); // disync ke client, supaya bisa ditampilkan di UI/HUD nanti

		return Registry.registerReference(Registries.ATTRIBUTE, key, attribute);
	}

	/**
	 * Panggil sekali dari ModInitializer supaya class ini (dan semua static field-nya)
	 * ke-load, sehingga registrasi di atas benar-benar jalan.
	 */
	public static void initialize() {
		HillSRPG.LOGGER.info("Registered {} custom RPG attributes", 5);
	}
}