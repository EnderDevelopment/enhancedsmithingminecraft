package com.enderdevelopment.advancedenchants;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public final class ModCommands {
    private static final Map<String, ResourceKey<Enchantment>> ENCHANTMENTS = Map.ofEntries(
            Map.entry("sharpness", key("sharpness")),
            Map.entry("looting", key("looting")),
            Map.entry("feather_falling", key("feather_falling")),
            Map.entry("protection", key("protection")),
            Map.entry("fire_protection", key("fire_protection")),
            Map.entry("blast_protection", key("blast_protection")),
            Map.entry("projectile_protection", key("projectile_protection"))
    );

    private static final Map<String, Integer> CAPS = Map.of(
            "sharpness", 7,
            "looting", 4,
            "feather_falling", 5,
            "protection", 5,
            "fire_protection", 5,
            "blast_protection", 5,
            "projectile_protection", 5
    );

    private ModCommands() {
    }

    private static ResourceKey<Enchantment> key(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.withDefaultNamespace(name));
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("advancedench")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .then(Commands.literal("combine").executes(context -> combine(context.getSource().getPlayerOrException(), context.getSource())))
                    .then(Commands.argument("enchantment", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                ENCHANTMENTS.keySet().forEach(builder::suggest);
                                return builder.buildFuture();
                            })
                            .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                                    .executes(context -> apply(context.getSource().getPlayerOrException(), context.getSource(),
                                            StringArgumentType.getString(context, "enchantment").toLowerCase(),
                                            IntegerArgumentType.getInteger(context, "level"))))));

            dispatcher.register(Commands.literal("color")
                    .then(Commands.argument("message", StringArgumentType.greedyString())
                            .executes(context -> {
                                Component message = Formatting.parse(StringArgumentType.getString(context, "message"));
                                context.getSource().getServer().getPlayerList().broadcastSystemMessage(message, false);
                                return Command.SINGLE_SUCCESS;
                            })));
        });
    }

    private static int apply(ServerPlayer player, net.minecraft.commands.CommandSourceStack source, String name, int requestedLevel) {
        ResourceKey<Enchantment> key = ENCHANTMENTS.get(name);
        Integer cap = CAPS.get(name);
        if (key == null) {
            source.sendFailure(Component.literal("Unknown enchantment. Use sharpness, looting, feather_falling, protection, fire_protection, blast_protection, or projectile_protection."));
            return 0;
        }
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.literal("Hold an item first."));
            return 0;
        }
        int level = Math.min(requestedLevel, cap);
        Holder<Enchantment> enchantment = source.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(key);
        EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(enchantment, level));
        source.sendSuccess(() -> Component.literal("Applied " + name + " " + level + " to the held item."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int combine(ServerPlayer player, net.minecraft.commands.CommandSourceStack source) {
        ItemStack main = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();
        if (main.isEmpty() || offhand.isEmpty()) {
            source.sendFailure(Component.literal("Hold the first item in your main hand and the second item in your off hand."));
            return 0;
        }
        var lookup = source.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        EnchantmentHelper.updateEnchantments(main, mutable -> {
            ENCHANTMENTS.forEach((name, key) -> {
                Holder<Enchantment> enchantment = lookup.getOrThrow(key);
                int mainLevel = mutable.getLevel(enchantment);
                int offhandLevel = EnchantmentHelper.getItemEnchantmentLevel(enchantment, offhand);
                if (offhandLevel > 0) {
                    int combined = Math.min(CAPS.get(name), Math.max(mainLevel, offhandLevel) + (mainLevel == offhandLevel ? 1 : 0));
                    mutable.set(enchantment, combined);
                }
            });
        });
        source.sendSuccess(() -> Component.literal("Combined supported enchantments into the main-hand item."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static final class Formatting {
        private Formatting() {
        }

        private static Component parse(String input) {
            MutableComponent result = Component.empty();
            StringBuilder segment = new StringBuilder();
            Style style = Style.EMPTY;
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == '&' && i + 1 < input.length()) {
                    ChatFormatting code = ChatFormatting.getByCode(input.charAt(i + 1));
                    if (code != null) {
                        if (!segment.isEmpty()) {
                            result.append(Component.literal(segment.toString()).setStyle(style));
                            segment.setLength(0);
                        }
                        style = code == ChatFormatting.RESET ? Style.EMPTY : style.applyLegacyFormat(code);
                        i++;
                        continue;
                    }
                }
                segment.append(input.charAt(i));
            }
            if (!segment.isEmpty()) {
                result.append(Component.literal(segment.toString()).setStyle(style));
            }
            return result;
        }
    }
}
