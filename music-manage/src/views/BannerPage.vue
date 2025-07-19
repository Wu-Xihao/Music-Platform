<template>
  <div class="container">
    <div class="handle-box">
      <el-button type="danger" @click="deleteAll">批量删除</el-button>
      <!-- 添加轮播图按钮 - 改进上传组件 -->
      <el-upload
          action="http://localhost:8888/banner/addBanner"
          :show-file-list="false"
          :on-success="handleAddSuccess"
          :on-error="handleUploadError"
          :on-progress="handleUploadProgress"
          :before-upload="beforeUpload"
          :headers="uploadHeaders"
          :data="uploadData"
          multiple
      >
        <el-button type="primary">添加轮播图</el-button>
      </el-upload>
    </div>

    <el-table
        height="600px"
        border
        size="small"
        :data="tableData"
        @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="40" align="center"></el-table-column>
      <el-table-column label="ID" prop="id" width="80" align="center"></el-table-column>

      <!-- 改进图片预览列 -->
      <el-table-column label="图片预览" width="160" align="center">
        <template v-slot="scope">
          <div class="preview-container">
            <img
                :src="attachImageUrl(scope.row.pic)"
                alt="轮播图预览"
                class="preview-img"
                @error="handleImageError(scope.row)"
            />
            <!-- 图片加载失败提示 -->
            <div v-if="imageLoadErrors.includes(scope.row.id)" class="error-text">
              图片加载失败
            </div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="图片路径" prop="pic">
        <template v-slot="scope">
          <el-input v-model="scope.row.pic" readonly>
            <template #append>
              <el-button @click="copyPath(scope.row.pic)">复制</el-button>
            </template>
          </el-input>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" align="center">
        <template v-slot="scope">
          <!-- 更新图片按钮 - 改进上传组件 -->
          <el-upload
              :action="'http://localhost:8888/banner/updateBannerImg/' + scope.row.id"
              :show-file-list="false"
              :on-success="handleUpdateSuccess"
              :on-error="handleUploadError"
              :on-progress="handleUploadProgress"
              :before-upload="beforeUpload"
              :headers="uploadHeaders"
          >
            <el-button type="warning" size="small">更换图片</el-button>
          </el-upload>

          <el-button
              type="danger"
              size="small"
              @click="deleteRow(scope.row.id)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 上传进度条 -->
    <el-progress
        v-if="uploading"
        :percentage="uploadPercentage"
        status="success"
        class="upload-progress"
    ></el-progress>

    <!-- 删除确认对话框 -->
    <yin-del-dialog
        :delVisible="delVisible"
        @confirm="confirm"
        @cancel="delVisible = false"
    ></yin-del-dialog>
  </div>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, ref, reactive, onMounted } from "vue";
import mixin from "@/mixins/mixin";
import { HttpManager } from "@/api";
import YinDelDialog from "@/components/dialog/YinDelDialog.vue";
import { ElMessage } from "element-plus";

// 定义响应类型
interface ResponseData {
  code: number;
  data: any;
  message?: string;
}

export default defineComponent({
  components: {
    YinDelDialog,
  },
  setup() {
    const { proxy } = getCurrentInstance() as any;
    const { routerManager } = mixin();

    const tableData = ref([]); // 轮播图数据
    const tempData = ref([]); // 临时数据备份
    const multipleSelection = ref([]); // 多选记录
    const delVisible = ref(false); // 删除对话框可见性
    const currentDeleteId = ref(-1); // 当前待删除ID
    const uploading = ref(false); // 上传状态
    const uploadPercentage = ref(0); // 上传进度百分比
    const imageLoadErrors = ref<number[]>([]); // 图片加载失败的ID列表

    // 上传请求头（如果需要认证）
    const uploadHeaders = ref({
      // 'Authorization': `Bearer ${localStorage.getItem('token')}`
    });

    // 上传额外数据
    const uploadData = ref({
      type: 'banner'
    });

    // 获取轮播图数据
    async function getBannerData() {
      try {
        const result = (await HttpManager.getAllBanner()) as ResponseData;
        tableData.value = result.data;
        tempData.value = result.data;
        // 清空图片加载错误列表
        imageLoadErrors.value = [];
      } catch (error) {
        ElMessage.error("获取轮播图数据失败");
        console.error(error);
      }
    }

    // 初始化加载数据
    onMounted(() => {
      getBannerData();
    });

    // 处理文件上传前的校验
    function beforeUpload(file: File) {
      const isImage = file.type.includes("image/");
      const isLt10M = file.size / 1024 / 1024 < 10;

      if (!isImage) {
        ElMessage.error("只能上传图片文件!");
        return false;
      }
      if (!isLt10M) {
        ElMessage.error("图片大小不能超过10MB!");
        return false;
      }

      // 开始上传
      uploading.value = true;
      uploadPercentage.value = 0;

      return true;
    }

    // 处理上传进度
    function handleUploadProgress(event: ProgressEvent, file: any, fileList: any) {
      if (event.total > 0) {
        uploadPercentage.value = Math.round((event.loaded / event.total) * 100);
      }
    }

    // 处理上传错误
    function handleUploadError(err: Error, file: any, fileList: any) {
      uploading.value = false;
      uploadPercentage.value = 0;
      ElMessage.error("上传失败: " + err.message);
    }

    // 处理添加成功
    function handleAddSuccess(response: ResponseData) {
      uploading.value = false;
      uploadPercentage.value = 0;

      if (response.code === 0) {
        ElMessage.success("添加成功");
        getBannerData(); // 刷新列表
      } else {
        ElMessage.error(response.message || "添加失败");
      }
    }

    // 处理更新成功
    function handleUpdateSuccess(response: ResponseData) {
      uploading.value = false;
      uploadPercentage.value = 0;

      if (response.code === 0) {
        ElMessage.success("更新成功");
        getBannerData(); // 刷新列表
      } else {
        ElMessage.error(response.message || "更新失败");
      }
    }

    // 处理图片加载错误
    function handleImageError(row: any) {
      if (!imageLoadErrors.value.includes(row.id)) {
        imageLoadErrors.value.push(row.id);
      }
    }

    // 复制路径到剪贴板
    function copyPath(path: string) {
      navigator.clipboard.writeText(path);
      ElMessage.success("已复制路径到剪贴板");
    }

    // 处理删除操作
    function deleteRow(id: number) {
      currentDeleteId.value = id;
      delVisible.value = true;
    }

    // 确认删除
    async function confirm() {
      try {
        const result = (await HttpManager.deleteBanner(currentDeleteId.value)) as ResponseData;
        if (result.code === 0) {
          ElMessage.success("删除成功");
          getBannerData(); // 刷新列表
        } else {
          ElMessage.error(result.message || "删除失败");
        }
      } catch (error) {
        ElMessage.error("删除轮播图失败");
        console.error(error);
      } finally {
        delVisible.value = false;
      }
    }

    // 批量删除
    async function deleteAll() {
      if (multipleSelection.value.length === 0) {
        ElMessage.warning("请选择要删除的轮播图");
        return;
      }

      try {
        for (const item of multipleSelection.value) {
          await HttpManager.deleteBanner(item.id);
        }
        ElMessage.success(`已删除${multipleSelection.value.length}条记录`);
        getBannerData(); // 刷新列表
      } catch (error) {
        ElMessage.error("批量删除失败");
        console.error(error);
      } finally {
        multipleSelection.value = [];
      }
    }

    // 处理多选
    function handleSelectionChange(selection: any[]) {
      multipleSelection.value = selection;
    }

    return {
      tableData,
      delVisible,
      multipleSelection,
      getBannerData,
      beforeUpload,
      handleAddSuccess,
      handleUpdateSuccess,
      copyPath,
      deleteRow,
      confirm,
      deleteAll,
      handleSelectionChange,
      attachImageUrl: HttpManager.attachImageUrl,
      uploading,
      uploadPercentage,
      imageLoadErrors,
      uploadHeaders,
      uploadData,
      handleUploadProgress,
      handleUploadError,
      handleImageError
    };
  },
});
</script>

<style scoped>
.handle-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.preview-container {
  width: 100%;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.preview-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.error-text {
  color: #f56c6c;
  font-size: 12px;
}

.upload-progress {
  margin: 10px 0;
}
</style>