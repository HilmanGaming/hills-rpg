package net.hilman.hilmanrpg.stat;

import net.hilman.hilmanrpg.HillSRPG;
import net.hilman.hilmanrpg.attribute.ModAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sumber kebenaran untuk "1 poin STR/INT/VIT/DEX/LCK = berapa bonus di attribute
 * asli". Ini gantinya sistem skill-tree (categories/adept) kalau kamu mau poin
 * mentahnya sendiri yang langsung ngasih efek terus-menerus, bukan lewat node
 * yang harus diklik satu-satu.
 *
 * Nilai "amountPerPoint" di bawah adalah TITIK AWAL, bukan angka final — dipilih
 * supaya kalau 1 stat di-dump penuh ke 1000 poin, hasil akhirnya masih masuk
 * akal untuk late-game (lihat komentar "@ 1000" di tiap baris). Gampang banget
 * diubah: tinggal edit angkanya di sini, tidak perlu sentuh StatAttributeSync.
 *
 * Kedua sistem (ini vs categories/adept skill tree) BISA jalan bareng kalau kamu
 * mau — mereka pakai modifierId yang beda jadi tidak akan saling menimpa. Tapi
 * kalau memang niatnya cuma pilih satu, hapus salah satunya supaya progression-nya
 * tidak dobel/membingungkan buat pemain.
 */
public class StatAttributeMapping {

	private static final Map<RegistryEntry<EntityAttribute>, List<StatLink>> LINKS = new LinkedHashMap<>();

	private static void link(RegistryEntry<EntityAttribute> rawStat, String linkName,
							  String targetAttributeId, double amountPerPoint) {
		link(rawStat, linkName, targetAttributeId, amountPerPoint, EntityAttributeModifier.Operation.ADD_VALUE);
	}

	private static void link(RegistryEntry<EntityAttribute> rawStat, String linkName,
							  String targetAttributeId, double amountPerPoint,
							  EntityAttributeModifier.Operation operation) {
		Identifier modifierId = HillSRPG.id("stat_link/" + linkName);
		Identifier targetId = Identifier.of(targetAttributeId);
		LINKS.computeIfAbsent(rawStat, k -> new ArrayList<>())
				.add(new StatLink(targetId, amountPerPoint, operation, modifierId));
	}

	static {
		// ============================= STR =====================================
		// per point 0.0004 -> +40% di stat itu kalau STR di-dump penuh ke 1000.
		link(ModAttributes.STRENGTH, "str_sword_damage", "puffish_attributes:sword_damage", 0.0004);
		link(ModAttributes.STRENGTH, "str_axe_damage", "puffish_attributes:axe_damage", 0.0004);
		link(ModAttributes.STRENGTH, "str_mace_damage", "puffish_attributes:mace_damage", 0.0004);
		link(ModAttributes.STRENGTH, "str_trident_damage", "puffish_attributes:trident_damage", 0.0004);
		// kecil -> +20% @ 1000
		link(ModAttributes.STRENGTH, "str_tamed_damage", "puffish_attributes:tamed_damage", 0.0002);
		// kecil, flat -> +4 armor/toughness points @ 1000
		link(ModAttributes.STRENGTH, "str_armor_shred", "puffish_attributes:armor_shred", 0.004);
		link(ModAttributes.STRENGTH, "str_toughness_shred", "puffish_attributes:toughness_shred", 0.004);
		// damage_reflection sengaja tidak dipetakan (sudah tidak dipakai)

		// ============================= INT =====================================
		link(ModAttributes.INTELLIGENCE, "int_magic_damage", "puffish_attributes:magic_damage", 0.0004);
		// pct, kecil -> +20% @ 1000 (bukan flat, lihat catatan koreksi di CONCEPT_ADEPT_TIER.md)
		link(ModAttributes.INTELLIGENCE, "int_magic_resistance_shred", "puffish_attributes:magic_resistance_shred", 0.0002);
		// porsi kecil ranged combat di INT (porsi utama ada di DEX)
		link(ModAttributes.INTELLIGENCE, "int_ranged_damage", "puffish_attributes:ranged_damage", 0.0002);
		link(ModAttributes.INTELLIGENCE, "int_ranged_resistance_shred", "puffish_attributes:ranged_resistance_shred", 0.0001);
		// active_spell_slot_amount (RPG Inventory) SENGAJA belum dipetakan di sini,
		// masih ditunda -> lihat CONCEPT_ADEPT_TIER.md bagian "DITUNDA"

		// ============================= VIT =====================================
		link(ModAttributes.VITALITY, "vit_magic_resistance", "puffish_attributes:magic_resistance", 0.0004);
		link(ModAttributes.VITALITY, "vit_melee_resistance", "puffish_attributes:melee_resistance", 0.0004);
		link(ModAttributes.VITALITY, "vit_ranged_resistance", "puffish_attributes:ranged_resistance", 0.0004);
		link(ModAttributes.VITALITY, "vit_tamed_resistance", "puffish_attributes:tamed_resistance", 0.0004);
		link(ModAttributes.VITALITY, "vit_healing", "puffish_attributes:healing", 0.0002);
		// flat, kecil -> +3 blok fall reduction @ 1000
		link(ModAttributes.VITALITY, "vit_fall_reduction", "puffish_attributes:fall_reduction", 0.003);
		// flat, medium -> +20 max stamina @ 1000 (mod "Stamina Attributes", bukan Puffish Attributes!)
		link(ModAttributes.VITALITY, "vit_max_stamina", "staminaattributes:max_stamina", 0.02);

		// ============================= DEX =====================================
		// flat, kecil -> +0.6 jump height @ 1000
		link(ModAttributes.DEXTERITY, "dex_jump", "puffish_attributes:jump", 0.0006);
		link(ModAttributes.DEXTERITY, "dex_mount_speed", "puffish_attributes:mount_speed", 0.0002);
		// flat, kecil -> +4 blok pengurang jarak deteksi @ 1000
		link(ModAttributes.DEXTERITY, "dex_stealth", "puffish_attributes:stealth", 0.004);
		link(ModAttributes.DEXTERITY, "dex_pickaxe_speed", "puffish_attributes:pickaxe_speed", 0.0006);
		link(ModAttributes.DEXTERITY, "dex_axe_speed", "puffish_attributes:axe_speed", 0.0006);
		link(ModAttributes.DEXTERITY, "dex_shovel_speed", "puffish_attributes:shovel_speed", 0.0006);
		link(ModAttributes.DEXTERITY, "dex_consuming_speed", "puffish_attributes:consuming_speed", 0.0006);
		link(ModAttributes.DEXTERITY, "dex_bow_projectile_speed", "puffish_attributes:bow_projectile_speed", 0.0004);
		link(ModAttributes.DEXTERITY, "dex_crossbow_projectile_speed", "puffish_attributes:crossbow_projectile_speed", 0.0004);
		// porsi utama ranged combat ada di DEX (INT cuma porsi kecil, lihat atas)
		link(ModAttributes.DEXTERITY, "dex_ranged_damage", "puffish_attributes:ranged_damage", 0.0004);
		link(ModAttributes.DEXTERITY, "dex_ranged_resistance_shred", "puffish_attributes:ranged_resistance_shred", 0.0002);
		// flat, kecil-medium -> +6 stamina regen @ 1000 (mod "Stamina Attributes")
		link(ModAttributes.DEXTERITY, "dex_stamina_regeneration", "staminaattributes:stamina_regeneration", 0.006);

		// ============================= LCK =====================================
		// flat, kecil -> +2 repair cost @ 1000 (lihat catatan "chance" di CONCEPT_ADEPT_TIER.md,
		// ini masih deterministik, bukan RNG beneran)
		link(ModAttributes.LUCK, "lck_repair_cost", "puffish_attributes:repair_cost", 0.002);
		// experience & life_steal sengaja tidak dipetakan (sudah dicoret dari rencana)
		// crit chance/damage (Spell Power Attributes) belum ditambah di sini —
		// beda mod/attribute, tambahkan link baru kalau sudah siap ID-nya.
	}

	public static Map<RegistryEntry<EntityAttribute>, List<StatLink>> all() {
		return Collections.unmodifiableMap(LINKS);
	}
}