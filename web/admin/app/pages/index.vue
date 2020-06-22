<template>
  <v-layout column justify-center align-center>
    <v-flex xs12 sm8 md6>
      <div class="text-center">
        <logo />
        <vuetify-logo />
      </div>
      <v-card>
        <v-card-title class="headline">
          Presto Pay - 管理者ページ
        </v-card-title>
        <v-card-text>
          <firebase-auth />
        </v-card-text>
      </v-card>
    </v-flex>
  </v-layout>
</template>

<script lang="ts">
import Vue from 'vue'
import FirebaseAuth from '~/components/FirebaseAuth.vue'
import Logo from '~/components/Logo.vue'
import VuetifyLogo from '~/components/VuetifyLogo.vue'

export default Vue.extend({
  components: {
    FirebaseAuth,
    Logo,
    VuetifyLogo
  }
})
</script>

<style>
.auth {
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}
</style>
