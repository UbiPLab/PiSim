// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store/store.js'
import Element from 'element-ui'
import './assets/css/global.css'
import 'element-ui/lib/theme-chalk/index.css'
//import './assets/iconfont/iconfont.css'

Vue.config.productionTip = false

Vue.use(Element)

import axios from 'axios'
Vue.prototype.$http = axios.create({
  baseURL:'http://114.55.33.26:6688/admin'
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
