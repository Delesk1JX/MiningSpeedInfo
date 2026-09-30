# Mining Speed Info

Adds the real mining speed of digging tools to their tooltip. Vanilla never shows this number, which is
why it is so often mixed up with attack speed.

![icon](https://cdn.modrinth.com/data/PLACEHOLDER/PLACEHOLDER/icon.png)

## What it does

- Adds a line to the tooltip of pickaxes, axes, shovels and hoes with their mining speed, for example
  `Mining Speed: 8` for a diamond pickaxe.
- Counts the bonus of the Efficiency enchantment the same way vanilla does while a block is being
  broken, so an Efficiency V iron pickaxe reads `32` instead of `6`. The bonus can also be shown on
  its own as `32 (+26)`.
- Adds the value as its own row inside Quark's attribute tooltip, with the same up and down arrows
  Quark uses for its own values, so you can see at a glance whether the tool you are hovering is
  faster or slower than the one in your hand. Holding sneak shows the plain line instead, exactly like
  the rest of the tooltip.
- Swords, shears and every other item that is not a digging tool are left alone, unless you ask for
  them in the settings.

## Settings

Open the settings with the keybind in *Options → Controls* (unbound by default, "Open settings" under
**Mining Speed Info**). The screen is written with plain vanilla widgets, so it looks the same on both
game versions and needs no library at all.

| Setting | Default | What it does |
| --- | --- | --- |
| Enabled | ON | Master switch |
| Only while sneaking | OFF | Show the plain line only while holding sneak |
| Colour | Dark Green | Colour of the added line |
| Decimals | Auto | `Auto` keeps `8` as `8` and `6.5` as `6.5` |
| Count the Efficiency bonus | ON | Add the Efficiency bonus to the number |
| Show the bonus on its own | OFF | Write it as `32 (+26)` |
| Also swords and shears | OFF | Report those items as well, with a speed of `1` |
| Add it to Quark's tooltip | ON | Use Quark's attribute panel instead of a plain line |
| Compare with the held tool | ON | Show Quark's up and down arrows |

The settings live in `config/miningspeedinfo.json` and can also be edited by hand. Values that make no
sense are corrected while the file is read, so a typo can never break the mod.

## Translations

Every line of text is a normal language file, and the mod ships with 31 languages, including Russian,
Ukrainian, German, French, Spanish, Italian, Dutch, Polish, Czech, Slovak, Portuguese, Turkish,
Swedish, Danish, Norwegian, Finnish, Hungarian, Romanian, Bulgarian, Greek, Croatian, Indonesian,
Vietnamese, Chinese (both), Japanese and Korean. Anything missing falls back to English.

The name of the stat is a translation key as well, so the word order of each language is correct
instead of being the English one pasted in.

## Requirements

- Minecraft 1.20.1 with Forge 47.1 or newer, or Minecraft 1.21.1 with NeoForge 21.1 or newer.
- Quark is optional. When it is installed its "Improved Tooltips" gets the extra row, and when the
  setting is switched off the mod falls back to a plain tooltip line.
- Nothing else. No library mod, no mixin framework.

Because the mod is declared client side, a dedicated server neither loads it nor needs it.

## Credits

Inspired by [Mining Speed Tooltips](https://modrinth.com/mod/mining-speed-tooltips) by
[Txni](https://modrinth.com/user/Txni). This is an independent, rewritten implementation without any of
the code of that mod.

Quark is the mod of [Violetmoon](https://quarkmc.dev) and is not part of this project.

## Building from source

Two independent Gradle builds share one source folder, because the only thing that differs between the
game versions is how the mining speed is read and which event classes exist.

```
cd forge-1.20.1     && gradlew build     # build/libs/miningspeedinfo-1.0.0-forge.jar
cd neoforge-1.21.1  && gradlew build     # build/libs/miningspeedinfo-1.0.0-neoforge.jar
```

## License

MIT.
