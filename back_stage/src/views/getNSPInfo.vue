<template>
    <el-main>
        <el-row>
            <el-col :span="24">
                    <el-card shadow="hover">
                    <div slot="header">
                        <span>NSP</span>
                    </div>
                    <el-table
                    :data="NSP_List"
                    border
                    style="width:100%;"
                    >
                    <el-table-column prop="id" label="序号" width="55" align="center"></el-table-column>
                    <el-table-column prop="name" label="参数" align="center"></el-table-column>
                    <el-table-column prop="number" label="数值" align="center"></el-table-column>
                    </el-table>
                    </el-card>
            </el-col>
        </el-row>
        <br/>
        <el-row>
            <el-col :span="24">
                    <el-card shadow="hover">
                    <div slot="header">
                        <span>接收到路况信息</span>
                    </div>

                    <el-table
                    :data="NSPReceiveCongest"
                    height="500"
                    border
                    style="width:100%;"
                    >
                    <el-table-column type="expand">
                        <template slot-scope="scope">
                          <el-form label-position="left" inline class="demo-table-expand">
                            <el-form-item label="路况信息">
                              <span>{{ scope.row.indj }}</span>
                            </el-form-item>
                            <el-form-item label="模糊搜索陷门">
                              <span>{{ scope.row.queryindex }}</span>
                            </el-form-item>
                            </el-form>
                        </template>
                    </el-table-column>
                    <el-table-column label="序号" align="center">
                        <template slot-scope="scope">
                            <span>{{ scope.row.id }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="时间戳" align="center">
                        <template slot-scope="scope">
                            <span>{{dateFormat('YYYY-mm-dd HH:MM',scope.row.timestamp) }}</span>
                        </template>
                    </el-table-column>
                    </el-table>
                    </el-card>
            </el-col>
        </el-row>
    </el-main>
</template>

<script>
export default {
  data() {
    return {
      NSPReceiveCongest:[],

      NSP_List: [
        {
          id: "1",
          name: "接收路况信息总数量",
          number: 0,
        },
        {
          id: "2",
          name: "接收路况查询请求总数量",
          number: 0,
        },
        {
          id: "3",
          name: "上一时间周期接收路况信息数量",
          number: 0,
        },
        {
          id: "4",
          name: "上一时间周期路况查询请求数量",
          number: 0,
        },
        {
          id: "5",
          name: "最近一次路况查询查询点数量",
          number: 0,
        },
      ],
    };
  },

  mounted(){
        //自动加载indexs方法
        this.getNSPInfo();
  },

  methods: {
    //获取NSP监控信息
    getNSPInfo() {
      this.$http
        .post("NSPInfo", { token: this.$store.state.token })
        .then((res) => {
          //在控制台打印一下后端接口返回的信息
          console.log(res.data);
          if (res.data.result) {
            this.NSPReceiveCongest = res.data.NSPReceiveCongest;
            this.NSP_List[0].number = res.data.NSPtrafficInfoCount;
            this.NSP_List[1].number = res.data.NSPNaviCount;
            this.NSP_List[2].number = res.data.NSPtrafficInfoCount_Last;
            this.NSP_List[3].number = res.data.NSPNaviCount_Last;
            this.NSP_List[4].number = res.data.NSPNaviPointCount_temp;
          } else {
            this.$message({
              message: "请登录",
              type: "error",
            });
          }
        });
    },
    dateFormat(fmt, date) {
      let ret = "";
      date = new Date(date);
      const opt = {
        "Y+": date.getFullYear().toString(), // 年
        "m+": (date.getMonth() + 1).toString(), // 月
        "d+": date.getDate().toString(), // 日
        "H+": date.getHours().toString(), // 时
        "M+": date.getMinutes().toString(), // 分
        "S+": date.getSeconds().toString(), // 秒
        // 有其他格式化字符需求可以继续添加，必须转化成字符串
      };
      for (let k in opt) {
        ret = new RegExp("(" + k + ")").exec(fmt);
        if (ret) {
          fmt = fmt.replace(
            ret[1],
            ret[1].length == 1 ? opt[k] : opt[k].padStart(ret[1].length, "0")
          );
        }
      }
      return fmt;
    }
  }
}
</script>

<style scoped>

</style>
