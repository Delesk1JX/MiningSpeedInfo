# Regenerates the banner that CurseForge and Modrinth show on the project page.
#
#   powershell -ExecutionPolicy Bypass -File tools\generate-banner.ps1
#
# 1280x400 is the size Modrinth asks for and CurseForge scales it down to whatever it needs.

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent $PSScriptRoot
$docs = Join-Path $root 'docs'
New-Item -ItemType Directory -Force -Path $docs | Out-Null

$width = 1280
$height = 400
$bitmap = New-Object System.Drawing.Bitmap $width, $height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$graphics.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::AntiAliasGridFit
$graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

# ---------------------------------------------------------------- background --

$background = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
    (New-Object System.Drawing.RectangleF 0, 0, $width, $height),
    [System.Drawing.Color]::FromArgb(255, 30, 74, 34),
    [System.Drawing.Color]::FromArgb(255, 8, 32, 14),
    [float]35)
$graphics.FillRectangle($background, 0, 0, $width, $height)
$background.Dispose()

# A few pickaxes in the background, very dark, only there to break up the empty space.
$ghost = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(16, 255, 255, 255))
foreach ($spot in @(@(300, 320, 220), @(700, 380, 170), @(1120, 350, 190))) {
    $graphics.FillEllipse($ghost, $spot[0], $spot[1], $spot[2], $spot[2])
}
$ghost.Dispose()

# -------------------------------------------------------------------- icon --

$iconPath = Join-Path $root 'common\src\main\resources\icon.png'
if (Test-Path $iconPath) {
    $icon = [System.Drawing.Image]::FromFile($iconPath)
    $graphics.DrawImage($icon, 64, 136, 128, 128)
    $icon.Dispose()
}

# -------------------------------------------------------------------- text --

function Get-Font([string]$name, [float]$size, [System.Drawing.FontStyle]$style) {
    return New-Object System.Drawing.Font $name, $size, $style, ([System.Drawing.GraphicsUnit]::Pixel)
}

$titleFont = Get-Font 'Segoe UI Semibold' 62 'Bold'
$taglineFont = Get-Font 'Segoe UI' 27 'Regular'
$factsFont = Get-Font 'Consolas' 21 'Regular'
$tooltipFont = Get-Font 'Consolas' 22 'Regular'
$white = [System.Drawing.Brushes]::White
$tagline = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(255, 206, 236, 206))
$facts = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(255, 150, 200, 152))
$grey = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(255, 160, 160, 160))
$speed = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(255, 0, 170, 0))

$graphics.DrawString('Mining Speed Info', $titleFont, $white, 232, 90)
$graphics.DrawString('The real mining speed of your tools', $taglineFont, $tagline, 236, 178)
$graphics.DrawString('1.20.1 Forge  |  1.21.1 NeoForge', $factsFont, $facts, 236, 230)
$graphics.DrawString('client side  |  no dependencies  |  31 languages', $factsFont, $facts, 236, 262)

# ------------------------------------------------------- tooltip mockup --

$box = New-Object System.Drawing.Rectangle 916, 70, 300, 260
$boxBrush = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(235, 16, 0, 16))
$graphics.FillRectangle($boxBrush, $box.X, $box.Y, $box.Width, $box.Height)
$boxBrush.Dispose()

# Minecraft draws a purple glow on the top and bottom edge of a tooltip.
foreach ($edge in @(@{ y = $box.Y; h = 4 }, @{ y = $box.Y + $box.Height - 4; h = 4 })) {
    $glow = New-Object System.Drawing.Drawing2D.LinearGradientBrush(
        (New-Object System.Drawing.RectangleF $box.X, $edge.y, $box.Width, $edge.h),
        [System.Drawing.Color]::FromArgb(110, 80, 0, 255),
        [System.Drawing.Color]::FromArgb(0, 80, 0, 255),
        [float]270)
    $graphics.FillRectangle($glow, $box.X, $edge.y, $box.Width, $edge.h)
    $glow.Dispose()
}
$sides = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(110, 40, 0, 127))
$graphics.FillRectangle($sides, $box.X, $box.Y, 4, $box.Height)
$graphics.FillRectangle($sides, $box.X + $box.Width - 4, $box.Y, 4, $box.Height)
$sides.Dispose()

$lines = @(
    @{ text = 'Diamond Pickaxe'; brush = $white },
    @{ text = 'When in Main Hand:'; brush = $grey },
    @{ text = '  7 Attack Damage'; brush = $white },
    @{ text = '  1.2 Attack Speed'; brush = $white },
    @{ text = '  8 Mining Speed'; brush = $speed }
)
$y = $box.Y + 16
$lastY = $y
foreach ($line in $lines) {
    $graphics.DrawString($line.text, $tooltipFont, $line.brush, $box.X + 26, $y)
    $lastY = $y
    $y += 40
}

# The little pickaxe the mod puts in front of the value, with the comparison arrow Quark also uses.
$tooltipIcon = [System.Drawing.Image]::FromFile((Join-Path $root 'common\src\main\resources\assets\miningspeedinfo\textures\gui\mining_speed.png'))
$graphics.DrawImage($tooltipIcon, $box.X + 12, $lastY + 5, 18, 18)
$tooltipIcon.Dispose()

$arrow = [System.Drawing.Image]::FromFile((Join-Path $root 'common\src\main\resources\assets\miningspeedinfo\textures\gui\upgrade.png'))
$graphics.DrawImage($arrow, $box.X + 2, $lastY - 3, 22, 22)
$arrow.Dispose()

# ---------------------------------------------------------------- cleanup --

$titleFont.Dispose()
$taglineFont.Dispose()
$factsFont.Dispose()
$tooltipFont.Dispose()
$tagline.Dispose()
$facts.Dispose()
$grey.Dispose()
$speed.Dispose()
$graphics.Dispose()

$path = Join-Path $docs 'banner.png'
$bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
$bitmap.Dispose()
Write-Host "wrote $path"
