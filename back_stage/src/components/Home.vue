<template>
<el-container  class="home_container">
  <el-header>
    <div>
      <span class="logo">导航后台系统</span>
    </div>
    <div>
      {{this.$store.state.currentUser}}
      <el-button type="primary" class="btn" size="medium" @click="exit">
        <i class="el-icon-coordinate" style="font-size: 15px;"></i> 退出
      </el-button>
    </div>
  </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu background-color="#545c64"  text-color="#fff" active-text-color="#ffd04b" router >
                <!-- TA-->
                <el-menu-item @click="getRSUInfo()" index="/manage/RSU">
                    <i class="el-icon-data-analysis"></i>
                    <span slot="title">RSU信息</span>
                </el-menu-item>
                <el-menu-item index="/manage/NSP">
                    <i class="el-icon-coin"></i>
                    <span slot="title">NSP信息</span>
                </el-menu-item>
                <el-menu-item index="/manage/TAUserInfo">
                    <i class="el-icon-s-custom"></i>
                    <span slot="title">正常用户身份信息</span>
                </el-menu-item>
                <el-menu-item index="/manage/TAMaliciousUserInfo">
                    <i class="el-icon-s-release"></i>
                    <span slot="title">恶意用户身份信息</span>
                </el-menu-item>
            </el-menu>
      </el-aside>
      <el-main>
        <el-col :span="24">
          <el-row :gutter="20" class="mgb20">
            <el-col :span="8">
                <el-card shadow="hover" :body-style="{padding: '0px'}">
                  <div class="grid-content grid-con-1">
                      <i @click="getTAuserCount();" class="el-icon-s-custom grid-con-icon"></i>
                      <div class="grid-cont-right">
                        <div class="grid-num" v-text="userCount"></div>
                        <div>系统用户数量</div>
                      </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" :body-style="{padding: '0px'}">
                  <div class="grid-content grid-con-2">
                    <i class="el-icon-error grid-con-icon"></i>
                    <div class="grid-cont-right">
                      <div class="grid-num" v-text="maliciousUserCount"></div>
                      <div>系统恶意用户数量</div>
                    </div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" :body-style="{padding: '0px'}">
                  <el-tooltip effect="dark" content="以两分钟为单位" placement="bottom">
                    <div class="grid-content grid-con-3">
                      <i class="el-icon-timer grid-con-icon"></i>
                      <div class="grid-cont-right">
                          <div class="grid-num" v-text="te">></div>
                          <div>当前时间周期</div>
                      </div>
                    </div>
                  </el-tooltip>
                </el-card>
              </el-col>
            </el-row>
        </el-col>
          <router-view>
          </router-view>
      </el-main>
    </el-container>
</el-container>
</template>

<script>
export default {
  data() {
    return {
      userCount: 0,
      maliciousUserCount: 0,
      te: 0,
    }
  },

  mounted(){
      //自动加载indexs方法
      this.getTAuserCount();
      this.getRSUInfo();
  },

  methods: {
      
      exit() {
        //重置vuex
        this.$store.dispatch("setUser", null);
        this.$store.dispatch("setToken", null);

        //跳转到登录页面
        this.$router.push("/");

        //提示
        this.$message({
          message: "成功退出登录",
          type: "success",
        });
      },
      
      getTAuserCount() {
        //向后端接口发起POST请求
        this.$http.post("TAuserCount", { token: this.$store.state.token })
        .then((res) => {
          //在控制台打印一下后端接口返回的数据
          console.log(res.data);
          //判断请求结果
          if (res.data.result) {
            //将返回的数据存放到data的变量中
            this.userCount = res.data.userCount;
            this.maliciousUserCount = res.data.maliciousUserCount;
          } else {
            //请求失败 提醒用户
            this.$message({
              message: "请登录",
              type: "error",
            });
          }
        });
    },

    //获取RSU监控信息
    getRSUInfo() {
      
      this.$http
        .post("RSUInfo", { token: this.$store.state.token })
        .then((res) => {
          //在控制台打印一下后端接口返回的信息
          console.log(res.data);
          if (res.data.result) {
            this.te = res.data.te; //当前时间历元
          } else {
            this.$message({
              message: "请登录",
              type: "error",
            });
          }
        });
    },
  }
}
</script>

<style scoped>

.home_container{
    height: 100%;
}
    
.el-header{
    background-color:#373d41;
    display: flex;
    justify-content: space-between;
    color:#ffffff;
    font-size: 20px;
    align-items: center;
}

.el-header logo {
    float: left;
    width: 250px;
    line-height: 70px;
}

.el-aside {
    background-color:#545c64;
}

.el-main{
    background-color: #eaedf1;
}

.grid-content {
    display: flex;
    align-items: center;
    height: 100px;
}

.grid-cont-right {
    flex: 1;
    text-align: center;
    font-size: 14px;
    color: #999;
}

.grid-num {
    font-size: 30px;
    font-weight: bold;
}

.grid-con-icon {
    font-size: 50px;
    width: 100px;
    height: 100px;
    text-align: center;
    line-height: 100px;
    color: #fff;
}

.grid-con-1 .grid-con-icon {
    background: rgb(100, 213, 114);
}

.grid-con-1 .grid-num {
    color: rgb(100, 213, 114);
}

.grid-con-2 .grid-con-icon {
    background: rgb(242, 94, 67);
}

.grid-con-2 .grid-num {
    color: rgb(242, 94, 67);
}

.grid-con-3 .grid-con-icon {
    background: rgb(45, 140, 240);
}

.grid-con-3 .grid-num {
    color: rgb(45, 140, 240);
}

.user-info-cont div:first-child {
    font-size: 30px;
    color: #222;
}

</style>
