import axios from 'axios'
import util from "./../util/index.js";
import router from './../router'
/**
 *api接口调用
 */
const http = {
    /**
     *服务器配置
     */
    server: process.env.VUE_APP_SERVER,
    /**
     *获取拼接后的地址
     */
    getUrl: function (url) {
        if (url.startsWith("/")) {
            return http.server + url
        } else if (url.startsWith("http")) {
            return url;
        } else {
            return http.server + "/" + url
        }
    },

    /**
     *get
     */
    get: function (url, success, fail) {
        axios.get(http.getUrl(url))
            .then(
                response => {
                    success && success(response.data)
                }
            ).catch(e => {
                http.handleError(e, fail)
            });
    },
    delete: function (url, success, fail) {
        axios.delete(http.getUrl(url))
            .then(
                response => {
                    success && success(response.data)
                }
            ).catch(e => {
                http.handleError(e, fail)
            });
    },
    /**
     *post
     */
    post: function (url, data, success, fail) {
        axios.post(http.getUrl(url), data)
            .then(
                response => {
                    success && success(response.data)
                }
            ).catch(e => {
                http.handleError(e, fail)
            });
    },
    handleError: function (e, callback) {
        console.error(e)
        if (e.response) {
            console.error(e.response)
            //未登录
            if (e.response.status == '401') {
                callback && callback({message:'未登录',code:401})
                //跳转到登录页面
                router.push("/login")
            } else if (e.response.status == '403') {
                callback && callback({message:'无权访问',code:403})
                //跳转到登录页面
                router.path!='/'&&router.replace('/')
            } else {
                //其他错误码
                callback && callback({
                    code: e.response.status,
                    message: e.response.data
                })
            }
        } else {
            //无错误码，一般为网络错误
            callback && callback({message:e.message,code:99})
        }
    }
}

export default http