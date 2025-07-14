<template>
  <yin-header></yin-header>
  <yin-aside></yin-aside>
  <div class="content-box" :class="{ 'content-collapse': collapse }">
    <router-view></router-view>
  </div>
  <yin-audio></yin-audio>
</template>

<script lang="ts" setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import YinHeader from "@/components/layouts/YinHeader.vue";
import YinAudio from "@/components/layouts/YinAudio.vue";
import YinAside from "@/components/layouts/YinAside.vue";
import emitter from "@/utils/emitter";

const collapse = ref(false);

const handleCollapse = (msg: boolean) => {
  collapse.value = msg;
};

onMounted(() => {
  emitter.on("collapse", handleCollapse);
});

onBeforeUnmount(() => {
  emitter.off("collapse", handleCollapse);
});
</script>

<style scoped>
.content-box {
  position: absolute;
  left: 240px;
  right: 0;
  top: 60px;
  bottom: 0;
  overflow-y: auto;
  transition: left 0.3s ease;
  padding: 24px;
  background-color: #f1f5f9;
}

.content-collapse {
  left: 80px;
}

.content-box::-webkit-scrollbar {
  width: 6px;
}

.content-box::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.content-box::-webkit-scrollbar-thumb {
  background-color: #94a3b8;
  border-radius: 3px;
}
</style>