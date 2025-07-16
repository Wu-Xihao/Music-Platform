<template>
  <div class="lyrics-player">
    <!-- 顶部歌曲信息 -->
    <div class="song-header">
      <div class="album-art">
        <el-image
            class="song-pic"
            fit="cover"
            :src="attachImageUrl(songPic)"
            :preview-src-list="[attachImageUrl(songPic)]"
        />
        <div class="vinyl-effect"></div>
      </div>

      <div class="song-info">
        <h2>{{ songTitle }}</h2>
        <p>{{ singerName }}</p>

        <!-- 波形图 -->
        <div class="waveform-container">
          <canvas ref="waveformCanvas" class="waveform"></canvas>
          <div v-if="!waveformVisible" class="waveform-placeholder">
            <span class="placeholder-text">正在加载波形...</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 歌词区域 -->
    <div class="lyric-container">
      <div class="particles-container">
        <div
            v-for="(p, i) in particles"
            :key="i"
            class="particle"
            :style="particleStyle(p)"
        />
      </div>

      <div class="lyric-wrapper" ref="lyricWrapper">
        <div class="center-line"></div>

        <transition name="fade" mode="out-in">
          <div v-if="lyricArr.length" key="has-lyric" class="lyric-content">
            <ul
                class="lyric-scroll"
                :style="{ transform: `translateY(${lyricOffset}px)` }"
            >
              <li
                  v-for="(line, idx) in lyricArr"
                  :key="idx"
                  class="lyric-line"
                  :class="{
                  current: idx === currentLineIndex,
                  'near-current': Math.abs(idx - currentLineIndex) === 1
                }"
                  ref="lyricLines"
              >
                {{ line[1] }}
              </li>
            </ul>
          </div>

          <div v-else key="no-lyric" class="no-lyric">
            <div class="placeholder">
              <div class="music-icon">
                <div class="note note-1">♪</div>
                <div class="note note-2">♫</div>
                <div class="note note-3">♩</div>
              </div>
              <p>暂无歌词</p>
              <p class="hint">正在努力寻找歌词中...</p>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comments-section">
      <comment :playId="songId" :type="0" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import {
  computed,
  ref,
  watch,
  onMounted,
  onUnmounted,
  nextTick
} from 'vue';
import { useStore } from 'vuex';
import Comment from '@/components/Comment.vue';
import { parseLyric } from '@/utils';
import { HttpManager } from '@/api';

interface Particle {
  x: number;
  y: number;
  size: number;
  speed: number;
  opacity: number;
  color: string;
  rotate: number;
}

/* ---------- store ---------- */
const store = useStore();
const songId       = computed(() => store.getters.songId);
const songTitle    = computed(() => store.getters.songTitle);
const singerName   = computed(() => store.getters.singerName);
const songPic      = computed(() => store.getters.songPic);
const lyric        = computed(() => store.getters.lyric);
const curTime      = computed(() => store.getters.curTime);
const isPlaying    = computed(() => store.getters.isPlaying);
const audioElement = computed(() => store.getters.audioElement);
const currentPlayList     = computed(() => store.getters.currentPlayList);
const currentPlayIndex    = computed(() => store.getters.currentPlayIndex);

/* ---------- 响应式 ---------- */
const lyricOffset      = ref(0);
const currentLineIndex = ref(-1);
const lyricArr         = ref<[number, string][]>([]);
const particles        = ref<Particle[]>([]);
const lyricWrapper     = ref<HTMLElement | null>(null);
const lyricLines       = ref<HTMLElement[]>([]);
const lineHeight       = ref(60);

/* ---------- 波形图 ---------- */
const waveformCanvas = ref<HTMLCanvasElement | null>(null);
const audioContext   = ref<AudioContext | null>(null);
const analyser       = ref<AnalyserNode | null>(null);
const dataArray      = ref<Uint8Array | null>(null);
const animationId    = ref<number | null>(null);
const waveformVisible = ref(false);
const isAudioAnalyserInitialized = ref(false);

/* ---------- 工具 ---------- */
const attachImageUrl = (url: string) => HttpManager.attachImageUrl(url);

/* ---------- 粒子 ---------- */
const particleStyle = (p: Particle) => ({
  left: `${p.x}%`,
  top: `${p.y}%`,
  width: `${p.size}px`,
  height: `${p.size}px`,
  opacity: p.opacity,
  backgroundColor: p.color,
  animationDuration: `${p.speed}s`,
  transform: `rotate(${p.rotate}deg)`
});

const initParticles = () => {
  const colors = [
    'rgba(149,210,246,.7)',
    'rgba(125,188,224,.6)',
    'rgba(100,166,202,.5)',
    'rgba(175,230,255,.4)',
    'rgba(77,161,255,.5)'
  ];
  particles.value = Array.from({ length: 65 }, () => ({
    x: Math.random() * 100,
    y: Math.random() * 100,
    size: Math.random() * 5 + 2,
    speed: Math.random() * 15 + 5,
    opacity: Math.random() * .6 + .3,
    color: colors[Math.floor(Math.random() * colors.length)],
    rotate: Math.random() * 360
  }));
};

const animateParticles = () => {
  particles.value.forEach(p => {
    p.y += 0.05;
    p.rotate += 0.3;
    if (p.y > 100) {
      p.y = -5;
      p.x = Math.random() * 100;
    }
  });
  requestAnimationFrame(animateParticles);
};

/* ---------- 波形 ---------- */
const initAudioAnalyser = () => {
  if (!audioElement.value || !waveformCanvas.value || isAudioAnalyserInitialized.value) return;
  try {
    const AudioContext = window.AudioContext || (window as any).webkitAudioContext;
    audioContext.value = new AudioContext();
    analyser.value = audioContext.value.createAnalyser();
    analyser.value.fftSize = 256;
    analyser.value.smoothingTimeConstant = 0.8;

    const source = audioContext.value.createMediaElementSource(audioElement.value);
    source.connect(analyser.value);
    analyser.value.connect(audioContext.value.destination);

    const bufferLength = analyser.value.frequencyBinCount;
    dataArray.value = new Uint8Array(bufferLength);

    isAudioAnalyserInitialized.value = true;
    waveformVisible.value = true;
    drawWaveform();
  } catch (e) {
    console.error('初始化音频分析器失败', e);
  }
};

const drawWaveform = () => {
  if (!waveformCanvas.value || !analyser.value || !dataArray.value) {
    animationId.value = requestAnimationFrame(drawWaveform);
    return;
  }
  const canvas = waveformCanvas.value;
  const ctx = canvas.getContext('2d')!;
  const rect = canvas.getBoundingClientRect();
  if (canvas.width !== rect.width || canvas.height !== rect.height) {
    canvas.width = rect.width;
    canvas.height = rect.height;
  }
  const w = canvas.width, h = canvas.height;
  analyser.value.getByteTimeDomainData(dataArray.value);
  ctx.clearRect(0, 0, w, h);

  const gradient = ctx.createLinearGradient(0, 0, 0, h);
  gradient.addColorStop(0, 'rgba(77,161,255,.1)');
  gradient.addColorStop(1, 'rgba(94,107,255,.05)');
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, w, h);

  ctx.lineWidth = 3;
  ctx.strokeStyle = '#4da1ff';
  ctx.shadowColor = 'rgba(77,161,255,.6)';
  ctx.shadowBlur = 8;
  ctx.beginPath();

  const slice = w / dataArray.value.length;
  let x = 0;
  for (let i = 0; i < dataArray.value.length; i++) {
    const v = dataArray.value[i] / 128.0;
    const y = v * h / 2;
    i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
    x += slice;
  }
  ctx.lineTo(w, h / 2);
  ctx.stroke();

  animationId.value = requestAnimationFrame(drawWaveform);
};

/* ---------- 歌词 ---------- */
const calculateLyricOffset = (idx: number) => {
  if (!lyricWrapper.value || !lyricLines.value[idx]) return 0;
  const containerHeight = lyricWrapper.value.clientHeight;
  const lineEl = lyricLines.value[idx];
  const lineTop = lineEl.offsetTop;
  const lineH = lineEl.clientHeight;
  return -(lineTop + lineH / 2 - containerHeight / 2);
};

/* ---------- watch ---------- */
watch(
    () => currentPlayList.value[currentPlayIndex.value]?.lyric,
    (newLyric) => {
      try {
        lyricArr.value = parseLyric(newLyric || '');
      } catch (e) {
        lyricArr.value = [];
      }
      currentLineIndex.value = -1;
      lyricOffset.value = 0;
      isAudioAnalyserInitialized.value = false;
      nextTick(() => {
        lyricLines.value = Array.from(
            lyricWrapper.value?.querySelectorAll('.lyric-line') || []
        ) as HTMLElement[];
        if (lyricLines.value.length) lineHeight.value = lyricLines.value[0].clientHeight;
        if (isPlaying.value) initAudioAnalyser();
      });
    },
    { immediate: true }
);

watch(isPlaying, (playing) => {
  if (playing && !isAudioAnalyserInitialized.value) initAudioAnalyser();
  else if (!playing) waveformVisible.value = false;
});

watch(curTime, () => {
  if (!lyricArr.value.length) return;
  let idx = -1;
  for (let i = 0; i < lyricArr.value.length; i++) {
    if (curTime.value >= lyricArr.value[i][0]) idx = i;
    else break;
  }
  if (idx !== currentLineIndex.value && idx >= 0) {
    currentLineIndex.value = idx;
    lyricOffset.value = calculateLyricOffset(idx);
  }
});

/* ---------- lifecycle ---------- */
onMounted(() => {
  initParticles();
  animateParticles();
  if (audioElement.value) {
    audioElement.value.addEventListener('canplay', () => {
      if (isPlaying.value && !isAudioAnalyserInitialized.value) initAudioAnalyser();
    });
    audioElement.value.addEventListener('play', () => {
      if (!isAudioAnalyserInitialized.value) initAudioAnalyser();
    });
  }
});

onUnmounted(() => {
  if (animationId.value) cancelAnimationFrame(animationId.value);
  if (audioContext.value) audioContext.value.close();
});
</script>

<style lang="scss" scoped>
@import '@/assets/css/var.scss';

.lyrics-player {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 25px;
  background: linear-gradient(to bottom, #fafcff, #f0f5ff);
  border-radius: 24px;
  box-shadow: 0 12px 40px rgba(100, 150, 255, 0.15);
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.song-header {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
  padding: 25px;
  background: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(245,248,255,0.95) 100%);
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 80, 200, 0.08);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255,255,255,0.6);

  .album-art {
    position: relative;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    overflow: hidden;
    box-shadow: 0 15px 35px rgba(0, 100, 255, 0.2);
    transition: transform 0.5s ease;
    &:hover { transform: scale(1.05); }

    .song-pic { width: 100%; height: 100%; border-radius: 50%; z-index: 2; position: relative; }

    .vinyl-effect {
      position: absolute;
      inset: 0;
      background:
          radial-gradient(circle at center, transparent 60%, rgba(0,0,0,.05) 100%),
          repeating-conic-gradient(from 0deg,
              rgba(255,255,255,.1) 0deg 10deg,
              transparent 10deg 20deg);
      border-radius: 50%;
      z-index: 1;
      animation: rotate 20s linear infinite;
      border: 3px solid rgba(255,255,255,.2);
    }
  }

  .song-info {
    flex: 1;
    h2 { font-size: 34px; font-weight: 700; color: #2c3e50; margin-bottom: 8px; letter-spacing: 0.3px; text-shadow: 0 1px 3px rgba(0,0,0,.05); }
    p { font-size: 20px; color: #5c7c9f; margin: 0 0 20px 0; font-weight: 500; }

    .waveform-container {
      width: 100%;
      height: 100px;
      background: rgba(224, 233, 245, 0.4);
      border-radius: 12px;
      overflow: hidden;
      position: relative;
      box-shadow: inset 0 1px 3px rgba(0,0,0,.1);

      .waveform { width: 100%; height: 100%; display: block; }

      .waveform-placeholder {
        position: absolute;
        inset: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        background: rgba(224,233,245,.6);
        backdrop-filter: blur(2px);
        .placeholder-text {
          color: #5c7c9f;
          font-size: 16px;
          font-weight: 500;
          padding: 8px 16px;
          background: rgba(255,255,255,.7);
          border-radius: 20px;
          box-shadow: 0 2px 10px rgba(0,0,0,.05);
        }
      }
    }
  }
}

.lyric-container {
  position: relative;
  flex: 1;
  background: rgba(255,255,255,.85);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 35px rgba(0,50,150,.1);
  min-height: 500px;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255,255,255,.6);
}

.particles-container {
  position: absolute;
  inset: 0;
  z-index: 1;
  overflow: hidden;
  .particle {
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
    animation: float linear infinite;
    z-index: 1;
    box-shadow: 0 0 10px rgba(77,161,255,.3);
    @keyframes float {
      0% { transform: translateY(0) rotate(0deg); }
      100% { transform: translateY(100vh) rotate(360deg); }
    }
  }
}

.lyric-wrapper {
  position: relative;
  height: 500px;
  overflow: hidden;
  z-index: 2;
  .center-line {
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(to right, transparent, rgba(77,161,255,.4), transparent);
    z-index: 3;
    transform: translateY(-1px);
    box-shadow: 0 0 10px rgba(77,161,255,.2);
  }

  .lyric-content {
    position: relative;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;

    &::before, &::after {
      content: '';
      position: absolute;
      left: 0; right: 0;
      height: 150px;
      background: linear-gradient(to bottom, rgba(255,255,255,.95), rgba(255,255,255,.5));
      z-index: 3;
      pointer-events: none;
    }
    &::before { top: 0; }
    &::after  { bottom: 0; transform: rotate(180deg); }

    .lyric-scroll {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      width: 100%;
      padding: 0;
      margin: 0;
      transition: transform 0.6s cubic-bezier(.22,.61,.36,1);
      z-index: 2;

      .lyric-line {
        padding: 18px 0;
        text-align: center;
        font-size: 20px;
        line-height: 1.8;
        color: #a0b1c0;
        transition: all 0.5s cubic-bezier(.175,.885,.32,1.275);
        text-shadow: 0 1px 3px rgba(255,255,255,.8);
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;

        &.near-current {
          color: #5c7c9f;
          font-size: 22px;
          font-weight: 500;
          opacity: 0.8;
          text-shadow: 0 2px 5px rgba(0,0,0,.05);
        }
        &.current {
          color: #4da1ff;
          font-size: 28px;
          font-weight: 800;
          text-shadow: 0 0 20px rgba(77,161,255,.4);
          transform: scale(1.15);
          opacity: 1;
          transition: transform 0.3s ease, color 0.3s ease;
        }
      }
    }
  }

  .no-lyric {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    z-index: 2;
    .placeholder {
      text-align: center;
      color: #a0b1c0;
      .music-icon {
        position: relative;
        width: 100px;
        height: 100px;
        margin: 0 auto 25px;
        color: #e0e9f5;
        .note {
          position: absolute;
          font-size: 42px;
          opacity: 0;
          animation: noteFloat 3s infinite ease-in-out;
        }
        .note-1 { left: 10px; top: 20px; animation-delay: 0.5s; }
        .note-2 { left: 40px; top: 40px; animation-delay: 1s; }
        .note-3 { left: 70px; top: 10px; animation-delay: 1.5s; }
      }
      p { margin: 8px 0; font-size: 22px; font-weight: 500; }
      .hint { font-size: 16px; color: #b0c0d0; margin-top: 10px; }
    }
  }
}

.comments-section {
  margin-top: 30px;
  padding: 25px;
  background: linear-gradient(135deg, rgba(255,255,255,.95) 0%, rgba(245,248,255,.95) 100%);
  border-radius: 20px;
  box-shadow: 0 6px 20px rgba(0,50,150,.08);
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255,255,255,.6);
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}
@keyframes noteFloat {
  0%   { opacity: 0; transform: translateY(20px) scale(.8); }
  30%  { opacity: 1; transform: translateY(0) scale(1); }
  70%  { opacity: 1; transform: translateY(0) scale(1); }
  100% { opacity: 0; transform: translateY(-20px) scale(1.2); }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0; transform: translateY(20px);
}

@media (max-width: $sm) {
  .lyrics-player { padding: 15px; }
  .song-header { flex-direction: column; text-align: center; gap: 20px; padding: 20px;
    .album-art { width: 180px; height: 180px; }
  }
  .song-info {
    width: 100%;
    h2 { font-size: 28px; }
    p  { font-size: 18px; margin-bottom: 15px; }
    .waveform-container { height: 80px; margin-bottom: 10px; }
  }
  .lyric-container { min-height: 400px; }
  .lyric-wrapper { height: 400px;
    .lyric-scroll .lyric-line {
      font-size: 18px; height: 46px; padding: 14px 0;
      &.near-current { font-size: 20px; }
      &.current      { font-size: 24px; }
    }
  }
}
</style>