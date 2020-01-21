import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import ViewUI from 'view-design';
import 'view-design/dist/styles/iview.css';

Vue.use(ViewUI);

axios.defaults.withCredentials = false


Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
