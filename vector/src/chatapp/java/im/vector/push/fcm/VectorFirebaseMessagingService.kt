/*
 * Copyright 2019 New Vector Ltd
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.push.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.android.volley.*
import com.chatapp.Settings
import com.chatapp.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonParser
import im.vector.Matrix
import im.vector.R
import im.vector.VectorApp
import im.vector.notifications.NotifiableEventResolver
import im.vector.notifications.NotifiableMessageEvent
import im.vector.notifications.SimpleNotifiableEvent
import im.vector.push.PushManager
import im.vector.services.EventStreamServiceX
import im.vector.ui.badge.BadgeProxy
import org.matrix.androidsdk.MXSession
import org.matrix.androidsdk.core.Log
import org.matrix.androidsdk.rest.model.Event
import org.matrix.androidsdk.rest.model.bingrules.BingRule


/**
 * Class extending FirebaseMessagingService.
 */
class VectorFirebaseMessagingService : FirebaseMessagingService() {

    private val notifiableEventResolver by lazy {
        NotifiableEventResolver(this)
    }

    // UI handler
    private val mUIHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    /**
     * Called when message is received.
     *
     * @param message the message
     */
    override fun onMessageReceived(message: RemoteMessage) {

//        if (BuildConfig.LOW_PRIVACY_LOG_ENABLE) {
        Log.i(LOG_TAG, "## onMessageReceived()" + message.data.toString())
//            Log.i(LOG_TAG, "## onMessageReceived() from FCM with priority " + message.priority)
//        }
        try {
            val pm: PowerManager =
                applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenOn = pm.isInteractive // check if screen is on

            if (!isScreenOn) {
                val wl = pm.newWakeLock(
                    PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "myApp:notificationLock"
                )
                wl.acquire(3000) //set your time in milliseconds
            }
        } catch (e: java.lang.Exception) {
        }
        //safe guard
        val pushManager = Matrix.getInstance(applicationContext).pushManager
        if (!pushManager.areDeviceNotificationsAllowed()) {
            Log.i(LOG_TAG, "## onMessageReceived() : the notifications are disabled")
            return
        }
        var processed = false

        if (message.data.isNotEmpty()) {
            try {

                val msgType = message.data["MsgType"].toString()
                if (msgType == "INCOMING_ALERT") {
                    Settings.PushMsgID = message.messageId.toString()

                    if (Settings.PushMsgID != "") {
                        try {
                            val fullScreenIntent = Intent(this, SplashActivity::class.java)
                            val fullScreenPendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                                PendingIntent.getActivity(
                                    this, 0,
                                    fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
                                )
                            } else {
                                PendingIntent.getActivity(
                                    this, 0,
                                    fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            }
                            val mNotificationManager =
                                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val channelID = "1234"
                            val mChannel: NotificationChannel
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mChannel = NotificationChannel(
                                    channelID,
                                    "Incoming",
                                    NotificationManager.IMPORTANCE_HIGH
                                )
                                mChannel.enableLights(true)
                                mChannel.description = "Incoming Call"
                                val audioAttributes: AudioAttributes = AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                                    .build()
                                mChannel.setSound(
                                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
                                    audioAttributes
                                )
                                mNotificationManager.createNotificationChannel(mChannel)
                            }
                            val notificationBuilder =
                                NotificationCompat.Builder(this, channelID)
                                    .setSmallIcon(R.drawable.incoming_call_notification_transparent)
                                    .setContentTitle(getString(R.string.app_name))
                                    .setContentText("Incoming call")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_CALL)
                                    .setAutoCancel(true)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE))
                                    .setFullScreenIntent(fullScreenPendingIntent, true)
                                    .addAction(
                                        R.drawable.incoming_call_notification_transparent,
                                        "Accept",
                                        fullScreenPendingIntent
                                    )

                            val incomingCallNotification = notificationBuilder.build()
                            mNotificationManager.notify(1234, incomingCallNotification)

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                    processed = true
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        //TODO if the app is in foreground, we could just ignore this. The sync loop is already going?
        if (!processed)
            mUIHandler.post { onMessageReceivedInternal(message.data, pushManager) }

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    override fun onNewToken(refreshedToken: String) {
        Log.i(LOG_TAG, "onNewToken: FCM Token has been updated")
        FcmHelper.storeFcmToken(this, refreshedToken)
        Matrix.getInstance(this)?.pushManager?.resetFCMRegistration(refreshedToken)
    }

    override fun onDeletedMessages() {
        Log.d(LOG_TAG, "## onDeletedMessages()")
    }

    /**
     * Internal receive method
     *
     * @param data Data map containing message data as key/value pairs.
     * For Set of keys use data.keySet().
     */
    private fun onMessageReceivedInternal(data: Map<String, String>, pushManager: PushManager) {
        try {
//            if (BuildConfig.LOW_PRIVACY_LOG_ENABLE) {
            Log.i(LOG_TAG, "## onMessageReceivedInternal() : $data")
//            }
            // update the badge counter
            val unreadCount = data["unread"]?.let { Integer.parseInt(it) } ?: 0
            BadgeProxy.updateBadgeCount(applicationContext, unreadCount)

            val session = Matrix.getInstance(applicationContext)?.defaultSession

            if (VectorApp.isAppInBackground() && !pushManager.isBackgroundSyncAllowed) {
                //Notification contains metadata and maybe data information
                handleNotificationWithoutSyncingMode(data, session)
            } else {
                // Safe guard... (race?)
                if (isEventAlreadyKnown(data["event_id"], data["room_id"])) return
                //Catch up!!
                EventStreamServiceX.onPushReceived(this)
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "## onMessageReceivedInternal() failed : " + e.message, e)
        }
    }

    // check if the event was not yet received
    // a previous catchup might have already retrieved the notified event
    private fun isEventAlreadyKnown(eventId: String?, roomId: String?): Boolean {
        if (null != eventId && null != roomId) {
            try {
                val sessions = Matrix.getInstance(applicationContext).sessions

                if (null != sessions && sessions.isNotEmpty()) {
                    for (session in sessions) {
                        if (session.dataHandler.store?.isReady == true) {
                            session.dataHandler.store?.getEvent(eventId, roomId)?.let {
                                Log.e(
                                    LOG_TAG,
                                    "## isEventAlreadyKnown() : ignore the event " + eventId
                                            + " in room " + roomId + " because it is already known"
                                )
                                return true
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    LOG_TAG,
                    "## isEventAlreadyKnown() : failed to check if the event was already defined " + e.message,
                    e
                )
            }

        }
        return false
    }

    private fun handleNotificationWithoutSyncingMode(
        data: Map<String, String>,
        session: MXSession?
    ) {

        if (session == null) {
            Log.e(LOG_TAG, "## handleNotificationWithoutSyncingMode cannot find session")
            return
        }
        val notificationDrawerManager = VectorApp.getInstance().notificationDrawerManager

        // The Matrix event ID of the event being notified about.
        // This is required if the notification is about a particular Matrix event.
        // It may be omitted for notifications that only contain updated badge counts.
        // This ID can and should be used to detect duplicate notification requests.
        val eventId = data["event_id"] ?: return //Just ignore


        val eventType = data["type"]
        if (eventType == null) {
            //Just add a generic unknown event
            val simpleNotifiableEvent = SimpleNotifiableEvent(
                session.myUserId,
                eventId,
                true, //It's an issue in this case, all event will bing even if expected to be silent.
                title = getString(R.string.notification_unknown_new_event),
                description = "",
                type = null,
                timestamp = System.currentTimeMillis(),
                soundName = BingRule.ACTION_VALUE_DEFAULT,
                isPushGatewayEvent = true
            )
            notificationDrawerManager.onNotifiableEventReceived(simpleNotifiableEvent)
            notificationDrawerManager.refreshNotificationDrawer(null)

            return
        } else {

            val event = parseEvent(data)
            if (event?.roomId == null) {
                //unsupported event
                Log.e(LOG_TAG, "Received an event with no room id")
                return
            } else {

                val notifiableEvent = notifiableEventResolver.resolveEvent(
                    event,
                    null,
                    session.fulfillRule(event),
                    session
                )

                if (notifiableEvent == null) {
                    Log.e(LOG_TAG, "Unsupported notifiable event $eventId")
//                    if (BuildConfig.LOW_PRIVACY_LOG_ENABLE) {
                    Log.e(LOG_TAG, "--> ${event}")
//                    }
                } else {


                    if (notifiableEvent is NotifiableMessageEvent) {
                        if (TextUtils.isEmpty(notifiableEvent.senderName)) {
                            notifiableEvent.senderName = data["sender_display_name"]
                                ?: data["sender"] ?: ""
                        }
                        if (TextUtils.isEmpty(notifiableEvent.roomName)) {
                            notifiableEvent.roomName = findRoomNameBestEffort(data, session) ?: ""
                        }
                    }

                    notifiableEvent.isPushGatewayEvent = true
                    notifiableEvent.matrixID = session.myUserId
                    notificationDrawerManager.onNotifiableEventReceived(notifiableEvent)
                    notificationDrawerManager.refreshNotificationDrawer(null)
                }
            }
        }
    }

    private fun findRoomNameBestEffort(data: Map<String, String>, session: MXSession?): String? {
        var roomName: String? = data["room_name"]
        val roomId = data["room_id"]
        if (null == roomName && null != roomId) {
            // Try to get the room name from our store
            if (session?.dataHandler?.store?.isReady == true) {
                val room = session.dataHandler.getRoom(roomId)
                roomName = room.getRoomDisplayName(this)
            }
        }
        return roomName
    }

    /**
     * Try to create an event from the FCM data
     *
     * @param data the FCM data
     * @return the event
     */
    private fun parseEvent(data: Map<String, String>?): Event? {
        // accept only event with room id.
        if (null == data || !data.containsKey("room_id") || !data.containsKey("event_id")) {
            return null
        }

        try {
            val event = Event()
            event.eventId = data["event_id"]
            event.sender = data["sender"]
            event.roomId = data["room_id"]
            event.setType(data["type"])
            event.originServerTs = System.currentTimeMillis()

            if (data.containsKey("content")) {
                event.updateContent(JsonParser().parse(data["content"]).asJsonObject)
            }

            return event
        } catch (e: Exception) {
            Log.e(LOG_TAG, "buildEvent fails " + e.localizedMessage, e)
        }

        return null
    }

    companion object {
        private val LOG_TAG = VectorFirebaseMessagingService::class.java.simpleName
    }
}
