package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;

/**
 * @author matrixelement
 */
public class ActivateAccountContract {

    public interface View extends IView {

        String getPhoneNumber();

        String getSMSCode();

        String getCountryCode();

        void showPhoneNumberError(String errorMsg);

        void showSMSCodeError(String errorMsg);

        void setActivateButtonEnable(boolean enable);

        void updateCountryCode(String countryCode);

        void startCountDown();
    }

    public interface Presenter extends IPresenter<View> {

        void activateAccount();

        boolean checkPhoneNumber();

        boolean checkSMSCode();

        boolean checkParams();

        void checkActivateButton();

        void updateCountryCode(String countryCode);

        void loadCurrentCountryCode();

        void getSMSCode();


    }
}
