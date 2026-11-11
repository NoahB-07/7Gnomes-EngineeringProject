# checks keys 1-5 work in order and exit comes last
import os, sys, xml.etree.ElementTree as ET
from collections import deque
from PIL import Image

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
tmx = os.path.join(ROOT, "assets", "maps", "building.tmx")
m = ET.parse(tmx).getroot()
W, H, T = int(m.get("width")), int(m.get("height")), int(m.get("tilewidth"))
err = []
max_gid = 0
for ts in m.findall("tileset"):
    img = ts.find("image")
    path = os.path.join(os.path.dirname(tmx), img.get("source"))
    if not os.path.exists(path):
        err.append(f"missing tileset image {path}"); continue
    if Image.open(path).size != (int(img.get("width")), int(img.get("height"))):
        err.append(f"size mismatch {path}")
    max_gid = max(max_gid, int(ts.get("firstgid")) + int(ts.get("tilecount")) - 1)
for layer in m.findall("layer"):
    v = [int(x) for x in layer.find("data").text.replace("\n", "").split(",")]
    if len(v) != W * H or any(g < 0 or g > max_gid for g in v):
        err.append(f"layer {layer.get('name')} bad data")

groups = {g.get("name"): list(g) for g in m.findall("objectgroup")}
def cells(o):
    x, y, w, h = (float(o.get(k)) / T for k in ("x", "y", "width", "height"))
    return {(cx, cy) for cy in range(int(y), int(y + h - 1e-6) + 1) for cx in range(int(x), int(x + w - 1e-6) + 1)}
def prop(o, name):
    for p in o.iter("property"):
        if p.get("name") == name: return p.get("value")
solid = set()
for o in groups["collision"]: solid |= cells(o)
doors = {int(prop(o, "key")): cells(o) for o in groups["doors"]}
keys = {int(prop(o, "key")): cells(o) for o in groups["keys"]}
exit_cells = set().union(*[cells(o) for o in groups["exit"]])
spawn = next(iter(cells(next(o for o in groups["spawns"] if o.get("type") == "player_spawn"))))

def reach(open_doors):
    blocked = set(solid)
    for n, c in doors.items():
        if n not in open_doors: blocked |= c
    seen = {spawn}; q = deque([spawn])
    while q:
        x, y = q.popleft()
        for nx, ny in ((x+1,y),(x-1,y),(x,y+1),(x,y-1)):
            if 0 <= nx < W and 0 <= ny < H and (nx, ny) not in blocked and (nx, ny) not in seen:
                seen.add((nx, ny)); q.append((nx, ny))
    return seen

opened = set()
for step in range(1, 6):
    r = reach(opened)
    got = sorted(n for n, c in keys.items() if c & r)
    expect = list(range(1, step + 1))
    if got != expect:
        err.append(f"step {step}: reachable keys {got}, expected {expect}")
    if exit_cells & r:
        err.append(f"step {step}: exit reachable too early")
    adj = {(x+dx, y+dy) for (x, y) in doors[step] for dx, dy in ((1,0),(-1,0),(0,1),(0,-1))}
    if not (adj & r):
        err.append(f"door {step} can't be reached with key {step}")
    print(f"  step {step}: keys reachable {got}, door {step} reachable: {bool(adj & r)}")
    opened.add(step)
final = reach(opened)
print(f"  all doors open: exit reachable: {bool(exit_cells & final)}")
if not exit_cells & final: err.append("exit unreachable at the end")

start = reach(set())
for o in groups["rooms"]:
    if prop(o, "redHerring") == "true" and not (cells(o) & start):
        err.append(f"red herring {o.get('name')} not reachable from start")
for g in ("spawns", "items", "keys"):
    for o in groups[g]:
        c = cells(o)
        if c & solid: err.append(f"{o.get('name')} inside collision")
        if not (c & final): err.append(f"{o.get('name')} unreachable")
for o in groups["events"]:
    if not (cells(o) & final): err.append(f"{o.get('name')} unreachable")
print(f"map {W}x{H} @ {T}px | " + ", ".join(f"{k}: {len(v)}" for k, v in groups.items()))
print("OK" if not err else "FAIL\n  " + "\n  ".join(err))
sys.exit(1 if err else 0)
