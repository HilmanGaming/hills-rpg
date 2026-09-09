package net.hilman.hilmanrpg.item.custom.classrpg;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ClassArmorItem extends ArmorItem implements ClassRestrictedItem {
	private final Identifier requiredClassAdvancement;

	public ClassArmorItem(RegistryEntry<ArmorMaterial> material, Type type, Settings settings,
						  Identifier requiredClassAdvancement) {
		super(material, type, settings);
		this.requiredClassAdvancement = requiredClassAdvancement;
	}

	@Override
	public Identifier getRequiredClassAdvancement() {
		return requiredClassAdvancement;
	}
}