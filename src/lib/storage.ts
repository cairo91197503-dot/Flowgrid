export interface UserData {
  streak: number;
  bestStreak: number;
  lastPlayedDate: number | null;
  totalLevels: number;
  totalMoves: number;
  freezes: number;
  isPro: boolean;
  daltonicMode: boolean;
}

const DEFAULT_DATA: UserData = {
  streak: 0,
  bestStreak: 0,
  lastPlayedDate: null,
  totalLevels: 0,
  totalMoves: 0,
  freezes: 1,
  isPro: false,
  daltonicMode: false,
};

export function loadUserData(): UserData {
  try {
    const data = localStorage.getItem('flowgrid_data');
    if (data) return { ...DEFAULT_DATA, ...JSON.parse(data) };
  } catch(e) {}
  return DEFAULT_DATA;
}

export function saveUserData(data: UserData) {
  localStorage.setItem('flowgrid_data', JSON.stringify(data));
}
