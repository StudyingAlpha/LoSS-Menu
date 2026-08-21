LoSS Menu
=========

LoSS Menu 是一个为正在开发的 Minecraft 1.20.1 Forge 剧情向整合包“石与魂的长歌”设计的辅助模组。它替换了原版的“创建世界”界面，增加了两种基于世界模板的剧情世界模式，并通过全局解锁文件控制模式的开放顺序。

For English version,please go to README_EN.md

功能特性
--------

- 替换单人游戏中的“创建新世界”入口，提供独立的世界模式选择界面。
- 新增两种剧情世界模式：
  - 错位的纪元：从固定模板存档复制新世界，用于从头体验剧情。
  - 沧海桑田：使用固定模板创建新世界，用于剧情发展到一定程度后的内容。
- 当剧情全部完成后，解锁原版世界创建界面。
- 通过 /worldmode 命令写入解锁状态，无需重启游戏即可生效。
- 支持多语言：简体中文、英文、西班牙语、文言（华夏）。


安装方法
--------

1. 确保已安装 Minecraft 1.20.1 和 Forge 47.x。
2. 将本模组的 .jar 文件放入游戏目录的 mods 文件夹。
3. 启动游戏。

注意：本模组依赖整合包提供的模板存档，请确保模板已正确放置（见下文“模板准备”）。

命令与解锁
----------

模组提供 /worldmode 命令用于修改解锁状态。

- 仅 OP（权限等级 2）或服务器控制台可执行。

语法：

/worldmode <模式名> <true/false>

示例：

/worldmode times_change true   # 解锁“沧海桑田”

/worldmode vanilla true        # 解锁“原版世界”

/worldmode times_change false  # 锁定“沧海桑田”

模式名仅支持：

- times_change（对应“沧海桑田”）
- vanilla（对应“原版世界”）

配置文件
--------

解锁状态保存在游戏目录下的：

config/LoSS Main/progression.json

默认内容：

{

  "times_changeUnlocked": false,
  
  "vanillaUnlocked": false
  
}

该文件会在执行 /worldmode 命令时自动更新。玩家无需手动编辑。


模板准备
--------------------------

虽然本模组服务于我自己的整合包，但得益于其自动化的设计，其他整合包作者也可以借助本模组轻松实现世界模板创建和分阶段解锁模板世界或者原版世界。

两种剧情模式均通过复制预设世界模板来创建新世界。模板存档放置于：

config/LoSS Main/templates/cuowei/   # 错位的纪元模板

config/LoSS Main/templates/canghai/  # 沧海桑田模板

模板要求：
- 必须是完整的 Minecraft 存档目录。
- 不要包含 playerdata、stats、advancements、session.lock 等玩家相关文件。
- 建议在模板制作完成后手动清理 level.dat 中的 Player 实体和时间字段。

请注意，虽然模组会在复制时自动跳过这些玩家信息文件，但保持模板干净更安全。

本地化
------

模组内置以下语言：

- 简体中文 (zh_cn)
- English (en_us)
- Español (es_es)
- 文言（華夏）(lzh)

如需添加其他语言，可在 assets/loss_menu/lang/ 下新建对应的语言文件。

构建
----

如果你需要从源码构建：

git clone https://github.com/StudyingAlpha/LoSS-Menu.git

cd LoSS-Menu

./gradlew build

构建产物位于 build/libs/。


反馈与支持
----------

如有问题或建议，欢迎在 GitHub 仓库提交 Issue。


许可证
------

本项目采用 GPL 3.0 License。
