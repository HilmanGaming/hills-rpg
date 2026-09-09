package net.hilman.hilmanrpg.item;

import net.hilman.hilmanrpg.HillSRPG;
import net.hilman.hilmanrpg.item.custom.classrpg.ModArmorItems;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {

    private ModItems() {
    }

    public static <T extends Item> T register(String path, T item) {
        Identifier id = HillSRPG.id(path);
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void register() {
        ModArmorItems.register();
        HillSRPG.LOGGER.info("Registering hills-rpg items");
    }
}