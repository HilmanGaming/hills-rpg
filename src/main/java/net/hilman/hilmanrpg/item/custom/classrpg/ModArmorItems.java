package net.hilman.hilmanrpg.item.custom.classrpg;

import net.hilman.hilmanrpg.HillSRPG;
import net.hilman.hilmanrpg.item.ModItems;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class ModArmorItems {

    public static final Item HEAVY_HELMET = registerArmor("heavy_helmet", ArmorMaterials.IRON, ArmorItem.Type.HELMET, "warrior");
    public static final Item HEAVY_CHESTPLATE = registerArmor("heavy_chestplate", ArmorMaterials.IRON, ArmorItem.Type.CHESTPLATE, "warrior");
    public static final Item HEAVY_LEGGINGS = registerArmor("heavy_leggings", ArmorMaterials.IRON, ArmorItem.Type.LEGGINGS, "warrior");
    public static final Item HEAVY_BOOTS = registerArmor("heavy_boots", ArmorMaterials.IRON, ArmorItem.Type.BOOTS, "warrior");

    public static final Item MEDIUM_HELMET = registerArmor("medium_helmet", ArmorMaterials.CHAIN, ArmorItem.Type.HELMET, "rogue");
    public static final Item MEDIUM_CHESTPLATE = registerArmor("medium_chestplate", ArmorMaterials.CHAIN, ArmorItem.Type.CHESTPLATE, "rogue");
    public static final Item MEDIUM_LEGGINGS = registerArmor("medium_leggings", ArmorMaterials.CHAIN, ArmorItem.Type.LEGGINGS, "rogue");
    public static final Item MEDIUM_BOOTS = registerArmor("medium_boots", ArmorMaterials.CHAIN, ArmorItem.Type.BOOTS, "rogue");

    public static final Item LIGHT_HELMET = registerArmor("light_helmet", ArmorMaterials.LEATHER, ArmorItem.Type.HELMET, "mage");
    public static final Item LIGHT_CHESTPLATE = registerArmor("light_chestplate", ArmorMaterials.LEATHER, ArmorItem.Type.CHESTPLATE, "mage");
    public static final Item LIGHT_LEGGINGS = registerArmor("light_leggings", ArmorMaterials.LEATHER, ArmorItem.Type.LEGGINGS, "mage");
    public static final Item LIGHT_BOOTS = registerArmor("light_boots", ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, "mage");

    private ModArmorItems() {
    }

    private static Item registerArmor(String path, RegistryEntry<ArmorMaterial> material, ArmorItem.Type type,
                                      String requiredBaseClass) {
        Identifier requiredAdvancement = HillSRPG.id("class/" + requiredBaseClass);

        ClassArmorItem item = new ClassArmorItem(
                material,
                type,
                new Item.Settings(),
                requiredAdvancement
        );

        return ModItems.register(path, item);
    }

    public static void register() {
        HillSRPG.LOGGER.info("Registering hills-rpg class-restricted armor (Heavy/Medium/Light)");
    }
}