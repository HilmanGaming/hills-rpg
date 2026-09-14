package net.hilman.hilmanrpg.stat;

import net.hilman.hilmanrpg.HillSRPG;
import net.hilman.hilmanrpg.attribute.AttributeRegistry;
import net.hilman.hilmanrpg.attribute.ModAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Menghitung ulang & menerapkan semua modifier turunan dari 5 stat inti
 * (STR/VIT/DEX/INT/LCK) sesuai "Sistem Attribute RPG - Spell Engine + Fabric 1.21.1".
 *
 * PENTING - cara baca "flat" vs "persen" dari stat sumber (STR/DEX/INT):
 * Stat sumber (misal STR) bisa dapet bonus dari 2 jenis modifier yang beda:
 *   - ADD_VALUE          -> "poin flat", misal item/skill kasih "+15 STR"
 *   - ADD_MULTIPLIED_*    -> "persen", misal item/skill kasih "+20% STR"
 * Buat kelompok "Flat + Persen" (STR/DEX-ranged/INT-magic) dan "Persen saja" (shred/resistance):
 *   - Flat yang ditembak ke attribute tujuan = (poin flat STR) x 0.75
 *   - Persen yang ditembak ke attribute tujuan = (persen asli yang nempel di STR) x 0.5
 *     (SEPARUH dari persen ASLI stat sumber, BUKAN separuh dari poin flat yang diubah jadi persen)
 * Formula lain yang cuma "satu angka" (crit chance/damage, speed, mana/stamina, luck, dst)
 * tetap pakai "poin flat" stat sumber seperti biasa - bagian ini tidak berubah.
 *
 * CATATAN UNIT: attribute persentase (resistance, shred, suppression, damage_reflection, dst)
 * diasumsikan disimpan sebagai pecahan 0.0-1.0 (0.05 = 5%). Sesuaikan kalau ternyata mod
 * targetnya pakai skala lain.
 */
public final class StatEngine {
    private StatEngine() {}

    private static final Operation ADD_VALUE = Operation.ADD_VALUE;
    // Dipakai buat nulis hasil ke attribute TUJUAN (bukan buat baca stat sumber).
    // ADD_MULTIPLIED_TOTAL dipilih supaya persen ngaliin TOTAL (base senjata + semua flat),
    // bukan cuma base default attribute yang sering cuma 1.0 (atau 0 buat attribute custom).
    private static final Operation ADD_MULTIPLIED_TOTAL = Operation.ADD_MULTIPLIED_TOTAL;

    public static void apply(ServerPlayerEntity player) {
        StatBreakdown str = readStat(player, ModAttributes.STRENGTH);
        StatBreakdown vit = readStat(player, ModAttributes.VITALITY);
        StatBreakdown dex = readStat(player, ModAttributes.DEXTERITY);
        StatBreakdown intel = readStat(player, ModAttributes.INTELLIGENCE);
        StatBreakdown lck = readStat(player, ModAttributes.LUCK);

        List<ModifierSpec> specs = new ArrayList<>();
        specs.addAll(buildStrModifiers(str));
        specs.addAll(buildVitModifiers(vit.flatPoints()));
        specs.addAll(buildDexModifiers(dex));
        specs.addAll(buildIntModifiers(intel));
        specs.addAll(buildLckModifiers(lck.flatPoints()));

        for (ModifierSpec spec : specs) {
            applySpec(player, spec);
        }
    }

    // ------------------------------------------------------------------
    // STR - Physical Power
    // ------------------------------------------------------------------
    private static List<ModifierSpec> buildStrModifiers(StatBreakdown str) {
        List<ModifierSpec> list = new ArrayList<>();
        double flat = str.flatPoints() * 0.75D;
        double percent = str.percentBonus() * 0.5D; // separuh dari persen ASLI yang nempel di STR

        String[] flatPercentTargets = {
                "minecraft:attack_damage",
                "spell_power:physical_melee",
                "puffish_attributes:melee_damage",
                "puffish_attributes:sword_damage",
                "puffish_attributes:axe_damage",
                "puffish_attributes:mace_damage",
                "puffish_attributes:trident_damage"
        };
        for (String target : flatPercentTargets) {
            String slug = slug(target);
            list.add(new ModifierSpec("str_" + slug + "_flat", target, flat, ADD_VALUE));
            list.add(new ModifierSpec("str_" + slug + "_pct", target, percent, ADD_MULTIPLIED_TOTAL));
        }

        // Flat saja
        list.add(new ModifierSpec("str_tamed_damage_flat", "puffish_attributes:tamed_damage", flat, ADD_VALUE));
        list.add(new ModifierSpec("str_knockback_flat", "puffish_attributes:knockback", flat, ADD_VALUE));

        // Persen saja (shred - attribute-nya sendiri sudah berupa pecahan persentase)
        String[] shredTargets = {
                "puffish_attributes:armor_shred",
                "puffish_attributes:toughness_shred",
                "puffish_attributes:protection_shred",
                "puffish_attributes:melee_resistance_shred"
        };
        for (String target : shredTargets) {
            list.add(new ModifierSpec("str_" + slug(target) + "_pct", target, percent, ADD_VALUE));
        }

        return list;
    }

    // ------------------------------------------------------------------
    // VIT - Survivability  (semua formula VIT cuma pakai poin flat, tidak ada pasangan persen)
    // ------------------------------------------------------------------
    private static List<ModifierSpec> buildVitModifiers(double vitPoints) {
        List<ModifierSpec> list = new ArrayList<>();
        double raw = vitPoints * 0.75D;

        // Max Health: bonus poin darah dibulatkan ke kelipatan genap, cap 60 (total cap 80)
        double bonusHeartPoints = Math.min(60D, 2D * Math.floor(raw / 2D));
        list.add(new ModifierSpec("vit_max_health", "minecraft:max_health", bonusHeartPoints, ADD_VALUE));

        // Armor: cap 10 (dari 30 vanilla), overflow -> resistance
        double armorFromVit = Math.min(10D, Math.floor(raw / 2D));
        double armorOverflow = Math.max(0D, vitPoints - 27D);
        double armorOverflowResistance = Math.floor(armorOverflow / 15D);
        list.add(new ModifierSpec("vit_armor", "minecraft:armor", armorFromVit, ADD_VALUE));

        // Armor Toughness: cap 8 (dari 20 vanilla), overflow -> resistance
        double toughnessFromVit = Math.min(8D, Math.floor(raw / 4D));
        double toughnessOverflow = Math.max(0D, vitPoints - 43D);
        double toughnessOverflowResistance = Math.floor(toughnessOverflow / 15D);
        list.add(new ModifierSpec("vit_armor_toughness", "minecraft:armor_toughness", toughnessFromVit, ADD_VALUE));

        // Gabungan overflow keduanya -> puffish_attributes:resistance (1 poin = 1%)
        double totalOverflowResistance = (armorOverflowResistance + toughnessOverflowResistance) * 0.01D;
        list.add(new ModifierSpec("vit_overflow_resistance", "puffish_attributes:resistance", totalOverflowResistance, ADD_VALUE));

        // Knockback resistance (skala kecil) & Max Stamina (flat)
        list.add(new ModifierSpec("vit_knockback_resistance", "minecraft:knockback_resistance", vitPoints * 0.005D, ADD_VALUE));
        list.add(new ModifierSpec("vit_max_stamina", "staminaattributes:max_stamina", Math.floor(vitPoints * 0.75D), ADD_VALUE));

        return list;
    }

    // ------------------------------------------------------------------
    // DEX - Agility & Precision
    // ------------------------------------------------------------------
    private static List<ModifierSpec> buildDexModifiers(StatBreakdown dex) {
        List<ModifierSpec> list = new ArrayList<>();
        double dexPoints = dex.flatPoints();

        // Formula satu-angka (bukan pasangan flat+persen) - tetap pakai poin flat DEX
        list.add(new ModifierSpec("dex_crit_chance", "spell_power:critical_chance", dexPoints * 0.0015D, ADD_VALUE));
        list.add(new ModifierSpec("dex_crit_damage", "spell_power:critical_damage", dexPoints * 0.025D, ADD_VALUE));

        // Ranged Damage: pasangan flat + persen (persen dari persen asli DEX, bukan dari poin)
        double flat = dexPoints * 0.75D;
        double percent = dex.percentBonus() * 0.5D;
        list.add(new ModifierSpec("dex_ranged_damage_flat", "puffish_attributes:ranged_damage", flat, ADD_VALUE));
        list.add(new ModifierSpec("dex_ranged_damage_pct", "puffish_attributes:ranged_damage", percent, ADD_MULTIPLIED_TOTAL));

        // Resistance & Shred (persen kecil) - juga pakai persen asli DEX, bukan poin
        list.add(new ModifierSpec("dex_ranged_resistance_pct", "puffish_attributes:ranged_resistance", percent, ADD_VALUE));
        list.add(new ModifierSpec("dex_ranged_resistance_shred_pct", "puffish_attributes:ranged_resistance_shred", percent, ADD_VALUE));

        // Kelompok kecepatan/mining/stamina - koefisien kecil seragam, tetap dari poin DEX
        double small = dexPoints * 0.005D;
        String[] speedTargets = {
                "minecraft:attack_speed",
                "minecraft:movement_speed",
                "puffish_attributes:sprinting_speed",
                "puffish_attributes:mount_speed",
                "puffish_attributes:bow_projectile_speed",
                "puffish_attributes:crossbow_projectile_speed",
                "puffish_attributes:consuming_speed",
                "puffish_attributes:breaking_speed",
                "puffish_attributes:mining_speed",
                "puffish_attributes:pickaxe_speed",
                "puffish_attributes:axe_speed",
                "puffish_attributes:shovel_speed",
                "staminaattributes:stamina_regeneration",
                "puffish_attributes:stealth"
        };
        for (String target : speedTargets) {
            list.add(new ModifierSpec("dex_" + slug(target), target, small, ADD_VALUE));
        }

        // Cost-reduction: dianggap negatif (makin tinggi DEX, makin murah biayanya)
        list.add(new ModifierSpec("dex_item_use_stamina_cost", "staminaattributes:item_use_stamina_cost", -small, ADD_VALUE));
        list.add(new ModifierSpec("dex_stamina_spell_cost_multiplier", "spellengineextension:generic.stamina_spell_cost_multiplier", -small, ADD_VALUE));

        // Jump (pembulatan per level 0.05)
        double jRaw = dexPoints * 0.005D;
        double jLevel = Math.floor(jRaw / 0.05D);
        double jumpBonus = jLevel * 0.05D;
        list.add(new ModifierSpec("dex_jump_strength", "minecraft:jump_strength", jumpBonus, ADD_VALUE));

        return list;
    }

    // ------------------------------------------------------------------
    // INT - Magic Power
    // ------------------------------------------------------------------
    private static List<ModifierSpec> buildIntModifiers(StatBreakdown intel) {
        List<ModifierSpec> list = new ArrayList<>();
        double intPoints = intel.flatPoints();

        // Formula satu-angka - tetap pakai poin flat INT
        list.add(new ModifierSpec("int_crit_chance", "spell_power:critical_chance", intPoints * 0.001D, ADD_VALUE));
        list.add(new ModifierSpec("int_crit_damage", "spell_power:critical_damage", intPoints * 0.002D, ADD_VALUE));

        // Flat + Persen: persen dari persen asli INT, bukan dari poin
        double flat = intPoints * 0.75D;
        double percent = intel.percentBonus() * 0.5D;
        String[] flatPercentTargets = {
                "spell_power:arcane",
                "spell_power:fire",
                "spell_power:frost",
                "spell_power:lightning",
                "spell_power:soul",
                "spell_power:healing",
                "puffish_attributes:magic_damage"
        };
        for (String target : flatPercentTargets) {
            String slug = slug(target);
            list.add(new ModifierSpec("int_" + slug + "_flat", target, flat, ADD_VALUE));
            list.add(new ModifierSpec("int_" + slug + "_pct", target, percent, ADD_MULTIPLIED_TOTAL));
        }

        // Flat saja & koefisien kecil - tetap dari poin INT
        list.add(new ModifierSpec("int_max_mana", "manaattributes:max_mana", Math.floor(flat), ADD_VALUE));
        list.add(new ModifierSpec("int_mana_regeneration", "manaattributes:mana_regeneration", intPoints * 0.005D, ADD_VALUE));
        list.add(new ModifierSpec("int_mana_spell_cost_multiplier", "spellengineextension:generic.mana_spell_cost_multiplier", -(intPoints * 0.005D), ADD_VALUE));

        double launchLevel = Math.floor(intPoints / 20D);
        list.add(new ModifierSpec("int_extra_launch_count", "spellengineextension:generic.extra_launch_count", launchLevel, ADD_VALUE));

        // Persen saja (resistance/shred) - pakai persen asli INT juga
        list.add(new ModifierSpec("int_magic_resistance_pct", "puffish_attributes:magic_resistance", percent, ADD_VALUE));
        list.add(new ModifierSpec("int_magic_resistance_shred_pct", "puffish_attributes:magic_resistance_shred", percent, ADD_VALUE));

        return list;
    }

    // ------------------------------------------------------------------
    // LCK - Fortune & Chance  (semua formula LCK cuma pakai poin flat, tidak ada pasangan persen)
    // ------------------------------------------------------------------
    private static List<ModifierSpec> buildLckModifiers(double lckPoints) {
        List<ModifierSpec> list = new ArrayList<>();

        list.add(new ModifierSpec("lck_luck", "minecraft:luck", lckPoints * 0.5D, ADD_VALUE));
        list.add(new ModifierSpec("lck_fortune", "puffish_attributes:fortune", lckPoints * 0.5D, ADD_VALUE));

        list.add(new ModifierSpec("lck_crit_chance", "spell_power:critical_chance", lckPoints * 0.005D, ADD_VALUE));
        list.add(new ModifierSpec("lck_crit_damage", "spell_power:critical_damage", lckPoints * 0.05D, ADD_VALUE));

        double reflection = Math.min(0.10D, lckPoints * 0.00125D);
        list.add(new ModifierSpec("lck_damage_reflection", "puffish_attributes:damage_reflection", reflection, ADD_VALUE));

        double repairReduction = Math.min(5D, Math.floor(lckPoints / 15D));
        list.add(new ModifierSpec("lck_repair_cost_reduction", "puffish_attributes:repair_cost", -repairReduction, ADD_VALUE));

        double projectileBonus = Math.min(6D, Math.floor((lckPoints * 0.75D) / 10D));
        String[] projectileTargets = {
                "spellengineextension:generic.extra_ricochet",
                "spellengineextension:generic.extra_ricochet_range",
                "spellengineextension:generic.extra_bounce",
                "spellengineextension:generic.extra_pierce",
                "spellengineextension:generic.extra_chain_reaction_size",
                "spellengineextension:generic.extra_chain_reaction_triggers"
        };
        for (String target : projectileTargets) {
            list.add(new ModifierSpec("lck_" + slug(target), target, projectileBonus, ADD_VALUE));
        }

        return list;
    }

    // ------------------------------------------------------------------
    // Baca stat sumber, dipisah jadi "poin flat" vs "persen asli"
    // ------------------------------------------------------------------
    private static StatBreakdown readStat(ServerPlayerEntity player, RegistryEntry<EntityAttribute> attribute) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance == null) {
            return new StatBreakdown(0.0D, 0.0D);
        }

        double flatPoints = instance.getBaseValue() + sumByOperation(instance, Operation.ADD_VALUE);
        double percentBonus = sumByOperation(instance, Operation.ADD_MULTIPLIED_BASE)
                + sumByOperation(instance, Operation.ADD_MULTIPLIED_TOTAL);

        return new StatBreakdown(flatPoints, percentBonus);
    }

    private static double sumByOperation(EntityAttributeInstance instance, Operation operation) {
        double sum = 0.0D;
        for (EntityAttributeModifier modifier : instance.getModifiers()) {
            if (modifier.operation() == operation) {
                sum += modifier.value();
            }
        }
        return sum;
    }

    // ------------------------------------------------------------------
    // Terapkan hasil hitungan ke attribute tujuan
    // ------------------------------------------------------------------
    private static void applySpec(ServerPlayerEntity player, ModifierSpec spec) {
        Optional<RegistryEntry.Reference<EntityAttribute>> targetOpt = AttributeRegistry.get(spec.targetAttributeId());
        if (targetOpt.isEmpty()) {
            return; // mod pemilik attribute ini tidak terpasang - lewati
        }

        EntityAttributeInstance instance = player.getAttributeInstance(targetOpt.get());
        if (instance == null) {
            return;
        }

        Identifier modifierId = HillSRPG.id(spec.key());
        instance.removeModifier(modifierId);
        if (spec.value() != 0.0D) {
            instance.addPersistentModifier(new EntityAttributeModifier(modifierId, spec.value(), spec.operation()));
        }
    }

    private static String slug(String attributeId) {
        return attributeId.replace(':', '_').replace('.', '_');
    }

    /** poin flat = base + total ADD_VALUE. persenBonus = total ADD_MULTIPLIED_BASE/TOTAL yang nempel di stat sumber. */
    private record StatBreakdown(double flatPoints, double percentBonus) {}

    private record ModifierSpec(String key, String targetAttributeId, double value, Operation operation) {}
}