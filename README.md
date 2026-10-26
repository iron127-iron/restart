🧩 RestartPlugin（Paper 1.21.11）

一個簡單、實用、可控的伺服器重啟插件
支援倒數提示 + 大標題提醒 + 權限管理，適合任何 Minecraft 伺服器使用。


---

✨ 功能特色

⏱ 倒數重啟

使用 /retart <秒> 開始倒數

最多支援 60 秒


📢 全服大標題提示

所有玩家會看到：

⚠ 即將重啟 ⚠
X 秒後重啟


🔐 權限控制（支援 LuckPerms）

權限節點：restart.use

可自由分配給管理員 / 特定玩家


🔁 雙指令支援

/retart

/restart（自動別名）


⚡ 輕量無依賴

不需要額外插件

即裝即用




---

🎮 指令

/retart <秒>
/restart <秒>

📌 範例：

/retart 10


---

🔑 權限設定

使用 LuckPerms：

/lp group admin permission set restart.use true

或指定玩家：

/lp user 玩家名 permission set restart.use true


---

📦 安裝方式

1. 將 RestartPlugin.jar 放入：



plugins/

2. 重啟伺服器




---

⚙️ 相容版本

✅ Paper 1.21.11

⚠ 其他版本未測試（但通常可用）



---

💡 使用場景

每日定時維護前提醒玩家

活動結束後安全重啟

避免突然斷線影響體驗

管理員快速控制伺服器狀態
