<template>
  <div>
    <div v-show="token == ''" id="firebaseui-auth-container" />
    <div v-show="token != ''">
      <p>UID: {{ id }}</p>
      <p class="token">Token: {{ token }}</p>

      <v-btn class="primary" @click="handleLogout">ログアウト</v-btn>
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import { mapActions, mapGetters } from 'vuex'
import * as firebaseUI from 'firebaseui'

export default Vue.extend({
  computed: {
    ...mapGetters('auth', ['id', 'token'])
  },

  mounted() {
    const config = {
      signInSuccessUrl: '/',
      signInOptions: [
        this.$firebase.auth.EmailAuthProvider.PROVIDER_ID,
        this.$firebase.auth.GoogleAuthProvider.PROVIDER_ID,
        this.$firebase.auth.FacebookAuthProvider.PROVIDER_ID,
        this.$firebase.auth.TwitterAuthProvider.PROVIDER_ID
      ]
    }

    const ui = new firebaseUI.auth.AuthUI(this.$firebase.auth())
    ui.start('#firebaseui-auth-container', config)

    this.$firebase.auth().onAuthStateChanged((user) => {
      this.login(user)
    })
  },

  methods: {
    handleLogout() {
      this.logout()
      this.$firebase.auth().signOut()
    },
    ...mapActions('auth', ['login', 'logout'])
  }
})
</script>

<style scoped>
p.token {
  word-break: break-all;
}
</style>
