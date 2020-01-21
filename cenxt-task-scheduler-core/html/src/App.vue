<style scoped>
.container {
    top: 50px;
    bottom: 0px;
    left: 0px;
    right: 0px;
    position: absolute;
    background: #fff;
}
</style>
<template>
<div id="app">
    <router-view class="container"></router-view>
</div>
</template>

<script>
import WebHeader from "./components/WebHeader";
import util from "./util/index.js";
export default {
    name: "app",
    data() {
        return {};
    },
    methods: {}
};
</script>
