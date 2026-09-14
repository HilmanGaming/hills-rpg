package net.hilman.hilmanrpg.attribute;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Cari EntityAttribute milik mod lain (Spell Power, Mana Attributes, Stamina Attributes,
 * Spell Engine Extension, Puffish Attributes, vanilla, dst) lewat Identifier string.
 *
 * Sengaja pakai lookup string, bukan import class dari mod-mod itu, karena mod-mod itu
 * tidak dijadikan compile-time dependency di build.gradle (soft/runtime dependency saja).
 * Kalau mod pemiliknya tidak terpasang, get() balik Optional.empty() dan efek terkait
 * otomatis dilewati tanpa error/crash.
 */

public final class AttributeRegistry {
    private static final Map<String, Optional<RegistryEntry.Reference<EntityAttribute>>> CACHE = new HashMap<>();

    private AttributeRegistry() {}

    public static Optional<RegistryEntry.Reference<EntityAttribute>> get(String namespacedId) {
        return CACHE.computeIfAbsent(namespacedId, key -> {
            Identifier id = Identifier.of(key);
            return Registries.ATTRIBUTE.getEntry(RegistryKey.of(RegistryKeys.ATTRIBUTE, id));
        });
    }

    public static void clearCache() {
        CACHE.clear();
    }
}