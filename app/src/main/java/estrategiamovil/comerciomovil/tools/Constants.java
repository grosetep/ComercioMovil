package estrategiamovil.comerciomovil.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import estrategiamovil.comerciomovil.R;
import estrategiamovil.comerciomovil.modelo.TypeNotification;

/**
 * Clase que contiene los códigos usados en "Comercio Movil" para
 * mantener la integridad en las interacciones entre actividades
 * y fragmentos
 */
public class Constants {

    public static final String PUERTO_HOST = "";//:63343
    /**
     * Dirección IP de genymotion o AVD
     */

    //desarrollo
    //private static final String IP = "10.0.3.2";//emulador de android
    //produccion
    public static final String IP = "mcommerce.estrategiamovilmx.com";
    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    /**
     * URLs del Web Service
     */
    /*Publications*/
    public static final String GET_PUBLICATIONS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getPublications.php";
    /*Purchases*/
    public static final String GET_PURCHASES = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getPurchases.php";
    /*Sales*/
    public static final String GET_SALES = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getSales.php";
    /*Categories*/
    public static final String GET_CATEGORIES = HTTP + IP + PUERTO_HOST + "/cmovilservice/getCategories.php";
    /*Countries*/
    public static final String GET_COUNTRIES = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getCountries.php";
    /*Cities*/
    public static final String GET_CITIES = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getCities.php";
    /*Sections*/
    public static final String GET_SECTIONS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getSection.php";
    /* User: methods: existUser, sign_up, login etc..*/
    public static final String USERS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/userOperations.php";

    public static final String SIGNUP_SUBSCRIBER = HTTPS + IP + PUERTO_HOST + "/cmovilservice/signup_subscriber.php";
    /* Sector*/
    public static final String GET_SECTORS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getSectors.php";
    /* Upload images*/
    public static final String UPLOAD_IMAGE = HTTP + IP + PUERTO_HOST + "/cmovilservice/uploadImage.php";
    /* Upload image voucher*/
    public static final String UPLOAD_VOUCHER_IMAGE = HTTP + IP + PUERTO_HOST + "/cmovilservice/uploadVoucherImage.php";
    /* url images profiles*/
    public static final String GET_PROFILE_IMAGE = HTTP + IP + PUERTO_HOST + "/marketing/profile_images/";
    /* url locations*/
    public static final String GET_ADDRESS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getAddress.php";
    /* Upload images publication*/
    public static final String UPLOAD_IMAGES_PUBLICATIONS = HTTP + IP + PUERTO_HOST + "/cmovilservice/uploadImagesPublications.php";
    /* Notifications*/
    public static final String NOTIFICATIONS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/notifications.php";
    /* Business*/
    public static final String GET_BUSINESS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getBusiness.php";
    /* Customers*/
    public static final String GET_CUSTOMERS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getCustomers.php";
    /* Shopping Card*/
    public static final String GET_FAVORITES = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getFavorites.php";
    /* Email Operations*/
    public static final String EMAIL = HTTPS + IP + PUERTO_HOST + "/cmovilservice/emailOperations.php";
    /* Remove Publication*/
    public static final String REMOVE_PUBLICATION = HTTP + IP + PUERTO_HOST + "/cmovilservice/removePublication.php";
    /* get price suscriptions*/
    public static final String SUSCRIPTIONS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getSuscription.php";
    /* Remove User*/
    public static final String REMOVE_USER = HTTPS + IP + PUERTO_HOST + "/cmovilservice/removeUser.php";
    /*  Payment */
    public static final String PAYMENT= HTTPS + IP + PUERTO_HOST + "/cmovilservice/payment.php";
    /* administration */
    public static final String ADMINISTRATION= HTTPS + IP + PUERTO_HOST + "/cmovilservice/administrator_operations.php";
    /* quetions*/
    public static final String GET_QUESTIONS = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getQuestions.php";
    /*Categories Help*/
    public static final String GET_CATEGORIES_HELP = HTTPS + IP + PUERTO_HOST + "/cmovilservice/getCategoryHelp.php";
    /**
     *
     * Clave para el valor extra que representa al identificador de una meta
     */
    public static final String CATEGORY_SELECTED = "category_selected";
    public static final String SUBCATEGORY_SELECTED = "subcategory_selected";
    public static final String SUBSUBCATEGORY_SELECTED = "subsubcategory_selected";
    public static final String ADDRESS_SELECTED = "address_selected";
    public static final String DESCRIPTION_SELECTED = "detailed_description_selected";
    public static final String OUTSTANDING_SELECTED = "outstanging_selected";
    public static final String CHARACTERISTICS_SELECTED = "characteristics_selected";
    public static final String CONNECTIVITY_DIALOG_SHOWED = "connectivity_dialog";
    public static final String GALLERY = "gallery";
    public static final int MY_SOCKET_TIMEOUT_MS = 15000;
    /* Preferences*/
    public static final String cityUser = "cityUser";
    public static final String nameCityUser = "nameCityUser";
    public static final String countryUser = "countryUser";
    public static final String nameCountryUser = "nameCountryUser";
    public static final String categoriesUser = "categoriesUser";
    public static final String sectionNames = "sectionNames";
    public static final String topicsUser = "topicsUser";
    public static final String topicEmergent = "emergent";
    public static final String topicMerchant = "merchants";
    public static final String defaultMessageNotif = "defaultMessageNotif";
    public static final String showTipImage = "showTipImage";
    public static final String showTipSignupSuscriber = "showTipSignupSuscriber";
    public static final String idCategoryProducts = "products_category";
    /* Flows */
    public static final String flowChangeCity = "flowChangeCity";
    public static final String flowSignupUser = "signupUser";
    /* Filtros */
    public static final String ads_category_selected = "ads_category_selected";
    public static final String ads_subcategory_selected = "ads_subcategory_selected";
    public static final String ads_sub_subcategory_selected = "ads_sub_subcategory_selected";
    public static final String ads_category_search = "ads_category_search";

    public static final String products_category_selected = "products_category_selected";
    public static final String products_subcategory_selected = "products_subcategory_selected";
    public static final String products_sub_subcategory_selected = "products_sub_subcategory_selected";

    public static final String business_category_name = "business_category_name";
    public static final String business_category_id = "business_category_id";
    public static final String business_subcategory_id = "business_subcategory_id";
    public static final String business_sub_subcategory_id = "business_sub_subcategory_id";
    public static final String business_id_city = "business_id_city";
    public static final String business_city = "business_city";
    public static final String find_business_module_created = "find_business_module_created";
    /* Intents Results */
    public static final int REQUEST_SIGNUP = 0;
    public static final int REQUEST_SIGNUP_SUBSCRIBER =1;
    public static final int SIGNUP_SUBSCRIBER_OK =2;
    public static final int REQUEST_LOCATIONS = 3;
    /* Avatar */
    public static final String localAvatarPath = "localAvatarPath";
    /*LogIn */
    public static final String localUserId = "userId";
    public static final String localEmail = "email";
    public static final String localPassword = "password";
    public static final String localUserAdministrator = "administrator";
    public static final String isFirstTimeUser = "isFirstTimeUser";
    /* local tables*/
    public static final String user_table = "user";
    public static final String suscription_table = "suscription";
    public static final String seedValue = "weespare190620172130";
    public static final String colorBackgroundAnimation = "#3303A9F4";
    public static final String colorError = "#FF9E80";
    public static final String colorSuccess = "#7fc120";
    public static final String colorWarning = "#ffff8800";
    public static final String colorTextGray = "#898989";


    public static final String colorBackgroundDisabled = "#EBEBEB";
    public static final String colorBackgroundEnabled = "#FFFFFF";
    public static final String colorTitle = "#03A9F4";
    public static final String colorNegro = "#000000";
    public static final String imageCategoryInLocalPath = "inApp";
    /* token*/
    public static final String firebase_token = "firebase_token";
    public static final String publication_added = "publication_added";
    //public static final String local_idCategory_Ads="IdCategoryAds";
    public static final String type_publication_ads="ads";
    public static final String type_publicacion_cupons="cupons";
    public static final String search_flow="search_flow";
    public static final String find_flow="find_flow";
    public static final String CITY_SELECTED = "city_selected";
    public static final String user_cupon_selected = "user_cupon_selected";
    public static final String user_current_purchase = "user_current_purchase";
    /* Publications*/
    public static final String status_active = "activa";
    public static final String status_paused = "suspendida";
    public static final String status_pending = "pendiente";
    public static final String status_rejected = "rechazada";
    public static final String status_new = "nueva";

    /* Suscriptions*/
    public static final String free_period = "4";
    /* User status inactive*/
    public static final String status_user_inactive = "inactivo";

    public static final String suscription_cupons = "2";
    /* Users */
    public static final String user_type_merchant = "2";
    public static final String user_type_merchant_desc = "suscriptor";
    /* resource types*/
    public static final String resource_local= "local";
    public static final String resource_remote = "remote";
    /*status payments*/
    public static final Map<String, String> estatus_mercado_pago = new HashMap<String, String>()
    {{
        put("1","Pendiente");
        put("2","Aprobado");
        put("3","En progreso");
        put("4","En mediacion");
        put("5","Rechazado");
        put("6","Cancelado");
        put("7","Devolución");
        put("8","Contracargo");
    }};
    public static final Map<String, TypeNotification> notifycation_types = new HashMap<String, TypeNotification>()
    {{
        put("customer",new TypeNotification("customer","1"));//reply for future release
        put("publication",new TypeNotification("publication","2"));//no reply
        put("administration",new TypeNotification("administration","3"));//no reply
        put("merchant",new TypeNotification("merchant","4"));//no reply
    }};
    public static final int cero = 0;
    public static final int one = 1;
    public static final int load_more_tax = 10;
    public static final int load_more_tax_extended = 15;
    public static final int publication_images_number = 5;
    public static final int SQLiteDatabase_version = 2;

    public static final String direct_deposit = "3";
}
