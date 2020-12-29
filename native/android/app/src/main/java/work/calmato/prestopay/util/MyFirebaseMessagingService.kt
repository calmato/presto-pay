package work.calmato.prestopay.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
  /**
   * Called when message is received.
   *
   * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
   */
  // [START receive_message]
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    // TODO(developer): Handle FCM messages here.
    // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

    // Check if message contains a data payload.
    if (remoteMessage.data.isNotEmpty()) {
    }

    // Check if message contains a notification payload.
    remoteMessage.notification?.let {
    }

    // Also if you intend on generating your own notifications as a result of a received FCM
    // message, here is where that should be initiated. See sendNotification method below.
  }
  // [END receive_message]

  // [START on_new_token]
  /**
   * Called if InstanceID token is updated. This may occur if the security of
   * the previous token had been compromised. Note that this is called when the InstanceID token
   * is initially generated so this is where you would retrieve the token.
   */
  override fun onNewToken(token: String) {

    // If you want to send messages to this application instance or
    // manage this apps subscriptions on the server side, send the
    // Instance ID token to your app server.
    sendRegistrationToServer(token)
  }
  // [END on_new_token]

  /**
   * Persist token to third-party servers.
   *
   * Modify this method to associate the user's FCM InstanceID token with any server-side account
   * maintained by your application.
   *
   * @param token The new token.
   */
  private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
  }

  companion object {
    private const val TAG = "MyFirebaseMsgService"
  }
}
