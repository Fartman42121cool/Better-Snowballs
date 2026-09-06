package com.bettersnowballs.mixin;

import com.bettersnowballs.BetterSnowballs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds a small combat buff to thrown snowballs.
 *
 * This only ever injects (@Inject at TAIL) into vanilla's Snowball#onHitEntity; it never
 * overwrites it. That means vanilla's own logic (including the special-case damage snowballs
 * already deal to Blazes) still runs exactly as before; we just run a little extra logic
 * afterwards. This is the safest way to change vanilla behaviour without breaking other mods
 * that also modify Snowball or projectiles in general.
 */
@Mixin(Snowball.class)
public abstract class SnowballMixin {

	// Tune these to taste.
	@Unique
	private static final float BETTERSNOWBALLS$DAMAGE = 1.0F; // half a heart
	@Unique
	private static final int BETTERSNOWBALLS$SLOWNESS_DURATION_TICKS = 3 * 20; // 3 seconds
	@Unique
	private static final int BETTERSNOWBALLS$SLOWNESS_AMPLIFIER = 0; // Slowness I
	@Unique
	private static final float BETTERSNOWBALLS$IGNITE_SECONDS = 5.0F;

	@Inject(method = "onHitEntity", at = @At("TAIL"))
	private void bettersnowballs$onHitEntity(EntityHitResult hitResult, CallbackInfo ci) {
		Snowball self = (Snowball) (Object) this;
		Entity hitEntity = hitResult.getEntity();

		// Only run our logic on the logical server, and only against living targets.
		if (self.level().isClientSide() || !(hitEntity instanceof LivingEntity target)) {
			return;
		}

		ServerLevel serverLevel = (ServerLevel) self.level();

		// Look up our data-driven damage type and build a DamageSource for it.
		// The snowball is the direct source entity, and whoever threw it (if anyone -
		// dispensers leave this null) is the attacker.
		Holder<DamageType> snowballDamageType = serverLevel.registryAccess()
				.lookupOrThrow(Registries.DAMAGE_TYPE)
				.get(BetterSnowballs.SNOWBALL_IMPACT_DAMAGE_TYPE.identifier())
				.orElseThrow();
		DamageSource damageSource = new DamageSource(snowballDamageType, self, self.getOwner());
		target.hurtServer(serverLevel, damageSource, BETTERSNOWBALLS$DAMAGE);

		// Slowness I for 3 seconds.
		target.addEffect(new MobEffectInstance(
				MobEffects.SLOWNESS,
				BETTERSNOWBALLS$SLOWNESS_DURATION_TICKS,
				BETTERSNOWBALLS$SLOWNESS_AMPLIFIER));

		// If the snowball itself was on fire when it hit, set the target alight too.
		if (self.isOnFire()) {
			target.igniteForSeconds(BETTERSNOWBALLS$IGNITE_SECONDS);
		}
	}
}
