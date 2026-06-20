import { motion } from 'motion/react';
import { GridCell } from '../lib/gameLogic';

interface PipeProps {
  cell: GridCell;
  onClick: () => void;
  daltonicMode: boolean;
}

export function PipePiece({ cell, onClick, daltonicMode }: PipeProps) {
  const waterColor = "var(--color-jade)";
  const pipeColor = "var(--color-terra)";
  const pipeWidth = 24;
  const waterWidth = 12;

  let inner = null;
  if (cell.type === 'straight') {
    inner = (
      <>
        <line x1="50" y1="0" x2="50" y2="100" stroke={pipeColor} strokeWidth={pipeWidth} strokeLinecap="square" />
        {cell.hasWater && <line x1="50" y1="-2" x2="50" y2="102" stroke={waterColor} strokeWidth={waterWidth} strokeLinecap="square" strokeDasharray={daltonicMode ? "4 4" : "none"} />}
      </>
    );
  } else if (cell.type === 'curve') {
    inner = (
      <>
        <path d="M 50 0 Q 50 50 100 50" fill="none" stroke={pipeColor} strokeWidth={pipeWidth} strokeLinecap="square" />
        {cell.hasWater && <path d="M 50 -2 Q 50 50 102 50" fill="none" stroke={waterColor} strokeWidth={waterWidth} strokeLinecap="square" strokeDasharray={daltonicMode ? "4 4" : "none"} />}
      </>
    );
  } else if (cell.type === 'source') {
    inner = (
      <>
        <line x1="50" y1="50" x2="50" y2="100" stroke={pipeColor} strokeWidth={pipeWidth} strokeLinecap="square" />
        <circle cx="50" cy="50" r="28" fill={pipeColor} />
        {cell.hasWater && (
          <>
            <line x1="50" y1="50" x2="50" y2="102" stroke={waterColor} strokeWidth={waterWidth} strokeLinecap="square" strokeDasharray={daltonicMode ? "4 4" : "none"} />
            <circle cx="50" cy="50" r="16" fill={waterColor} />
          </>
        )}
        <circle cx="50" cy="50" r="6" fill="var(--color-sand)" />
      </>
    );
  } else if (cell.type === 'sink') {
    inner = (
      <>
        <line x1="50" y1="0" x2="50" y2="50" stroke={pipeColor} strokeWidth={pipeWidth} strokeLinecap="square" />
        <circle cx="50" cy="50" r="28" fill={pipeColor} stroke={waterColor} strokeWidth="4" />
        {cell.hasWater && (
          <>
            <line x1="50" y1="-2" x2="50" y2="50" stroke={waterColor} strokeWidth={waterWidth} strokeLinecap="square" strokeDasharray={daltonicMode ? "4 4" : "none"} />
            <circle cx="50" cy="50" r="16" fill={waterColor} />
          </>
        )}
      </>
    );
  }

  if (cell.type === 'empty') return <div className="w-full h-full" />;

  return (
    <motion.div
      animate={{ rotate: cell.rotation * 90 }}
      transition={{ duration: 0.2, ease: "easeInOut" }}
      className="w-full h-full cursor-pointer relative touch-manipulation"
      onClick={onClick}
      whileTap={!cell.fixed ? { scale: 0.95 } : {}}
    >
      <svg viewBox="0 0 100 100" className="w-full h-full overflow-visible">
        {inner}
      </svg>
    </motion.div>
  );
}
