import { createStore } from "vuex"
import global from '@/store/modules/global'
import getters from './getters'

const store = createStore({
  modules: {
	global
  },
  getters
})

export default store
