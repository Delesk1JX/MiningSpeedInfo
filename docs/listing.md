# Texts for the mod pages

Everything the two sites ask for, ready to be copied in.

| | CurseForge | Modrinth |
| --- | --- | --- |
| Project | <https://www.curseforge.com/minecraft/mc-mods/1719095> | <https://modrinth.com/mod/LDNii7gT> |
| Project id | `1719095` | `LDNii7gT` |

## Summary

**Modrinth**, the summary field takes 200 characters. This one is 199:

> Shows the real mining speed of your tools in their tooltip, with the Efficiency bonus and inside
> Quark's attribute panel. Client side, no dependencies, 31 languages, 1.20.1 Forge and 1.21.1
> NeoForge.

**CurseForge**, the summary field takes 255 characters. This one is 219:

> Adds the real mining speed of pickaxes, axes, shovels and hoes to their tooltip, with the Efficiency
> bonus, and as a row inside Quark's attribute panel with comparison arrows. Client side, no
> dependencies, 31 languages.

## Description

* `modrinth-description.md` for Modrinth, paste the text as it is.
* `curseforge-description.md` for CurseForge, paste it into the description field.

Only one thing has to be adjusted: the banner line at the top of the Modrinth text. Replace `VERSION`
with the version id of the uploaded file, or simply drop the image and upload `docs/banner.png` with the
image button of the description editor, the site then writes the link itself.

## Images

| File | Where |
| --- | --- |
| `logo.png` | The project icon on both sites, 128x128 |
| `docs/banner.png` | The header image, 1280x400 |

## Categories

* Mods &rarr; Utility & QoL
* Mods &rarr; Cosmetic, because of the tooltip style
* Modrinth tags: `utility`, `cosmetic`, `forge`, `neoforge`, `client-side`, `tooltip`, `translations`,
  `quark`, `1.20.1`, `1.21.1`

## Game versions and loaders

| Minecraft | Loader | File | Type |
| --- | --- | --- | --- |
| 1.20.1 | Forge 47.1 or newer | `miningspeedinfo-1.0.0-forge.jar` | Release |
| 1.21.1 | NeoForge 21.1 or newer | `miningspeedinfo-1.0.0-neoforge.jar` | Release |

Dependencies: nothing, neither library nor another mod. Optional: `Quark` from version 4.0, marked as
optional on both sites.

## License

MIT for both sites, the same `LICENSE` file that is in the repository. Modrinth asks for the license
identifier `MIT`, CurseForge for "MIT License".

## Client and server

The mod only changes tooltips, so it is marked as

* CurseForge: **Client Side: yes**, **Server Side: no**
* Modrinth: **Client side: yes**, **Server side: no**

A dedicated server does not load it at all, the mod declares the client side dependency in its metadata
and nothing of it runs there.

## Source code

| | Link |
| --- | --- |
| Repository | <https://github.com/Delesk1JX/MiningSpeedInfo> |
| 1.20.1 Forge | <https://github.com/Delesk1JX/MiningSpeedInfo/tree/main> |
| 1.21.1 NeoForge | <https://github.com/Delesk1JX/MiningSpeedInfo/tree/1.21.1-neoforge> |
| Issue tracker | <https://github.com/Delesk1JX/MiningSpeedInfo/issues> |
