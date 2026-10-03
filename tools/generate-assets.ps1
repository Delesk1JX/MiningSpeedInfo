# Regenerates every image the mod ships with.
#
#   powershell -ExecutionPolicy Bypass -File tools\generate-assets.ps1
#
# The small tooltip images are plain pixel art so they stay crisp at 9x9, the mod icon is drawn
# larger and scaled down by whoever uploads it to CurseForge or Modrinth.

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$gui = Join-Path $root 'common\src\main\resources\assets\miningspeedinfo\textures\gui'
$out = Join-Path $root 'common\src\main\resources'

New-Item -ItemType Directory -Force -Path $gui | Out-Null

# ---------------------------------------------------------------- pixel art --

# . transparent, S steel, H handle, A/B gem
$pickaxe = @(
    '..SSSSSS.',
    '.SS.HH.SS',
    '.S..HH..S',
    '....HH...',
    '....HH...',
    '....HH...',
    '....HH...',
    '....HH...',
    '.........'
)

# A small chevron in the corner of a 13x13 image, the same place Quark puts its own arrows, so that the
# icon of the row next to it stays readable. Quark draws it at (x - 2, y - 2).
$arrowUp = @(
    '.............',
    '..........#..',
    '.........###.',
    '.........#.#.',
    '.............',
    '.............',
    '.............',
    '.............',
    '.............',
    '.............',
    '.............',
    '.............',
    '.............'
)

$steel = [System.Drawing.Color]::FromArgb(255, 199, 199, 199)
$steelDark = [System.Drawing.Color]::FromArgb(255, 138, 138, 138)
$wood = [System.Drawing.Color]::FromArgb(255, 141, 94, 49)
$green = [System.Drawing.Color]::FromArgb(255, 85, 255, 85)
$greenDark = [System.Drawing.Color]::FromArgb(255, 55, 190, 55)
$red = [System.Drawing.Color]::FromArgb(255, 255, 85, 85)
$redDark = [System.Drawing.Color]::FromArgb(255, 190, 55, 55)

function New-PixelImage {
    param(
        [string[]]$Rows,
        [int]$Width,
        [int]$Height,
        [hashtable]$Colors
    )

    $bitmap = New-Object System.Drawing.Bitmap $Width, $Height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($y = 0; $y -lt $Height; $y++) {
        for ($x = 0; $x -lt $Width; $x++) {
            $key = [string]$Rows[$y][$x]
            if ($Colors.ContainsKey($key)) {
                $bitmap.SetPixel($x, $y, $Colors[$key])
            }
        }
    }
    return $bitmap
}

function Save-Png {
    param([System.Drawing.Bitmap]$Bitmap, [string]$Path)
    $Bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $Bitmap.Dispose()
    Write-Host "wrote $Path"
}

$pickaxeColors = @{ 'S' = $steel; 'H' = $wood }
Save-Png (New-PixelImage -Rows $pickaxe -Width 9 -Height 9 -Colors $pickaxeColors) (Join-Path $gui 'mining_speed.png')

# A gem, which is what the tier of a tool comes down to in the end.
$gem = @(
    '.........',
    '...AAB...',
    '..AABBB..',
    '.AABBBAA.',
    '..BBBBA..',
    '...BBA...',
    '....B....',
    '.........',
    '.........'
)
$gemColors = @{
    A = [System.Drawing.Color]::FromArgb(255, 173, 240, 240)
    B = [System.Drawing.Color]::FromArgb(255, 49, 199, 199)
}
Save-Png (New-PixelImage -Rows $gem -Width 9 -Height 9 -Colors $gemColors) (Join-Path $gui 'harvest_level.png')

Save-Png (New-PixelImage -Rows $arrowUp -Width 13 -Height 13 -Colors @{ '#' = $green }) (Join-Path $gui 'upgrade.png')

$arrowDown = [string[]]($arrowUp[($arrowUp.Length - 1)..0])
Save-Png (New-PixelImage -Rows $arrowDown -Width 13 -Height 13 -Colors @{ '#' = $red }) (Join-Path $gui 'downgrade.png')

# --------------------------------------------------------------- mod icon --

function New-Icon {
    param([int]$Size, [string]$Path)

    $bitmap = New-Object System.Drawing.Bitmap $Size, $Size, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.Clear([System.Drawing.Color]::Transparent)

    $unit = $Size / 128.0
    function P([double]$v) { return [float]($v * $unit) }

    # Rounded background with a soft vertical gradient.
    $corner = P 26
    $background = New-Object System.Drawing.Drawing2D.GraphicsPath
    $background.AddArc((P 0), (P 0), (P 52), (P 52), 180, 90)
    $background.AddArc((P 76), (P 0), (P 52), (P 52), 270, 90)
    $background.AddArc((P 76), (P 76), (P 52), (P 52), 0, 90)
    $background.AddArc((P 0), (P 76), (P 52), (P 52), 90, 90)
    $background.CloseFigure()

    $gradient = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.RectangleF 0, 0, (P 128), (P 128)),
        [System.Drawing.Color]::FromArgb(255, 56, 142, 60),
        [System.Drawing.Color]::FromArgb(255, 21, 87, 26),
        [float]90)
    $graphics.FillPath($gradient, $background)

    $border = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(255, 12, 58, 17)), (P 5)
    $graphics.DrawPath($border, $background)
    $gradient.Dispose()
    $border.Dispose()
    $background.Dispose()

    # Pickaxe head: a crescent that opens downwards.
    $head = New-Object System.Drawing.Drawing2D.GraphicsPath
    $head.AddArc((P 38), (P 16), (P 80), (P 80), 200, 140)
    $head.AddArc((P 52), (P 30), (P 52), (P 52), 340, -140)
    $head.CloseFigure()
    $steelBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(255, 214, 214, 214))
    $steelEdge = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(255, 40, 40, 40)), (P 4)
    $steelEdge.LineJoin = [System.Drawing.Drawing2D.LineJoin]::Round
    $graphics.FillPath($steelBrush, $head)
    $graphics.DrawPath($steelEdge, $head)
    $head.Dispose()

    # Handle.
    $handle = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(255, 150, 98, 48)), (P 12)
    $handle.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $handle.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $graphics.DrawLine($handle, (P 30), (P 106), (P 76), (P 34))
    $handle.Dispose()

    $graphics.Dispose()
    $steelBrush.Dispose()
    $steelEdge.Dispose()

    $bitmap.Save($Path, [System.Drawing.Imaging.ImageFormat]::Png)
    $bitmap.Dispose()
    Write-Host "wrote $Path"
}

# 128x128 is the size every other mod uses, so the icon does not stand out in the mod list.
New-Icon -Size 128 -Path (Join-Path $out 'icon.png')

# Shaped for CurseForge and Modrinth, which want a file of their own next to the sources.
Copy-Item (Join-Path $out 'icon.png') (Join-Path $root 'logo.png') -Force
Write-Host "wrote $root\logo.png"
