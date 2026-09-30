# Texts for the mod pages

Everything the two sites ask for, ready to be copied in. Both sites use the same text, the only
difference is the length limits.

## Summary

**Modrinth** (up to 200 characters):

> Client side mod that adds the real mining speed of pickaxes, axes, shovels and hoes to their tooltip,
> with the Efficiency bonus, and inside Quark's attribute tooltip. 1.20.1 Forge and 1.21.1 NeoForge,
> no dependencies, 31 languages.

**CurseForge** (up to 255 characters):

> Adds the real mining speed of pickaxes, axes, shovels and hoes to their tooltip, including the bonus
> of the Efficiency enchantment, and as a row inside Quark's attribute tooltip with the same comparison
> arrows. 1.20.1 Forge and 1.21.1 NeoForge, client side, no dependencies, 31 languages.

## Categories

* Mods → Utility & QoL
* Mods → Cosmetic (optional, because of the tooltip style)
* Modrinth tags: `utility`, `forge`, `neoforge`, `client-side`, `tooltip`, `1.20.1`, `1.21.1`

## Game versions and loaders

| Minecraft | Loader | File |
| --- | --- | --- |
| 1.20.1 | Forge | `miningspeedinfo-1.0.0-forge.jar` |
| 1.21.1 | NeoForge | `miningspeedinfo-1.0.0-neoforge.jar |

Client side only, so the CurseForge "Client Side" and "Server Side" flags are *Client: yes*,
*Server: no*.

## License

MIT for both sites, the same `LICENSE` file that is in the repository. Modrinth asks for the license
identifier `MIT`, CurseForge for "MIT License".

## Description

The long description is in two ready to paste files:

* `curseforge-description.html` for CurseForge, the text has to be pasted into the HTML editor.
* `modrinth-description.md` for Modrinth, Markdown is pasted as it is.

The one thing that has to be replaced in both is the image link, which points at
`cdn.modrinth.com/data/PLACEHOLDER/PLACEHOLDER/icon.png`. After the project exists the link looks like
`cdn.modrinth.com/data/<project id>/<version id>/icon.png` and is shown in the file section of the
Modrinth page. `logo.png` in the repository root is the icon to upload, 128x128.
