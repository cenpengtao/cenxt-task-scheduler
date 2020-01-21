const util = {
    /**
     * 存储工具
     */
    storage: {
        /**
         * 本地存储
         */
        local: {
            /**
             * 设置
             * @param {String} key 
             * @param {String} value 
             */
            set: function (key, value) {
                window.localStorage.setItem(key, value);
            },
            /**
             * 获取
             * @param {String} key 
             */
            get: function (key) {
                return window.localStorage.getItem(key)
            },
            /**
             * 删除
             * @param {String} key 
             */
            remove: function (key) {
                window.localStorage.removeItem(key)
            },
            /**
             * 清空
             */
            clear: function () {
                window.localStorage.clear()
            }
        },
        /**
         * 会话存储
         */
        session: {
            /**
             * 设置
             * @param {String} key 
             * @param {String} value 
             */
            set: function (key, value) {
                window.sessionStorage.setItem(key, value);
            },
            /**
             * 获取
             * @param {String} key 
             */
            get: function (key) {
                return window.sessionStorage.getItem(key)
            },
            /**
             * 删除
             * @param {String} key 
             */
            remove: function (key) {
                window.sessionStorage.removeItem(key)
            },
            /**
             * 清空
             */
            clear: function () {
                window.sessionStorage.clear()
            }
        }
    },
    /**
     * 格式化时间戳
     */
    formatDate: function (timestamp) {
        if(timestamp==undefined){
            return ""
        }
        var date = new Date(timestamp)
        var Y = date.getFullYear()
        var m = date.getMonth() + 1
        var d = date.getDate()
        var H = date.getHours()
        var i = date.getMinutes()
        var s = date.getSeconds()
        if (m < 10) {
            m = '0' + m;
        }
        if (d < 10) {
            d = '0' + d;
        }
        if (H < 10) {
            H = '0' + H;
        }
        if (i < 10) {
            i = '0' + i;
        }
        if (s < 10) {
            s = '0' + s;
        }
        var t = Y + '-' + m + '-' + d + ' ' + H + ':' + i+':' + s;
        return t;
    },
}


export default util;