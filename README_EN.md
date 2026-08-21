LoSS Menu
=========

LoSS Menu is an auxiliary mod designed for the in-development Minecraft 1.20.1 Forge story-driven modpack "The Lay of Stone and Soul". It replaces the vanilla "Create World" screen, adds two story world modes based on world templates, and controls the unlock order through a global unlock file.

Features
--------

- Replaces the "Create New World" entry in singleplayer, providing a separate world mode selection screen.
- Adds two story world modes:
  - Dislocated Epoch: copies a new world from a fixed template save, used to experience the story from the beginning.
  - Vicissitudes: creates a new world from a fixed template, used for content after the story has progressed to a certain point.
- Unlocks the vanilla world creation screen after the story is fully completed.
- Writes unlock states via the /worldmode command, taking effect without restarting the game.
- Supports multiple languages: Simplified Chinese, English, Spanish, Classical Chinese.

Installation
--------

1. Make sure Minecraft 1.20.1 and Forge 47.x are installed.
2. Place the mod's .jar file into the mods folder of the game directory.
3. Launch the game.

Note: This mod depends on template saves provided by the modpack. Ensure the templates are correctly placed (see "Template Preparation" below).

Commands and Unlocking
--------

The mod provides the /worldmode command to modify unlock states.

- Executable only by OPs (permission level 2) or the server console.

Syntax:
/worldmode <mode name> <true/false>

Examples:
/worldmode times_change true   # Unlock "Vicissitudes"
/worldmode vanilla true        # Unlock "Vanilla World"
/worldmode times_change false  # Relock "Vicissitudes"

Supported mode names:
- times_change (corresponds to "Vicissitudes")
- vanilla (corresponds to "Vanilla World")

Configuration File
--------

Unlock states are saved in the game directory under:

config/LoSS Main/progression.json

Default contents:

{
  "times_changeUnlocked": false,
  "vanillaUnlocked": false
}

This file is automatically updated when the /worldmode command or FTB quest commands are executed. Players do not need to edit it manually.

Template Preparation
--------

Although this mod serves my own modpack, thanks to its automated design, other modpack authors can also use it to easily implement world template creation and stage-based unlocking of template worlds or the vanilla world.

Both story modes create new worlds by copying preset world templates. Template saves should be placed in:

config/LoSS Main/templates/cuowei/   # Dislocated Epoch template
config/LoSS Main/templates/canghai/  # Vicissitudes template

Template requirements:
- Must be a complete Minecraft save directory.
- Do not include playerdata, stats, advancements, session.lock, or other player-related files.
- It is recommended to manually clean the Player entity and time fields in level.dat after template creation.

Please note that although the mod automatically skips these player information files during copying, keeping the template clean is safer.

Localization
--------

The mod includes the following languages:

- 简体中文 (zh_cn)
- English (en_us)
- Español (es_es)
- 文言（華夏） (lzh)

To add other languages, create corresponding language files under assets/loss_menu/lang/.

Building
--------

If you need to build from source:

git clone https://github.com/StudyingAlpha/LoSS-Menu.git

cd LoSS-Menu

./gradlew build

The build artifact is located in build/libs/.

Feedback and Support
--------

If you have any questions or suggestions, feel free to open an issue in the GitHub repository.

License
--------

This project is licensed under the GPL-3.0 License.

PS
--------

Given my limited English skills, this document was translated with AI assistance. Although I've checked the main parts, there may still be mistakes. If you find any inconsistencies, please refer to the Chinese version.

