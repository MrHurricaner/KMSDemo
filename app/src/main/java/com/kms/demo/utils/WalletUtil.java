package com.kms.demo.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juzix.kms.HexUtil;
import com.juzix.kms.SecureRandomUtils;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.web3j.crypto.Keys.ADDRESS_LENGTH_IN_HEX;
import static org.web3j.crypto.Keys.PRIVATE_KEY_LENGTH_IN_HEX;

/**
 * @author matrixelement
 */
public class WalletUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private WalletUtil() {

    }

    public static String generateRandom(int num) {
        return HexUtil.getHexString(secureRandom.generateSeed(num));
    }

    public static WalletFile loadWalletFileByJson(String json) throws Exception {
        return objectMapper.readValue(json, WalletFile.class);
    }

    public static String writeWalletFileAsString(WalletFile walletFile) throws Exception {
        return objectMapper.writeValueAsString(walletFile);
    }

    public static String getWalletFileName(String address) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("'UTC--'yyyy-MM-dd'T'HH-mm-ss.SSS'--'");
        return dateFormat.format(new Date()) + address + ".json";
    }

    public static String generateWalletKey(String privateKey, String password) {
        try {
            ECKeyPair ecKeyPair = ECKeyPair.create(HexUtil.hexToByteArray(privateKey));
            WalletFile walletFile = org.web3j.crypto.Wallet.createLight(password, ecKeyPair);
            return writeWalletFileAsString(walletFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String exportPrivateKey(com.kms.demo.entity.Wallet wallet, String password) {
        try {
            ECKeyPair ecKeyPair = decrypt(wallet.getKey(), password);
            if (ecKeyPair == null) {
                return null;
            }
            return Numeric.toHexStringNoPrefix(ecKeyPair.getPrivateKey());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }

    public static ECKeyPair decrypt(String keystore, String password) {
        try {
            return Wallet.decrypt(password, loadWalletFileByJson(keystore));
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    public static boolean isValidPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
    }

    /**
     * 通过公钥获取钱包地址
     *
     * @param publicKey
     * @return
     */
    public static String getWalletAddressByPublicKey(String publicKey) {
        return Keys.getAddress(publicKey);
    }

    /**
     * 通过私钥获取公钥
     *
     * @param privateKey
     * @return
     */
    public static String getPublicKeyByPrivateKey(String privateKey) {
        BigInteger bigInteger = new BigInteger(HexUtil.hexToByteArray(privateKey));
        return Sign.publicKeyFromPrivate(bigInteger).toString(16);
    }

    /**
     * @param input
     * @return
     */
    public static boolean isValidAddress(String input) {

        String cleanInput = Numeric.cleanHexPrefix(input);

        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }

        return cleanInput.length() == ADDRESS_LENGTH_IN_HEX;
    }

}
