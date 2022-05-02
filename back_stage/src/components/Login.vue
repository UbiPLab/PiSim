<template>
    <div class="login-wrap">
        <div class="ms-login">
            <div class="ms-title">导航后台系统</div>
            <el-form :model="login_form" :rules="login_form_rules" label-width="0px" class="ms-content">
                <el-form-item prop="user">
                    <el-input v-model="login_form.username" placeholder="用户名">
                      <el-button slot="prepend"><i class="el-icon-user" style="font-size: 15px;"></i></el-button>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input
                        type="password"
                        placeholder="密码"
                        v-model="login_form.password"
                    >
                    <el-button slot="prepend"><i class="el-icon-lock" style="font-size: 15px;"></i></el-button>
                    </el-input>
                </el-form-item>
                <div class="login-btn">
                    <el-button type="primary" @click="login">登录</el-button>
                </div>
            </el-form>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            login_form: {
                username: "",
                password: "",
            },
            login_form_rules: {
                user: [
                    { required: true, message: "请输入用户名", trigger: "blur" },
                    { min: 1, max: 10, message: "请输入正确用户名", trigger: "blur" },
                ],
                password: [{ required: true, message: "请输入密码", trigger: "blur" }],
            },
        };
    },
    methods: {
        login() {
            this.$http.post("login", this.login_form).then((res) => {
                //在控制台打印一下后端接口返回的信息
                console.log(res.data);
                if (res.data.result) {
                    this.$message({
                        message: "登录成功",
                        type: "success",
                    });
                    //    放入vuex
                    this.$store.dispatch('setUser', this.login_form.username)
                    this.$store.dispatch("setToken", res.data.token);
                    console.log(this.$store.state.token);
                    //跳转
                    this.$router.push("/home");
                } else {
                    this.$message({
                        message: "登录失败",
                        type: "error",
                    });
                }
            });
        },
    },
};
</script>

<style scoped>
.login-wrap {
    position: relative;
    width: 100%;
    height: 100%;
    background-image: url(/static/img/login-bg.jpg);
    background-size: 100%;
}
.ms-title {
    width: 100%;
    line-height: 50px;
    text-align: center;
    font-size: 20px;
    color: #fff;
    border-bottom: 1px solid #ddd;
}
.ms-login {
    position: absolute;
    left: 50%;
    top: 50%;
    width: 350px;
    margin: -190px 0 0 -175px;
    border-radius: 5px;
    background: rgba(255, 255, 255, 0.3);
    overflow: hidden;
}
.ms-content {
    padding: 30px 30px;
}
.login-btn {
    text-align: center;
}
.login-btn button {
    width: 100%;
    height: 36px;
    margin-bottom: 10px;
}
.login-tips {
    font-size: 12px;
    line-height: 30px;
    color: #fff;
}
</style>