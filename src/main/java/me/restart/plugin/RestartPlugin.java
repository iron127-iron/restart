package me.restart.plugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class RestartPlugin extends JavaPlugin {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private boolean isRestarting = false;
    private RestartCountdown countdownTask = null;

    @Override
    public void onEnable() {
        getLogger().info("RestartPlugin 已啟用！");
    }

    private Component msg(String legacy) {
        return LEGACY.deserialize(legacy);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("retart") || cmd.equals("restart")) {

            // Permission check
            if (!sender.hasPermission("restart.use")) {
                sender.sendMessage(msg("§c你沒有權限使用這個指令！"));
                return true;
            }

            // /retart stop — 停止重啟倒數
            if (args.length >= 1 && args[0].equalsIgnoreCase("stop")) {
                if (!isRestarting || countdownTask == null) {
                    sender.sendMessage(msg("§e目前沒有重啟倒數！"));
                    return true;
                }
                countdownTask.cancel();
                countdownTask = null;
                isRestarting = false;

                // 清除畫面上的標題
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.showTitle(Title.title(msg("§a已取消重啟"), Component.empty()));
                }

                sender.sendMessage(msg("§a已停止重啟倒數！"));
                return true;
            }

            if (isRestarting) {
                sender.sendMessage(msg("§c已經在重啟倒數中了！"));
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage(msg("§c用法: /retart <秒> | /retart stop"));
                return true;
            }

            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(msg("§c請輸入數字！"));
                return true;
            }

            if (time <= 0 || time > 60) {
                sender.sendMessage(msg("§c時間必須在 1~60 秒之間！"));
                return true;
            }

            isRestarting = true;

            RestartCountdown task = new RestartCountdown(time);
            this.countdownTask = task;
            task.runTaskTimer(this, 0L, 20L);

            sender.sendMessage(msg("§a已開始重啟倒數！"));
            return true;
        }

        return false;
    }

    private class RestartCountdown extends BukkitRunnable {
        private int countdown;

        RestartCountdown(int time) {
            this.countdown = time;
        }

        @Override
        public void run() {
            if (countdown <= 0) {
                RestartPlugin.this.countdownTask = null;
                RestartPlugin.this.isRestarting = false;
                Bukkit.getServer().sendMessage(msg("§c伺服器正在重啟..."));
                cancel();
                //noinspection deprecation
                Bukkit.shutdown();
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.showTitle(Title.title(
                        msg("§c⚠ 即將重啟 ⚠"),
                        msg("§e" + countdown + " 秒後重啟"),
                        Title.Times.times(Duration.ZERO, Duration.ofMillis(1000), Duration.ZERO)
                ));
            }

            countdown--;
        }
    }
}
