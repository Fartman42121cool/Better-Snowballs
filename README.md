# Better Snowballs

A small Fabric mod for Minecraft 26.2 that buffs snowballs:

- Deals a small amount of damage on hit (0.5 heart by default) using its own custom,
  data-driven damage type (`bettersnowballs:snowball_impact`), instead of hijacking a
  vanilla one.
- Applies **Slowness I** for **3 seconds** to whatever it hits.
- If the snowball itself was on fire when it hit (e.g. it flew through fire or lava on
  the way there), it **ignites** the target too.

All of this is added via a single `@Inject` at the *tail* of vanilla's
`Snowball#onHitEntity`, not an `@Overwrite`. That means every bit of vanilla's own
snowball logic (including the extra damage snowballs already deal to Blazes) keeps
running exactly as before - this mod only adds behavior on top, it never replaces
vanilla code outright. That's the safest way to change built-in behavior without
stepping on other mods that also touch `Snowball` or projectiles in general.

## Building

1. Unzip this project.
2. This zip does **not** include the `gradlew` / `gradlew.bat` wrapper scripts or
   `gradle-wrapper.jar`, since those are binary files I can't safely generate without
   network access. Get them one of two ways:
   - **Easiest:** Open the folder in IntelliJ IDEA (2025.3 or newer, for full Java 25
     support) as a Gradle project. It will detect `settings.gradle` and offer to set
     up the wrapper/import automatically.
   - **Manual:** If you have Gradle installed locally, run this once inside the
     project folder to generate the wrapper (it will read the version from
     `gradle/wrapper/gradle-wrapper.properties`, which is already set to 9.5.1):
     ```
     gradle wrapper
     ```
3. Build the mod:
   ```
   ./gradlew build
   ```
   (or `gradle build` if you don't have the wrapper set up)
4. The finished jar will be at `build/libs/bettersnowballs-1.0.0.jar`.

## Installing (for players)

1. Install **Fabric Loader 0.19.5+** for **Minecraft 26.2**.
2. Download **Fabric API** for **26.2** (currently `0.159.0+26.2` or newer) from
   Modrinth or CurseForge.
3. Install **Java 25+**.
4. Put `bettersnowballs-1.0.0.jar` and the Fabric API jar in your `.minecraft/mods`
   folder.

## Tuning

Open `src/main/java/com/bettersnowballs/mixin/SnowballMixin.java` and edit the
constants at the top of the class:

- `BETTERSNOWBALLS$DAMAGE` - damage dealt per hit (1.0 = half a heart)
- `BETTERSNOWBALLS$SLOWNESS_DURATION_TICKS` - Slowness duration (20 ticks = 1 second)
- `BETTERSNOWBALLS$SLOWNESS_AMPLIFIER` - 0 = Slowness I, 1 = Slowness II, etc.
- `BETTERSNOWBALLS$IGNITE_SECONDS` - how long targets burn for when hit by a burning
  snowball
