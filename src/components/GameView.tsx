import { useState, useEffect } from 'react';
import { ArrowLeft, ShareIcon } from 'lucide-react';
import { ViewState, GameMode } from '../App';
import { UserData } from '../lib/storage';
import { generateLevel, validateGrid, GridCell } from '../lib/gameLogic';
import { PipePiece } from './PipePiece';
import { playClickSound, playWinSound, triggerHaptic } from '../lib/audio';

interface Props {
  setView: (v: ViewState) => void;
  mode: GameMode;
  user: UserData;
  setUser: (u: UserData | ((u: UserData) => UserData)) => void;
}

export default function GameView({ setView, mode, user, setUser }: Props) {
  const [grid, setGrid] = useState<GridCell[]>([]);
  const [size, setSize] = useState(5);
  const [moves, setMoves] = useState(0);
  const [isWon, setIsWon] = useState(false);
  const [showVictory, setShowVictory] = useState(false);

  useEffect(() => {
    startLevel();
  }, []);

  const startLevel = () => {
    let seed = Math.floor(Math.random() * 1000000);
    let currSize = size;
    if (mode === 'daily') {
      const d = new Date();
      seed = d.getFullYear() * 10000 + (d.getMonth() + 1) * 100 + d.getDate();
      currSize = 6;
      setSize(6);
    } else {
      currSize = Math.min(8, 5 + Math.floor(user.totalLevels / 10));
      setSize(currSize);
    }

    const initialGrid = generateLevel(currSize, currSize, seed);
    const { newGrid } = validateGrid(initialGrid);
    setGrid(newGrid);
    setMoves(0);
    setIsWon(false);
    setShowVictory(false);
  };

  const handleCellClick = (x: number, y: number) => {
    if (isWon) return;
    const cell = grid.find(c => c.x === x && c.y === y);
    if (!cell || cell.type === 'empty' || cell.fixed) return;

    playClickSound();
    triggerHaptic('light');

    const newGridStr = JSON.parse(JSON.stringify(grid));
    const tgt = newGridStr.find((c:any) => c.x === x && c.y === y);
    tgt.rotation = (tgt.rotation + 1) % 4;

    const { newGrid, won } = validateGrid(newGridStr);
    setGrid(newGrid);
    setMoves(m => m + 1);

    if (won) {
      setIsWon(true);
      triggerHaptic('success');
      playWinSound();
      setTimeout(() => {
        handleWin();
        setShowVictory(true);
      }, 1000);
    }
  };

  const handleWin = () => {
    const todayInt = parseInt(new Date().toISOString().slice(0, 10).replace(/-/g, ''));
    setUser(prev => {
      let newStreak = prev.streak;
      if (mode === 'daily' && prev.lastPlayedDate !== todayInt) {
        newStreak += 1;
      }
      return {
        ...prev,
        streak: newStreak,
        bestStreak: Math.max(prev.bestStreak, newStreak),
        lastPlayedDate: mode === 'daily' ? todayInt : prev.lastPlayedDate,
        totalLevels: prev.totalLevels + 1,
        totalMoves: prev.totalMoves + moves
      };
    });
  };

  return (
    <div className="flex-1 flex flex-col">
      <div className="flex items-center justify-between p-4 bg-sand border-b border-earth/20 z-10 sticky top-0 relative">
        <button onClick={() => setView('home')} className="p-2 -ml-2 text-dark">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <div className="font-display font-bold text-lg">
          {mode === 'daily' ? 'Nível do Dia' : 'Modo Livre'}
        </div>
        <div className="text-sm font-medium text-dark/70 bg-earth/20 px-3 py-1 rounded-full">
          {moves} mov
        </div>
      </div>

      <div className="flex-1 flex items-center justify-center p-4">
        {!showVictory ? (
          <div
            className="grid gap-1 w-full max-w-[400px] aspect-square bg-earth/10 p-2 rounded-xl"
            style={{ gridTemplateColumns: `repeat(${size}, minmax(0, 1fr))` }}
          >
            {grid.map(c => (
              <PipePiece
                key={`${c.x}-${c.y}`}
                cell={c}
                onClick={() => handleCellClick(c.x, c.y)}
                daltonicMode={user.daltonicMode}
              />
            ))}
          </div>
        ) : (
          <div className="text-center space-y-6 animate-in slide-in-from-bottom flex flex-col items-center">
            <div className="w-24 h-24 bg-jade text-white rounded-full flex items-center justify-center shadow-2xl">
              <svg className="w-12 h-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={3} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-3xl font-display font-bold text-dark">Nível Concluído!</h2>
            <p className="text-earth font-medium">Resolvido em {moves} movimentos.</p>

            <div className="flex flex-col gap-4 mt-8 w-full max-w-xs">
              <button
                onClick={() => {
                  const text = `FlowGrid ${mode === 'daily' ? 'Diário' : 'Livre'} 🌊\nResolvido em ${moves} movimentos!\nBaixe agora: https://flowgrid.example`;
                  if (navigator.share) navigator.share({ text });
                  else navigator.clipboard.writeText(text).then(() => alert('Copiado!'));
                }}
                className="py-4 rounded-xl font-display font-bold bg-dark text-sand flex items-center justify-center space-x-2"
              >
                <ShareIcon className="w-5 h-5"/>
                <span>Compartilhar</span>
              </button>
              <button
                onClick={mode === 'daily' ? () => setView('home') : startLevel}
                className="py-4 rounded-xl font-display font-bold bg-terra text-white"
              >
                {mode === 'daily' ? 'Voltar Início' : 'Próximo Nível'}
              </button>
            </div>
          </div>
        )}
      </div>
      
      {!user.isPro && !showVictory && (
        <div className="absolute bottom-0 w-full h-16 bg-white/50 border-t border-earth/30 flex items-center justify-center text-xs text-dark/50 font-sans pointer-events-none">
          Ad
        </div>
      )}
    </div>
  );
}
