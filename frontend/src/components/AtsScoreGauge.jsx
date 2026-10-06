import React from 'react';

const AtsScoreGauge = ({ score = 0, size = 160 }) => {
  const strokeWidth = 12;
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const clampedScore = Math.max(0, Math.min(100, score));
  const offset = circumference - (clampedScore / 100) * circumference;

  let color = '#ef4444'; // Red
  let label = 'Needs Work';
  if (clampedScore >= 80) {
    color = '#10b981'; // Emerald Green
    label = 'Excellent';
  } else if (clampedScore >= 65) {
    color = '#6366f1'; // Indigo
    label = 'Good ATS Read';
  } else if (clampedScore >= 50) {
    color = '#f59e0b'; // Amber
    label = 'Average';
  }

  return (
    <div className="ats-gauge-container">
      <div style={{ position: 'relative', width: size, height: size }}>
        <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke="#1e293b"
            strokeWidth={strokeWidth}
            fill="none"
          />
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke={color}
            strokeWidth={strokeWidth}
            fill="none"
            strokeDasharray={circumference}
            strokeDashoffset={offset}
            strokeLinecap="round"
            style={{ transition: 'stroke-dashoffset 1s ease' }}
          />
        </svg>
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
        }}>
          <span style={{ fontSize: '2.25rem', fontWeight: 800, color: 'var(--text-primary)', lineHeight: 1 }}>
            {clampedScore}
          </span>
          <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', marginTop: '2px' }}>
            ATS SCORE
          </span>
        </div>
      </div>
      <div style={{
        marginTop: '0.75rem',
        padding: '0.25rem 0.75rem',
        borderRadius: '9999px',
        backgroundColor: `${color}20`,
        color: color,
        fontSize: '0.8rem',
        fontWeight: 700
      }}>
        {label}
      </div>
    </div>
  );
};

export default AtsScoreGauge;
