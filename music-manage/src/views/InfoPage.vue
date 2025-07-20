<template>
  <el-row :gutter="20">
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><user /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ userCount }}</div>
            <div>用户总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><headset /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ songCount }}</div>
            <div>歌曲总数</div>
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><mic /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ singerCount }}</div>
            <div>歌手数量</div>
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" :body-style="{ padding: '0px' }">
        <div class="card-content">
          <div class="card-left">
            <el-icon><document /></el-icon>
          </div>
          <div class="card-right">
            <div class="card-num">{{ songListCount }}</div>
            <div>歌单数量</div>
          </div>
        </div>
      </el-card>
    </el-col>
  </el-row>
  <el-row :gutter="20">
    <el-col :span="12">
      <h3></h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="userSex"></el-card>
    </el-col>
    <el-col :span="12">
      <h3></h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="songStyle"></el-card>
    </el-col>
  </el-row>
  <el-row :gutter="20">
    <el-col :span="12">
      <h3></h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="singerSex"></el-card>
    </el-col>
    <el-col :span="12">
      <h3></h3>
      <el-card class="cav-info" shadow="hover" :body-style="{ padding: '0px' }" id="country"></el-card>
    </el-col>
  </el-row>
</template>
<script lang="ts" setup>
import { ref, reactive, onMounted } from "vue";
import * as echarts from "echarts";
import { Mic, Document, User, Headset } from "@element-plus/icons-vue";
import { HttpManager } from "@/api/index";

// 数量统计
const userCount = ref(0);
const songCount = ref(0);
const singerCount = ref(0);
const songListCount = ref(0);

// 饼图渐变色配置（男蓝 女粉）
const pieColorMap = {
  0: {
    name: "男",
    color: {
      type: "linear",
      x: 0, y: 0, x2: 1, y2: 1,
      colorStops: [
        { offset: 0, color: "#409EFF" },
        { offset: 1, color: "#66b1ff" }
      ]
    }
  },
  1: {
    name: "女",
    color: {
      type: "linear",
      x: 0, y: 0, x2: 1, y2: 1,
      colorStops: [
        { offset: 0, color: "#FF69B4" },
        { offset: 1, color: "#FFB6C1" }
      ]
    }
  }
};

// 饼图基础配置
function createPieOption(title = "") {
  return {
    title: {
      text: title,
      left: "center",
      top: 10,
      textStyle: { fontSize: 16 },
    },
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)",
    },
    legend: {
      orient: "vertical",
      left: "left",
      textStyle: { fontSize: 14 },
    },
    series: [
      {
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: "{b}\n{d}%",
          fontSize: 14,
        },
        labelLine: { show: true },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.3)",
          },
        },
        data: [],
      },
    ],
  };
}

// 柱状图基础配置
function createBarOption(categories: string[], title = "") {
  return {
    title: {
      text: title,
      left: "center",
      top: 10,
      textStyle: { fontSize: 16 },
    },
    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: categories,
      axisLabel: {
        rotate: 30,
        fontSize: 12,
      },
      axisLine: { show: false },
      axisTick: { show: false },
    },
    yAxis: {
      type: "value",
      splitLine: {
        lineStyle: { type: "dashed" },
      },
    },
    series: [
      {
        name: "数量",
        data: new Array(categories.length).fill(0),
        type: "bar",
        barWidth: "35%",
        itemStyle: {
          color: "#409EFF",
          borderRadius: [6, 6, 0, 0],
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: "rgba(0, 0, 0, 0.3)",
          },
        },
      },
    ],
  };
}

// 图表配置对象
const userSex = reactive(createPieOption("用户性别比例"));
const singerSex = reactive(createPieOption("歌手性别比例"));
const songStyle = reactive(createBarOption(["华语", "粤语", "欧美", "日韩", "BGM", "轻音乐", "乐器"], "歌曲类型"));
const country = reactive(createBarOption(["中国", "韩国", "意大利", "新加坡", "美国", "西班牙", "日本"], "歌手国籍"));

// 设置饼图数据（附带渐变色）
function setSexWithColor(sex: number, arr: any[]) {
  const base = pieColorMap[sex];
  return {
    value: arr.filter((item) => item.sex === sex).length,
    name: base.name,
    itemStyle: {
      color: base.color,
    },
  };
}

// 渲染图表
function renderChart(id: string, option: any) {
  const el = document.getElementById(id);
  if (el) {
    const chart = echarts.init(el);
    chart.setOption(option, true);
  }
}

// 数据加载并渲染图表
onMounted(() => {
  HttpManager.getAllUser().then((res) => {
    const result = res as ResponseBody;
    const data = result.data;
    userCount.value = data.length;

    userSex.series[0].data = [
      setSexWithColor(0, data),
      setSexWithColor(1, data),
    ];
    renderChart("userSex", userSex);
  });

  HttpManager.getAllSong().then((res) => {
    const data = (res as ResponseBody).data;
    songCount.value = data.length;
  });

  HttpManager.getSongList().then((res) => {
    const data = (res as ResponseBody).data;
    songListCount.value = data.length;

    for (let item of data) {
      songStyle.xAxis.data.forEach((style, i) => {
        if (item.style.includes(style)) {
          songStyle.series[0].data[i]++;
        }
      });
    }
    renderChart("songStyle", songStyle);
  });

  HttpManager.getAllSinger().then((res) => {
    const data = (res as ResponseBody).data;
    singerCount.value = data.length;

    singerSex.series[0].data = [
      setSexWithColor(0, data),
      setSexWithColor(1, data),
    ];
    renderChart("singerSex", singerSex);

    for (let item of data) {
      country.xAxis.data.forEach((nation, i) => {
        if (item.location.includes(nation)) {
          country.series[0].data[i]++;
        }
      });
    }
    renderChart("country", country);
  });
});
</script>

<style scoped>
.card-content {
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 100px;
  padding-left: 20%;
  text-align: center;
}

.card-left {
  display: flex;
  font-size: 3rem;
}

.card-right {
  flex: 1;
  font-size: 14px;
}

.card-num {
  font-size: 30px;
  font-weight: bold;
}

h3 {
  margin: 10px 0;
  text-align: center;
}
.cav-info {
  border-radius: 6px;
  overflow: hidden;
  height: 250px;
  background-color: white;
}
</style>
