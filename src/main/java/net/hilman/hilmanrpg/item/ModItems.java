package net.hilman.hilmanrpg.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hilman.hilmanrpg.HillSRPG;
import net.hilman.hilmanrpg.rpgclass.PlayerClass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {

    public static final Item MAGE_WAND = register(
            "mage_wand",
            Item::new,
            new Item.Settings().maxCount(1).maxDamage(200)
    );

    public static final Item MAGE_TOME = register(
            "mage_tome",
            settings -> new ClassTomeItem(settings, PlayerClass.MAGE, () -> MAGE_WAND),
            new Item.Settings().maxCount(1)
    );

    private ModItems() {
    }

    private static Item register(String path, java.util.function.Function<Item.Settings, Item> factory, Item.Settings settings) {
        Identifier id = HillSRPG.id(path);
        Item item = factory.apply(settings);
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(MAGE_TOME);
            entries.add(MAGE_WAND);
        });
    }
}