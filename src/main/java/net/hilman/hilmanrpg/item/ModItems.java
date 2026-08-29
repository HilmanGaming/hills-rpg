package net.hilman.hilmanrpg.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hilman.hilmanrpg.HillSRPG;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {

    public static final Item SCORCHING_STAFF = registerItem("scorching_staff", new Item(new Item.Settings().maxCount(1)));


    public static final Item RUNIC_SHARD = registerItem("runic_shard", new Item(new Item.Settings()));



    private static Item registerItem (String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(HillSRPG.MOD_ID, name), item);
    }

    public static void registerModItems(){
        HillSRPG.LOGGER.info("Registering Mod Items for " + HillSRPG.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries ->{
            entries.add(SCORCHING_STAFF);
            entries.add(RUNIC_SHARD);
        });
    }
}
