<template>
  <button
      v-if="showActivationButton"
      class="audio-activator"
      @click="activateAudio"
  >
    点击激活音频播放
  </button>
  <audio
      v-show="!showActivationButton"
      controls="controls"
      preload="true"
      v-if="url"
      :ref="player"
      :src="attachImageUrl(url)"
      @canplay="startPlay"
      @ended="ended"
  ></audio>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, watch, ref, onMounted } from "vue";
import { useStore } from "vuex";
import { HttpManager } from "@/api";

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const divRef = ref<HTMLAudioElement>();
    const player = (el) => {
      divRef.value = el;
    };

    // 新增：用户交互状态
    const userInteracted = ref(false);
    const showActivationButton = ref(true);

    // 检测用户交互
    onMounted(() => {
      const handleInteraction = () => {
        userInteracted.value = true;
        showActivationButton.value = false;
        window.removeEventListener('click', handleInteraction);
        window.removeEventListener('touchstart', handleInteraction);
      };

      window.addEventListener('click', handleInteraction);
      window.addEventListener('touchstart', handleInteraction);
    });

    const url = computed(() => store.getters.url);
    const isPlay = computed(() => store.getters.isPlay);

    watch(isPlay, () => {
      togglePlay();
    });

    function activateAudio() {
      userInteracted.value = true;
      showActivationButton.value = false;
      // 立即尝试播放（如果需要）
      if (isPlay.value && divRef.value) {
        divRef.value.play().catch(e => console.log("播放请求被拒绝:", e));
      }
    }

    function togglePlay() {
      if (!userInteracted.value || !divRef.value) return;

      try {
        isPlay.value ? divRef.value.play() : divRef.value.pause();
      } catch (e) {
        console.error("播放控制错误:", e);
      }
    }

    function startPlay() {
      if (userInteracted.value && isPlay.value && divRef.value) {
        divRef.value.play().catch(e => {
          console.log("自动播放被阻止，需要用户交互");
        });
      }
    }

    function ended() {
      proxy.$store.commit("setIsPlay", false);
    }

    return {
      url,
      isPlay,
      player,
      startPlay,
      ended,
      showActivationButton,
      activateAudio,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style>
audio {
  display: none;
}

.audio-activator {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 10px 20px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  z-index: 1000;
}
</style>
