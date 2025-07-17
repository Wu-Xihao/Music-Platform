<template>
  <el-container class="album-container">
    <el-aside class="album-slide">
      <el-image class="album-img" fit="contain" :src="attachImageUrl(songDetails.pic)" />
      <h3 class="album-info">{{ songDetails.title }}</h3>
      <div class="album-meta">
        <p v-if="songDetails.introduction" class="album-intro">{{ songDetails.introduction }}</p>
      </div>
    </el-aside>
    <el-main class="album-main">
      <div class="album-content">
        <div class="album-header">
          <h1 class="album-title">歌单详情</h1>
          <div class="divider"></div>
        </div>

        <!--评分-->
        <div class="album-score">
          <div class="score-card">
            <h3>歌单评分</h3>
            <el-rate v-model="rank" allow-half disabled></el-rate>
            <div class="score-value">{{ (rank * 2).toFixed(1) }}</div>
          </div>
          <div class="score-card user-score">
            <h3>{{ assistText }}</h3>
            <el-rate allow-half v-model="score" :disabled="disabledRank" @click="pushValue()"></el-rate>
            <div class="score-value">{{ (score * 2).toFixed(1) }}</div>
          </div>
        </div>

        <!--歌曲-->
        <div class="album-section">
          <h2 class="section-title">包含歌曲</h2>
          <song-list class="album-body" :songList="currentSongList"></song-list>
        </div>

        <div class="album-section">
          <h2 class="section-title">评论</h2>
          <comment :playId="songListId" :type="1"></comment>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, getCurrentInstance } from "vue";
import { useStore } from "vuex";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import Comment from "@/components/Comment.vue";
import { HttpManager } from "@/api";

export default defineComponent({
  components: {
    SongList,
    Comment,
  },
  setup() {
    const { proxy } = getCurrentInstance();
    const store = useStore();
    const { checkStatus } = mixin();

    const currentSongList = ref([]); // 存放的音乐
    const nowSongListId = ref(""); // 歌单 ID
    const nowScore = ref(0);
    const nowRank = ref(0);
    const disabledRank = ref(false);
    const assistText = ref("评价");
    const songDetails = computed(() => store.getters.songDetails); // 单个歌单信息
    const nowUserId = computed(() => store.getters.userId);

    nowSongListId.value = songDetails.value.id; // 给歌单ID赋值

    // 收集歌单里面的歌曲
    async function getSongId(id) {
      const result = (await HttpManager.getListSongOfSongId(id)) as ResponseBody;
      // 获取歌单里的歌曲信息
      for (const item of result.data) {
        // 获取单里的歌曲
        const resultSong = (await HttpManager.getSongOfId(item.songId)) as ResponseBody;
        currentSongList.value.push(resultSong.data[0]);
      }
    }
    // 获取评分
    async function getRank(id) {
      const result = (await HttpManager.getRankOfSongListId(id)) as ResponseBody;
      nowRank.value = result.data / 2;
    }
    async function getUserRank(userId, songListId) {
      const result = (await HttpManager.getUserRank(userId, songListId)) as ResponseBody;
      nowScore.value = result.data / 2;
      disabledRank.value = true;
      assistText.value = "已评价";
    }
    // 提交评分
    async function pushValue() {
      if (disabledRank.value || !checkStatus()) return;

      const songListId = nowSongListId.value;
      var consumerId = nowUserId.value;
      const score = nowScore.value*2;
      try {
        const result = (await HttpManager.setRank({songListId,consumerId,score})) as ResponseBody;
        (proxy as any).$message({
          message: result.message,
          type: result.type,
        });

        if (result.success) {
          getRank(nowSongListId.value);
          disabledRank.value = true;
          assistText.value = "已评价";
        }
      } catch (error) {
        console.error(error);
      }
    }

    getUserRank(nowUserId.value, nowSongListId.value);
    getRank(nowSongListId.value); // 获取评分
    getSongId(nowSongListId.value); // 获取歌单里面的歌曲ID

    return {
      songDetails,
      rank: nowRank,
      score: nowScore,
      disabledRank,
      assistText,
      currentSongList,
      songListId: nowSongListId,
      attachImageUrl: HttpManager.attachImageUrl,
      pushValue,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.album-container {
  background-color: var(--el-bg-color);
  min-height: 100vh;
  transition: all 0.3s ease;
}

.album-slide {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem 1rem;
  background-color: var(--el-bg-color-page);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border-right: 1px solid var(--el-border-color-light);

  .album-img {
    height: 250px;
    width: 250px;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.03);
    }
  }

  .album-info {
    width: 100%;
    padding: 1.5rem 0 0.5rem;
    text-align: center;
    font-size: 1.5rem;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  .album-meta {
    width: 100%;
    padding: 1rem;
    text-align: center;
  }

  .album-intro {
    color: var(--el-text-color-secondary);
    font-size: 0.95rem;
    line-height: 1.6;
    margin-top: 1rem;
    padding: 0 1rem;
  }
}

.album-main {
  padding: 2rem;
  background-color: var(--el-bg-color);

  .album-content {
    max-width: 900px;
    margin: 0 auto;
  }

  .album-header {
    margin-bottom: 2rem;

    .album-title {
      font-size: 1.8rem;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 0.5rem;
    }

    .divider {
      height: 2px;
      background: linear-gradient(to right, var(--el-color-primary), transparent);
      width: 100px;
      margin: 0.5rem 0 1.5rem;
    }
  }

  .album-section {
    margin-bottom: 2.5rem;

    .section-title {
      font-size: 1.3rem;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin-bottom: 1rem;
      padding-bottom: 0.5rem;
      border-bottom: 1px solid var(--el-border-color-light);
    }
  }
}

/*歌单打分*/
.album-score {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin: 2rem 0;
  gap: 2rem;

  .score-card {
    flex: 1;
    padding: 1.5rem;
    background-color: var(--el-bg-color-page);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    text-align: center;
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    h3 {
      margin: 0 0 1rem;
      font-size: 1.1rem;
      color: var(--el-text-color-primary);
    }

    .el-rate {
      margin-bottom: 0.5rem;
    }

    .score-value {
      font-size: 1.8rem;
      font-weight: 600;
      color: var(--el-color-primary);
      margin-top: 0.5rem;
    }
  }

  .user-score {
    border: 1px solid var(--el-color-primary-light-5);
    background-color: rgba(var(--el-color-primary-rgb), 0.05);
  }
}

.album-body {
  margin: 1rem 0;
}

@media screen and (min-width: $sm) {
  .album-slide {
    position: fixed;
    width: 320px;
    height: 100vh;
    overflow-y: auto;
  }

  .album-main {
    min-width: 600px;
    margin-left: 320px;
    padding: 3rem;
  }
}

@media screen and (max-width: $sm) {
  .album-slide {
    display: none;
  }

  .album-main {
    padding: 1.5rem;
  }

  .album-score {
    flex-direction: column;
    gap: 1.5rem;

    .score-card {
      width: 100%;
    }
  }
}
</style>
