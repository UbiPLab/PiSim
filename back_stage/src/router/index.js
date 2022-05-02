import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login'
import Home from '@/components/Home'
import getTAUserInfo from '@/views/getTAUserInfo'
import getRSUInfo from '@/views/getRSUInfo'
import getNSPInfo from '@/views/getNSPInfo'
import getTAMaliciousUserInfo from '@/views/getTAMaliciousUserInfo'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      component: Login
    },
    {
      path:'/home',
      component: Home,
      children:[
        {
          path: '/manage/TAUserInfo',
          component: getTAUserInfo
        },
        {
          path: '/manage/RSU',
          component: getRSUInfo
        },
        {
          path: '/manage/NSP',
          component: getNSPInfo
        },
        {
          path: '/manage/TAMaliciousUserInfo',
          component: getTAMaliciousUserInfo
        }
      ]
    }
  ]
})
