package net.hilman.hilmanrpg.mixin;

import net.hilman.hilmanrpg.attribute.ModAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Nempelin STR/VIT/DEX/INT/LCK ke setiap pemain lewat createPlayerAttributes().
 * Builder yang dikembalikan method itu masih mutable, jadi cukup di-inject di RETURN
 * lalu ditambahin langsung - tidak perlu mengganti return value.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityAttributeMixin {

	@Inject(method = "createPlayerAttributes", at = @At("RETURN"))
	private static void hillsRpg$addCoreStats(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		cir.getReturnValue()
				.add(ModAttributes.STRENGTH)
				.add(ModAttributes.VITALITY)
				.add(ModAttributes.DEXTERITY)
				.add(ModAttributes.INTELLIGENCE)
				.add(ModAttributes.LUCK);
	}
}