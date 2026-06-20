/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
import { loadUserData, saveUserData, UserData } from './lib/storage';
import HomeView from './components/HomeView';
import GameView from './components/GameView';
import SettingsView from './components/SettingsView';

export type ViewState = 'home' | 'game' | 'settings';
export type GameMode = 'daily' | 'free';

export default function App() {
  const [view, setView] = useState<ViewState>('home');
  const [mode, setMode] = useState<GameMode>('daily');
  const [user, setUser] = useState<UserData>(loadUserData());

  useEffect(() => {
    saveUserData(user);
  }, [user]);

  return (
    <div className="min-h-screen bg-sand text-dark flex flex-col mx-auto max-w-md w-full relative sm:border-x sm:border-earth/30 sm:shadow-2xl overflow-hidden">
      {view === 'home' && (
        <HomeView setView={setView} setMode={setMode} user={user} />
      )}
      {view === 'game' && (
        <GameView setView={setView} mode={mode} user={user} setUser={setUser} />
      )}
      {view === 'settings' && (
        <SettingsView setView={setView} user={user} setUser={setUser} />
      )}
    </div>
  );
}
