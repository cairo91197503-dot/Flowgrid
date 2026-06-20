import { Play, Settings, Crown, Calendar, Zap } from 'lucide-react';
import { ViewState, GameMode } from '../App';
import { UserData } from '../lib/storage';

interface Props {
  setView: (v: ViewState) => void;
  setMode: (m: GameMode) => void;
  user: UserData;
}

export default function HomeView({ setView, setMode, user }: Props) {
  const isTodayPlayed = user.lastPlayedDate === parseInt(new Date().toISOString().slice(0, 10).replace(/-/g, ''));

  return (
    <div className="flex-1 flex flex-col items-center justify-center p-6 space-y-10">
      <div className="text-center">
        <h1 className="text-5xl font-display font-bold text-dark mb-2 tracking-tight">FlowGrid</h1>
        <p className="text-earth font-medium tracking-widest uppercase text-sm">Jardim de Pedra</p>
      </div>

      <div className="flex items-center space-x-2 bg-earth/20 px-4 py-2 rounded-full">
        <Zap className="w-5 h-5 text-terra" />
        <span className="font-display font-bold">{user.streak} day streak</span>
      </div>

      <div className="w-full space-y-4">
        <button
          onClick={() => { setMode('daily'); setView('game'); }}
          className={`w-full py-5 rounded-2xl flex items-center justify-center space-x-3 text-lg font-display font-bold transition-transform active:scale-95 ${
            isTodayPlayed
              ? 'bg-earth/30 text-dark/70'
              : 'bg-jade text-white shadow-lg shadow-jade/30'
          }`}
        >
          <Calendar className="w-6 h-6" />
          <span>{isTodayPlayed ? 'Rejogar Nível do Dia' : 'Jogar Nível do Dia'}</span>
        </button>

        <button
          onClick={() => { setMode('free'); setView('game'); }}
          className="w-full py-5 rounded-2xl flex items-center justify-center space-x-3 text-lg font-display font-bold bg-terra text-white shadow-lg shadow-terra/30 transition-transform active:scale-95"
        >
          <Play className="w-6 h-6" />
          <span>Modo Livre</span>
        </button>
      </div>

      <div className="flex space-x-6 pt-8">
        <button onClick={() => setView('settings')} className="p-4 bg-earth/20 rounded-full text-dark hover:bg-earth/40 transition-colors">
          <Settings className="w-6 h-6" />
        </button>
        <button onClick={() => alert("FlowGrid Pro: Simulação da Paywall Native")} className="p-4 bg-gradient-to-tr from-amber-400 to-amber-600 rounded-full text-white shadow-md hover:scale-105 transition-transform">
          <Crown className="w-6 h-6" />
        </button>
      </div>

      {!user.isPro && (
        <div className="absolute bottom-0 w-full h-16 bg-white/50 border-t border-earth/30 flex items-center justify-center text-xs text-dark/50 font-sans">
          Advertisement (Placeholder)
        </div>
      )}
    </div>
  );
}
