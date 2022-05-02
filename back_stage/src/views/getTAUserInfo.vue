<template>
    <el-main>
        <el-row>
            <el-col :span="24">
                    <el-card shadow="hover">
                    <div slot="header">
                        <span>正常用户</span>
                    </div>

                    <el-table
                    :data="UserInfo"
                    height="500"
                    border
                    style="width:100%;"
                    >
                    <el-table-column label="用户名" align="center">
                        <template slot-scope="scope">
                            <span>{{ scope.row.username }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="车牌号" align="center">
                        <template slot-scope="scope">
                            <span>{{ scope.row.idCar }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="身份证号" align="center">
                        <template slot-scope="scope">
                            <span>{{ scope.row.idNumber }}</span>
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
            UserInfo: [],
        };
    },
    
    mounted(){
        //自动加载indexs方法
        this.getTAUserInfo();
    },

    methods: {

        //获取恶意用户监控信息
        getTAUserInfo() {
            this.$http.post("TAUserInfo", { token: this.$store.state.token })
            .then((res) => {
                //在控制台打印一下后端接口返回的数据
                console.log(res.data);
            if (res.data.result) {
                this.UserInfo = res.data.UserInfo;
            } else {
                this.$message({
                    message: "请登录",
                    type: "error",
                });
            }
        });
    }
  }
};
</script>

<style scoped>

</style>