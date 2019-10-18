package com.kms.demo.app;

import android.Manifest;

/**
 * @author matrixelement
 */
public class Constants {

    public final static class Extra {
        public final static String EXTRA_PRIVATE_KEY = "extra_private_key";
        public final static String EXTRA_PASSWORD = "extra_password";
        public final static String EXTRA_KEYSTORE = "extra_keystore";
        public final static String EXTRA_WALLET = "extra_walletEntity";
        public final static String EXTRA_ADDRESS = "extra_address";
        public final static String EXTRA_TRANSACTION = "extra_transaction";
        public final static String EXTRA_AVATAR = "extra_avatar";
        public final static String EXTRA_HASH = "extra_hash";
        public final static String EXTRA_MNEMONIC = "extra_mnemonic";
        public final static String EXTRA_PIC = "extra_pic";
        public final static String EXTRA_SCAN_QRCODE_DATA = "extra_scan_qrcode_data";
        public final static String EXTRA_TYPE = "extra_type";
        public final static String EXTRA_SHARED_OWNERS = "extra_shared_owners";
        public final static String EXTRA_REQUIRED_SIGNATURES = "extra_required_signatures";
        public final static String EXTRA_WALLET_NAME = "extra_wallet_name";
        public final static String EXTRA_COUNTRY_CODE = "extra_country_code";
        public final static String EXTRA_SEND_AMOUNT = "extra_send_amount";
        public final static String EXTRA_FEE_AMOUNT = "extra_fee_amount";
        public final static String EXTRA_NOTE = "extra_note";
        public final static String EXTRA_NODE = "extra_node";
        public final static String EXTRA_PRE_SELECTED_NODE = "extra_pre_selected_node";
        public final static String EXTRA_NEW_SELECTED_NODE = "extra_new_selected_node";
        public final static String EXTRA_SELECTED_NODE_ID = "extra_selected_node_id";
    }

    public final static class Bundle {

        public final static String BUNDLE_X_OFFSET = "bundle_x_offset";
        public final static String BUNDLE_Y_OFFSET = "bundle_y_offset";
        public final static String BUNDLE_WALLET = "bundle_wallet";
        public final static String BUNDLE_TRANSFER_AMOUNT = "bundle_transfer_amount";
        public final static String BUNDLE_TO_ADDRESS = "bundle_to_address";
        public final static String BUNDLE_FEE_AMOUNT = "bundle_fee_amount";
        public final static String BUNDLE_UUID = "bundle_UUID";
        public final static String BUDLE_PASSWORD = "bundle_password";
        public final static String BUNDLE_CONFIRM_NOTICE = "bundle_confirm_notice";
        public final static String BUNDLE_LAYOUT_RES = "bundle_layout_res";
        public final static String BUNDLE_PHONE_NUMBER = "bundle_phone_number";
        public final static String BUNDLE_WALLET_LIST = "bundle_wallet_list";
        public final static String BUNDLE_KYC_OPERATION = "bundle_kyc_operation";
        public final static String BUNDLE_SHARE_APPINFO_LIST = "bundle_share_appinfo_list";
        public final static String BUNDLE_COUNTRY_CODE = "bundle_country_code";
    }

    public final static class Action {

        public final static String ACTION_NONE = "action_done";
        public final static String ACTION_GET_ADDRESS = "action_get_address";
        public final static String ACTION_SWITCH_LANGUAGE = "action_switch_language";
        public final static String ACTION_RESTORE_ESCROW_WALLET = "action_restore_escrow_wallet";
        public final static String ACTION_CREATE_ESCROW_WALLET = "action_create_escrow_wallet";
    }

    public final static class Preference {
        public final static String KEY_SERVICE_TERMS_FLAG = "serviceTermsFlag";
        public final static String KEY_OPERATE_MENU_FLAG = "operateMenuFlag";
        public final static String KEY_LANGUAGE = "language";
        public final static String KEY_FACE_TOUCH_ID_FLAG = "faceTouchIdFlag";
        public final static String KEY_FIRST_ENTER = "key_first_enter";
        public final static String KEY_DEVICE_ID= "key_device_id";
    }

    public final static class Permission {
        public final static String[] WRITE_STORAG = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    public final static class RequestCode {

        public final static int REQUEST_CODE_SELEECT_WALLET_ADDRESS = 1;
        public final static int REQUEST_CODE_SELEECT_WALLET = 2;
        public final static int REQUEST_CODE_EDIT_ADDRESS = 3;
        public final static int REQUEST_CODE_ADD_ADDRESS = 4;
        public final static int REQUEST_CODE_GET_ADDRESS = 5;
        public final static int REQUEST_CODE_SCAN_QRCODE = 6;
        public final static int REQUEST_CODE_GET_REGION_NUMBER = 7;
        public final static int REQUEST_CODE_ADD_NODE = 8;

    }

    public final static class Realm {

        public final static String PORTAL = "portal.realm";
    }

    public final static class Api {

        public final static String URL_APP_SERVER_BASE = "http://58.250.250.234:18080/";
        public final static String URL_KMS_BASE_URL = "http://58.250.250.233:18080/";
        public final static String URL_WEB3J = "https://rinkeby.infura.io/v3/c22152c1fa194298a92f8c064c95a565";

        public final static String NAME_KMS_URL = "kms_api";
    }
}
