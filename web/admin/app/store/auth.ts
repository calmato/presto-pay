export const state = () => ({
  id: '' as String,
  token: '' as String
})

export const getters = {
  id: (state) => state.id,
  token: (state) => state.token
}

export const mutations = {
  setId(state, id: String) {
    state.id = id
  },

  setToken(state, token: String) {
    state.token = token
  }
}

export const actions = {
  async login({ commit }, user: any) {
    const token = await user.getIdToken(true)

    commit('setId', user.uid)
    commit('setToken', token)
  },

  logout({ commit }) {
    commit('setId', '')
    commit('setToken', '')
  }
}
