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

        <!-- 评分区域 -->
        <div class="album-score">
          <div class="score-card">
            <h3>歌单评分</h3>
            <el-rate
                v-model="averageRank"
                allow-half
                disabled
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            ></el-rate>
            <div class="score-value">{{ (averageRank * 2).toFixed(1) }}</div>
          </div>

          <div class="score-card user-score" :class="{ 'rated': hasRated }">
            <h3>{{ assistText }}</h3>
            <el-rate
                v-model="userScore"
                allow-half
                :disabled="hasRated || !nowUserId"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
                @change="handleRatingChange"
            ></el-rate>
            <div class="score-value">{{ (userScore * 2).toFixed(1) }}</div>
            <div class="score-hint" v-if="!hasRated && nowUserId">
              点击星星进行评分（当前: {{ userScore }} 星）
            </div>
            <div class="score-hint" v-if="!nowUserId">
              <el-link type="primary" @click="handleLogin">登录</el-link>后可评价歌单
            </div>
          </div>
        </div>

        <!-- 歌曲列表 -->
        <div class="album-section">
          <h2 class="section-title">包含歌曲</h2>
          <song-list class="album-body" :songList="currentSongList"></song-list>
        </div>

        <!-- 评论区域 -->
        <div class="album-section">
          <h2 class="section-title">评论</h2>
          <comment :playId="songListId" :type="1"></comment>
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script lang="ts">
import { defineComponent, ref, computed, getCurrentInstance, onMounted, watch } from "vue";
import { useStore } from "vuex";
import { useRoute, useRouter } from "vue-router";
import mixin from "@/mixins/mixin";
import SongList from "@/components/SongList.vue";
import Comment from "@/components/Comment.vue";
import { HttpManager } from "@/api";
import { ElMessage } from "element-plus";

interface Song {
  id: string;
  // 其他歌曲属性...
}

interface ResponseBody<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  type?: string;
}

export default defineComponent({
  components: {
    SongList,
    Comment,
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    const route = useRoute(); // 添加路由参数获取
    const { checkStatus } = mixin();

    // 响应式数据
    const currentSongList = ref<Song[]>([]);
    const nowSongListId = ref(route.params.id as string || ""); // 从路由参数获取歌单ID
    const averageRank = ref(0);
    const userScore = ref(0);
    const hasRated = ref(false);
    const assistText = ref("评价");
    const loading = ref(false);

    const songDetails = computed(() => store.getters.songDetails);
    const nowUserId = computed(() => store.getters.userId);

    // 获取歌单歌曲
    async function getSongId(id: string) {
      try {
        const result = await HttpManager.getListSongOfSongId(id) as ResponseBody<{songId: string}[]>;
        const songs = await Promise.all(
            result.data!.map(async (item) => {
              const song = await HttpManager.getSongOfId(item.songId) as ResponseBody<Song[]>;
              return song.data?.[0] || null;
            })
        );
        currentSongList.value = songs.filter((song): song is Song => song !== null);
      } catch (error) {
        console.error("获取歌曲失败:", error);
        ElMessage.error("获取歌曲列表失败");
      }
    }

    // 获取平均评分
    async function getRankData() {
      try {
        const result = await HttpManager.getRankOfSongListId(nowSongListId.value) as ResponseBody<number>;
        averageRank.value = result.data ? result.data / 2 : 0;
      } catch (error) {
        console.error("获取评分失败:", error);
        averageRank.value = 0;
      }
    }

    // 获取用户评分 - 修复持久化问题
    async function getUserRank() {
      // 未登录用户处理
      if (!nowUserId.value) {
        hasRated.value = false;
        assistText.value = "登录后可评价";
        userScore.value = 0;
        return;
      }

      try {
        const result = await HttpManager.getUserRank(nowUserId.value, nowSongListId.value) as ResponseBody<number>;

        // 检查是否已评价（分数大于0）
        if (result.data !== null && result.data !== undefined && result.data > 0) {
          userScore.value = result.data / 2;
          hasRated.value = true;
          assistText.value = "已评价";
        } else {
          userScore.value = 0;
          hasRated.value = false;
          assistText.value = "评价";
        }
      } catch (error) {
        console.error("获取用户评分失败:", error);
        hasRated.value = false;
        assistText.value = "评价";
        userScore.value = 0;
      }
    }

    // 处理评分变化
    const handleRatingChange = async (value: number) => {
      // 忽略0星的选择
      if (value === 0) return;

      // 检查状态
      if (loading.value || hasRated.value || !nowUserId.value) return;

      try {
        loading.value = true;
        const result = await HttpManager.setRank({
          songListId: nowSongListId.value,
          consumerId: nowUserId.value,
          score: value * 2
        }) as ResponseBody;

        if (result.success) {
          userScore.value = value;
          hasRated.value = true;
          assistText.value = "已评价";

          // 更新平均评分
          await getRankData();

          ElMessage.success("评分成功");
        } else {
          ElMessage.warning(result.message || "评分失败");
          // 失败后重新获取用户评分状态
          await getUserRank();
        }
      } catch (error) {
        console.error("评分失败:", error);
        ElMessage.error("评分提交失败");
        // 出错后重新获取用户评分状态
        await getUserRank();
      } finally {
        loading.value = false;
      }
    };

    // 处理登录跳转
    const handleLogin = () => {
      router.push("/login");
    };

    // 初始化加载数据
    onMounted(async () => {
      // 确保歌单ID存在
      if (!nowSongListId.value) {
        ElMessage.error("未获取到歌单ID");
        return;
      }

      // 并行获取歌曲列表和平均评分
      await Promise.all([
        getSongId(nowSongListId.value),
        getRankData()
      ]);

      // 获取用户评分状态
      await getUserRank();
    });

    // 添加路由监听 - 修复刷新问题
    watch(() => route.params.id, async (newId) => {
      if (newId) {
        nowSongListId.value = newId as string;

        // 重新加载数据
        await Promise.all([
          getSongId(nowSongListId.value),
          getRankData(),
          getUserRank()
        ]);
      }
    });

    return {
      songDetails,
      currentSongList,
      songListId: nowSongListId,
      nowUserId,
      averageRank,
      userScore,
      hasRated,
      assistText,
      attachImageUrl: HttpManager.attachImageUrl,
      handleRatingChange,
      handleLogin
    };
  },
});
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
@import "@/assets/css/var.scss";

.album-container {
  background-color: var(--el-bg-color);
  min-height: 100vh;
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

/* 评分区域样式 */
.album-score {
  display: flex;
  justify-content: space-around;
  align-items: stretch;
  margin: 2rem 0;
  gap: 2rem;

  .score-card {
    flex: 1;
    padding: 1.5rem;
    background-color: var(--el-bg-color-page);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    text-align: center;
    transition: all 0.3s ease;
    display: flex;
    flex-direction: column;
    align-items: center;

    h3 {
      margin: 0 0 1rem;
      font-size: 1.1rem;
      color: var(--el-text-color-primary);
    }

    .el-rate {
      margin: 0.5rem 0;
      font-size: 1.5rem;

      :deep(.el-rate__icon) {
        margin-right: 2px;
      }
    }

    .score-value {
      font-size: 1.8rem;
      font-weight: 600;
      color: var(--el-color-primary);
      margin: 0.5rem 0;
    }

    .score-hint {
      font-size: 0.8rem;
      color: var(--el-text-color-secondary);
      margin-top: 0.5rem;
    }
  }

  .user-score {
    border: 1px solid var(--el-color-primary-light-5);
    background-color: rgba(var(--el-color-primary-rgb), 0.03);

    &.rated {
      border-color: var(--el-color-success-light-5);
      background-color: rgba(var(--el-color-success-rgb), 0.03);
    }
  }
}

/* 响应式设计 */
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