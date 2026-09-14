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
 * 5 stat inti custom mod ini: STR, VIT, DEX, INT, LCK.
 * Nilai mentahnya sengaja tidak dibatasi (lihat Prinsip 1.4 di dokumen desain) -
 * yang dibatasi adalah attribute TUJUAN, bukan stat ini.
 */
public final class ModAttributes {
	private ModAttributes() {}

	public static final RegistryEntry<EntityAttribute> STRENGTH = register("strength");
	public static final RegistryEntry<EntityAttribute> VITALITY = register("vitality");
	public static final RegistryEntry<EntityAttribute> DEXTERITY = register("dexterity");
	public static final RegistryEntry<EntityAttribute> INTELLIGENCE = register("intelligence");
	public static final RegistryEntry<EntityAttribute> LUCK = register("luck");

	/**
	 * Panggil sekali di HillSRPG#onInitialize supaya class ini (dan static field di atas)
	 * dipaksa ke-load sejak awal, sebelum vanilla membekukan default attribute container.
	 */
	public static void bootstrap() {
		HillSRPG.LOGGER.info("Hill's RPG: stat inti STR/VIT/DEX/INT/LCK terdaftar.");
	}

	private static RegistryEntry<EntityAttribute> register(String path) {
		Identifier id = HillSRPG.id(path);

		EntityAttribute attribute = new ClampedEntityAttribute(
				"attribute.name." + HillSRPG.MOD_ID + "." + path,
				0.0D,
				0.0D,
				100000.0D
		).setTracked(true);

		Registry.register(Registries.ATTRIBUTE, id, attribute);

		return Registries.ATTRIBUTE
				.getEntry(RegistryKey.of(RegistryKeys.ATTRIBUTE, id))
				.orElseThrow(() -> new IllegalStateException("Gagal registrasi attribute: " + id));
	}
}