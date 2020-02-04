<style scoped>

</style>
<template>
<div>
    <div style="width:100%;height:40px">
        <Button v-if="role=='ADMIN'" type="success" icon="md-add" style="float:left" @click="onAddTask">新增任务</Button>
        <Button type="success" icon="md-refresh" style="float:right" @click="getTasks">刷新</Button>
    </div>
    <Table :loading="loading" :columns="columns" :data="tasks" size="small" highlight-row border stripe></Table>

    <TaskInfo v-model="taskModel" :taskInfoProp="taskInfo" @on-ok="getTasks" />

    <Modal v-model="execHistoryModal" fullscreen>
        <p slot="header">
            <span>执行记录（最近{{execHistorySize}}条）</span>
        </p>
        <Row>
            <Col span="6">
            <span>名称：</span>
            <span>{{ execHistoryTask.name }}</span>
            </Col>
            <Col span="12">
            <span>描述：</span>
            <span>{{ execHistoryTask.description }}</span>
            </Col>
            <Col span="6">
                <Checkbox v-model="exceptionHistory" @on-change="getExecHistory">异常记录</Checkbox>
                <Button type="success" icon="md-refresh" @click="getExecHistory">刷新</Button>
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
            role: "GUEST",
            loading: false,
            exceptionHistory:false,
            execHistory: [],
            execHistoryTask: {},
            execHistorySize: 18,
            execHistoryModal: false,
            taskModel: false,
            taskInfo: {},
            columns: [{
                    title: '任务编号',
                    key: 'id',
                    width: 100,
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
                    tooltip: true
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
                        return h("span", util.formatDate(params.row.createTime));
                    }
                },
                {
                    title: '更新时间',
                    key: 'updateTime',
                    width: 150,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", util.formatDate(params.row.updateTime));
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
                    width: 330,
                    align: "center",
                    fixed: "right",
                    render: (h, params) => {
                        const menu = []
                        if (this.role == "ADMIN") {
                            menu.push(h(
                                "Button", {
                                    props: {
                                        type: params.row.enabled ? "error" : "success",
                                        size: "small",
                                    },
                                    on: {
                                        click: () => {
                                            this.enableTask(params.row)
                                        }
                                    }
                                }, params.row.enabled ? "禁用" : "启用"
                            ))
                            menu.push(h(
                                "Button", {
                                    props: {
                                        type: "info",
                                        size: "small",
                                        icon: "md-create"
                                    },
                                    style: {
                                        marginLeft: "5px"
                                    },
                                    on: {
                                        click: () => {
                                            this.taskInfo = params.row
                                            this.taskModel = true;
                                        }
                                    }
                                }, "编辑"
                            ))
                            menu.push(h(
                                "Button", {
                                    props: {
                                        type: "error",
                                        size: "small",
                                        icon: "md-trash"
                                    },
                                    style: {
                                        marginLeft: "5px"
                                    },
                                    on: {
                                        click: () => {
                                            this.deleteTask(params.row)
                                        }
                                    }
                                }, "删除"
                            ))
                        }
                        menu.push(h(
                            "Button", {
                                props: {
                                    type: "warning",
                                    size: "small",
                                    icon: "md-list"
                                },
                                style: {
                                    marginLeft: "5px"
                                },
                                on: {
                                    click: () => {
                                        this.execHistoryTask=params.row
                                        this.getExecHistory()
                                    }
                                }
                            }, "执行记录"
                        ))
                        return h("div", menu);
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
                        var label = params.row.execResult
                        if (params.row.execResult == 0) {
                            color = 'blue'
                            label = '待执行'
                        } else if (params.row.execResult == 1) {
                            color = 'orange'
                            label = '执行中'
                        } else if (params.row.execResult == 2) {
                            color = 'green'
                            label = '成功'
                        } else if (params.row.execResult == 3) {
                            color = 'black'
                            label = '重试中'
                        } else if (params.row.execResult == 4) {
                            color = 'green'
                            label = '重试成功'
                        } else if (params.row.execResult == 5) {
                            color = 'red'
                            label = '执行失败'
                        } else if (params.row.execResult == 6) {
                            color = 'red'
                            label = '超时中断'
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
                 f   }
                },
                {
                    title: '重试次数',
                    key: 'retryTimes',
                    width: 100,
                    tooltip: true
                },
                {
                    title: '成功记录数',
                    width: 120,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", params.row.execReport.successCount);
                    }
                },
                {
                    title: '失败记录数',
                    width: 120,
                    tooltip: true,
                    render: (h, params) => {
                        return h("span", params.row.execReport.failCount);
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
                    title: '执行信息',
                    key: 'execMessage',
                    minWidth: 160,
                    tooltip: true
                }
            ]

        };
    },
    mounted: function () {
        this.getTasks()
        this.role = window.sessionStorage.getItem("ROLE")
    },
    methods: {
        onAddTask: function () {
            this.taskInfo = {
                expire: 5,
                retryTimes: 0,
                params: "{}"
            }
            this.taskModel = true;
        },
        deleteTask: function (task) {
            const that = this;
            this.$Modal.confirm({
                title: "确定删除" + task.name,
                content: "删除" + task.name + " 编号：" + task.id,
                onOk: function () {
                    that.$Spin.show();
                    http.delete("/api/admin/task/" + task.id, () => {
                        that.$Spin.hide();
                        that.$Notice.success({
                            title: "删除成功"
                        });
                        that.getTasks()
                    }, e => {
                        that.$Spin.hide();
                        that.$Notice.error({
                            title: e.message
                        });
                    })
                }
            })

        },
        enableTask: function (task) {
            var url = "/api/admin/task/enabled/"
            if (task.enabled) {
                url = "/api/admin/task/disabled/"
            }
            this.$Spin.show();
            http.post(url + task.id, {},
                () => {
                    this.$Spin.hide();
                    this.$Notice.success({
                        title: "操作成功"
                    });
                    this.getTasks()
                }, e => {
                    this.$Spin.hide();
                    this.$Notice.error({
                        title: e.message
                    });
                })
        },
        getTasks: function () {
            this.loading = true
            http.get("/api/tasks", data => {
                this.tasks = data;
                this.loading = false
            }, e => {
                this.loading = false
            });
        },
        getExecHistory: function () {
            this.$Spin.show();
            this.execHistory = []
            var url="/api/exec-history/"
            if(this.exceptionHistory){
                url="/api/exec-history/error/"
            }
            http.get( url+ this.execHistoryTask.id + "/" + this.execHistorySize, data => {
                this.execHistory = data;
                this.execHistoryModal = true;
                this.$Spin.hide();
            }, e => {
                this.$Spin.hide();
            });
        }
    }
};
</script>
