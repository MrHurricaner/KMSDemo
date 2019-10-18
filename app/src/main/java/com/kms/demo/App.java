package com.kms.demo;

import android.content.Context;

import com.kms.appcore.CoreApp;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.app.AppFramework;
import com.kms.demo.app.Constants;
import com.kms.demo.config.AppSettings;

import androidx.multidex.MultiDex;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * @author matrixelement
 */
public class App extends CoreApp {

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        //初始化realm
        initRealm(context);
        //初始化RUtils
        RUtils.init(context);
        //初始化偏好设置
        AppSettings.getInstance().init(context);
        //业务相关的初始化
        AppFramework.getAppFramework().initAppFramework(this);
    }

    private void initRealm(Context context) {
        Realm.init(context);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .assetFile(Constants.Realm.PORTAL)
                .name(Constants.Realm.PORTAL)
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        if (oldVersion == 0) {
                            // Create a WalletEntity class
                            RealmSchema schema = realm.getSchema();
                            schema.create("WalletEntity")
                                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                                    .addField("name", String.class)
                                    .addField("createTime", long.class)
                                    .addField("updateTime", long.class)
                                    .addField("isBackuped", boolean.class)
                                    .addField("avatar", int.class)
                                    .addField("symbol", String.class)
                                    .addField("userPhone", String.class)
                                    .addField("key", String.class)
                                    .addField("pk", String.class)
                                    .addField("multPk", String.class)
                                    .addField("multSk", String.class)
                                    .addField("sk2", String.class);

                            schema.create("TransactionEntity")
                                    .addField("id", String.class, FieldAttribute.PRIMARY_KEY)
                                    .addField("hash", String.class)
                                    .addField("createTime", long.class)
                                    .addField("endTime", long.class)
                                    .addField("walletName", String.class)
                                    .addField("from", String.class)
                                    .addField("to", String.class)
                                    .addField("note", String.class)
                                    .addField("avatar", int.class)
                                    .addField("transactionStatusCode", int.class)
                                    .addField("feeAmount", double.class)
                                    .addField("sendAmount", double.class)
                                    .addField("blockNumber", long.class)
                                    .addField("latestBlockNumber", long.class);

                            schema.create("NodeEntity")
                                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                                    .addField("nodeAddress", String.class)
                                    .addField("isDefaultNode", boolean.class)
                                    .addField("isChecked", boolean.class);

                            oldVersion++;
                        }
                    }
                })
                .build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //因为引用的包过多，实现多包问题
        MultiDex.install(this);
    }

    @Override
    protected String getConfiguredReleaseType() {
        return null;
    }
}
