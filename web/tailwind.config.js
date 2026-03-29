/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{vue,js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      colors: {
        blush:  '#e9cbd1',
        sand:   '#f1d7a5',
        azure:  '#368dd2',
        mist:   '#c0d0e6',
        char:   '#373737',
        ice:    '#d3e1ee',
        teal:   '#00a7c2',
      },
      fontFamily: {
        display: ['"Playfair Display"', 'serif'],
        body:    ['"Noto Sans KR"', 'sans-serif'],
        mono:    ['"DM Mono"', 'monospace'],
      },
    },
  },
  plugins: [],
}
