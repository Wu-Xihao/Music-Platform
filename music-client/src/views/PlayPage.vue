<template>
  <div>
    <!-- 1️⃣ 真实音频元素（仅本页有效） -->
    <audio
        ref="audioRef"
        :src="songUrl"
        @canplay="onCanPlay"
    ></audio>

    <!-- 2️⃣ 歌词/波形 -->
    <LyricsPlayer />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useStore } from 'vuex'
import { defineComponent, getCurrentInstance, toRefs, computed, reactive } from "vue";
import LyricsPlayer from '@/views/Lyric.vue'   // 放歌词


const store = useStore()
const audioRef = ref<HTMLAudioElement>()

/* 把真实 DOM 存进 Vuex */
watch(
    () => audioRef.value,
    el => el && store.commit('setAudioElement', el),
    { immediate: true }
)

const songUrl = computed(() => store.getters.songUrl)

const onCanPlay = () => store.commit('setIsPlay', true)
</script>
