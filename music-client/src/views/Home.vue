<template>
  <div class="home-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <i class="icon-error"></i>
      <p>数据加载失败，请稍后重试</p>
      <el-button type="primary" @click="fetchData">重新加载</el-button>
    </div>

    <!-- 内容展示 -->
    <div v-else>
      <!-- 轮播图 -->
      <section v-if="swiperList.length" class="swiper-section">
        <el-carousel
            class="swiper-container"
            indicator-position="outside"
            :height="carouselHeight"
            :interval="5000"
            :autoplay="true"
            arrow="always"
        >
          <el-carousel-item v-for="(item, index) in swiperList" :key="index">
            <div class="carousel-content" :class="item.type">
              <!-- 左侧图片 -->
              <div class="carousel-image">
                <img
                    :src="HttpManager.attachImageUrl(item.pic)"
                    :alt="item.name"
                    loading="lazy"
                    class="carousel-img"
                />
              </div>

              <!-- 右侧介绍 -->
              <div class="carousel-info">
                <div class="type-badge">
                  <span v-if="item.type === 'songList'">精选歌单</span>
                  <span v-else>热门歌手</span>
                </div>


                <div v-if="item.type === 'songList'" class="info-details">
                  <div class="meta-info">
                    <div class="creator-info">
                      <img :src="getCreatorAvatar(item)" class="creator-avatar" />
                      <span>{{ item.creator || '匿名用户' }}</span>
                    </div>

                  </div>

                  <div class="info-desc">
                    <p>{{ item.introduction || '这是一个精选歌单，收录了各种风格的音乐作品。' }}</p>
                  </div>
                </div>

                <div v-else class="info-details">
                  <h2 class="info-title">{{ item.name }}</h2>

                  <div class="info-desc">
                    <p>{{ item.introduction || '这位歌手拥有独特的嗓音和出色的演唱技巧，深受广大乐迷喜爱。' }}</p>
                  </div>
                </div>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 热门歌单 -->
      <section class="playlist-section">
        <div class="section-header">
          <h2 class="section-title">热门歌单</h2>
          <router-link to="/song-sheet" class="view-more">查看更多</router-link>
        </div>
        <play-list
            class="play-list-container"
            path="song-sheet-detail"
            :playList="songList"
            type="song"
        ></play-list>
      </section>

      <!-- 热门歌手 -->
      <section class="playlist-section">
        <div class="section-header">
          <h2 class="section-title">热门歌手</h2>
          <router-link to="/singer" class="view-more">查看更多</router-link>
        </div>
        <play-list
            class="play-list-container"
            path="singer-detail"
            :playList="singerList"
            type="singer"
        ></play-list>
      </section>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, onUnmounted } from "vue";
import PlayList from "@/components/PlayList.vue";
import { NavName } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

const { changeIndex } = mixin();

// 响应式数据
const songList = ref([]);
const singerList = ref([]);
const swiperList = ref([]);
const loading = ref(true);
const error = ref(false);
const windowWidth = ref(window.innerWidth); // 窗口宽度跟踪

// 计算轮播图高度（响应式）- 减小到原来的一半
const carouselHeight = computed(() => {
  return windowWidth.value < 768 ? "30vw" : "20vw";
});

// 窗口大小变化处理函数
function handleResize() {
  windowWidth.value = window.innerWidth;
}

// 获取创建者头像 - 处理默认情况
function getCreatorAvatar(item) {
  if (item.creatorPic) {
    return HttpManager.attachImageUrl(item.creatorPic);
  }
  // 返回默认头像的Base64编码
  return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Ccircle cx='12' cy='8' r='4' fill='%239C27B0'/%3E%3Cpath fill='%239C27B0' d='M20 19v1a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-1a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6z' opacity='0.3'/%3E%3C/svg%3E";
}

// 获取数据
async function fetchData() {
  try {
    loading.value = true;
    error.value = false;

    // 使用Promise.all并行请求
    const [banners, songLists, singers] = await Promise.all([
      HttpManager.getBannerList(),
      HttpManager.getSongList(),
      HttpManager.getAllSinger()
    ]);

    // 处理歌单数据（取前5个）
    const topSongLists = (songLists as any).data?.sort().slice(0, 5) || [];

    // 处理歌手数据（取前5个）
    const topSingers = (singers as any).data?.sort().slice(0, 5) || [];

    // 组合轮播图数据（5个歌单 + 5个歌手）
    swiperList.value = [
      ...topSongLists.map(item => ({
        ...item,
        type: 'songList'
      })),
      ...topSingers.map(item => ({
        ...item,
        type: 'singer'
      }))
    ];

    // 处理完整歌单数据（取前10个）
    songList.value = (songLists as any).data?.sort().slice(0, 10) || [];

    // 处理完整歌手数据（取前10个）
    singerList.value = (singers as any).data?.sort().slice(0, 10) || [];

  } catch (err) {
    console.error("数据获取失败:", err);
    error.value = true;
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  changeIndex(NavName.Home);
  fetchData();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.home-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;

  .loading-spinner {
    width: 50px;
    height: 50px;
    border: 5px solid $color-light-grey;
    border-top: 5px solid $color-blue;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 20px;
  }

  p {
    font-size: 18px;
    color: $color-grey;
  }
}

/* 错误状态 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  text-align: center;

  .icon-error {
    font-size: 60px;
    color: #ff4d4f;
    margin-bottom: 20px;
  }

  p {
    font-size: 18px;
    margin-bottom: 20px;
    color: $color-dark;
  }
}

/* 轮播图区域 */
.swiper-section {
  margin-bottom: 40px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 轮播图指示器居中 */
.swiper-container:deep(.el-carousel__indicators) {
  display: flex;
  justify-content: center;
  left: 0;
  right: 0;
  transform: none;
}

.swiper-container {
  width: 100%;

  .carousel-content {
    display: flex;
    align-items: center;
    width: 100%;
    height: 100%;
    padding: 15px;
    box-sizing: border-box;

    &.songList {
      .type-badge {
        background-color: rgba(156,39,176,0.1);
        color: #9c27b0;
      }

      .icon-play {
        background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%239c27b0' d='M8 5v14l11-7z'/%3E%3C/svg%3E") no-repeat center;
      }
    }

    &.singer {
      .type-badge {
        background-color: rgba(33,150,243,0.1);
        color: #2196f3;
      }

      .icon-music {
        background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Cpath fill='%232196f3' d='M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z'/%3E%3C/svg%3E") no-repeat center;
      }
    }

    .carousel-image {
      flex: 0 0 25%; /* 图片占据1/4宽度 */
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 10px;
      box-sizing: border-box;

      .carousel-img {
        width: 100%;
        height: auto;
        max-height: 100%;
        object-fit: contain;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.03);
        }
      }
    }

    .carousel-info {
      flex: 0 0 75%; /* 信息占据3/4宽度 */
      height: 100%;
      padding: 0 20px;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .type-badge {
        align-self: flex-start;
        padding: 5px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-bottom: 15px;
      }

      .info-title {
        font-size: 22px;
        font-weight: 700;
        margin: 0 0 15px 0; /* 增加下边距 */
        color: $color-dark;
        line-height: 1.2;
        padding-top: 5px; /* 下移标题 */
        /* 标题最多显示两行 */
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .info-details {
        display: flex;
        flex-direction: column;
        height: 100%;
      }

      .meta-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px solid #f0f0f0;
      }

      .info-stats {
        display: flex;
        align-items: center;
        font-size: 14px;
        color: $color-grey;
        margin: 0;

        i {
          width: 16px;
          height: 16px;
          margin-right: 6px;
          background-size: contain;
        }
      }

      .creator-info {
        display: flex;
        align-items: center;

        .creator-avatar {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          margin-right: 8px;
          object-fit: cover;
          border: 1px solid #f0f0f0;
          background-color: #f8f9fa; /* 默认背景色 */
        }
      }

      .info-desc {
        flex: 1;
        overflow: hidden;
        width: 95%; /* 减少文本宽度 */
        margin-top: 5px; /* 下移文本 */

        p {
          font-size: 14px;
          line-height: 1.7;
          color: lighten($color-dark, 15%);
          margin: 0;
          text-align: justify;
          word-wrap: break-word;
          overflow-wrap: break-word;
          hyphens: auto;
          max-height: 100%;
          display: -webkit-box;
          -webkit-line-clamp: 5; /* 增加行数到5行 */
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }
  }
}

/* 播放列表区域 */
.playlist-section {
  margin-bottom: 50px;

  .section-header {
    display: flex;
    flex-direction: column; /* 改为垂直布局 */
    align-items: center; /* 居中 */
    text-align: center; /* 文本居中 */
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid $color-light-grey;

    .section-title {
      font-size: 24px;
      font-weight: 700;
      color: $color-dark;
      margin: 0 0 10px 0; /* 添加下边距 */
      position: relative;
      display: inline-block;

      /* 标题装饰线 */
      &:after {
        content: "";
        position: absolute;
        bottom: -8px;
        left: 50%;
        transform: translateX(-50%);
        width: 100px;
        height: 3px;
        background: $color-blue;
        border-radius: 2px;
      }
    }
    .view-more {
      font-size: 14px;
      color: $color-grey;
      text-decoration: none;
      transition: color 0.3s;
      display: flex;
      align-items: center;
      justify-content: center; /* 居中 */

      &:after {
        content: "→";
        margin-left: 5px;
        transition: transform 0.3s;
      }

      &:hover {
        color: $color-blue;

        &:after {
          transform: translateX(3px);
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 992px) {
  .home-container {
    padding: 15px;
  }

  .playlist-section .section-header .section-title {
    font-size: 20px;
  }

  .swiper-container .carousel-content .carousel-info {
    padding: 0 15px;

    .info-title {
      font-size: 20px;
    }
  }
}

@media (max-width: 768px) {
  .swiper-section {
    margin-bottom: 30px;
  }

  .playlist-section {
    margin-bottom: 40px;
  }

  .swiper-container .carousel-content {
    flex-direction: column;
    padding: 10px;

    .carousel-image {
      flex: 0 0 auto;
      height: auto;
      width: 50%;
      max-width: 200px;
      margin: 0 auto 15px;
    }

    .carousel-info {
      flex: 0 0 auto;
      width: 100%;
      padding: 0;
    }

    .info-title {
      font-size: 18px;
      text-align: center;
      padding-top: 0;
    }

    .meta-info {
      justify-content: center;
      gap: 20px;
    }

    .info-desc {
      width: 100%;
      margin-top: 10px;

      p {
        font-size: 13px;
        text-align: center;
        -webkit-line-clamp: 3; /* 移动端减少行数 */
      }
    }
  }
}

@media (max-width: 576px) {
  .home-container {
    padding: 10px;
  }

  .playlist-section .section-header {
    flex-direction: column;
    align-items: flex-start;

    .view-more {
      margin-top: 10px;
    }
  }

  .swiper-container .carousel-content {
    .carousel-image {
      width: 60%;
    }

    .meta-info {
      flex-direction: column;
      align-items: center;
      gap: 10px;
      padding-bottom: 10px;
    }

    .info-stats {
      margin-top: 5px;
    }

    .type-badge {
      align-self: center;
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
