<template>
  <div class="container">
    <div>
      <h1 class="title">管理用ページ</h1>
      <firebase-auth />
    </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue'
import FirebaseAuth from '~/components/FirebaseAuth.vue'

export default Vue.extend({
  components: {
    FirebaseAuth
  }
})
</script>

<style>
.container {
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.title {
  font-family: 'Quicksand', 'Source Sans Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto,
    'Helvetica Neue', Arial, sans-serif;
  display: block;
  font-weight: 300;
  font-size: 30px;
  color: #35495e;
  letter-spacing: 1px;
}
</style>
