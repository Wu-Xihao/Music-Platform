<template>
  <audio
      :src="attachImageUrl(songUrl)"
      autoplay
      controls="controls"
      :ref="player"
      preload="none"
      @canplay="canplay"
      @timeupdate="timeupdate"
      @ended="ended"
      @loadedmetadata="onLoadedmetadata"
  >
  </audio>
</template>

<script lang="ts">
import { defineComponent, ref, getCurrentInstance, computed, watch } from "vue";
import { useStore } from "vuex";
import { HttpManager } from "@/api";
import { onMounted } from 'vue';

export default defineComponent({
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const divRef = ref<HTMLAudioElement>();
    const player = (el) => {
      divRef.value = el;
    };

    const muted = ref(true);

    const audioDom = document.querySelector('audio');
    if (audioDom) {
      audioDom.muted = true;
      audioDom.play()
          .then(() => {
            //
          })
          .catch(error => {
            console.error('自动播放失败，需要用户交互。', error);
          });
    }

    const songUrl = computed(() => store.getters.songUrl);
    const isPlay = computed(() => store.getters.isPlay);
    const volume = computed(() => store.getters.volume);
    const changeTime = computed(() => store.getters.changeTime);
    const autoNext = computed(() => store.getters.autoNext);

    watch(isPlay, () => togglePlay());
    watch(changeTime, (value) => {
      if (divRef.value && !isNaN(value)) {
        // 添加阈值检查，避免微小变化触发重新定位
        divRef.value.currentTime = value;
        console.log(value);
        console.log(changeTime.value);
        console.log(store.getters.duration);
      }
    });
    watch(volume, (value) => (divRef.value.volume = value));

    function togglePlay() {
      isPlay.value ? divRef.value.play() : divRef.value.pause();
    }

    function onLoadedmetadata(res) {
      console.log(parseInt(res.target.duration))
      proxy.$store.commit("setDuration", parseInt(res.target.duration));
    }

    function canplay() {
      proxy.$store.commit("setDuration", divRef.value.duration);
      if (muted.value) {
        divRef.value.muted = true;
        divRef.value.play()
            .then(() => {
              proxy.$store.commit("setIsPlayed", true);
            })
            .catch((err) => {
              console.log("静音自动播放被阻止:", err);
            });
      }
      proxy.$store.commit("setIsPlayed", true);
    }

    function timeupdate() {
      if (!store.getters.isDragging) {
        proxy.$store.commit("setCurTime", divRef.value.currentTime);
      }
    }

    function ended() {
      proxy.$store.commit("setIsPlayed", false);
      proxy.$store.commit("setCurTime", 0);
      proxy.$store.commit("setAutoNext", !autoNext.value);
    }

    document.addEventListener('click', () => {
      if (divRef.value && muted.value) {
        divRef.value.muted = false;
        muted.value = false;
      }
    }, { once: true });

    return {
      songUrl,
      player,
      canplay,
      timeupdate,
      ended,
      onLoadedmetadata,
      muted,
      attachImageUrl: HttpManager.attachImageUrl,
    };
  },
});
</script>

<style scoped>
audio {
  display: none;
}
</style>
