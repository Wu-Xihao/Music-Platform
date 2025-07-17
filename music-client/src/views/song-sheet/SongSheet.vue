<template>
  <div class="play-list-container">
    <!-- 轮播图部分 - 采用Singer风格但保留歌单特性 -->
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
          <div class="carousel-content songList">
            <!-- 左侧图片 -->
            <div class="carousel-image">
              <div class="image-wrapper">

                <div class="hover-overlay"></div>
              </div>
            </div>

            <!-- 右侧介绍 -->
            <div class="carousel-info">
              <div class="type-badge">
                <span>精选歌单</span>
              </div>

              <div class="info-details">
                <h2 class="info-title">{{ item.name }}</h2>
                <div class="meta-info">
                  <div class="creator-info">

                    <span>{{ item.creator || '匿名用户' }}</span>
                  </div>
                </div>
                <div class="info-desc">
                  <p>{{ item.introduction || '这是一个精选歌单，收录了各种风格的音乐作品。' }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 导航栏 - 采用SongSheet风格 -->
    <div class="nav-section">
      <div class="nav-wrapper">
        <yin-nav :styleList="songStyle" :activeName="activeName" @click="handleChangeView" class="custom-nav"></yin-nav>
        <div class="view-count">
          共 <span class="highlight">{{ allPlayList.length }}</span> 个歌单&nbsp;
        </div>
      </div>
    </div>

    <!-- 歌单列表 -->
    <play-list
        :playList="data"
        path="song-sheet-detail"
        class="enhanced-playlist"
    ></play-list>

    <!-- 分页器 -->
    <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="allPlayList.length"
        @current-change="handleCurrentChange"
    ></el-pagination>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { SONGSTYLE } from "@/enums";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    YinNav,
    PlayList,
  },
  setup() {
    const activeName = ref("全部歌单");
    const pageSize = ref(12);
    const currentPage = ref(1);
    const songStyle = ref(SONGSTYLE);
    const allPlayList = ref([]);
    const swiperList = ref([]);
    const windowWidth = ref(window.innerWidth);

    const data = computed(() => allPlayList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value));

    // 轮播图高度响应式计算
    const carouselHeight = computed(() => {
      return windowWidth.value < 768 ? "30vw" : "20vw";
    });

    // 处理窗口大小变化
    function handleResize() {
      windowWidth.value = window.innerWidth;
    }

    // 获取创建者头像
    function getCreatorAvatar(item) {
      if (item.creatorPic) {
        return HttpManager.attachImageUrl(item.creatorPic);
      }
      return "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24'%3E%3Ccircle cx='12' cy='8' r='4' fill='%239C27B0'/%3E%3Cpath fill='%239C27B0' d='M20 19v1a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-1a6 6 0 0 1 6-6h4a6 6 0 0 1 6 6z' opacity='0.3'/%3E%3C/svg%3E";
    }

    // 处理图片加载错误
    function handleImageError(event) {
      event.target.src = '@/assets/images/tubiao.jpg';
      event.target.alt = '默认歌单图片';
    }

    // 获取歌单数据（同时设置轮播图）
    async function getSongList() {
      const result = (await HttpManager.getSongList()) as any;
      allPlayList.value = result.data || [];
      // 设置轮播图数据（取前5个歌单）
      swiperList.value = (result.data?.slice(0, 5) || []).map(item => ({
        ...item,
        type: 'songList'
      }));
      currentPage.value = 1;
    }

    // 通过类别获取歌单（同时设置轮播图）
    async function getSongListOfStyle(style) {
      const result = (await HttpManager.getSongListOfStyle(style)) as any;
      allPlayList.value = result.data || [];
      // 设置轮播图数据（取前5个歌单）
      swiperList.value = (result.data?.slice(0, 5) || []).map(item => ({
        ...item,
        type: 'songList'
      }));
      currentPage.value = 1;
    }

    onMounted(() => {
      getSongList();
      window.addEventListener("resize", handleResize);
    });

    onUnmounted(() => {
      window.removeEventListener("resize", handleResize);
    });

    // 切换歌单分类
    async function handleChangeView(item) {
      activeName.value = item.name;
      allPlayList.value = [];
      swiperList.value = [];
      try {
        if (item.name === "全部歌单") {
          await getSongList();
        } else {
          await getSongListOfStyle(item.name);
        }
      } catch (error) {
        console.error(error);
      }
    }

    function handleCurrentChange(val) {
      currentPage.value = val;
    }

    return {
      activeName,
      songStyle,
      pageSize,
      currentPage,
      allPlayList,
      data,
      swiperList,
      carouselHeight,
      getCreatorAvatar,
      HttpManager,
      handleChangeView,
      handleCurrentChange,
      handleImageError
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.play-list-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 40px;
  background-color: #f8f9fa;
  min-height: calc(100vh - 60px);
}

/* 轮播图区域 - 采用Singer风格但保留歌单特性 */
.swiper-section {
  margin-bottom: 40px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  }
}

.swiper-container {
  width: 100%;
  border-radius: 16px;

  &:deep(.el-carousel__indicators) {
    display: flex;
    justify-content: center;
    left: 0;
    right: 0;
    transform: none;
    bottom: 20px;

    .el-carousel__indicator {
      padding: 8px 5px;

      .el-carousel__button {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        background-color: rgba(255, 255, 255, 0.6);
        transition: all 0.3s ease;
      }

      &.is-active .el-carousel__button {
        width: 24px;
        border-radius: 8px;
        background-color: $color-primary;
      }
    }
  }

  &:deep(.el-carousel__arrow) {
    background-color: rgba(255, 255, 255, 0.8);
    color: $color-dark;
    width: 50px;
    height: 50px;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;

    &:hover {
      background-color: white;
      transform: scale(1.1);
    }
  }

  .carousel-content {
    display: flex;
    align-items: center;
    width: 100%;
    height: 100%;
    padding: 25px 40px;
    box-sizing: border-box;
    background: linear-gradient(135deg, rgba(255,255,255,0.95) 0%, rgba(245,245,245,0.98) 100%);

    &.songList {
      .type-badge {
        background-color: rgba(156, 39, 176, 0.12);
        color: #9c27b0;
      }
    }

    .carousel-image {
      flex: 0 0 25%;
      height: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 15px;
      box-sizing: border-box;

      .image-wrapper {
        position: relative;
        width: 100%;
        height: 0;
        padding-bottom: 100%; /* 保持正方形 */
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);

          .hover-overlay {
            opacity: 1;
          }
        }

        .hover-overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, rgba($color-primary, 0.4) 0%, rgba($color-primary, 0.1) 100%);
          opacity: 0;
          transition: opacity 0.3s ease;
        }
      }

      .carousel-img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        background-color: #f0f2f5;
      }
    }

    .carousel-info {
      flex: 0 0 75%;
      height: 100%;
      padding: 0 30px;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .type-badge {
        align-self: flex-start;
        padding: 7px 18px;
        border-radius: 30px;
        font-size: 14px;
        font-weight: 600;
        letter-spacing: 1px;
        margin-bottom: 20px;
      }

      .info-title {
        font-size: 36px;
        font-weight: 800;
        margin: 0 0 15px 0;
        color: $color-dark;
        line-height: 1.2;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
      }

      .meta-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        padding-bottom: 15px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      }

      .creator-info {
        display: flex;
        align-items: center;

        .creator-avatar {
          width: 36px;
          height: 36px;
          border-radius: 50%;
          margin-right: 12px;
          object-fit: cover;
          border: 2px solid white;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: all 0.3s ease;

          &:hover {
            transform: scale(1.1);
          }
        }

        span {
          font-weight: 600;
          color: lighten($color-dark, 10%);
        }
      }

      .info-desc {
        flex: 1;
        overflow: hidden;
        width: 95%;
        margin-top: 5px;

        p {
          font-size: 16px;
          line-height: 1.8;
          color: lighten($color-dark, 15%);
          margin: 0;
          text-align: justify;
          max-height: 100%;
          display: -webkit-box;
          -webkit-line-clamp: 5;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }
      }
    }
  }
}

.view-count {
  white-space: nowrap;
  display: inline-flex; /* 或 inline-block */
  align-items: center;
}
/* 导航区域 - 采用SongSheet风格 */
.nav-section {
  margin: 30px 0;

  .nav-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  }

  .view-count {
    font-size: 15px;
    color: lighten($color-dark, 25%);

    .highlight {
      color: $color-primary;
      font-weight: 700;
    }
  }
}

.custom-nav {
  margin: 0;
}

/* 歌单列表优化 */
.enhanced-playlist {
  margin-top: 20px;

  &:deep(.play-list) {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 25px;
    padding: 0 10px;

    .play-item {
      transition: all 0.3s ease;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
      }

      .img-wrap {
        border-radius: 12px 12px 0 0;
        overflow: hidden;

        img {
          transition: transform 0.5s ease;
        }

        &:hover img {
          transform: scale(1.05);
        }
      }

      .name {
        font-weight: 600;
        padding: 12px 15px;
        color: $color-dark;
      }
    }
  }
}

/* 分页器优化 */
.pagination {
  display: flex;
  justify-content: center;
  margin: 40px 0 20px;

  &:deep(.el-pagination.is-background .el-pager li) {
    border-radius: 8px;
    margin: 0 5px;
    font-weight: 600;
    transition: all 0.3s ease;

    &:not(.is-active):hover {
      color: $color-primary;
      transform: translateY(-2px);
    }

    &.is-active {
      background-color: $color-primary;
      box-shadow: 0 4px 12px rgba(156, 39, 176, 0.3);
    }
  }

  &:deep(.el-pagination.is-background .btn-prev),
  &:deep(.el-pagination.is-background .btn-next) {
    border-radius: 8px;
    padding: 0 15px;
    transition: all 0.3s ease;

    &:hover {
      color: $color-primary;
      transform: translateY(-2px);
    }
  }
}

/* 响应式设计优化 */
@media (max-width: 1200px) {
  .play-list-container {
    padding: 25px 30px;
  }

  .swiper-container .carousel-content {
    padding: 20px;

    .carousel-info {
      padding: 0 20px;

      .info-title {
        font-size: 28px;
      }
    }
  }
}

@media (max-width: 992px) {
  .play-list-container {
    padding: 20px;
  }

  .swiper-container .carousel-content {
    .carousel-image {
      flex: 0 0 30%;
    }

    .carousel-info {
      flex: 0 0 70%;

      .info-title {
        font-size: 24px;
      }

      .info-desc p {
        font-size: 14px;
      }
    }
  }

  .enhanced-playlist:deep(.play-list) {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .swiper-section {
    margin-bottom: 30px;
    border-radius: 12px;
  }

  .swiper-container .carousel-content {
    flex-direction: column;
    padding: 15px;
    text-align: center;

    .carousel-image {
      flex: 0 0 auto;
      width: 60%;
      max-width: 300px;
      margin: 0 auto 20px;
      padding: 0;
    }

    .carousel-info {
      flex: 0 0 auto;
      width: 100%;
      padding: 0;

      .type-badge {
        align-self: center;
      }

      .info-title {
        text-align: center;
        font-size: 22px;
      }

      .meta-info {
        flex-direction: column;
        gap: 15px;
        padding-bottom: 15px;
      }

      .info-desc p {
        text-align: center;
        -webkit-line-clamp: 3;
      }
    }
  }

  .nav-section .nav-wrapper {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;

    .view-count {
      align-self: flex-end;
    }
  }

  .enhanced-playlist:deep(.play-list) {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 15px;
  }
}

@media (max-width: 576px) {
  .play-list-container {
    padding: 15px;
  }

  .swiper-container .carousel-content {
    .carousel-image {
      width: 70%;
    }

    .carousel-info {
      .info-title {
        font-size: 20px;
      }

      .creator-info span {
        font-size: 14px;
      }
    }
  }

  .enhanced-playlist:deep(.play-list) {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 12px;
  }

  .pagination {
    margin: 30px 0 15px;
  }
}
</style>
