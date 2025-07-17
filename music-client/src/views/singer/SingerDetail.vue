<template>
  <el-container class="singer-container">
    <el-aside class="album-slide">
      <div class="singer-card">
        <el-image class="singer-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
        <div class="album-info">
          <h2 class="info-title">基本资料</h2>
          <ul class="info-list">
            <li v-if="songDetails.sex !== 2" class="info-item">
              <span class="info-label">性别：</span>
              <span>{{ getUserSex(songDetails.sex) }}</span>
            </li>
            <li class="info-item">
              <span class="info-label">生日：</span>
              <span>{{ getBirth(songDetails.birth) }}</span>
            </li>
            <li class="info-item">
              <span class="info-label">故乡：</span>
              <span>{{ songDetails.location }}</span>
            </li>
          </ul>
        </div>
      </div>
    </el-aside>
    <el-main class="album-main">
      <div class="singer-header">
        <h1 class="singer-name">{{ songDetails.name }}</h1>
        <p class="singer-intro">{{ songDetails.introduction }}</p>
      </div>
      <div class="song-list-container">
        <song-list :songList="currentSongList"></song-list>
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import { HttpManager } from "@/api";
import { getBirth } from "@/utils";

export default defineComponent({
  components: {
    SongList,
  },
  setup() {
    const store = useStore();
    const { getUserSex } = mixin();

    const currentSongList = ref([]);
    const songDetails = computed(() => store.getters.songDetails) as any;

    onMounted(async () => {
      try {
        const result = (await HttpManager.getSongOfSingerId(songDetails.value.id)) as ResponseBody;
        currentSongList.value = result.data;
      } catch (error) {
        console.error(error);
      }
    });

    return {
      songDetails,
      currentSongList,
      attachImageUrl: HttpManager.attachImageUrl,
      getBirth,
      getUserSex,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.singer-container {
  background-color: #f8f9fa;
  min-height: 100vh;
}

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

  .singer-card {
    width: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .singer-img {
      height: 250px;
      width: 250px;
      border-radius: 10%;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.03);
      }
    }

    .album-info {
      width: 100%;
      padding-top: 2rem;

      .info-title {
        color: #333;
        font-size: 1.2rem;
        margin-bottom: 1rem;
        padding-bottom: 0.5rem;
        border-bottom: 1px solid #eee;
      }

      .info-list {
        list-style: none;
        padding: 0;

        .info-item {
          display: flex;
          align-items: center;
          height: 40px;
          line-height: 40px;
          color: #555;
          font-size: 0.95rem;

          .info-label {
            font-weight: 500;
            color: #333;
            margin-right: 8px;
          }
        }
      }
    }
  }
}

.album-main {
  padding: 40px;
  background-color: transparent;

  .singer-header {
    margin-bottom: 30px;

    .singer-name {
      color: #333;
      font-size: 2rem;
      font-weight: 600;
      margin-bottom: 1rem;
    }

    .singer-intro {
      color: #666;
      font-size: 0.95rem;
      line-height: 1.6;
      margin: 0;
    }
  }

  .song-list-container {
    background-color: white;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  }
}

@media screen and (min-width: $sm) {
  .album-slide {
    position: fixed;
    width: 350px;
    height: 100vh;
    overflow-y: auto;
  }

  .album-main {
    min-width: 600px;
    padding-right: 10vw;
    margin-left: 350px;
  }
}

@media screen and (max-width: $sm) {
  .album-slide {
    display: none;
  }

  .album-main {
    padding: 20px;

    .singer-header {
      .singer-name {
        font-size: 1.5rem;
      }
    }
  }
}
</style>
