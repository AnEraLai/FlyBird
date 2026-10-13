Add-Type -AssemblyName System.Drawing
$ErrorActionPreference = "Stop"
$assets = "g:\CodeSoftWare\JetBrains\IDEA\WorkCode\FlyBird\assets"
$TW = 1024

foreach ($name in @("lose","restart")) {
    $src = Join-Path $assets "$name.png"
    $img = [System.Drawing.Image]::FromFile($src)
    Write-Output "$name : $($img.Width)x$($img.Height) fmt=$($img.PixelFormat)"

    $bmp = New-Object System.Drawing.Bitmap($TW, $TW)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $g.DrawImage($img, 0, 0, $TW, $TW)
    $g.Dispose(); $img.Dispose()

    $w = $TW; $h = $TW
    $rect = New-Object System.Drawing.Rectangle(0, 0, $w, $h)
    $data = $bmp.LockBits($rect, [System.Drawing.Imaging.ImageLockMode]::ReadWrite, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $stride = $data.Stride; $size = $stride * $h
    $buf = New-Object byte[] $size
    [void][System.Runtime.InteropServices.Marshal]::Copy($data.Scan0, $buf, 0, $size)

    $visited = New-Object byte[] ($w * $h)
    $q = New-Object System.Collections.Generic.Queue[int]

    for ($x = 0; $x -lt $w; $x++) {
        foreach ($y in @(0, ($h - 1))) {
            $li = $y * $w + $x
            if ($visited[$li] -eq 0) { $bi = $y * $stride + $x * 4; if (($buf[$bi+2] -gt 225) -and ($buf[$bi+1] -gt 225) -and ($buf[$bi] -gt 225)) { $visited[$li] = 1; $buf[$bi+3] = 0; $q.Enqueue($li) } }
        }
    }
    for ($y = 0; $y -lt $h; $y++) {
        foreach ($x in @(0, ($w - 1))) {
            $li = $y * $w + $x
            if ($visited[$li] -eq 0) { $bi = $y * $stride + $x * 4; if (($buf[$bi+2] -gt 225) -and ($buf[$bi+1] -gt 225) -and ($buf[$bi] -gt 225)) { $visited[$li] = 1; $buf[$bi+3] = 0; $q.Enqueue($li) } }
        }
    }

    $cnt = 0
    while ($q.Count -gt 0) {
        $li = $q.Dequeue(); $cnt++
        $y = [int]($li / $w); $x = $li - $y * $w
        if ($x -gt 0) { $nl = $li - 1; if ($visited[$nl] -eq 0) { $bi = $y * $stride + ($x - 1) * 4; if (($buf[$bi+2] -gt 225) -and ($buf[$bi+1] -gt 225) -and ($buf[$bi] -gt 225)) { $visited[$nl] = 1; $buf[$bi+3] = 0; $q.Enqueue($nl) } } }
        if ($x -lt ($w - 1)) { $nl = $li + 1; if ($visited[$nl] -eq 0) { $bi = $y * $stride + ($x + 1) * 4; if (($buf[$bi+2] -gt 225) -and ($buf[$bi+1] -gt 225) -and ($buf[$bi] -gt 225)) { $visited[$nl] = 1; $buf[$bi+3] = 0; $q.Enqueue($nl) } } }
        if ($y -gt 0) { $nl = $li - $w; if ($visited[$nl] -eq 0) { $bi = ($y - 1) * $stride + $x * 4; if (($buf[$bi+2] -gt 225) -and ($buf[$bi+1] -gt 225) -and ($buf[$bi] -gt 225)) { $visited[$nl] = 1; $buf[$bi+3] = 0; $q.Enqueue($nl) } } }
        if ($y -lt ($h - 1)) { $nl = $li + $w; if ($visited[$nl] -eq 0) { $bi = ($y + 1) * $stride + $x * 4; if (($buf[$bi+2] -gt 225) -and ($buf[$bi+1] -gt 225) -and ($buf[$bi] -gt 225)) { $visited[$nl] = 1; $buf[$bi+3] = 0; $q.Enqueue($nl) } } }
    }

    [void][System.Runtime.InteropServices.Marshal]::Copy($buf, 0, $data.Scan0, $size)
    $bmp.UnlockBits($data)
    $bmp.Save($src, [System.Drawing.Imaging.ImageFormat]::Png)
    Write-Output "$name -> ${TW}x${TW}, removed white bg pixels: $cnt"
    $bmp.Dispose()
}
Write-Output "DONE"