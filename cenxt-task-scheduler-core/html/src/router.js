import Vue from 'vue'
import Router from 'vue-router'

import Login from './views/Login.vue'
import Index from './views/Index.vue'

Vue.use(Router)

var routes = [{
        path: '/',
        name: 'index',
        component: Index
    },
    {
        path: '/login',
        name: 'login',
        component: Login,
    }
]

const router = new Router({
    // mode:'history',
    routes: routes
})

export default router;