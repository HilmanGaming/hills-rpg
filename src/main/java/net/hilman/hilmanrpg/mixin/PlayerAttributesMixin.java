package net.hilman.hilmanrpg.mixin;

import net.hilman.hilmanrpg.attribute.ModAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * PlayerEntity#createPlayerAttributes() itu method STATIC yang menghasilkan builder
 * daftar atribut bawaan player (max health, movement speed, dst). Registry.ATTRIBUTE
 * hanya "mendaftarkan jenis" atribut - atribut itu baru benar-benar dimiliki entity
 * kalau ditambahkan ke builder ini.
 *
 * Karena PlayerEntity bukan entity kita sendiri, satu-satunya cara nambahin atribut
 * custom ke player adalah lewat mixin ke method static ini (bukan lewat
 * FabricDefaultAttributeRegistry, yang cuma berlaku untuk EntityType buatan sendiri).
 */
@Mixin(PlayerEntity.class)
public class PlayerAttributesMixin {

	@Inject(method = "createPlayerAttributes", at = @At("RETURN"))
	private static void hillsrpg$addCustomAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		cir.getReturnValue()
				.add(ModAttributes.STRENGTH)
				.add(ModAttributes.INTELLIGENCE)
				.add(ModAttributes.VITALITY)
				.add(ModAttributes.DEXTERITY)
				.add(ModAttributes.LUCK);
	}
}