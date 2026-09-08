package net.hilman.hilmanrpg.item;

import net.hilman.hilmanrpg.rpgclass.ClassManager;
import net.hilman.hilmanrpg.rpgclass.PlayerClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.function.Supplier;

/**
 * Generic "become class X" tome. Any class's tome is just an instance of this with a
 * different PlayerClass + starter item - no per-class Item subclass needed.
 *
 * Translation keys used (see en_us.json), where <class> = playerClass.name().toLowerCase():
 *  - hills-rpg.class.<class>.chosen
 *  - hills-rpg.class.already_chosen  (shared across all classes)
 */
public class ClassTomeItem extends Item {

    private final PlayerClass playerClass;
    private final Supplier<Item> starterItem;

    public ClassTomeItem(Settings settings, PlayerClass playerClass, Supplier<Item> starterItem) {
        super(settings);
        this.playerClass = playerClass;
        this.starterItem = starterItem;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack heldStack = user.getStackInHand(hand);

        if (world.isClient) {
            return TypedActionResult.success(heldStack);
        }

        if (ClassManager.hasAnyClass(user)) {
            user.sendMessage(Text.translatable("hills-rpg.class.already_chosen"), true);
            return TypedActionResult.fail(heldStack);
        }

        ClassManager.assign(user, playerClass);

        ItemStack starter = new ItemStack(starterItem.get());
        if (!user.giveItemStack(starter)) {
            user.dropItem(starter, false);
        }

        String key = "hills-rpg.class." + playerClass.name().toLowerCase() + ".chosen";
        user.sendMessage(Text.translatable(key), false);
        return TypedActionResult.consume(heldStack);
    }
}