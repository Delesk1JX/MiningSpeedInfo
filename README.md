# Mining Speed Info


Adds the real mining speed of digging tools to their tooltip. Vanilla never shows this number, which
is why it is so often mixed up with attack speed.


Works on **Minecraft 1.20.1 (Forge)** and **Minecraft 1.21.1 (NeoForge)**, client side only, with no
required libraries and no server side install.


## What it does


* Adds a line to the tooltip of pickaxes, axes, shovels and hoes with their mining speed, for example
  `Mining Speed: 8` for a diamond pickaxe.
* Adds the harvest level on its own line, so it is visible right away which blocks a tool can break:
  `0` mines stone, `1` iron ore, `2` diamonds, `3` obsidian, `4` ancient debris.
* Counts the bonus of the Efficiency enchantment the same way vanilla does while a block is being
  broken, so an Efficiency V iron pickaxe reads `32` instead of `6`. The bonus can also be shown on its
  own as `32 (+26)`.
* Adds the value as its own row inside Quark's attribute tooltip, with the same up and down arrows
  Quark uses for its own values, so you can see at a glance whether the tool you are hovering is
  faster or slower than the one in your hand. Holding sneak shows the plain line instead, exactly like
  the rest of the tooltip.
* Swords, shears and every other item that is not a digging tool are left alone, unless you ask for
  them in the settings.


## Settings


Open the settings with the keybind in *Options → Controls* (unbound by default, "Open settings" under
**Mining Speed Info**). The screen is written with plain vanilla widgets, so it looks the same on both
game versions and needs no library at all.


| Setting | Default | What it does |
| --- | --- | --- |
| Enabled | ON | Master switch |
| Only while sneaking | OFF | Show the plain line only while holding sneak |
| Colour | White | Colour of the added line |
| Decimals | Auto | `Auto` keeps `8` as `8` and `6.5` as `6.5` |
| Count the Efficiency bonus | ON | Add the Efficiency bonus to the number |
| Show the bonus on its own | OFF | Write it as `32 (+26)` |
| Show the harvest level | ON | Also say which blocks the tool can break |
| Also swords and shears | OFF | Report those items as well, with a speed of `1` |
| Add it to Quark's tooltip | ON | Use Quark's attribute panel instead of a plain line |
| Compare with the held tool | ON | Show Quark's up and down arrows |


The settings are stored in `config/miningspeedinfo.json` and can also be edited by hand. Values that
make no sense are corrected when the file is read, so a typo can never break the mod.


Because the mod is declared client side, a dedicated server neither loads it nor needs it.


## Branches


Each branch carries one game version. The mod itself lives in the same `common/` folder on both, because
the only things that differ between the game versions are how the mining speed is read and which event
classes exist.


| Branch | Minecraft | Loader | Build folder |
| --- | --- | --- | --- |
| `main` | 1.20.1 | Forge 47.1 or newer | `forge-1.20.1/` |
| `1.21.1-neoforge` | 1.21.1 | NeoForge 21.1 or newer | `neoforge-1.21.1/` |


## Building


```
cd forge-1.20.1     && gradlew build     # build/libs/miningspeedinfo-1.1.0-forge.jar
cd neoforge-1.21.1  && gradlew build     # build/libs/miningspeedinfo-1.1.0-neoforge.jar
```


Java 17 is needed for the 1.20.1 build and Java 21 for the 1.21.1 one, both are set up by the Gradle
build itself.


`gradlew runClient` starts a development client, `gradlew runServer` a development server, which is
handy for checking that a dedicated server really does ignore the mod. `tools/generate-assets.ps1`
regenerates the icon and the three tooltip images.


## Credits


[Quark](https://www.curseforge.com/minecraft/mc-mods/quark) is the mod of [Vazkii](https://www.curseforge.com/members/vazkii/projects) and is not part of this project.


## License


MIT, see [LICENSE](LICENSE).
