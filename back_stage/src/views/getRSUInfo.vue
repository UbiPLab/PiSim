<template>
  <el-main>
    <el-row :gutter="15">
      <el-col :span="12">
        <el-card shadow="hover" style="height:450px">
          <div slot="header">
            <span>RSU数据统计</span>
          </div>
          <el-table :data="RSU_List" border style="width:100%;">
            <el-table-column prop="id" label="序号" width="55" align="center"></el-table-column>
            <el-table-column prop="name" label="参数" align="center"></el-table-column>
            <el-table-column prop="number" label="数值" align="center"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" style="height:450px">
          <div slot="header">
            <span>RSU上一周期数据统计</span>
          </div>
          <el-table :data="RSU_Last_List" border style="width:100%;">
            <el-table-column prop="id" label="序号" width="55" align="center"></el-table-column>
            <el-table-column prop="name" label="参数" width="300" align="center"></el-table-column>
            <el-table-column prop="number" label="数值" align="center"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <br />
    <el-row>
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header">
            <span>导航查询请求</span>
          </div>

          <el-table :data="RSUNavi" height="400" border style="width:100%;">
            <el-table-column type="expand">
              <template slot-scope="scope">
                <el-form label-position="left" inline class="demo-table-expand">
                  <el-form-item label="匿名身份">
                    <span>{{ scope.row.rei1 }}<br>{{ scope.row.rei2 }}</span>
                  </el-form-item>
                  <el-form-item label="限制假名">
                    <span>{{ scope.row.grlpi }}</span>
                  </el-form-item>
                  <el-form-item label="请求假名">
                    <span>{{ scope.row.rlpi }}</span>
                  </el-form-item>
                  <el-form-item label="零知识证明参数a1">
                    <span>{{ scope.row.a1 }}</span>
                  </el-form-item>
                  <el-form-item label="零知识证明参数▲">
                    <span>{{ scope.row.daierta }}</span>
                  </el-form-item>
                  <el-form-item label="零知识证明参数M">
                    <span>{{ scope.row.m }}</span>
                  </el-form-item>
                  <el-form-item label="模糊搜索向量">
                    <span>{{ scope.row.index_EncKiI }}</span>
                  </el-form-item>
                  <el-form-item label="查询点数量">
                    <span>{{ scope.row.count }}</span>
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
                <span>{{dateFormat('YYYY-mm-dd HH:MM',scope.row.timestamp)}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <br />
    <el-row>
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header">
            <span>路况报告</span>
          </div>
          <el-table :data="RSUReport" height="500" border style="width:100%;">
            <el-table-column type="expand">
              <template slot-scope="props">
                <el-form label-position="left" inline class="demo-table-expand">
                  <el-form-item label="匿名身份">
                    <span>{{ props.row.REi1 }}<br>{{ props.row.REi2 }}</span>
                  </el-form-item>
                  <el-form-item label="限制假名">
                    <span>{{ props.row.grlpi }}</span>
                  </el-form-item>
                  <el-form-item label="请求假名">
                    <span>{{ props.row.rlpi }}</span>
                  </el-form-item>
                  <el-form-item label="零知识证明参数a1">
                    <span>{{ props.row.a1 }}</span>
                  </el-form-item>
                  <el-form-item label="零知识证明参数▲">
                    <span>{{ props.row.daierta }}</span>
                  </el-form-item>
                  <el-form-item label="零知识证明参数M">
                    <span>{{ props.row.M }}</span>
                  </el-form-item>
                  <el-form-item label="用户伪ID">
                    <span>{{ props.row.pidj }}</span>
                  </el-form-item>
                  <el-form-item label="握手用户伪ID">
                    <span>{{ props.row.pidjs }}</span>
                  </el-form-item>
                  <el-form-item label="路况信息">
                    <span>{{ props.row.indj }}</span>
                  </el-form-item>
                  <el-form-item label="模糊搜索陷门">
                    <span>{{ props.row.Query_EncKI }}</span>
                  </el-form-item>
              
                
                </el-form>
              </template>
            </el-table-column>
            <el-table-column label="序号" align="center">
              <template slot-scope="props">
                <span>{{ props.row.id }}</span>
              </template>
            </el-table-column>
            <el-table-column label="时间戳" align="center">
              <template slot-scope="props">
                <span>{{ dateFormat('YYYY-mm-dd HH:MM',props.row.timestamp) }}</span>
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
      RSUReport: [],
      RSUNavi: [],

      RSU_List: [
        {
          id: "1",
          name: "接收到有效导航查询数量",
          number: 0,
        },
        {
          id: "2",
          name: "接收到无效导航查询数量",
          number: 0,
        },
        {
          id: "3",
          name: "接收到路况报告数量",
          number: 0,
        },
        {
          id: "4",
          name: "过滤后有效路况报告数量",
          number: 0,
        },
        {
          id: "5",
          name: "过滤后虚假路况报告数量",
          number: 0,
        },
        {
          id: "6",
          name: "最近一次路况查询查询点数量",
          number: 0,
        },
      ],

      RSU_Last_List: [
        // {
        //   id: "1",
        //   name: "当前时间周期路况报告数量",
        //   number: 0,
        // },
        {
          id: "1",
          name: "上一时间周期接收到有效导航查询数量",
          number: 0,
        },
        {
          id: "2",
          name: "上一时间周期接收到无效导航查询数量",
          number: 0,
        },
        {
          id: "3",
          name: "上一时间周期接收到路况报告数量",
          number: 0,
        },
        {
          id: "4",
          name: "上一时间周期过滤后有效路况报告数量",
          number: 0,
        },
        {
          id: "5",
          name: "上一时间周期过滤后虚假路况报告数量",
          number: 0,
        },
      ],
    };
  },

  mounted() {
    //自动加载indexs方法
    this.getRSUInfo();
  },

  methods: {
    //获取RSU监控信息
    getRSUInfo() {
      this.$http
        .post("RSUInfo", { token: this.$store.state.token })
        .then((res) => {
          //在控制台打印一下后端接口返回的信息
          console.log(res.data);
          if (res.data.result) {
            this.RSUReport = res.data.RSUReport;

            /*for(var i = 0; i < this.RSUReport.length; i++)
            {
              this.RSUReport[i].report = this.strToJson(this.RSUReport[i].report_string);
            }
            console.log(this.RSUReport);*/

            this.RSUNavi = res.data.RSUNavi;

            this.te = res.data.te;
            this.RSU_List[0].number = res.data.RSUNaviValidCount; //接收到有效导航查询次数
            this.RSU_List[1].number =
              res.data.RSUNaviCount - res.data.RSUNaviValidCount; //接收到无效导航查询次数
            this.RSU_List[2].number = res.data.RSUReportRequestValidCount; //接收到路况报告数量
            this.RSU_List[3].number = res.data.RSUReportValidCount; //过滤后有效路况报告数量
            //this.RSU_List[4].number = res.data.RSUReportRequestValidCount - res.data.RSUReportValidCount; //过滤后虚假路况报告数量
            this.RSU_List[4].number = res.data.RSUReportMaliciousCount;
            this.RSU_List[5].number = res.data.RSUNaviPointCount_temp;

            // this.RSU_Last_List[0].number =
            //   res.data.RSUReportRequestValidCount_temp; //当前时间周期路况报告数量
            this.RSU_Last_List[0].number = res.data.RSUNaviValidCount_Last; //上一时间周期接收到有效导航查询次数
            this.RSU_Last_List[1].number =
              res.data.RSUNaviCount_Last - res.data.RSUNaviValidCount_Last; //上一时间周期接收到无效导航查询次数
            this.RSU_Last_List[2].number =
              res.data.RSUReportRequestValidCount_Last; //上一时间周期接收到路况报告数量
            this.RSU_Last_List[3].number = res.data.RSUReportValidCount_Last; //上一时间周期过滤后有效路况报告数量
            this.RSU_Last_List[4].number =
              res.data.RSUReportRequestValidCount_Last -
              res.data.RSUReportValidCount_Last; //上一时间周期过滤后虚假路况报告数量
          } else {
            this.$message({
              message: "请登录",
              type: "error",
            });
          }
        });
    },

    strToJson(str) {
      return JSON.parse(str);
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
    },
  },
};
</script>

<style>
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 130px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 10000px;
}
</style>
