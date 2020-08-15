package work.calmato.prestopay.util

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import work.calmato.prestopay.R

fun startHttpConnection(v: View,t:TextView,c:Context) {
  v.isEnabled = false
  t.visibility = View.VISIBLE
  t.startAnimation(AnimationUtils.loadAnimation(c, R.anim.blink))
}

fun finishHttpConnection(v: View,t:TextView){
  v.isEnabled = true
  t.clearAnimation()
  t.visibility = View.INVISIBLE
}

fun startHttpConnectionMenu(v: MenuItem,t:TextView,c:Context) {
  v.isEnabled = false
  t.visibility = View.VISIBLE
  t.startAnimation(AnimationUtils.loadAnimation(c, R.anim.blink))
}

fun finishHttpConnectionMenu(v: MenuItem,t:TextView){
  v.isEnabled = true
  t.clearAnimation()
  t.visibility = View.INVISIBLE
}
