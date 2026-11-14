package com.enderdevelopment.advancedenchants;

import net.fabricmc.api.ModInitializer;

public final class AdvancedEnchantsMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModCommands.register();
    }
}
