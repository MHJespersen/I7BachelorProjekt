package mmm.i7bachelor_smartsale.app.Utilities;

public class Constants {

    //Activities
    //CreateSale
    public static final String CREATE_SALE_ACTIVITY = "CreateSaleActivity";

    //ProfilePage
    public static final String PROFILE_PAGE_ACTIVITY = "ProfilePageActivity";

    //Details
    public static final String DETAILS_ACTIVITY = "DetailsActivity";
    public static final String EXTRA_ITEM_ID = "extra_itemId";
    public static final String EXTRA_COORDS = "extra_coords";
    //https://mobilepaydev.github.io/MobilePay-PoS-v10/pos_integratorauthentication#client_onboarding
    //These are all mobilepay smartsale project related
    public static final String SANDBOX_URL = "https://api.sandbox.mobilepay.dk/integrator-authentication/connect/token";
    public static final String PoS_CHECKIN_URL = "https://api.sandbox.mobilepay.dk/pos/app/usersimulation/checkin";
    public static final String ACCEPT_PAYMENT_URL = "https://api.sandbox.mobilepay.dk/pos/app/usersimulation/acceptpayment";
    public static final String NEW_PAYMENT_URL = "https://api.mobilepay.dk/pos/v10/payments";
    public static final String CLIENT_ID = "1170825e-c923-47c2-bdb7-ef35c7967efc";
    public static final String CLIENT_CREDENTIALS_SECRET = "hEt5IUrYrVY8pKnyp2SAOvWAqqpIzC3qqAAz9tOA3JE";
    public static final String MERCHANT_VAT = "DK90000093";
    public static final String BEACON_ID = "9522134410";



    //Inbox
    public static final String INBOX_ACTIVITY = "InboxActivity";
    public static final String EXTRA_INDEX = "EXTRA_INDEX";

    //Login
    public static final int REQUEST_LOGIN = 1337;

    //Maps
    public static final String MAPS_ACTIVITY = "MapsActivity";

    //Market


    //SendMessageActivity
    public static final String SEND_MESSAGE_ACTIVITY = "SendMessageActivity";
    public static final String DETAILS_TITLE = "Title";
    public static final String DETAILS_USER = "User";

    //ViewMessageActivity

    //Service
    public static final String SERVICE_NOTIFICATION_CHANNEL = "service_notification_channel";
    public static final int NOTIFICATION_ID = 42;
    public static final String NOTIFICATION_CHANNEL = "notification_channel";

}
