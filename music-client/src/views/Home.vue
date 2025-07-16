<template>
  <div class="main-container">
    <!-- 轮播图 -->
    <el-carousel
        v-if="swiperList.length"
        class="swiper-container"
        type="card"
        height="22vw"
        :interval="4000"
        indicator-position="outside"
    >
      <el-carousel-item
          v-for="(item, index) in swiperList"
          :key="item.id || index"
          class="carousel-item"
      >
        <div class="carousel-img-wrapper">
          <img
              :src="HttpManager.attachImageUrl(item.pic)"
              alt="轮播图"
              class="carousel-img"
          />
          <div class="carousel-mask"></div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 热门歌单 -->
    <div class="section">
      <div class="section-header">
        <h2>热门歌单</h2>
        <span class="more" @click="goToPage('/song-sheet')">查看更多 &gt;</span>
      </div>
      <div class="card-grid">
        <div
            class="song-card"
            v-for="item in validSongList"
        :key="item.id"
        @click="goDetail('song-sheet-detail', item)"
        >
          <img :src="HttpManager.attachImageUrl(item.pic)" alt="cover" />
          <div class="title">{{ item.name || item.title }}</div>
        </div>
      </div>
    </div>

    <!-- 推荐歌手 -->
    <div class="section">
      <div class="section-header">
        <h2>推荐歌手</h2>
        <span class="more" @click="goToPage('/singer')">查看更多 &gt;</span>
      </div>
      <div class="card-grid">
        <div
            class="singer-card"
            v-for="item in validSingerList"
            :key="item.id"
            @click="goDetail('singer-detail', item)"
        >
          <img :src="HttpManager.attachImageUrl(item.pic)" alt="avatar" />
          <div class="name">{{ item.name }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import { NavName } from "@/enums";
import { HttpManager } from "@/api";
import mixin from "@/mixins/mixin";

const router = useRouter();
const { proxy } = getCurrentInstance();
const { routerManager, changeIndex } = mixin();

const songList = ref([]);
const singerList = ref([]);
const swiperList = ref([]);
const validSongList = ref([]);
const validSingerList = ref([]);

function goToPage(path: string) {
  router.push(path);
}

function goDetail(routeName: string, item: any) {
  // 严格校验数据有效性
  if (!item || !item.id) {
    console.error("无效数据，无法跳转", item);
    return;
  }

  // 保存数据到store（与PlayList组件一致）
  proxy.$store.commit("setSongDetails", item);

  // 使用相同的routerManager方法跳转
  routerManager(routeName, { path: `/${routeName}/${item.id}` });
}

onMounted(() => {
  changeIndex(NavName.Home);

  // 获取轮播图数据
  HttpManager.getBannerList().then((res) => {
    const data = (res as any).data || [];
    swiperList.value = data.filter(item => item && item.id);
  });

  // 获取歌单数据并过滤无效项
  HttpManager.getSongList().then((res) => {
    const data = (res as any).data || [];
    validSongList.value = data
        .filter(item => item && item.id) // 过滤有效项
        .sort()
        .slice(0, 14);
  });

  // 获取歌手数据并过滤无效项
  HttpManager.getAllSinger().then((res) => {
    const data = (res as any).data || [];
    validSingerList.value = data
        .filter(item => item && item.id) // 过滤有效项
        .sort()
        .slice(0, 14);
  });
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.main-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  background-color: $color-bg;
}

.swiper-container {
  width: 100%;
  margin: 25px auto 40px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);
  }
}

.carousel-item {
  overflow: hidden;
  border-radius: 8px;
}

.carousel-img-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.8s ease;

  &:hover {
    transform: scale(1.03);
  }
}

.carousel-mask {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 40%;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.5));
  pointer-events: none;
}

.swiper-container:deep(.el-carousel__indicators--outside) {
  bottom: -30px;
  display: flex;
  justify-content: center;

  .el-carousel__indicator {
    width: 10px;
    height: 10px;
    margin: 0 6px;
    border-radius: 50%;
    background-color: rgba(255, 255, 255, 0.5);
    transition: all 0.3s ease;

    &.is-active {
      width: 30px;
      border-radius: 5px;
      background-color: $color-primary;
    }
  }
}

.section {
  margin-bottom: 60px;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 15px 0 25px;

    h2 {
      font-size: 24px;
      font-weight: 600;
      color: $color-text-primary;
      border-left: 4px solid $color-primary;
      padding-left: 10px;
    }

    .more {
      font-size: 14px;
      color: $color-primary;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: flex-start;
}

.song-card {
  width: 180px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fff;
  text-align: center;

  &:hover {
    transform: translateY(-4px) scale(1.03);
  }

  img {
    width: 100%;
    height: 180px;
    object-fit: cover;
    display: block;
  }

  .title {
    padding: 10px;
    font-size: 15px;
    font-weight: 500;
    color: $color-text-primary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.singer-card {
  width: 160px;
  height: 200px;
  padding: 20px 10px;
  background: #fff;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px) scale(1.03);
    border: 1px solid $color-primary;
  }

  img {
    width: 150px;
    height: 150px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 12px;
    border: 2px solid transparent;
  }

  .name {
    font-size: 14px;
    font-weight: 500;
    color: $color-text-primary;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>