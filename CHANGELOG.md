# Changelog

All notable changes to this project are documented here. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and the project uses
[semantic versioning](https://semver.org/).

## [1.1.0] - 2026-10-01

### Added

- Harvest level, so it is visible at a glance which blocks a tool can break: `0` mines stone, `1` iron
  ore, `2` diamonds, `3` obsidian, `4` ancient debris. It is drawn as its own value and can be switched
  off in the settings.

### Changed

- Inside Quark's attribute tooltip the values are now drawn like the ones Quark shows itself: an icon
  and the number, on the same row behind attack damage and attack speed, instead of a row of its own
  with the name of the stat in front of it. Holding sneak shows the plain lines with the full names
  instead, `Mining Speed: 8` and `Harvest Level: 3`, the same way the rest of the tooltip works.
- The numbers are white while there is nothing to compare, green while the hovered tool is the better
  one and red while it is the worse one, the same three colours Quark uses for its own values, both
  in its attribute panel and on the plain tooltip lines.
- The down arrow was mirrored the wrong way and ended up below the value instead of above it. Both
  arrows are now the images Quark uses and sit in the same spot, at the top right of the icon.
- The plain tooltip lines now sit in front of the item id and the NBT count that the game adds with
  the advanced tooltips (F3+H), so they stay next to the other attributes.

### Fixed

- The values never reached Quark's panel before. They are now added by handing the loader a wrapper for
  Quark's attribute panel, which needs nothing but the public loader API: no mixin, no library, and
  nothing that has to be kept in step with Quark's own code. When Quark is missing or changes the
  panel, the mod falls back to the plain tooltip lines and says so in the log.
- The two icons in Quark's panel were drawn on top of each other, because the position was measured
  from a width that already included them.
- NeoForge stopped the mod from loading because a class it handed to Mixin lived in the package the
  mixin config owns.

## [1.0.1] - 2026-09-30

### Fixed

- The row that is added to Quark's attribute tooltip is a real tooltip component, and the loaders
  refuse to draw a component that has no factory. Registering it fixes the crash that happened as soon
  as a tool was hovered while Quark was installed.
- The plain tooltip line now starts with a space, so it lines up with the values of Quark's panel
  instead of sitting against the edge of the tooltip.

## [1.0.0] - 2026-09-30

First release.

### Added

- Mining speed line in the tooltip of pickaxes, axes, shovels and hoes, including the bonus of the
  Efficiency enchantment.
- Own row inside Quark's attribute tooltip, with the comparison arrows Quark uses for its own values.
- Settings screen with its own keybind, opened from *Options → Controls*. The options are: enabled,
  only while sneaking, colour, decimals, count the Efficiency bonus, show the bonus on its own, also
  swords and shears, add to Quark's tooltip and compare with the held tool.
- `config/miningspeedinfo.json`, which can also be edited by hand and repairs values it cannot use.
- 31 language files, every piece of text in the mod is a translation key.
- 128x128 mod icon and the three tooltip images as ordinary resource pack textures.
- Minecraft 1.20.1 with Forge and Minecraft 1.21.1 with NeoForge, both client side only.
