package com.bettersnowballs;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

/**
 * Main entry point for Better Snowballs.
 *
 * The custom damage type is data-driven and therefore comes from
 * resources/data/bettersnowballs/damage_type/snowball_impact.json.
 * This class only exposes the ResourceKey used by the Snowball mixin.
 */
public final class BetterSnowballs implements ModInitializer {

    public static final String MOD_ID = "bettersnowballs";

    public static final ResourceKey<DamageType> SNOWBALL_IMPACT_DAMAGE_TYPE =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    Identifier.fromNamespaceAndPath(MOD_ID, "snowball_impact")
            );

    @Override
    public void onInitialize() {
        // Damage types are loaded from data; no Java-side registration is required.
    }
}
