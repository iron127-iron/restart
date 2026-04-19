package me.restart.plugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RestartPlugin extends JavaPlugin {

    private boolean isRestarting = false;

    @Override
    public void onEnable() {
        getLogger().info("RestartPlugin 已啟用！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("retart") || command.getName().equalsIgnoreCase("restart")) {

            // Permission check
            if (!sender.hasPermission("restart.use")) {
                sender.sendMessage("§c你沒有權限使用這個指令！");
                return true;
            }

            if (isRestarting) {
                sender.sendMessage("§c已經在重啟倒數中了！");
                return true;
            }

            if (args.length != 1) {
                sender.sendMessage("§c用法: /retart <秒>");
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

            new BukkitRunnable() {
                int countdown = time;

                @Override
                public void run() {

                    if (countdown <= 0) {
                        Bukkit.broadcastMessage("§c伺服器正在重啟...");
                        Bukkit.shutdown();
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
            }.runTaskTimer(this, 0L, 20L);

            sender.sendMessage("§a已開始重啟倒數！");
            return true;
        }

        return false;
    }
}
