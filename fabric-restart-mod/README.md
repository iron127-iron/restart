# 🧩 RestartMod (Fabric)

一個簡單、實用、可控的伺服器重啟模組，專為 **Fabric 1.21.11** 設計。

## ✨ 功能特色

⏱ **倒數重啟**
- 使用 `/restart <秒>` 開始倒數
- 支援 1~120 秒

📢 **全服倒數提醒**
- 所有玩家會收到聊天訊息提醒

🔐 **權限控制**
- 預設需要 OP 權限（等級 2）
- 使用 Fabric 權限系統

🔁 **雙指令支援**
- `/restart <秒>` - 開始倒數
- `/restart stop` - 取消倒數
- `/restart status` - 查看狀態

⚡ **輕量無依賴**
- 不需要額外模組
- 即裝即用

---

## 🎮 指令

| 指令 | 說明 |
|------|------|
| `/restart <秒>` | 開始倒數重啟（1~120 秒） |
| `/restart stop` | 取消正在進行的倒數 |
| `/restart status` | 顯示目前倒數狀態 |

📌 範例：
```
/restart 10     → 倒數 10 秒後重啟
/restart stop   → 停止倒數，取消重啟
/restart status → 查看目前狀態
```

---

## 🔑 權限設定

預設需要 **OP 權限（等級 2）**，即使用 `/op 玩家名` 給予權限。

如需更細緻的權限控制，可配合 LuckPerms 等權限管理模組：
```
/lp group admin permission set restart.use true
```

---

## 📦 安裝方式

1. 將 `RestartMod-1.0.0.jar` 放入：
   ```
   mods/
   ```
2. 重啟伺服器

---

## ⚙️ 相容版本

✅ **Fabric 1.21.11** (Minecraft 1.21.11)
✅ **Fabric Loader 0.15.9+**
✅ **Fabric API 0.138.2+**
✅ **Java 21**

---

## 💡 使用場景

- 每日定時維護前提醒玩家
- 活動結束後安全重啟
- 避免突然斷線影響體驗
- 管理員快速控制伺服器狀態

---

## 📥 下載

- [RestartMod-1.0.0.jar](https://github.com/iron127-iron/restart/releases/download/v1.0.0/RestartMod-1.0.0.jar)

---

## 🔧 編譯需求

- Java 21
- Gradle 9.7.1
- Fabric Loom 1.18.4

---

## 📄 授權

MIT License