package com.kms.demo.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * @author matrixelement
 */
public class PhoneUtil {

    private PhoneUtil() {

    }

    /**
     * 根据国家代码和手机号  判断手机号是否有效
     *
     * @param phoneNumber
     * @param countryCode
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber, String countryCode) {
        Phonenumber.PhoneNumber pn = new Phonenumber.PhoneNumber();
        pn.setCountryCode(NumberParserUtil.parseInt(countryCode));
        pn.setNationalNumber(NumberParserUtil.parseLong(phoneNumber));
        return PhoneNumberUtil.getInstance().isValidNumber(pn);

    }
}
