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
          <canvas ref="waveformCanvas" class="waveform" v-show="waveformVisible"></canvas>
          <div v-if="!waveformVisible" class="waveform-placeholder">
            <span class="placeholder-text">{{ waveformMessage }}</span>
            <el-button
                v-if="showActivateButton"
                type="primary"
                size="small"
                @click="activateWaveform"
                :loading="activating"
            >
              {{ activating ? '激活中...' : '激活波形图' }}
            </el-button>
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
                <div class="note note-2">♫♫</div>
                <div class="note note-3">♩♩</div>
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
import { ElMessage } from 'element-plus';
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
const songId = computed(() => store.getters.songId);
const songTitle = computed(() => store.getters.songTitle);
const singerName = computed(() => store.getters.singerName);
const songPic = computed(() => store.getters.songPic);
const lyric = computed(() => store.getters.lyric);
const curTime = computed(() => store.getters.curTime);
const isPlaying = computed(() => store.getters.isPlay);
const audioElement = computed(() => store.getters.audioElement);
const currentPlayList = computed(() => store.getters.currentPlayList);
const currentPlayIndex = computed(() => store.getters.currentPlayIndex);

/* ---------- 响应式 ---------- */
const lyricOffset = ref(0);
const currentLineIndex = ref(-1);
const lyricArr = ref<[number, string][]>([]);
const particles = ref<Particle[]>([]);
const lyricWrapper = ref<HTMLElement | null>(null);
const lyricLines = ref<HTMLElement[]>([]);
const lineHeight = ref(60);

/* ---------- 波形图 ---------- */
const waveformCanvas = ref<HTMLCanvasElement | null>(null);
const audioContext = ref<AudioContext | null>(null);
const analyser = ref<AnalyserNode | null>(null);
const dataArray = ref<Uint8Array | null>(null);
const animationId = ref<number | null>(null);
const waveformVisible = ref(false);
const waveformMessage = ref('正在加载波形...');
const showActivateButton = ref(false);
const isAudioAnalyserInitialized = ref(false);
const activating = ref(false);
const waveformActivated = ref(false);
const usingFallbackWaveform = ref(false);

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

/* ---------- 波形图激活 ---------- */
const activateWaveform = async () => {
  if (!audioElement.value || activating.value || waveformActivated.value) return;

  activating.value = true;
  waveformMessage.value = '正在激活波形图...';

  try {
    // 创建或恢复音频上下文
    if (!audioContext.value || audioContext.value.state === 'closed') {
      const AudioContext = window.AudioContext || (window as any).webkitAudioContext;
      audioContext.value = new AudioContext();
    }

    // 检查AudioContext状态
    if (audioContext.value.state === 'suspended') {
      await audioContext.value.resume();
    }

    initAudioAnalyser();
  } catch (e) {
    console.error('激活波形图失败:', e);
    waveformMessage.value = '激活失败，将使用模拟波形';
    showActivateButton.value = false;
    provideFallbackWaveform();
  } finally {
    activating.value = false;
  }
};

/* ---------- 波形图初始化 ---------- */
const initAudioAnalyser = () => {
  if (!audioElement.value || !waveformCanvas.value || isAudioAnalyserInitialized.value) {
    return;
  }

  try {
    // 创建分析器节点
    analyser.value = audioContext.value!.createAnalyser();
    analyser.value.fftSize = 256;
    analyser.value.smoothingTimeConstant = 0.8;

    // 连接音频源
    const source = audioContext.value!.createMediaElementSource(audioElement.value as HTMLAudioElement);
    source.connect(analyser.value);
    analyser.value.connect(audioContext.value!.destination);

    // 创建数据数组
    const bufferLength = analyser.value.frequencyBinCount;
    dataArray.value = new Uint8Array(bufferLength);

    isAudioAnalyserInitialized.value = true;
    waveformVisible.value = true;
    waveformMessage.value = '波形图已激活';
    showActivateButton.value = false;
    waveformActivated.value = true;
    usingFallbackWaveform.value = false;

    // 开始绘制波形
    drawWaveform();
  } catch (e) {
    console.error('初始化音频分析器失败:', e);
    waveformMessage.value = '初始化失败，将使用模拟波形';
    provideFallbackWaveform();
  }
};

const provideFallbackWaveform = () => {
  if (!waveformCanvas.value) return;

  const canvas = waveformCanvas.value;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // 更新画布尺寸
  const rect = canvas.getBoundingClientRect();
  if (canvas.width !== rect.width || canvas.height !== rect.height) {
    canvas.width = rect.width;
    canvas.height = rect.height;
  }

  // 停止之前的动画
  if (animationId.value) {
    cancelAnimationFrame(animationId.value);
    animationId.value = null;
  }

  let time = 0; // 时间计数器
  const amplitude = 0.8; // 振幅 (0-1之间)
  const frequency = 0.005; // 频率
  const waveCount = 5; // 波形数量
  const waveColors = ['#4da1ff','#b951ee','#8aff63', '#e7d558', '#ee933a' ]; // 波形颜色

  const drawFallback = () => {
    if (!waveformVisible.value) return;

    const w = canvas.width;
    const h = canvas.height;

    time += 0.05; // 增加时间计数器

    ctx.clearRect(0, 0, w, h);

    // 创建渐变背景
    const gradient = ctx.createLinearGradient(0, 0, 0, h);
    gradient.addColorStop(0, 'rgba(77,161,255,.1)');
    gradient.addColorStop(1, 'rgba(94,107,255,.05)');
    ctx.fillStyle = gradient;
    ctx.fillRect(0, 0, w, h);

    // 绘制多个正弦波叠加
    for (let waveIndex = 0; waveIndex < waveCount; waveIndex++) {
      const phaseShift = waveIndex * Math.PI / waveCount; // 相位偏移
      const waveAmplitude = amplitude * (1 - waveIndex * 0.2); // 每个波形振幅递减
      const waveFrequency = frequency * (1 + waveIndex * 0.3); // 每个波形频率递增

      ctx.beginPath();
      ctx.lineWidth = waveIndex === 0 ? 3 : 2; // 主波形更粗
      ctx.strokeStyle = waveColors[waveIndex % waveColors.length];
      ctx.shadowColor = `rgba(${waveIndex === 0 ? '77,161,255' : '42,107,198'},.${3 + waveIndex})`;
      ctx.shadowBlur = 8 - waveIndex * 2;

      // 绘制正弦波路径
      for (let x = 0; x < w; x++) {
        // 正弦波公式: y = A * sin(2πf(x + t) + φ)
        const y = h / 2 +
            waveAmplitude * h / 2 *
            Math.sin(2 * Math.PI * waveFrequency * x + time + phaseShift);

        if (x === 0) {
          ctx.moveTo(x, y);
        } else {
          ctx.lineTo(x, y);
        }
      }

      ctx.stroke();
    }

    // 继续绘制
    animationId.value = requestAnimationFrame(drawFallback);
  };

  waveformVisible.value = true;
  waveformMessage.value = '使用模拟波形';
  showActivateButton.value = false;
  usingFallbackWaveform.value = true;
  drawFallback();
};

const drawWaveform = () => {
  if (!waveformCanvas.value || !analyser.value || !dataArray.value) {
    if (waveformVisible.value) {
      animationId.value = requestAnimationFrame(drawWaveform);
    }
    return;
  }

  const canvas = waveformCanvas.value;
  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  // 更新画布尺寸 - 确保canvas有正确尺寸
  const container = canvas.parentElement;
  if (container) {
    const { width, height } = container.getBoundingClientRect();
    if (width > 0 && height > 0) {
      canvas.width = width;
      canvas.height = height;
    }
  }

  const w = canvas.width;
  const h = canvas.height;

  if (w === 0 || h === 0) {
    // 如果尺寸为0，等待下一帧
    animationId.value = requestAnimationFrame(drawWaveform);
    return;
  }

  // 获取音频数据
  analyser.value.getByteTimeDomainData(dataArray.value);

  // 清除画布
  ctx.clearRect(0, 0, w, h);

  // 创建渐变背景
  const gradient = ctx.createLinearGradient(0, 0, 0, h);
  gradient.addColorStop(0, 'rgba(77,161,255,.1)');
  gradient.addColorStop(1, 'rgba(94,107,255,.05)');
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, w, h);

  // 设置波形样式
  ctx.lineWidth = 3;
  ctx.strokeStyle = '#4da1ff';
  ctx.shadowColor = 'rgba(77,161,255,.6)';
  ctx.shadowBlur = 8;
  ctx.beginPath();

  // 计算波形路径
  const slice = w / dataArray.value.length;
  let x = 0;
  for (let i = 0; i < dataArray.value.length; i++) {
    const v = dataArray.value[i] / 128.0;
    const y = v * h / 2;
    i === 0 ? ctx.moveTo(x, y) : ctx.lineTo(x, y);
    x += slice;
  }

  // 绘制波形
  ctx.lineTo(w, h / 2);
  ctx.stroke();

  // 添加填充效果
  ctx.lineTo(w, h);
  ctx.lineTo(0, h);
  ctx.closePath();

  const fillGradient = ctx.createLinearGradient(0, 0, w, 0);
  fillGradient.addColorStop(0, 'rgba(77,161,255,.1)');
  fillGradient.addColorStop(1, 'rgba(94,107,255,.1)');
  ctx.fillStyle = fillGradient;
  ctx.fill();

  // 继续绘制
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
      nextTick(() => {
        lyricLines.value = Array.from(
            lyricWrapper.value?.querySelectorAll('.lyric-line') || []
        ) as HTMLElement[];
        if (lyricLines.value.length) lineHeight.value = lyricLines.value[0].clientHeight;
      });
    },
    { immediate: true }
);

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

/* ---------- 生命周期 ---------- */
onMounted(() => {
  initParticles();
  animateParticles();

  // 增强的音频元素检查
  const isHTMLElement = (obj: any): obj is HTMLElement => {
    return obj && typeof obj.addEventListener === 'function';
  };

  const checkAudioAndSetup = () => {
    if (isHTMLElement(audioElement.value)) {
      // 有效的 DOM 元素
      const audioEl = audioElement.value as HTMLAudioElement;
      const onLoadedData = () => {
        if (!waveformActivated.value) {
          waveformMessage.value = '音频加载完成，点击激活波形图';
          showActivateButton.value = true;
        }
      };

      if (audioEl.readyState >= 2) {
        // HAVE_CURRENT_DATA
        onLoadedData();
      } else {
        audioEl.addEventListener('loadeddata', onLoadedData);
      }
    } else {
      waveformMessage.value = '无法访问音频元素，将使用模拟波形';
      provideFallbackWaveform();
    }
  };

  // 尝试自动激活（需要用户交互）
  const tryAutoActivate = () => {
    if (!waveformActivated.value) {
      checkAudioAndSetup();
    }
  };

  // 初始检查
  checkAudioAndSetup();

  // 监听用户交互以激活
  document.addEventListener('click', tryAutoActivate, { once: true });

  // 确保 Canvas 初始尺寸正确
  nextTick(() => {
    if (waveformCanvas.value) {
      const container = waveformCanvas.value.parentElement;
      if (container) {
        const { width, height } = container.getBoundingClientRect();
        waveformCanvas.value.width = width;
        waveformCanvas.value.height = height;
      }
    }
  });
});

onUnmounted(() => {
  if (animationId.value) {
    cancelAnimationFrame(animationId.value);
  }
  if (audioContext.value) {
    audioContext.value.close().catch(e => {
      console.error('关闭AudioContext失败:', e);
    });
  }
});
</script>

<style lang="scss" scoped>
@import '@/assets/css/var.scss';

/* 添加必要的样式确保容器尺寸正确 */
.waveform-container {
  position: relative;
  width: 100%;
  height: 100px; /* 确保有固定高度 */
  overflow: hidden;

  .waveform {
    display: block;
    width: 100%;
    height: 100%;
  }

  .waveform-placeholder {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(224,233,245,.8);
    backdrop-filter: blur(4px);
    z-index: 2;

    .placeholder-text {
      color: #5c7c9f;
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 10px;
      text-align: center;
    }
  }
}
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
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 35px rgba(0, 80, 200, 0.15);
  }

  .album-art {
    position: relative;
    width: 200px;
    height: 200px;
    border-radius: 50%;
    overflow: hidden;
    box-shadow: 0 15px 35px rgba(0, 100, 255, 0.2);
    transition: transform 0.5s ease;

    &:hover {
      transform: scale(1.05);
    }

    .song-pic {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      z-index: 2;
      position: relative;
      transition: transform 0.8s ease;
    }

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

    h2 {
      font-size: 34px;
      font-weight: 700;
      color: #2c3e50;
      margin-bottom: 8px;
      letter-spacing: 0.3px;
      text-shadow: 0 1px 3px rgba(0,0,0,.05);
    }

    p {
      font-size: 20px;
      color: #5c7c9f;
      margin: 0 0 20px 0;
      font-weight: 500;
    }

    .waveform-container {
      width: 100%;
      height: 100px;
      background: rgba(224, 233, 245, 0.4);
      border-radius: 12px;
      overflow: hidden;
      position: relative;
      box-shadow: inset 0 1px 3px rgba(0,0,0,.1);
      transition: height 0.3s ease;

      .waveform {
        width: 100%;
        height: 100%;
        display: block;
      }

      .waveform-placeholder {
        position: absolute;
        inset: 0;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: rgba(224,233,245,.8);
        backdrop-filter: blur(4px);
        z-index: 2;

        .placeholder-text {
          color: #5c7c9f;
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 10px;
          text-align: center;
        }

        .el-button {
          background: #4da1ff;
          border: none;
          box-shadow: 0 2px 8px rgba(77, 161, 255, 0.3);
          transition: all 0.3s ease;

          &:hover {
            background: #3a8de0;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(77, 161, 255, 0.4);
          }
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
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 12px 40px rgba(0, 80, 200, 0.15);
  }
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
    box-shadow: 0 0 10px rgba(77,161,255,.15);
    transition: all 0.5s ease;

    @keyframes float {
      0% { transform: translateY(0) rotate(0deg); }
      100% { transform: translateY(100vh) rotate(360deg); }
    }

    // 为不同粒子添加不同动画速度
    @for $i from 1 through 5 {
      &:nth-child(#{$i}) {
        animation-duration: random(20) + 20s;
        animation-delay: random(5) + s;
      }
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
    box-shadow: 0 0 10px rgba(77,161,255,.1);
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
      left: 0;
      right: 0;
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
        cursor: default;
        user-select: none;

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
          text-shadow: 0 0 20px rgba(77,161,255,.2);
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
        width: 120px;
        height: 120px;
        margin: 0 auto 25px;
        color: #e0e9f5;
        display: flex;
        align-items: center;
        justify-content: center;

        .note {
          position: absolute;
          font-size: 48px;
          opacity: 0;
          animation: noteFloat 3s infinite ease-in-out;
          font-weight: bold;
        }

        .note-1 {
          left: 15px;
          top: 30px;
          animation-delay: 0.5s;
          color: rgba(77, 161, 255, 0.7);
        }

        .note-2 {
          left: 50px;
          top: 50px;
          animation-delay: 1s;
          color: rgba(77, 161, 255, 0.5);
        }

        .note-3 {
          left: 80px;
          top: 20px;
          animation-delay: 1.5s;
          color: rgba(77, 161, 255, 0.3);
        }
      }

      p {
        margin: 8px 0;
        font-size: 22px;
        font-weight: 500;
      }

      .hint {
        font-size: 16px;
        color: #b0c0d0;
        margin-top: 10px;
      }
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
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 8px 25px rgba(0, 80, 200, 0.12);
  }
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
  opacity: 0;
  transform: translateY(20px);
}

@media (max-width: $sm) {
  .lyrics-player {
    padding: 15px;
  }

  .song-header {
    flex-direction: column;
    text-align: center;
    gap: 20px;
    padding: 20px;

    .album-art {
      width: 180px;
      height: 180px;
    }
  }

  .song-info {
    width: 100%;

    h2 {
      font-size: 28px;
    }

    p  {
      font-size: 18px;
      margin-bottom: 15px;
    }

    .waveform-container {
      height: 80px;
      margin-bottom: 10px;
    }
  }

  .lyric-container {
    min-height: 400px;
  }

  .lyric-wrapper {
    height: 400px;

    .lyric-scroll .lyric-line {
      font-size: 18px;
      height: 46px;
      padding: 14px 0;

      &.near-current {
        font-size: 20px;
      }

      &.current {
        font-size: 24px;
      }
    }
  }
}
</style>
