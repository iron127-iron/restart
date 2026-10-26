package me.restart.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartPlugin extends JavaPlugin {

    private boolean isRestarting = false;
    private RestartCountdown countdownTask = null;

    @Override
    public void onEnable() {
        getLogger().info("RestartPlugin 已啟用！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("retart") || cmd.equals("restart")) {

            // Permission check
            if (!sender.hasPermission("restart.use")) {
                sender.sendMessage("§c你沒有權限使用這個指令！");
                return true;
            }

            // /retart stop — 停止重啟倒數
            if (args.length >= 1 && args[0].equalsIgnoreCase("stop")) {
                if (!isRestarting || countdownTask == null) {
                    sender.sendMessage("§e目前沒有重啟倒數！");
                    return true;
                }
                countdownTask.cancel();
                countdownTask = null;
                isRestarting = false;

                // 清除畫面上的標題
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("§a已取消重啟", "", 0, 0, 0);
                }

                sender.sendMessage("§a已停止重啟倒數！");
                return true;
            }

            if (isRestarting) {
                sender.sendMessage("§c已經在重啟倒數中了！");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c用法: /retart <秒> | /retart stop");
                return true;
            }

            int time;
            try {
                time = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c請輸入數字！");
                return true;
            }

            if (time <= 0 || time > 60) {
                sender.sendMessage("§c時間必須在 1~60 秒之間！");
                return true;
            }

            isRestarting = true;

            RestartCountdown task = new RestartCountdown(time);
            this.countdownTask = task;
            task.runTaskTimer(this, 0L, 20L);

            sender.sendMessage("§a已開始重啟倒數！");
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
                Bukkit.broadcastMessage("§c伺服器正在重啟...");
                cancel();
                return;
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle(
                        "§c⚠ 即將重啟 ⚠",
                        "§e" + countdown + " 秒後重啟",
                        0, 20, 0
                );
            }

            countdown--;
        }
    }
}