import Vue from "vue"
import Vuex from 'vuex'
Vue.use(Vuex)

const  store = new Vuex.Store({
        state: {
            currentUser: null,
            isLogin: false,
            token: '',
        },
        getters: {
            currentUser: state => state.currentUser,
            isLogin: state => state.isLogin
        },
        mutations: {
            userStatus(state,user){
                if(user) {
                    state.currentUser = user
                    state.isLogin = true
                }
                else if(user == null) {
                    sessionStorage.setItem('userName', null)
                    sessionStorage.setItem('userToken', '')
                    state.currentUser = null
                    state.token = ''
                    state.isLogin = false
                }
            },
            userToken(state,token){
                if (token) {
                    state.token = token
                }
            }
        },
        actions: {
            setUser({commit}, user) {
                commit('userStatus', user)
            },
            setToken({commit}, token) {
                commit('userToken', token)
            }
        }

})


export default store
