package mmm.i7bachelor_smartsale.app.Utilities;

public class Constants {

    //Activities
    //CreateSale
    public static final String CREATE_SALE_ACTIVITY = "CreateSaleActivity";
    public static final String EMPTY_CART_PNG = "emptycart.png";

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
    public static final String NEW_PAYMENT_URL = "https://api.sandbox.mobilepay.dk/pos/v10/payments";
    public static final String CLIENT_ID = "1170825e-c923-47c2-bdb7-ef35c7967efc";
    public static final String CLIENT_CREDENTIALS_SECRET = "sE5wD8qP1lQ8uM5wJ0uO0nE3kR8aU5iA2oI5iK0eQ6tB1kN0uL";
    public static final String MERCHANT_VAT = "DK90000093";
    public static final String BEACON_ID = "147025836912345";
    public static final String CORRELATION_ID = "70bd51d7-da28-46b0-b26f-d19e2a44490a";
    public static final String POS_ID = "5e6bbcc6-154c-44bb-9a82-45acc1aaea7b";



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
