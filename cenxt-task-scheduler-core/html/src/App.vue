<style scoped>
.container {
    top: 0px;
    bottom: 0px;
    left: 0px;
    right: 0px; 
    position: absolute;
    background: #fff;
    padding: 10px;
}
</style>
<template>
<div id="app">
    <router-view class="container"></router-view>
</div>
</template>

<script>
export default {
    name: "app",
    data() {
        return {};
    },
    methods: {}
};
</script>
