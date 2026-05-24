package com.arnav.evenbettercombat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class EvenBetterCombatClient implements ClientModInitializer {
    private static KeyBinding toggleHud;
    private static boolean wasDead = false;
    private static String currentServer = "singleplayer";

    private static final int[] MILESTONES = {5, 10, 15, 20, 25, 30, 50};

    @Override
    public void onInitializeClient() {
        toggleHud = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.evenbettercombat.toggle_hud",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.category.evenbettercombat.general"
        ));

        HudRenderCallback.EVENT.register(HudOverlay::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHud.wasPressed()) {
                TrackerState.hudVisible = !TrackerState.hudVisible;
            }
            if (client.player == null) return;
            boolean isDead = client.player.getHealth() <= 0;
            if (isDead && !wasDead) {
                TrackerState.onDeath();
                StatsStorage.save(currentServer);
            }
            wasDead = isDead;
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;
            String localName = client.player.getName().getString();
            String msg = message.getString();
            if (msg.startsWith(localName + " ")) return; // local player died — handled by tick
            if (msg.contains(localName)) {
                TrackerState.onKill();
                StatsStorage.save(currentServer);
                checkMilestone(client, TrackerState.streak);
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            wasDead = false;
            currentServer = handler.getConnection().getAddress() != null
                ? handler.getConnection().getAddress().toString()
                : "singleplayer";
            TrackerState.reset();
            StatsStorage.load(currentServer);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            StatsStorage.save(currentServer);
        });
    }

    private static void checkMilestone(MinecraftClient client, int streak) {
        for (int m : MILESTONES) {
            if (streak == m) {
                client.inGameHud.getChatHud().addMessage(
                    Text.literal("§6[Even Better Combat] §e" + streak + " kill streak! Keep it up!")
                );
                break;
            }
        }
    }
}
