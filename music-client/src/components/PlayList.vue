<template>
  <div class="play-list">
    <div class="play-title" v-if="title">{{ title }}</div>
    <ul class="play-body">
      <li class="card-frame" v-for="(item, index) in playList" :key="index">
        <div class="card" @click="goAblum(item)" tabindex="0" @keyup.enter="goAblum(item)">
          <div class="card-image-container">
            <el-image
                class="card-img"
                fit="cover"
                :src="attachImageUrl(item.pic)"
                :lazy="true"
                @error="handleImageError"
            >
              <template #error>
                <div class="image-error">
                  <i class="el-icon-picture-outline"></i>
                  <span>加载失败</span>
                </div>
              </template>
              <template #placeholder>
                <div class="image-loading">
                  <i class="el-icon-loading"></i>
                </div>
              </template>
            </el-image>
          </div>

          <!-- 悬停遮罩：底部到顶部渐变半透明浅蓝 -->
          <div class="mask"></div>

          <!-- 悬停图标：查看图标，透明度 0.4 -->
          <div class="hover-icon">
            <i class="el-icon-view" />
          </div>

          <div class="card-hover-content">
            <div class="hover-text">
              <span>查看</span>
            </div>
          </div>
        </div>
        <p class="card-name">{{ item.name || item.title }}</p>
        <p class="card-artist" v-if="item.artist">{{ item.artist }}</p>
      </li>
    </ul>

    <div v-if="loading" class="loading-indicator">
      <i class="el-icon-loading"></i>
      <span>加载中...</span>
    </div>
    <div v-else-if="playList.length === 0" class="empty-state">
      <i class="el-icon-folder-opened"></i>
      <p>暂无播放列表</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, toRefs } from "vue";
import { useRouter } from "vue-router";
import { Icon } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

interface PlayItem {
  id: number | string;
  name?: string;
  title?: string;
  pic: string;
  artist?: string;
}

export default defineComponent({
  name: "PlayList",
  components: {},
  props: {
    title: String,
    playList: {
      type: Array as () => PlayItem[],
      default: () => []
    },
    path: {
      type: String,
      required: true
    },
    loading: Boolean
  },
  setup(props) {
    const { proxy } = getCurrentInstance();
    const router = useRouter();
    const { routerManager } = mixin();

    const { path } = toRefs(props);

    function goAblum(item: PlayItem) {
      proxy?.$store.commit("setSongDetails", item);
      routerManager(path.value, { path: `/${path.value}/${item.id}` });
    }

    function handleImageError(e: Event) {
      const img = e.target as HTMLImageElement;
      img.src = require("@/assets/images/tubiao.jpg");
    }

    return {
      BOFANG: Icon.BOFANG,
      goAblum,
      handleImageError,
      attachImageUrl: HttpManager.attachImageUrl
    };
  }
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

$card-radius: 12px;
$hover-elevation: 0 8px 16px rgba(0, 0, 0, 0.06);
$transition-speed: 0.3s ease;

.play-list {
  padding: 0 15px;
  margin-bottom: 40px;

  .play-title {
    font-size: 1.8rem;
    font-weight: 600;
    color: $color-blue-primary;
    margin-bottom: 25px;
    padding-bottom: 15px;
    border-bottom: 2px solid $color-border-light;
    position: relative;

    &::after {
      content: "";
      position: absolute;
      bottom: -2px;
      left: 0;
      width: 120px;
      height: 3px;
      background: $color-blue-primary;
      border-radius: 3px;
    }
  }

  .play-body {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 25px;
  }
}

.card-frame {
  perspective: 1000px;

  .card {
    position: relative;
    overflow: hidden;
    border-radius: $card-radius;
    /* 非常浅的背景色，几乎接近白色 */
    background: #fdfdfd;
    /* 减弱阴影 */
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
    transition: all $transition-speed;
    cursor: pointer;
    transform-style: preserve-3d;

    &:hover {
      transform: translateY(-6px) scale(1.01);
      box-shadow: $hover-elevation;
    }

    &:focus-visible {
      outline: 2px solid $color-blue-primary;
      outline-offset: 2px;
    }
  }

  .card-image-container {
    position: relative;
    padding-bottom: 100%; /* 1:1 宽高比 */
  }

  .card-img {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: $card-radius;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4edf5 100%);
  }

  .card-name {
    margin-top: 12px;
    font-weight: 500;
    color: $color-text-primary;
    font-size: 1.05rem;
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
  }

  .card-artist {
    color: $color-text-secondary;
    font-size: 0.9rem;
    margin-top: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &:hover .card-name {
    color: $color-blue-primary;
  }
}

/* 悬停遮罩：从底部到顶部渐变半透明浅蓝，渐变透明 */
.mask {
  position: absolute;
  inset: 0;
  border-radius: $card-radius;
  opacity: 0;
  transition: opacity $transition-speed;
  pointer-events: none;
  background: linear-gradient(
          to top,
          rgba(173, 216, 230, 0.5) 0%,
          rgba(173, 216, 230, 0) 100%
  );
}

.card:hover .mask {
  opacity: 1;
}

/* 悬停图标：查看图标，透明度 0.4 */
.hover-icon {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  opacity: 0;
  transition: opacity $transition-speed;

  i {
    font-size: 32px;
    color: #fff;
    opacity: 0.4;
  }
}

.card:hover .hover-icon {
  opacity: 1;
}

.card-hover-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px;
  background: rgba(255, 255, 255, 0.9);
  opacity: 0;
  transform: translateY(10px);
  transition: all $transition-speed;
  z-index: 3;

  .hover-text {
    display: flex;
    align-items: center;
    justify-content: center;
    color: $color-blue-primary;
    font-weight: 500;
    font-size: 0.95rem;

    i {
      margin-right: 6px;
      font-size: 1.1rem;
    }
  }
}

.card:hover .card-hover-content {
  opacity: 1;
  transform: translateY(0);
}

.image-error,
.image-loading {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: $color-text-secondary;
  background: $color-background-light;
  border-radius: $card-radius;

  i {
    font-size: 2.5rem;
    margin-bottom: 10px;
  }
}

.image-loading i {
  animation: rotate 1.5s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.loading-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: $color-text-secondary;

  i {
    font-size: 2rem;
    margin-bottom: 15px;
    animation: rotate 1.5s linear infinite;
  }
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  text-align: center;
  color: $color-text-secondary;

  i {
    font-size: 3rem;
    margin-bottom: 20px;
    color: $color-border-light;
  }

  p {
    font-size: 1.1rem;
    max-width: 300px;
    line-height: 1.6;
  }
}

@media screen and (max-width: 768px) {
  .play-body {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)) !important;
    gap: 20px !important;
  }

  .play-title {
    font-size: 1.5rem !important;
  }
}

@media screen and (max-width: 480px) {
  .play-body {
    grid-template-columns: repeat(2, 1fr) !important;
    gap: 15px !important;
  }

  .play-title {
    font-size: 1.4rem !important;
    margin-bottom: 20px !important;
  }

  .card-name {
    font-size: 0.95rem !important;
  }
}
</style>
