export type PipeType = 'straight' | 'curve' | 'empty' | 'source' | 'sink';

export interface GridCell {
  x: number;
  y: number;
  type: PipeType;
  rotation: number;
  fixed: boolean;
  hasWater: boolean;
}

export function mulberry32(a: number) {
  return function() {
    let t = a += 0x6D2B79F5;
    t = Math.imul(t ^ t >>> 15, t | 1);
    t ^= t + Math.imul(t ^ t >>> 7, t | 61);
    return ((t ^ t >>> 14) >>> 0) / 4294967296;
  }
}

function getDir(from: {x:number, y:number}, to: {x:number, y:number}) {
  if (to.x - from.x === 1) return 1;
  if (from.x - to.x === 1) return 3;
  if (to.y - from.y === 1) return 2;
  if (from.y - to.y === 1) return 0;
  return 0;
}

function buildPath(w: number, h: number, random: () => number) {
  while(true) {
    let path = [{x: 0, y: 0}];
    let visited = Array(h).fill(0).map(() => Array(w).fill(false));
    visited[0][0] = true;
    let curr = path[0];

    while(curr.x !== w-1 || curr.y !== h-1) {
      let neighbors = [
        {x: curr.x, y: curr.y - 1},
        {x: curr.x + 1, y: curr.y},
        {x: curr.x, y: curr.y + 1},
        {x: curr.x - 1, y: curr.y}
      ].filter(n => n.x >= 0 && n.x < w && n.y >= 0 && n.y < h && !visited[n.y][n.x]);

      if (neighbors.length === 0) break;
      neighbors.sort(() => random() - 0.5);
      let next = neighbors[0];
      visited[next.y][next.x] = true;
      path.push(next);
      curr = next;
    }

    if (curr.x === w-1 && curr.y === h-1) {
      if (path.length > Math.floor((w * h) / 1.5)) return path;
    }
  }
}

export function generateLevel(w: number, h: number, seed: number): GridCell[] {
  const random = mulberry32(seed);
  const grid: GridCell[] = [];
  for(let y=0; y<h; y++) {
    for(let x=0; x<w; x++) {
      grid.push({ x, y, type: 'empty', rotation: 0, fixed: false, hasWater: false });
    }
  }
  const getCell = (x:number, y:number) => grid.find(c => c.x === x && c.y === y)!;
  const path = buildPath(w, h, random);

  path.forEach((pt, i) => {
    let c = getCell(pt.x, pt.y);
    if (i === 0) {
      c.type = 'source';
      c.fixed = true;
      let dir2 = getDir(pt, path[i+1]);
      c.rotation = (dir2 - 2 + 4) % 4;
    } else if (i === path.length - 1) {
      c.type = 'sink';
      c.fixed = true;
      let dir1 = getDir(pt, path[i-1]);
      c.rotation = dir1; 
    } else {
      let d1 = getDir(pt, path[i-1]);
      let d2 = getDir(pt, path[i+1]);
      if (d1 % 2 === d2 % 2) {
        c.type = 'straight';
        c.rotation = d1 % 2 === 0 ? 0 : 1;
      } else {
        c.type = 'curve';
        for(let r=0; r<4; r++) {
          let rotConn = [(0+r)%4, (1+r)%4];
          if (rotConn.includes(d1) && rotConn.includes(d2)) {
            c.rotation = r;
            break;
          }
        }
      }
    }
  });

  grid.forEach(c => {
    if (!c.fixed && c.type !== 'empty') {
      c.rotation = Math.floor(random() * 4);
    }
  });

  return grid;
}

export function getNeighbors(c: GridCell) {
  let dirs: number[] = [];
  if (c.type === 'straight') dirs = [(0+c.rotation)%4, (2+c.rotation)%4];
  if (c.type === 'curve') dirs = [(0+c.rotation)%4, (1+c.rotation)%4];
  if (c.type === 'source') dirs = [(2+c.rotation)%4];
  if (c.type === 'sink') dirs = [(0+c.rotation)%4];
  return dirs;
}

export function validateGrid(grid: GridCell[]) {
  let newGrid = JSON.parse(JSON.stringify(grid)) as GridCell[];
  newGrid.forEach(c => c.hasWater = false);

  let source = newGrid.find(c => c.type === 'source');
  if (!source) return { newGrid, won: false };

  let queue = [source];
  source.hasWater = true;

  while(queue.length > 0) {
    let c = queue.shift()!;
    let dirs = getNeighbors(c);
    dirs.forEach(d => {
      let nx = c.x + (d === 1 ? 1 : d === 3 ? -1 : 0);
      let ny = c.y + (d === 2 ? 1 : d === 0 ? -1 : 0);
      let n = newGrid.find(x => x.x === nx && x.y === ny);
      if (n && n.type !== 'empty' && !n.hasWater) {
        let oppDir = (d + 2) % 4;
        let nDirs = getNeighbors(n);
        if (nDirs.includes(oppDir)) {
          n.hasWater = true;
          queue.push(n);
        }
      }
    });
  }

  let totalPieces = newGrid.filter(c => c.type !== 'empty').length;
  let waterPieces = newGrid.filter(c => c.type !== 'empty' && c.hasWater).length;
  let won = (totalPieces === waterPieces);

  return { newGrid, won };
}
