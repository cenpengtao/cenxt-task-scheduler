<template>
<Modal v-model="taskModel" :width="900" :mask-closable="false" :closable="false">
    <p slot="header">
        <span v-if="taskInfo.id">修改任务</span>
        <span v-else>新建任务</span>
    </p>

    <Form ref="taskInfoValidate" :model="taskInfo" :rules="taskInfoRules" label-position="left" :label-width="120">
        <Row>
            <Col span="10">
            <FormItem label="名称" prop="name">
                <Select v-model="taskInfo.name" placeholder="名称" clearable>
                    <Option v-for="item in jobs" :value="item" :key="item">{{ item }}</Option>
                </Select>
            </FormItem>
            </Col>
            <Col span="13" offset="1">
            <FormItem label="表达式" prop="cronStr" :label-width="70">
                <Poptip :disabled="cronExplain.length==0" :value="cronExplain.length>0" :title="'接下来'+explainSize+'次的执行时间'" style="width:100%" placement="bottom">
                    <Input v-model="taskInfo.cronStr" placeholder="Spring cron表达式，如：0/30 * * * * ? (每30秒执行一次)" clearable show-word-limit maxlength="200" />
                    <div slot="content">
                        <table v-if="cronExplain.length>0" border="1" bordercolor="#a0c6e5" style="border-collapse:collapse;">
                            <tbody>
                                <tr v-for="(item,i) in cronExplain" :value="item" :key="i">
                                    <td style="padding:0 10px">第{{i+1}}次</td>
                                    <td style="padding:0 10px">{{item|formatDate}}</td>
                                </tr>
                            </tbody>
                        </table>
                        <span v-else style="color:red">无效的表达式</span>
                    </div>
                </Poptip>
            </FormItem>
            </Col>

        </Row>
        <Row>
            <Col span="10">
            <FormItem label="下次执行时间" prop="nextTime">
                <DatePicker type="datetime" v-model="taskInfo.nextTime" placeholder="下次执行时间" clearable />
            </FormItem>
            </Col>
            <Col span="6" offset="1">
            <FormItem label="超时时间(分)" prop="expire">
                <InputNumber :min="1" v-model="taskInfo.expire" placeholder="超时时间（分）" clearable />
            </FormItem>
            </Col>
            <Col span="6" offset="1">
            <FormItem label="重试次数" prop="retryTimes">
                <InputNumber :min="0" :max="5" v-model="taskInfo.retryTimes" placeholder="重试次数" clearable />
            </FormItem>
            </Col>
        </Row>
        <Row>
            <FormItem label="描述" prop="description">
                <Input v-model="taskInfo.description" placeholder="描述" type="textarea" :rows="2" clearable show-word-limit maxlength="200" />
            </FormItem>
        </Row>
        <Row>
            <FormItem label="参数(JSON)" prop="params">
                <Input v-model="taskInfo.params" placeholder="参数(JSON)" type="textarea" :rows="10" clearable show-word-limit maxlength="4000" />
            </FormItem>
        </Row>
    </Form>
    <div slot="footer">
        <Button type="success" @click="onConfirm">确定</Button>
        <Button v-if="!!!taskInfo.id" type="error" @click="onClear">清空</Button>
        <Button @click="onCancel">取消</Button>
    </div>
</Modal>
</template>

<script>
import http from "./../api/http.js";
import util from "./../util/index.js";
var _this;
export default {
    data() {
        return {
            taskModel: false,
            taskInfo: {},
            jobs: [],
            explainSize: 5,
            cronExplain: [],
            taskInfoRules: {
                name: [{
                    required: true,
                    message: "名称必填",
                    trigger: "blur"
                }],
                cronStr: [{
                        required: true,
                        message: "表达式必填",
                        trigger: "blur"
                    },
                    {
                        validator(rule, value, callback, source, options) {
                            var errors = [];
                            if (!!!value) {
                                callback(errors);
                                return
                            }
                            _this.getCronExplain(value, flag => {
                                if (!flag) {
                                    errors.push(new Error("无效的表达式"));
                                }
                                callback(errors);
                            })
                        }
                    }
                ],
                nextTime: [{
                    required: true,
                    type: "date",
                    message: "下次执行时间必填",
                    trigger: "blur"
                }],
                expire: [{
                    required: true,
                    type: "integer",
                    message: "超时时间必填",
                    trigger: "blur"
                }],
                retryTimes: [{
                    required: true,
                    type: "integer",
                    message: "重试次数必填",
                    trigger: "blur"
                }],
                description: [{
                    required: true,
                    message: "描述必填",
                    trigger: "blur"
                }],
                params: [{
                    required: true,
                    message: "参数必填",
                    trigger: "blur"
                },
                {
                    validator(rule, value, callback, source, options) {
                        var errors = [];
                        try {
                            var param = JSON.parse(value)
                            if (typeof (param) != 'object') {
                                errors.push(new Error(typeof (param) + "非JSON Object类型"));
                            }
                        } catch (e) {
                            errors.push(new Error("参数非JSON类型,错误信息:" + e.message));
                        }
                        callback(errors);
                        return
                    }
                }]
            }
        }
    },
    filters: {
        formatDate: util.formatDate
    },
    props: {
        value: {
            type: Boolean,
            default () {
                return false;
            }
        },
        taskInfoProp: {
            type: Object,
            default () {
                return {};
            }
        }
    },
    created: function () {
        _this = this;
        this.getJobs()
    },
    watch: {
        value: function (value) {
            if (value) {
                this.$refs.taskInfoValidate.resetFields()
                this.cronExplain = []
                this.taskInfo = JSON.parse(JSON.stringify(this.taskInfoProp))
            }
            this.taskModel = value
        }
    },
    methods: {
        getJobs: function () {
            http.get("/api/jobs", data => {
                this.jobs = data;
            });
        },
        getCronExplain: function (str, callback) {
            http.post("/api/cron-explain", {
                    cronStr: str,
                    size: this.explainSize
                },
                data => {
                    this.cronExplain = data
                    if(data.length>0){
                        this.taskInfo.nextTime=util.formatDate(data[0])
                    }
                    callback && callback(data.length > 0)
                }, e => {
                    callback && callback(false)
                });
        },
        onConfirm: function () {
            const that = this;
            that.$refs.taskInfoValidate.validate(valid => {
                that.taskInfo.params=JSON.stringify(JSON.parse(that.taskInfo.params),null,2)
                that.$Spin.show();
                http.post("/api/admin/task", that.taskInfo,
                    () => {
                        this.$emit('on-ok',that.taskInfo)
                        that.$Spin.hide();
                        that.$Notice.success({
                            title: "新增成功"
                        });
                        this.$emit('input', false)
                        this.taskModel = false
                    },
                    e => {
                        that.$Spin.hide();
                        that.$Notice.error({
                            title: e.message
                        });
                    })
            })

        },
        onCancel: function () {
            this.taskModel = false
            this.$emit('input', false)
        },
        onClear: function () {
            this.$refs.taskInfoValidate.resetFields()
            this.taskInfo = {}
        }
    }
}
</script>

<style>
.ivu-poptip-rel {
    width: 100%;
}

.ivu-poptip-popper {
    width: 100%;
}
</style>
