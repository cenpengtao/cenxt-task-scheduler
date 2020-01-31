<style scoped>

</style>
<template>
<div>
    <div style="width:100%;height:40px">
        <Button type="success" icon="md-add" style="float:left" @click="newTaskModel=true">新增任务</Button>
        <Button type="success" icon="md-refresh" style="float:right" @click="getTasks">刷新</Button>
    </div>
    <Table :loading="loading" :columns="columns" :data="tasks" size="small" highlight-row border stripe></Table>

    <Modal v-model="newTaskModel" fullscreen>
        <p slot="header">
            <span>新建任务</span>
        </p>
        <TaskInfo />
        <div slot="footer">
            <Button type="success">确定</Button>
            <Button type="error">清空</Button>
        </div>
    </Modal>
    <Modal v-model="execHistoryModal" fullscreen>
        <p slot="header">
            <span>执行记录（最近{{execHistorySize}}条）</span>
        </p>
        <Row>
            <Col span="8">
            <span>名称：</span>
            <span>{{ execHistoryTask.name }}</span>
            </Col>
            <Col span="16">
            <span>描述：</span>
            <span>{{ execHistoryTask.description }}</span>
            </Col>
        </Row>
        <Table :columns="execHistorycolumns" :data="execHistory" size="small" highlight-row border stripe></Table>

        <div slot="footer">
        </div>
    </Modal>

</div>
</template>

<script>
import util from "./../util/index.js";
import http from "./../api/http.js";
import TaskInfo from "./../components/TaskInfo";
export default {
    components: {
        TaskInfo
    },
    data() {
        return {
            tasks: [],
            loading: false,
            execHistory: [],
            execHistoryTask: {},
            execHistorySize: 18,
            execHistoryModal: false,
            newTaskModel: false,
            newTask: {},
            columns: [{
                    title: '序号',
                    type: 'index',
                    width: 70
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
                        if (params.row.flag == 1) {
                            color = 'orange'
                            label = '执行中'
                        } else if (params.row.flag == 2) {
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
                                }, "启用"
                            ), h(
                                "Button", {
                                    props: {
                                        type: "info",
                                        size: "small",
                                        icon: "md-create"
                                    },
                                    style: {
                                        margin: "0 10px"
                                    },
                                    on: {
                                        click: () => {}
                                    }
                                }, "编辑"
                            ), h(
                                "Button", {
                                    props: {
                                        type: "warning",
                                        size: "small",
                                        icon: "md-list"
                                    },
                                    on: {
                                        click: () => {
                                            this.getExecHistory(params.row)
                                        }
                                    }
                                }, "执行记录"
                            )
                        ]);
                    }
                }
            ],
            execHistorycolumns: [{
                    title: '序号',
                    type: 'index',
                    width: 70,
                    fixed: "left"
                },
                {
                    title: '执行编号',
                    key: 'execId',
                    width: 300,
                    tooltip: true
                },
                {
                    title: '执行结果',
                    key: 'execResult',
                    width: 120,
                    render: (h, params) => {
                        var color = 'red'
                        var label = '失败'
                        if (params.row.execResult == 0) {
                            color = 'green'
                            label = '成功'
                        } else if (params.row.execResult == 1) {
                            color = 'orange'
                            label = '重试后成功'
                        }
                        return h("span", {
                            style: {
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '执行IP',
                    key: 'execIp',
                    width: 160,
                    tooltip: true
                },
                {
                    title: '耗时',
                    key: 'cost',
                    width: 120,
                    tooltip: true,
                    render: (h, params) => {
                        var min = Math.floor((params.row.cost / 1000 / 60) << 0)
                        var sec = Math.floor((params.row.cost / 1000) % 60)
                        return h("span", min + "分" + sec + "秒");
                    }
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
                    title: '结束时间',
                    key: 'finishTime',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", util.formatDate(params.row.finishTime));
                    }
                },
                {
                    title: '错误信息',
                    key: 'errorMessage',
                    minWidth: 160,
                    tooltip: true
                }
            ]

        };
    },
    mounted: function () {
        this.getTasks()
    },
    methods: {
        getTasks: function () {
            this.loading = true
            http.get("/api/tasks", data => {
                this.tasks = data;
                this.loading = false
            }, e => {
                this.loading = false
            });
        },
        getExecHistory: function (task) {
            this.execHistory = [];
            this.execHistoryTask = task;
            http.get("/api/exec-history/" + task.id + "/" + this.execHistorySize, data => {
                this.execHistory = data;
                this.execHistoryModal = true;
            });
        }
    }
};
</script>
