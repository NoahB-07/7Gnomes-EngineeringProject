# usage: python3 tools/map/gen_building_map.py --pack <pack folder> [--tile 16|32|48]
import argparse
import os
import shutil
import sys
import xml.etree.ElementTree as ET
from PIL import Image, ImageDraw

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from stamps import STAMPS

ap = argparse.ArgumentParser()
ap.add_argument("--pack", required=True, help='folder "Modern tiles_Free" from the asset zip')
ap.add_argument("--tile", type=int, default=32, choices=(16, 32, 48))
ap.add_argument("--out", default=os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..")))
args = ap.parse_args()
T = args.tile
ROOT = args.out
MAP_DIR = os.path.join(ROOT, "assets", "maps")
TS_DIR = os.path.join(MAP_DIR, "tilesets")
DOC_DIR = os.path.join(ROOT, "docs", "map")

RB_NAME = f"Room_Builder_free_{T}x{T}.png"
IN_NAME = f"Interiors_free_{T}x{T}.png"
EX_NAME = f"building_extras_{T}x{T}.png"
rb_img = Image.open(os.path.join(args.pack, "Interiors_free", f"{T}x{T}", RB_NAME)).convert("RGBA")
in_img = Image.open(os.path.join(args.pack, "Interiors_free", f"{T}x{T}", IN_NAME)).convert("RGBA")
RB_COLS, RB_ROWS = rb_img.width // T, rb_img.height // T
IN_COLS, IN_ROWS = in_img.width // T, in_img.height // T

W, H = 50, 26

# name: x0, y0, x1, y1, floor, walls (top 2 rows are wall)
ROOMS = {
    "Lab":              (1,  1,  8,  9, "blue_tile", "grey"),
    "Seminar Room":     (10, 1, 16,  9, "carpet_b",  "yellow"),
    "Storage Room":     (18, 1, 22,  9, "brick",     "dark_wood"),
    "Lecture Theatre":  (24, 1, 35,  9, "herringbone", "red_wood"),
    "Office":           (37, 1, 41,  9, "carpet_a",  "light_wood"),
    "Study Space":      (43, 1, 48,  9, "carpet_c",  "salmon"),
    "Common Room":      (1, 11,  9, 24, "yellow_tile", "mint"),
    "Atrium":           (11, 11, 48, 24, "grey",     "beige"),
}

def hpass(x, y_wall, into):
    return ([(x, y_wall), (x + 1, y_wall), (x, y_wall + 1), (x + 1, y_wall + 1), (x, y_wall + 2), (x + 1, y_wall + 2)], into)

OPENINGS = [
    hpass(19, 10, "Storage Room"),
    hpass(38, 10, "Office"),
    ([(10, 17), (10, 18), (10, 19)], "Atrium"),
]

# key n opens door n, door 5 is exit
DOORS = {
    1: (13, 10, "Seminar Room"),
    2: (45, 10, "Study Space"),
    3: (4, 10, "Lab"),
    4: (29, 10, "Lecture Theatre"),
    5: (33, 0, "EXIT"),
}
EXIT_DOOR = 5

WALL_DECOR = [
    ("window_white", 16, 11), ("noticeboard", 21, 11), ("window_white", 24, 11), ("window_white", 33, 11),
    ("window_white", 41, 11), ("painting_a", 47, 11),
    ("poster_chart", 2, 1), ("window_wood", 6, 1),
    ("blackboard", 12, 1),
    ("blackboard", 26, 1), ("blackboard", 29, 1),
    ("painting_b", 37, 1), ("window_wood", 39, 1),
    ("window_white", 44, 1), ("painting_a", 47, 1),
    ("window_wood", 6, 11),
]

FURNITURE = [
    ("teacher_desk", 1, 4), ("teacher_desk", 6, 4), ("teacher_desk", 1, 7), ("teacher_desk", 6, 7),
    ("locker", 8, 3), ("globe", 8, 7),
    ("student_desks", 11, 5), ("student_desks", 11, 7), ("teacher_desk", 15, 3),
    ("wardrobe", 18, 3), ("wardrobe", 21, 3), ("chairs_pair", 21, 7),
    ("student_desks", 24, 5), ("student_desks", 31, 5), ("student_desks", 24, 7), ("student_desks", 31, 7),
    ("teacher_desk", 25, 3), ("globe", 24, 3),
    ("office_desk_a", 38, 4), ("floor_lamp", 41, 3), ("bookshelf_c", 40, 6),
    ("bookshelf_a", 43, 3), ("bookshelf_b", 47, 3), ("bench", 44, 7),
    ("drinks_fridges", 6, 13), ("table_brown", 2, 17),
    ("sofa_grey", 1, 21), ("sofa_light", 5, 21), ("plant_pot", 9, 24),
    ("rug_red", 27, 17), ("sofa_grey", 26, 21), ("sofa_light", 31, 21),
    ("table_brown", 12, 20), ("stool", 13, 23), ("stool", 16, 21), ("bench", 17, 15),
    ("table_light", 40, 16), ("stool", 39, 17), ("stool", 44, 17),
    ("rug_blue", 40, 21), ("palm", 47, 13), ("plant_tall", 11, 22), ("plant_pot", 36, 13),
    ("plant_pot", 20, 24), ("plant_pot", 48, 24), ("bench", 21, 22), ("bench", 36, 22),
]

# spots in tiles, .5 = tile centre
PLAYER_SPAWN = (30.0, 19.5)
GOOSE_SPAWNS = [(25, 15), (34, 15), (23, 19), (37, 18), (27, 24), (34, 24), (19, 13), (42, 14),
                (15, 18), (45, 20), (3, 15), (7, 19)]
KEYS = {1: (2.5, 23.5), 2: (12.5, 3.5), 3: (45.5, 4.5), 4: (4.5, 3.5), 5: (35.5, 4.5)}
ITEM_SPOTS = [(18.5, 19.5), (44.5, 23.5), (35.5, 15.5), (5.5, 23.5), (20.5, 4.5), (37.5, 7.5),
              (16.5, 8.5), (8.5, 9.5)]
EVENT_SPOTS = [(22, 17, 2, 2), (36, 19, 2, 2), (14, 16, 2, 2), (7, 18, 2, 2), (19, 6, 2, 2), (38, 7, 2, 2)]

WALL_ROWS = {"salmon": 5, "yellow": 7, "mint": 9, "light_wood": 11, "dark_wood": 13, "red_wood": 15,
             "grey": 17, "beige": 19}
FLOORS = {"brick": (11, 5), "yellow_tile": (11, 7), "blue_tile": (11, 9), "grey": (11, 11),
          "herringbone": (11, 13), "carpet_a": (14, 5), "carpet_b": (14, 7), "carpet_c": (14, 9),
          "carpet_d": (14, 11)}
NO_SHADOW = {"herringbone"}

FIRST_RB = 1
FIRST_IN = FIRST_RB + RB_COLS * RB_ROWS
EXTRA_NAMES = [f"ceil_{m}" for m in range(16)] + [f"plaque_{n}" for n in range(1, 6)] + ["exit_sign"]
EX_COLS = 8
FIRST_EX = FIRST_IN + IN_COLS * IN_ROWS

def rb(c, r): return FIRST_RB + r * RB_COLS + c
def it(c, r): return FIRST_IN + r * IN_COLS + c
def ex(name): return FIRST_EX + EXTRA_NAMES.index(name)

CEIL, FLOOR, FACE, PASS = "C", "F", "W", "P"
kind = [[CEIL] * W for _ in range(H)]
room_at = [[None] * W for _ in range(H)]
for name, (x0, y0, x1, y1, fl, wl) in ROOMS.items():
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            kind[y][x] = FACE if y < y0 + 2 else FLOOR
            room_at[y][x] = name

door_cells = {}
for n, (x, yw, into) in DOORS.items():
    cells = [(x + dx, yw + dy) for dy in range(3) for dx in range(2)]
    door_cells[n] = cells
    floor_room = "Lecture Theatre" if into == "EXIT" else into
    for (cx, cy) in cells:
        kind[cy][cx] = PASS
        room_at[cy][cx] = floor_room
for cells, into in OPENINGS:
    for (cx, cy) in cells:
        kind[cy][cx] = PASS
        room_at[cy][cx] = into

def k(x, y):
    return kind[y][x] if 0 <= x < W and 0 <= y < H else None

floor_l = [[0] * W for _ in range(H)]
furn_l = [[0] * W for _ in range(H)]
doors_l = [[0] * W for _ in range(H)]
above_l = [[0] * W for _ in range(H)]
RUG_L = [[0] * W for _ in range(H)]
solid = [[False] * W for _ in range(H)]

for y in range(H):
    for x in range(W):
        kk = kind[y][x]
        if kk == CEIL:
            m = 0
            if k(x, y - 1) not in (CEIL,): m |= 1
            if k(x + 1, y) not in (CEIL,): m |= 2
            if k(x, y + 1) not in (CEIL, FACE): m |= 4
            if k(x - 1, y) not in (CEIL,): m |= 8
            floor_l[y][x] = ex(f"ceil_{m}")
            solid[y][x] = True
        elif kk == FACE:
            room = room_at[y][x]
            row = WALL_ROWS[ROOMS[room][5]] + (y - ROOMS[room][1])
            left = k(x - 1, y) == FACE and room_at[y][x - 1] == room
            right = k(x + 1, y) == FACE and room_at[y][x + 1] == room
            col = 1 if (left and right) else (0 if right else (2 if left else 3))
            floor_l[y][x] = rb(col, row)
            solid[y][x] = True
        else:
            fc, fr = FLOORS[ROOMS[room_at[y][x]][4]]
            if ROOMS[room_at[y][x]][4] in NO_SHADOW or kk == PASS:
                floor_l[y][x] = rb(fc + 1, fr + 1)
            else:
                top = k(x, y - 1) in (FACE, CEIL)
                lft = k(x - 1, y) in (FACE, CEIL)
                if top and lft: floor_l[y][x] = rb(fc, fr)
                elif top:       floor_l[y][x] = rb(fc + 1, fr)
                elif lft:       floor_l[y][x] = rb(fc, fr + 1)
                else:           floor_l[y][x] = rb(fc + 1, fr + 1)

def stamp(name, x, y, wall=False):
    c, r, w, h, tall, is_solid = STAMPS[name]
    for dy in range(h):
        for dx in range(w):
            g = it(c + dx, r + dy)
            tx, ty = x + dx, y + dy
            if not (0 <= tx < W and 0 <= ty < H):
                raise SystemExit(f"{name} at {x},{y} goes off the map")
            if wall:
                if kind[ty][tx] != FACE:
                    raise SystemExit(f"wall decor {name} at {x},{y} is not on a wall face")
                furn_l[ty][tx] = g
            elif dy < tall:
                above_l[ty][tx] = g
            else:
                if kind[ty][tx] != FLOOR:
                    raise SystemExit(f"{name} at {x},{y} overlaps a wall/doorway at {tx},{ty}")
                if is_solid:
                    furn_l[ty][tx] = g
                    solid[ty][tx] = True
                else:
                    RUG_L[ty][tx] = g

for name, x, y in FURNITURE:
    stamp(name, x, y)
for name, x, y in WALL_DECOR:
    stamp(name, x, y, wall=True)

dc, dr = STAMPS["door_double"][:2]
gc, gr = STAMPS["door_glass"][:2]
for n, (x, yw, into) in DOORS.items():
    src_c, src_r = (gc, gr) if into == "EXIT" else (dc, dr)
    for dy in range(2):
        for dx in range(2):
            doors_l[yw + 1 + dy][x + dx] = it(src_c + dx, src_r + dy)
    if into == "EXIT":
        px, py = x - 1, yw + 1
        if k(px, py) != FACE:
            raise SystemExit(f"door {n}: no wall face at {px},{py} for exit sign")
        furn_l[py][px] = ex("exit_sign")

WHITE = (248, 248, 248, 255)
INK = (58, 58, 80, 255)

def ceil_tile(m):
    im = Image.new("RGBA", (32, 32), WHITE)
    d = ImageDraw.Draw(im)
    b = 3
    if m & 1: d.rectangle([0, 0, 31, b - 1], fill=INK)
    if m & 2: d.rectangle([32 - b, 0, 31, 31], fill=INK)
    if m & 4: d.rectangle([0, 32 - b, 31, 31], fill=INK)
    if m & 8: d.rectangle([0, 0, b - 1, 31], fill=INK)
    return im

def plaque(text, bg, fg):
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    tw = d.textlength(text)
    w = max(14, int(tw) + 8)
    x0 = (32 - w) // 2
    d.rectangle([x0, 9, x0 + w - 1, 24], fill=INK)
    d.rectangle([x0 + 1, 10, x0 + w - 2, 23], fill=bg)
    d.text(((32 - tw) / 2, 11), text, fill=fg)
    return im

extras = [ceil_tile(m) for m in range(16)]
extras += [plaque(str(n), (236, 196, 72, 255), (40, 30, 20, 255)) for n in range(1, 6)]
extras += [plaque("EXIT", (40, 160, 70, 255), (255, 255, 255, 255))]
EX_ROWS = (len(extras) + EX_COLS - 1) // EX_COLS
ex_img = Image.new("RGBA", (EX_COLS * 32, EX_ROWS * 32), (0, 0, 0, 0))
for i, im in enumerate(extras):
    ex_img.paste(im, ((i % EX_COLS) * 32, (i // EX_COLS) * 32))
if T != 32:
    ex_img = ex_img.resize((EX_COLS * T, EX_ROWS * T), Image.NEAREST)

def merge(grid):
    used = [[False] * W for _ in range(H)]
    out = []
    for y in range(H):
        for x in range(W):
            if grid[y][x] and not used[y][x]:
                w = 1
                while x + w < W and grid[y][x + w] and not used[y][x + w]:
                    w += 1
                h = 1
                while y + h < H and all(grid[y + h][xx] and not used[y + h][xx] for xx in range(x, x + w)):
                    h += 1
                for yy in range(y, y + h):
                    for xx in range(x, x + w):
                        used[yy][xx] = True
                out.append((x, y, w, h))
    return out

collision = merge(solid)

mp = ET.Element("map", {"version": "1.10", "tiledversion": "1.10.2", "orientation": "orthogonal",
                        "renderorder": "right-down", "width": str(W), "height": str(H),
                        "tilewidth": str(T), "tileheight": str(T), "infinite": "0"})
props = ET.SubElement(mp, "properties")
for kk_, vv in [("name", "Goose Patrol - building")]:
    ET.SubElement(props, "property", {"name": kk_, "value": vv})

def tileset(first, name, src, cols, rows):
    ts = ET.SubElement(mp, "tileset", {"firstgid": str(first), "name": name, "tilewidth": str(T),
                                       "tileheight": str(T), "tilecount": str(cols * rows), "columns": str(cols)})
    ET.SubElement(ts, "image", {"source": "tilesets/" + src, "width": str(cols * T), "height": str(rows * T)})

tileset(FIRST_RB, "room_builder", RB_NAME, RB_COLS, RB_ROWS)
tileset(FIRST_IN, "interiors", IN_NAME, IN_COLS, IN_ROWS)
tileset(FIRST_EX, "building_extras", EX_NAME, EX_COLS, EX_ROWS)

lid = [1]
oid = [1]

def tile_layer(name, grid):
    le = ET.SubElement(mp, "layer", {"id": str(lid[0]), "name": name, "width": str(W), "height": str(H)})
    lid[0] += 1
    d = ET.SubElement(le, "data", {"encoding": "csv"})
    d.text = "\n" + ",\n".join(",".join(str(v) for v in row) for row in grid) + "\n"

def group(name, visible=True, color=None):
    a = {"id": str(lid[0]), "name": name}
    if not visible: a["visible"] = "0"
    if color: a["color"] = color
    lid[0] += 1
    return ET.SubElement(mp, "objectgroup", a)

def obj(g, name, otype, x, y, w, h, p=None):
    o = ET.SubElement(g, "object", {"id": str(oid[0]), "name": name, "type": otype,
                                    "x": f"{x * T:g}", "y": f"{y * T:g}", "width": f"{w * T:g}", "height": f"{h * T:g}"})
    oid[0] += 1
    if p:
        pe = ET.SubElement(o, "properties")
        for kk_, vv in p.items():
            t = "int" if isinstance(vv, int) and not isinstance(vv, bool) else ("bool" if isinstance(vv, bool) else "string")
            ET.SubElement(pe, "property", {"name": kk_, "type": t, "value": str(vv).lower() if t == "bool" else str(vv)})

def point(g, name, otype, px, py, p=None, size=0.75):
    obj(g, name, otype, px - size / 2, py - size / 2, size, size, p)

tile_layer("floor", floor_l)
tile_layer("rugs", RUG_L)
tile_layer("furniture", furn_l)
tile_layer("doors", doors_l)
tile_layer("above", above_l)

g = group("collision", visible=False, color="#ff0000")
for (x, y, w, h) in collision:
    obj(g, "", "solid", x, y, w, h)

g = group("doors", color="#ff8800")
for n, (x, yw, into) in DOORS.items():
    obj(g, f"door_{n}", "locked_door", x, yw, 2, 3,
        {"key": n, "leadsTo": into, "isExit": n == EXIT_DOOR})

g = group("exit", color="#00ff66")
ex_x, ex_y, _ = DOORS[EXIT_DOOR]
obj(g, "exit", "exit", ex_x, ex_y, 2, 1, {"requiresDoor": EXIT_DOOR})

g = group("spawns", color="#00aaff")
point(g, "player", "player_spawn", *PLAYER_SPAWN)
for i, (x, y) in enumerate(GOOSE_SPAWNS):
    point(g, f"goose_{i + 1}", "goose_spawn", x + 0.5, y + 0.5)

g = group("keys", color="#ffdd00")
for n, (x, y) in KEYS.items():
    point(g, f"key_{n}", "key_spot", x, y, {"key": n, "opensDoor": f"door_{n}"})

g = group("items", color="#ffaa00")
for i, (x, y) in enumerate(ITEM_SPOTS):
    r = room_at[int(y)][int(x)]
    point(g, f"item_spot_{i + 1}", "item_spot", x, y, {"room": r})

g = group("events", visible=False, color="#ff00ff")
for i, (x, y, w, h) in enumerate(EVENT_SPOTS):
    obj(g, f"event_spot_{i + 1}", "event_spot", x, y, w, h, {"room": room_at[y][x], "oneShot": True})

g = group("rooms", visible=False, color="#55ff55")
for name, (x0, y0, x1, y1, fl, wl) in ROOMS.items():
    obj(g, name, "room", x0, y0, x1 - x0 + 1, y1 - y0 + 1,
        {"redHerring": name in ("Storage Room", "Office")})

mp.set("nextlayerid", str(lid[0]))
mp.set("nextobjectid", str(oid[0]))

os.makedirs(TS_DIR, exist_ok=True)
os.makedirs(DOC_DIR, exist_ok=True)
shutil.copyfile(os.path.join(args.pack, "Interiors_free", f"{T}x{T}", RB_NAME), os.path.join(TS_DIR, RB_NAME))
shutil.copyfile(os.path.join(args.pack, "Interiors_free", f"{T}x{T}", IN_NAME), os.path.join(TS_DIR, IN_NAME))
ex_img.save(os.path.join(TS_DIR, EX_NAME))
ET.indent(mp, space="  ")
ET.ElementTree(mp).write(os.path.join(MAP_DIR, "building.tmx"), encoding="UTF-8", xml_declaration=True)

sheets = [(FIRST_RB, rb_img, RB_COLS), (FIRST_IN, in_img, IN_COLS), (FIRST_EX, ex_img, EX_COLS)]

def tile_img(gid):
    for first, im, cols in reversed(sheets):
        if gid >= first:
            i = gid - first
            return im.crop(((i % cols) * T, (i // cols) * T, (i % cols + 1) * T, (i // cols + 1) * T))

img = Image.new("RGBA", (W * T, H * T), (30, 30, 40, 255))
for layer in (floor_l, RUG_L, furn_l, doors_l, above_l):
    for y in range(H):
        for x in range(W):
            if layer[y][x]:
                img.alpha_composite(tile_img(layer[y][x]), (x * T, y * T))
img.convert("RGB").save(os.path.join(DOC_DIR, "building_preview.png"))

ov = img.convert("RGB")
d = ImageDraw.Draw(ov, "RGBA")
for (x, y, w, h) in collision:
    d.rectangle([x * T, y * T, (x + w) * T - 1, (y + h) * T - 1], fill=(255, 0, 0, 70))
for n, (x, yw, into) in DOORS.items():
    d.rectangle([x * T, yw * T, (x + 2) * T - 1, (yw + 3) * T - 1], outline=(255, 140, 0, 255), width=3)
    d.text((x * T + 3, (yw + 3) * T + 2), f"DOOR {n}", fill=(255, 160, 0, 255))
for name, (x0, y0, x1, y1, fl, wl) in ROOMS.items():
    d.text((x0 * T + 4, (y0 + 2) * T + 3), name, fill=(0, 0, 0, 255))
for (x, y, w, h) in EVENT_SPOTS:
    d.rectangle([x * T, y * T, (x + w) * T, (y + h) * T], fill=(255, 0, 255, 70), outline=(255, 0, 255, 255), width=2)
for (x, y) in GOOSE_SPAWNS:
    cx, cy = (x + .5) * T, (y + .5) * T
    d.ellipse([cx - 8, cy - 8, cx + 8, cy + 8], fill=(255, 255, 255, 255), outline=(0, 0, 0, 255), width=2)
    d.text((cx - 3, cy - 6), "G", fill=(0, 0, 0, 255))
for (x, y) in ITEM_SPOTS:
    cx, cy = x * T, y * T
    d.rectangle([cx - 7, cy - 7, cx + 7, cy + 7], fill=(255, 170, 0, 255), outline=(0, 0, 0, 255), width=2)
for n, (x, y) in KEYS.items():
    cx, cy = x * T, y * T
    d.ellipse([cx - 10, cy - 10, cx + 10, cy + 10], fill=(255, 221, 0, 255), outline=(0, 0, 0, 255), width=2)
    d.text((cx - 3, cy - 6), str(n), fill=(0, 0, 0, 255))
cx, cy = PLAYER_SPAWN[0] * T, PLAYER_SPAWN[1] * T
d.polygon([(cx, cy - 12), (cx + 11, cy + 9), (cx - 11, cy + 9)], fill=(0, 140, 255, 255), outline=(255, 255, 255, 255))
ov.save(os.path.join(DOC_DIR, "building_overlay.png"))
print(f"{W}x{H} tiles @ {T}px, collision rects: {len(collision)}, objects: {oid[0] - 1}")
