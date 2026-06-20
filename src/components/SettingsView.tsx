import { ArrowLeft } from 'lucide-react';
import { ViewState } from '../App';
import { UserData } from '../lib/storage';

interface Props {
  setView: (v: ViewState) => void;
  user: UserData;
  setUser: (u: UserData | ((u: UserData) => UserData)) => void;
}

export default function SettingsView({ setView, user, setUser }: Props) {
  return (
    <div className="flex-1 flex flex-col">
      <div className="flex items-center p-4 border-b border-earth/20 bg-sand">
        <button onClick={() => setView('home')} className="p-2 -ml-2 text-dark">
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h2 className="font-display font-bold text-lg ml-2">Configurações</h2>
      </div>

      <div className="p-6 space-y-6">
        <div className="bg-white p-4 rounded-2xl shadow-sm border border-earth/10">
          <h3 className="font-display font-bold mb-4 text-dark">Acessibilidade</h3>
          <label className="flex items-center justify-between cursor-pointer">
            <span className="font-medium text-dark/80">Modo Daltônico</span>
            <input
              type="checkbox"
              checked={user.daltonicMode}
              onChange={(e) => setUser({...user, daltonicMode: e.target.checked})}
              className="w-6 h-6 text-terra rounded focus:ring-terra bg-earth/20 border-none"
            />
          </label>
        </div>

        <div className="bg-white p-4 rounded-2xl shadow-sm border border-earth/10">
          <h3 className="font-display font-bold mb-4 text-dark">Estatísticas</h3>
          <div className="grid grid-cols-2 gap-4">
            <div className="bg-earth/10 p-4 rounded-xl text-center">
              <div className="text-3xl font-display font-bold text-terra mb-1">{user.totalLevels}</div>
              <div className="text-[10px] font-bold uppercase tracking-widest text-dark/60">Níveis Totais</div>
            </div>
            <div className="bg-earth/10 p-4 rounded-xl text-center">
              <div className="text-3xl font-display font-bold text-jade mb-1">{user.bestStreak}</div>
              <div className="text-[10px] font-bold uppercase tracking-widest text-dark/60">Melhor Streak</div>
            </div>
            <div className="bg-earth/10 p-4 rounded-xl text-center col-span-2">
              <div className="text-3xl font-display font-bold text-dark mb-1">{user.totalMoves}</div>
              <div className="text-[10px] font-bold uppercase tracking-widest text-dark/60">Total Movimentos</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
