<style scoped>
.container {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
}

.login-container {
    widows: 100%;
    background: #fff;
    padding: 20px 30px;
}

.login-title {
    height: 40px;
    font-size: 20px;
    line-height: 40px;
    vertical-align: middle;
    border-bottom: 1px solid #ccc;
}

.login-form {
    margin-top: 20px;
}

.register-form .ivu-form-item {
    margin-bottom: 20px;
}

.login-btn {
    margin-top: -15px;
    margin-bottom: 15px;
    height: 20px;
}
</style>
<template>
<div id="app">
    <Row type="flex" justify="center" align="middle" class="container">
        <Col span="8" />
        <Col span="8">
        <div class="login-container">
            <div class="login-title">登录</div>
            <Form class="login-form">
                <FormItem>
                    <Input v-model="username" size="large" placeholder="用户名" />
                </FormItem>
                <FormItem>
                    <Input type="password" size="large" v-model="password" placeholder="密码" />
                </FormItem>
                <FormItem>
                    <Button type="success" size="large" long @click="login">登录</Button>
                </FormItem>
            </Form>
        </div>
        </Col>
        <Col span="8" />
    </Row>
</div>
</template>

<script>
import util from "./../util/index.js";
import http from "./../api/http.js";
var _this;
export default {
    name: "app",
    data() {
        return {
            username: "",
            password: "",
        };
    },

    methods: {
        login: function () {
            http.post("/api/login", {
                username: this.username,
                password: this.password
            }, role => {
                this.$router.push({path:'/',params:{role:role}})
            }, e => {
                this.$Notice.error({
                    title: '[' + e.code + '] ' + e.message
                });
            })
        }
    }
};
</script>
