package work.calmato.prestopay.ui.notificationSet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import work.calmato.prestopay.R
import work.calmato.prestopay.databinding.FragmentNotificationSetBinding

class NotificationSetFragment:Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding: FragmentNotificationSetBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_notification_set, container, false
    )
    return binding.root
  }
}
