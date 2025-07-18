<template>
  <div class="play-list-container">
    <!-- 轮播图部分 - 采用Singer风格 -->
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
          <div class="carousel-content singer">
            <!-- 左侧图片 -->
            <div class="carousel-image">
              <div class="image-wrapper">
                <div class="carousel-image">
                  <img
                      :src="HttpManager.attachImageUrl(item.pic)"
                      :alt="item.name"
                      loading="lazy"
                      class="carousel-img"
                  />
                </div>
                <div class="hover-overlay"></div>
              </div>
            </div>

            <!-- 右侧介绍 -->
            <div class="carousel-info">
              <div class="type-badge">
                <span>热门歌手</span>
              </div>

              <div class="info-details">
                <h2 class="info-title">{{ item.name || '知名歌手' }}</h2>

                <div class="info-desc">
                  <p>{{ item.introduction || '这位歌手拥有独特的嗓音和出色的演唱技巧，深受广大乐迷喜爱。' }}</p>
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
        <yin-nav :styleList="singerStyle" :activeName="activeName" @click="handleChangeView" class="custom-nav"></yin-nav>
        <div class="view-count">
          共&nbsp;<span class="highlight">{{ allPlayList.length }}</span>&nbsp;位歌手&nbsp;
        </div>
      </div>
    </div>

    <!-- 歌手列表 -->
    <div class="play-list-wrapper">
      <play-list :playList="data" path="singer-detail" class="enhanced-playlist"></play-list>
    </div>

    <!-- 分页器 -->
    <div class="pagination-wrapper">
      <el-pagination
          class="pagination"
          background
          layout="total, prev, pager, next, jumper"
          :current-page="currentPage"
          :page-size="pageSize"
          :total="allPlayList.length"
          @current-change="handleCurrentChange"
      ></el-pagination>
    </div>

    <!-- 空状态提示 -->
    <div v-if="!allPlayList.length" class="empty-state">
      <i class="icon-music-empty"></i>
      <p>暂无歌手数据</p>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import YinNav from "@/components/layouts/YinNav.vue";
import PlayList from "@/components/PlayList.vue";
import { singerStyle } from "@/enums";
import { HttpManager } from "@/api";

// data
const activeName = ref("全部歌手");
const pageSize = ref(12); // 页数
const currentPage = ref(1); // 当前页
const allPlayList = ref([]); // 使用空数组初始化

// 轮播图相关数据
const swiperList = ref([]); // 使用空数组初始化
const windowWidth = ref(window.innerWidth);

// 计算轮播图高度（响应式）
const carouselHeight = computed(() => {
  return windowWidth.value < 768 ? "30vw" : "20vw";
});

// computed
const data = computed(() => {
  return allPlayList.value.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value);
});

// 窗口大小变化处理函数
function handleResize() {
  windowWidth.value = window.innerWidth;
}

// 获取所有歌手
async function getAllSinger() {
  try {
    const result = (await HttpManager.getAllSinger()) as any;
    if (result && result.data) {
      currentPage.value = 1;
      allPlayList.value = result.data || [];

      // 设置轮播图数据（取前5个歌手）
      swiperList.value = result.data.slice(0, 5) || [];
    }
  } catch (error) {
    console.error("获取歌手数据失败:", error);
    allPlayList.value = [];
    swiperList.value = [];
  }
}

// 初始化
getAllSinger();

// 获取当前页
function handleCurrentChange(val) {
  currentPage.value = val;
}

function handleChangeView(item) {
  activeName.value = item.name;
  allPlayList.value = [];
  swiperList.value = [];

  if (item.name === "全部歌手") {
    getAllSinger();
  } else {
    getSingerSex(item.type);
  }
}

// 通过性别对歌手分类
async function getSingerSex(sex) {
  try {
    const result = (await HttpManager.getSingerOfSex(sex)) as any;
    if (result && result.data) {
      currentPage.value = 1;
      allPlayList.value = result.data || [];

      // 更新轮播图数据（取前5个歌手）
      swiperList.value = result.data.slice(0, 5) || [];
    }
  } catch (error) {
    console.error("按性别获取歌手失败:", error);
    allPlayList.value = [];
    swiperList.value = [];
  }
}

// 处理图片加载错误
function handleImageError(event) {
  event.target.src = '@/assets/images/tubiao.jpg';
  event.target.alt = '默认歌手图片';
}

// 生命周期钩子
onMounted(() => {
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
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

/* 轮播图区域 - Singer风格 */
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
        background-color: $color-theme;
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
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);

    &.singer {
      .type-badge {
        background: linear-gradient(135deg, $color-theme 0%, lighten($color-theme, 15%) 100%);
        color: white;
        box-shadow: 0 4px 10px rgba($color-theme, 0.2);
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
          background: linear-gradient(135deg, rgba($color-theme, 0.4) 0%, rgba($color-theme, 0.1) 100%);
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
      color: $color-theme;
      font-weight: 700;
    }
  }
}

.custom-nav {
  margin: 0;
}

/* 歌手列表容器 */
.play-list-wrapper {
  margin: 20px 0 40px;
}

.enhanced-playlist:deep(.play-list) {
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

/* 分页器 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin: 40px 0 20px;

  .pagination:deep(.el-pagination.is-background .el-pager li) {
    border-radius: 8px;
    margin: 0 5px;
    font-weight: 600;
    transition: all 0.3s ease;

    &:not(.is-active):hover {
      color: $color-theme;
      transform: translateY(-2px);
    }

    &.is-active {
      background-color: $color-theme;
      box-shadow: 0 4px 12px rgba($color-theme, 0.3);
    }
  }

  .pagination:deep(.el-pagination.is-background .btn-prev),
  .pagination:deep(.el-pagination.is-background .btn-next) {
    border-radius: 8px;
    padding: 0 15px;
    transition: all 0.3s ease;

    &:hover {
      color: $color-theme;
      transform: translateY(-2px);
    }
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 20px;
  text-align: center;
  color: #999;

  .icon-music-empty {
    font-size: 80px;
    margin-bottom: 20px;
    opacity: 0.3;
  }

  p {
    font-size: 18px;
    margin-top: 10px;
  }
}
.view-count {
  white-space: nowrap;
  display: inline-flex; /* 或 inline-block */
  align-items: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .play-list-container {
    padding: 25px 30px;
  }

  .swiper-container .carousel-content {
    padding: 20px;

    .info-title {
      font-size: 28px;
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
      padding: 0 20px;

      .info-title {
        font-size: 24px;
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
  }

  .enhanced-playlist:deep(.play-list) {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: 12px;
  }

  .pagination-wrapper {
    margin: 30px 0 15px;
  }
}
</style>