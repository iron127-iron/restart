package me.restart.mod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * RestartMod — Fabric 版重啟倒數指令插件
 *
 * 指令：
 *   /restart <秒數>   開始倒數後重啟（1~120 秒）
 *   /restart stop     取消正在進行的倒數
 *   /restart status   顯示目前倒數狀態
 *
 * 權限：restart.use（預設為 op / 權限等級 2）
 */
public class RestartMod implements ModInitializer {

    private static final Logger LOGGER = Logger.getLogger("RestartMod");
    private static final int OP_PERMISSION_LEVEL = 2;

    // 倒數狀態
    private final AtomicBoolean restarting = new AtomicBoolean(false);
    private volatile long remainingSeconds = 0;
    private volatile ScheduledExecutorService scheduler = null;

    @Override
    public void onInitialize() {
        LOGGER.info("RestartMod 已啟用！Minecraft 1.21.11");

        // 註冊指令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) ->
                dispatcher.register(CommandManager.literal("restart")
                        .requires(source -> CommandManager.ADMINS_CHECK.allows(source.getPermissions()))
                        .then(CommandManager.argument("seconds", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 120))
                                .executes(context -> executeRestart(
                                        context.getSource(),
                                        context.getArgument("seconds", Integer.class)))
                        )
                        .then(CommandManager.literal("stop")
                                .executes(context -> executeStop(context.getSource())))
                        .then(CommandManager.literal("status")
                                .executes(context -> executeStatus(context.getSource())))
                )
        );
    }

    // ─── /restart <秒> ───────────────────────────────────────────────

    private int executeRestart(ServerCommandSource source, int seconds) {
        if (restarting.compareAndSet(false, true)) {
            remainingSeconds = seconds;
            startCountdown(seconds, source.getServer());
            source.sendFeedback(
                    () -> net.minecraft.text.Text.literal("§a已開始重啟倒數！「" + seconds + "秒後重啟」"),
                    false
            );
            return seconds;
        } else {
            source.sendFeedback(
                    () -> net.minecraft.text.Text.literal("§c已經在重啟倒數中了！請先用 /restart stop 取消。"),
                    false
            );
            return 0;
        }
    }

    private void startCountdown(int seconds, MinecraftServer server) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (!restarting.get()) {
                return;
            }

            // 更新剩餘秒數
            remainingSeconds--;

            if (remainingSeconds <= 0) {
                restarting.set(false);
                scheduler.shutdownNow();
                // 廣播重啟訊息
                server.sendMessage(net.minecraft.text.Text.literal("§c伺服器正在重啟..."));
                // 觸發伺服器關閉
                server.shutdown();
                return;
            }

            // 顯示倒數訊息給所有在線玩家
            net.minecraft.text.Text message = net.minecraft.text.Text.literal(
                "§c⚠ 即將重啟 ⚠§r\n§e" + remainingSeconds + " 秒後重啟"
            );
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                player.sendMessage(message, false);
            }
        }, 0, 20, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    // ─── /restart stop ───────────────────────────────────────────────

    private int executeStop(ServerCommandSource source) {
        if (!restarting.compareAndSet(true, false)) {
            source.sendFeedback(
                    () -> net.minecraft.text.Text.literal("§e目前沒有重啟倒數！"),
                    false
            );
            return 0;
        }

        // 取消計時器
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }

        // 發送取消訊息
        net.minecraft.text.Text message = net.minecraft.text.Text.literal("§a已取消重啟");
        MinecraftServer server = source.getServer();
        for (net.minecraft.server.network.ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(message, false);
        }

        source.sendFeedback(
                () -> net.minecraft.text.Text.literal("§a已停止重啟倒數！"),
                false
        );
        return 1;
    }

    // ─── /restart status ─────────────────────────────────────────────

    private int executeStatus(ServerCommandSource source) {
        if (!restarting.get()) {
            source.sendFeedback(
                    () -> net.minecraft.text.Text.literal("§e目前沒有進行中的重啟倒數。"),
                    false
            );
        } else {
            source.sendFeedback(
                    () -> net.minecraft.text.Text.literal("§c重啟倒數中！剩餘 §e" + remainingSeconds + " §c秒"),
                    false
            );
        }
        return 1;
    }
}