<style scoped>

</style>
<template>
<div>
    <div style="width:100%;height:40px">
        <Button type="success" icon="md-add" style="float:left" >新增任务</Button>
        <Button type="success" icon="md-refresh" style="float:right" @click="getTasks" >刷新</Button>
    </div>
    <Table :loading="loading" :columns="columns" :data="tasks" size="small" highlight-row border stripe></Table>
</div>
</template>

<script>

import util from "./../util/index.js";
import http from "./../api/http.js";
export default {
    data() {
        return {
            tasks:[],
            loading:false,
            execHistory:[],
            execHistoryModal:false,
            columns:[{
                    title: '序号',
                    type: 'index',
                    width: 70,
                    fixed: "left"
                },
                {
                    title: '编号',
                    key: 'id',
                    width: 80,
                    tooltip: true,
                    fixed: "left"
                },
                {
                    title: '任务名称',
                    key: 'name',
                    width: 180,
                    tooltip: true,
                    fixed: "left"
                },
                {
                    title: '任务描述',
                    key: 'description',
                    width: 180,
                    tooltip: true
                },
                {
                    title: '启用状态',
                    key: 'enabled',
                    width: 100,
                    render: (h, params) => {
                        var color = 'red'
                        var label = '禁用'
                        if (params.row.enabled) {
                            color = 'green'
                            label = '启用'
                        }
                        return h("span", {
                            style: {
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '执行状态',
                    key: 'flag',
                    width: 100,
                    render: (h, params) => {
                        var color = 'green'
                        var label = '待执行'
                        if (params.row.flag==1) {
                            color = 'orange'
                            label = '执行中'
                        }else if (params.row.flag==2) {
                            color = 'red'
                            label = '执行失败'
                        }
                        return h("span", {
                            style: {
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '时间表达式',
                    key: 'cronStr',
                    width: 130,
                    tooltip: true
                },
                {
                    title: '执行时间',
                    key: 'execTime',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", util.formatDate(params.row.execTime));
                    }
                },
                {
                    title: '下次执行时间',
                    key: 'nextTime',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", util.formatDate(params.row.nextTime));
                    }
                },
                {
                    title: '执行ip',
                    key: 'execIp',
                    width: 120,
                    tooltip: true
                },
                {
                    title: '参数',
                    key: 'params',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", JSON.stringify(params.row.params));
                    }
                },
                {
                    title: '超时(分)',
                    key: 'expire',
                    width: 90,
                    tooltip: true
                },
                {
                    title: '重试次数',
                    key: 'retryTimes',
                    width: 90,
                    tooltip: true
                },
                {
                    title: '创建时间',
                    key: 'createTime',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", util.formatDate(params.row.nextTime));
                    }
                },
                {
                    title: '更新时间',
                    key: 'updateTime',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", util.formatDate(params.row.nextTime));
                    }
                },
                {
                    title: '创建人',
                    key: 'creator',
                    width: 100,
                    tooltip: true
                },
                {
                    title: '更新人',
                    key: 'updator',
                    width: 90,
                    tooltip: true
                },
                {
                    title: '操作',
                    width: 300,
                    align: "center",
                    fixed: "right",
                    render: (h, params) => {

                        return h("div", [
                            h(
                                "Button", {
                                    props: {
                                        type: "success",
                                        size: "small",
                                    },
                                    on: {
                                        click: () => {
                                        }
                                    }
                                },"启用"
                            ),h(
                                "Button", {
                                    props: {
                                        type: "info",
                                        size: "small",
                                        icon: "md-create"
                                    },
                                    style:{
                                        margin:"0 10px"
                                    },
                                    on: {
                                        click: () => {
                                        }
                                    }
                                },"编辑"
                            ),h(
                                "Button", {
                                    props: {
                                        type: "text",
                                        size: "small",
                                        icon: "md-list"
                                    },
                                    on: {
                                        click: () => {
                                        }
                                    }
                                },"执行记录"
                            )
                        ]);
                    }
                }
                ]

        };
    },
    mounted: function () {
        this.getTasks()
    },
    methods: {
        getTasks:function(){
            this.loading=true
            http.get("/api/tasks",data=>{
                this.tasks=data;
                this.loading=false
            },e=>{
                this.loading=false
            });
        },
        getExecHistory:function(taskId){
            http.get("/api/exec-history/"+taskId+"/10",data=>{
                this.execHistory=data;
                this.execHistoryModal=true;
            });
        }
    }
};
</script>
