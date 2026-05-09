package stop_delaying.utils.notifications_and_scheduling;

/**
 * Constants for intent extra keys used when scheduling and displaying notifications.
 */
class NotifExtraIntentNames {
    // --- Intent Extra Keys (Constants for Generic Alarms) ---
    /** Unique ID for the notification. */
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    /** The ID of the notification channel. */
    public static final String EXTRA_CHANNEL_ID = "channel_id";
    /** The user-visible name of the channel. */
    public static final String EXTRA_CHANNEL_NAME = "channel_name";
    /** The description of the channel. */
    public static final String EXTRA_CHANNEL_DESCRIPTION = "channel_description";
    /** The title displayed in the notification. */
    public static final String EXTRA_NOTIFICATION_TITLE = "notification_title";
    /** The main text content of the notification. */
    public static final String EXTRA_NOTIFICATION_DESCRIPTION = "notification_description";
    /** Resource ID for the notification icon. */
    public static final String EXTRA_SMALL_ICON = "small_icon";
    /** The priority level for the notification. */
    public static final String EXTRA_NOTIFICATION_PRIORITY = "notification_priority";
    /** The intent to trigger when the notification is clicked. */
    public static final String EXTRA_INTENT = "intent";
}
