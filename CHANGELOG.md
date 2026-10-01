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

- Inside Quark's attribute tooltip the value is now drawn like the ones Quark shows itself: an icon and
  the number, on the same row behind attack damage and attack speed, instead of a row of its own with
  the name of the stat in front of it.
- The number is white by default, like the values Quark draws, and turns green only while the hovered
  tool really is the faster one. The arrows Quark uses are unchanged.
- The plain tooltip lines now sit in front of the item id and the NBT count that the game adds with
  the advanced tooltips (F3+H), so they stay next to the other attributes.

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
