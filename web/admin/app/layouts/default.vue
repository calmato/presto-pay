<template>
  <v-app>
    <v-app-bar color="primary" app dark>
      <v-toolbar-title>Presto Pay</v-toolbar-title>
      <v-spacer />
    </v-app-bar>

    <v-menu left bottom>
      <template v-slot:activator="{ on }">
        <v-btn icon v-on="on">
          <v-icon>mdi-dots-vertical</v-icon>
        </v-btn>
      </template>

      <v-list v-if="token != ''">
        <v-list-item disabled @click="handleLogout">
          ログアウト
        </v-list-item>
      </v-list>
    </v-menu>

    <v-content>
      <v-container>
        <nuxt />
      </v-container>
    </v-content>

    <v-footer app fixed>
      <span>&copy; Calmato</span>
    </v-footer>
  </v-app>
</template>

<script lang="ts">
import Vue from 'vue'
import { mapActions, mapGetters } from 'vuex'

export default Vue.extend({
  computed: {
    ...mapGetters('auth', ['token'])
  },

  methods: {
    handleLogout(): void {
      this.logout()
      this.$firebase.auth().signOut()
    },
    ...mapActions('auth', ['login', 'logout'])
  }
})
</script>
