"""Generate original FlyBird sprites and crisp bitmap fonts (requires Pillow)."""

from pathlib import Path
from math import pi, sin

from PIL import Image, ImageDraw, ImageFont


ASSETS = Path(__file__).resolve().parents[1] / "assets"


def background():
    width, height = 1600, 2848
    image = Image.new("RGB", (width, height))
    pixels = image.load()
    for y in range(height):
        t = y / height
        color = tuple(round(a + (b - a) * t) for a, b in zip((53, 157, 224), (206, 242, 229)))
        for x in range(width):
            pixels[x, y] = color
    draw = ImageDraw.Draw(image)
    for x, y, size in ((350, 450, 140), (1150, 720, 175), (490, 1220, 105)):
        draw.ellipse((x - size, y - size // 3, x + size, y + size // 3), fill="#e9f9f5")
        draw.ellipse((x - size // 2, y - size, x + size // 2, y + size // 3), fill="#e9f9f5")
    for base, amplitude, phase, color in (
        (2110, 130, 0, "#84cda0"),
        (2250, 145, pi / 2, "#67b990"),
        (2460, 110, pi, "#7fc484"),
    ):
        edge = [(x, round(base + amplitude * sin(2 * pi * x / width + phase)))
                for x in range(0, width, 8)]
        edge.append((width - 1, edge[0][1]))
        draw.polygon(edge + [(width - 1, height), (0, height)], fill=color)
    image.save(ASSETS / "sky_clean.png", optimize=True)


def bird():
    s = 2
    image = Image.new("RGBA", (600 * s, 500 * s))
    draw = ImageDraw.Draw(image)
    def ellipse(box, fill, outline=None, width=1):
        draw.ellipse(tuple(v * s for v in box), fill, outline, width * s)

    ellipse((80, 150, 490, 410), "#166a83")
    ellipse((100, 160, 480, 390), "#40b4d0")
    ellipse((55, 280, 250, 405), "#1488aa", "#166a83", 6)
    ellipse((120, 192, 350, 360), "#239bc2", "#166a83", 7)
    ellipse((156, 210, 290, 330), "#64d2db")
    ellipse((260, 300, 435, 390), "#fff2ba")
    ellipse((375, 187, 450, 275), "white", "#166a83", 5)
    ellipse((411, 207, 439, 256), "#173951")
    ellipse((420, 215, 429, 229), "white")
    draw.polygon([(445*s, 273*s), (540*s, 290*s), (445*s, 325*s)], fill="#ffb63a")
    draw.line([(445*s, 273*s), (540*s, 290*s), (445*s, 325*s)], fill="#bb6427", width=5*s)
    for x in (270, 330):
        draw.line([(x*s, 385*s), ((x - 8)*s, 421*s), ((x + 22)*s, 423*s)],
                  fill="#bb6427", width=11*s, joint="curve")
    image.resize((600, 500), Image.Resampling.LANCZOS).save(ASSETS / "bird_clean.png")


def ui():
    panel = Image.new("RGBA", (900, 1160))
    draw = ImageDraw.Draw(panel)
    draw.rounded_rectangle((18, 24, 882, 1140), radius=110, fill="#175573")
    draw.rounded_rectangle((27, 18, 873, 1117), radius=105, fill="#f5fff2")
    draw.rounded_rectangle((46, 37, 854, 1098), radius=90, outline="#8dcfbe", width=9)
    panel.save(ASSETS / "ui_panel.png")

    button = Image.new("RGBA", (720, 200))
    draw = ImageDraw.Draw(button)
    draw.rounded_rectangle((10, 17, 710, 196), radius=95, fill="#216d68")
    draw.rounded_rectangle((10, 3, 710, 178), radius=90, fill="#36b88d")
    draw.arc((22, 12, 697, 163), 190, 350, fill="#a1e5bb", width=9)
    button.save(ASSETS / "ui_button.png")


def font_atlas(name, size):
    font = ImageFont.truetype(str(ASSETS / "Fredoka.ttf"), size)
    font.set_variation_by_axes([650, 100])
    ascent, descent = font.getmetrics()
    atlas_size = 2048
    atlas = Image.new("RGBA", (atlas_size, atlas_size))
    draw = ImageDraw.Draw(atlas)
    glyphs = []
    x, y, row_height = 2, 2, 0
    for code in range(32, 127):
        char = chr(code)
        left, top, right, bottom = font.getbbox(char, anchor="ls")
        width, height = right - left, bottom - top
        advance = round(font.getlength(char))
        if x + width + 4 > atlas_size:
            x, y, row_height = 2, y + row_height + 4, 0
        if y + height + 4 > atlas_size:
            raise ValueError("Font atlas is too small")
        if width and height:
            draw.text((x - left, y - top), char, font=font, anchor="ls", fill="white")
        glyphs.append(
            f"char id={code} x={x} y={y} width={width} height={height} "
            f"xoffset={left} yoffset={ascent + top} xadvance={advance} page=0 chnl=0"
        )
        x += width + 4
        row_height = max(row_height, height)
    atlas.save(ASSETS / f"{name}.png", optimize=True)
    lines = [
        f'info face="Fredoka" size={size} bold=1 italic=0 charset="" unicode=1 stretchH=100 '
        "smooth=1 aa=1 padding=0,0,0,0 spacing=1,1",
        f"common lineHeight={ascent + descent + 8} base={ascent} "
        f"scaleW={atlas_size} scaleH={atlas_size} pages=1 packed=0",
        f'page id=0 file="{name}.png"',
        f"chars count={len(glyphs)}",
        *glyphs,
    ]
    (ASSETS / f"{name}.fnt").write_text("\n".join(lines) + "\n", encoding="utf-8")


if __name__ == "__main__":
    background()
    bird()
    ui()
    font_atlas("font_title", 136)
    font_atlas("font_label", 72)
