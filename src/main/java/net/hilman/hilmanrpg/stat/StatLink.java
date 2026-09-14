package net.hilman.hilmanrpg.stat;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

/**
 * Satu baris mapping: "1 poin stat mentah (STR/INT/VIT/DEX/LCK) = sekian bonus
 * di attribute target (biasanya dari Puffish Attributes / Stamina Attributes)".
 *
 * @param targetAttributeId id attribute tujuan, mis. "puffish_attributes:sword_damage".
 *                          Dicari lewat Registries.ATTRIBUTE saat runtime — TIDAK
 *                          butuh dependency compile-time ke mod pemiliknya. Kalau
 *                          mod itu ternyata tidak terpasang, link ini otomatis
 *                          di-skip (lihat StatAttributeSync), bukan crash.
 * @param amountPerPoint    besar bonus per 1 poin stat mentah. Untuk attribute
 *                          "pct" (dynamic attribute Puffish Attributes), ini
 *                          pecahan (0.0004 = +0.04% per poin). Untuk attribute
 *                          "flat" (armor shred, jump, dll), ini angka apa adanya.
 * @param operation         hampir selalu ADD_VALUE untuk dynamic attribute
 *                          Puffish Attributes (base-nya 0, lihat catatan yang
 *                          sama di CONCEPT_ADEPT_TIER.md).
 * @param modifierId        id UNIK & TETAP untuk modifier ini. Dipakai untuk
 *                          bisa ditimpa/dihapus tiap kali nilai stat mentah
 *                          berubah, tanpa numpuk modifier lama. JANGAN diubah
 *                          setelah dipakai di server yang sedang jalan (nanti
 *                          modifier lama nyangkut selamanya di player yang
 *                          sudah login).
 */
public record StatLink(
		Identifier targetAttributeId,
		double amountPerPoint,
		EntityAttributeModifier.Operation operation,
		Identifier modifierId
) {
}