package net.hilman.hilmanrpg.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.hilman.hilmanrpg.HillSRPG;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block RUNIC_ORE_BLOCK = registerBlock("runic_ore_block",
            new Block(AbstractBlock.Settings.create().strength(5.5f)
                    .requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));



    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(HillSRPG.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(HillSRPG.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        HillSRPG.LOGGER.info("Registering Mod Blocks for " + HillSRPG.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(ModBlocks.RUNIC_ORE_BLOCK);
        });
    }
}