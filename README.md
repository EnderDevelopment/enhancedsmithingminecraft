# Minecraft Mod: Advanced Enchants

Advanced Enchants is a Fabric Minecraft mod for advanced enchantment levels, combining supported enchantments between held items, and legacy `&` color and formatting codes in chat. It supports Sharpness VII, Looting IV, Feather Falling V, and Protection, Fire Protection, Blast Protection, and Projectile Protection V.

## Install

1. Install Minecraft 26.3, Java 25 or newer, Fabric Loader 0.20.1 or newer, and Fabric API.
2. Place the built mod JAR in your Minecraft instance’s `mods` folder.
3. Launch the game with the Fabric profile.

## Use

1. Hold an item in your main hand and run `/advancedench <enchantment> <level>` to apply an enchantment. Supported names are `sharpness`, `looting`, `feather_falling`, `protection`, `fire_protection`, `blast_protection`, and `projectile_protection`. Requested levels are limited to each enchantment’s supported cap.
2. To combine enchantments, hold the first item in your main hand and the second in your off hand, then run `/advancedench combine`. Supported enchantments on the offhand item are merged into the main-hand item. Equal levels increase by one, up to the supported cap; unequal levels use the higher level. The offhand item is not consumed.
3. To broadcast colored or formatted text, run `/color <message>`. For example: `/color &6Legendary &lgear&r unlocked!`. Supported codes include `&0`–`&9`, `&a`–`&f`, `&k`, `&l`, `&m`, `&n`, `&o`, and `&r`.

This is a Fabric mod, not a datapack. It does not change anvil behavior or remove the “Too Expensive” limit.

---

More Minecraft mod projects by EnderDevelopment: https://github.com/EnderDevelopment

Project page: https://dash.enderdevelopment.com/community/79760b00-492f-445c-a476-ec31cfab6332?utm_source=github&utm_medium=readme&utm_campaign=auto_publish&utm_content=79760b00-492f-445c-a476-ec31cfab6332
