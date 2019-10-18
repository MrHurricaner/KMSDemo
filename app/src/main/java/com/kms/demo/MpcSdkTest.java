package com.kms.demo;

import android.util.Log;

import com.juzix.kms.HexUtil;
import com.juzix.kms.NativeECDSA;
import com.juzix.kms.NativeECDSA.ReturnVerify;
import com.juzix.sdk.ComputeEcdsaKeyGen;
import com.juzix.sdk.ComputeEcdsaSign;
import com.juzix.sdk.MpcContext;
import com.juzix.sdk.MpcSdk;

import java.util.HashMap;
import java.util.Map;

public class MpcSdkTest {

	private static final String TAG = MpcSdkTest.class.getSimpleName();

	public static void main(String[] args) {
		Log.e(TAG,"test KeyGen begin");
		testECDSAKeyGen();
		Log.e(TAG,"test KeyGen end");
		Log.e(TAG,"test sign begin");
		testECDSASign();
		Log.e(TAG,"test sign begin");
	}


	/**
	 * 算法名称： 生成公钥
	 * 算法参与者： P1 和  P2
	 * 算法流程：
	 * 步骤           参与者               方法                                                    入参                                                                          出参
	 * 0     P1 or P2 createSession
	 * -----------------------------------------------------------------------------------
	 * 1     P1       step1               P1_SK（p1的私钥分量）
     * -----------------------------------------------------------------------------------
	 * 2     P2       step2               P2_SK（p2的私钥分量）                          MULT_PK（乘法器公钥，P2需要保存，签名逻辑中会使用）
	 *                                                             PUBKEY（公钥 ，P2需要保存，签名逻辑中会使用）
     * -----------------------------------------------------------------------------------
	 * 3     P1       step3                                        MULT_SK（乘法器私钥，P1需要保存，签名逻辑中会使用）
	 * 															   MULT_PK（乘法器公钥，P1需要保存，签名逻辑中会使用）
	 *  	       									               PUBKEY（公钥，P1需要保存，签名逻辑中会使用）
	 */
	public static void testECDSAKeyGen() {
		//声明计算参与方
		MpcSdk p1 = MpcSdk.getMpcSdk(ComputeEcdsaKeyGen.PARTY_P1);
		MpcSdk p2 = MpcSdk.getMpcSdk(ComputeEcdsaKeyGen.PARTY_P2);

//		如果集成的系统为分布式的，需要实现分布式的会话管理 （常用的为redis实现）
//		MpcSdk p2 = MpcSdk.getMpcSdk(ComputeEcdsaKeyGen.PARTY_P2, new SessionDao() {
//			@Override
//			public void update(Session session) {
//			}
//			@Override
//			public Session readSession(String sessionId) {
//				return null;
//			}
//			@Override
//			public void delete(Session session) {
//			}
//			@Override
//			public String create(Session session) {
//				return null;
//			}
//		});

		//step=0  生成计算的会话
		MpcContext mpcContext = p2.createSession(ComputeEcdsaKeyGen.COMPUTE_KEY);

		//step=1 P1 计算
		Map<String, Object> p1_input = new HashMap<>();
		p1_input.put(ComputeEcdsaKeyGen.P1_SK, "91929491211981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7");
		mpcContext = p1.nextStep(mpcContext.getToOthers(),p1_input);

		//step=2 P2 计算
		Map<String, Object> p2_input = new HashMap<>();
		p2_input.put(ComputeEcdsaKeyGen.P2_SK, "88998998899883FD0BC31D10FD780CFEBE6DB81B44828F310A301CA1DEC2F25A");
		mpcContext = p2.nextStep(mpcContext.getToOthers(),p2_input);
		Log.e(TAG,"KeyGen P2 MULT_PK = "+ mpcContext.getResult().get(ComputeEcdsaKeyGen.MULT_PK));
		Log.e(TAG,"KeyGen P2 PUBKEY = "+ mpcContext.getResult().get(ComputeEcdsaKeyGen.PUBKEY));

		//step=3 P1计算
		mpcContext = p1.nextStep(mpcContext.getToOthers());
		Log.e(TAG,"KeyGen P1 MULT_PK = "+ mpcContext.getResult().get(ComputeEcdsaKeyGen.MULT_PK));
		Log.e(TAG,"KeyGen P1 MULT_SK = "+ mpcContext.getResult().get(ComputeEcdsaKeyGen.MULT_SK));
		Log.e(TAG,"KeyGen P1 PUBKEY = "+ mpcContext.getResult().get(ComputeEcdsaKeyGen.PUBKEY));
	}


	/**
	 * 算法名称： 签名
	 * 算法参与者： P1 和  P2
	 * 算法流程：
	 * 步骤           参与者               方法                                                    入参                                                                          出参
	 * 0     P1 or P2 createSession
	 * -----------------------------------------------------------------------------------
	 * 1     P1       step1               DATA_HASH（待签名数据）
	 *                                    P1_SK（p1的私钥分量）
	 *                                    MULT_PK（乘法器公钥）
	 *                                    MULT_SK（乘法器私钥 ）
     * -----------------------------------------------------------------------------------
	 * 2     P2       step2               DATA_HASH（待签名数据）
	 *                                    P2_SK（p2的私钥分量）
	 *                                    MULT_PK（乘法器公钥）
     * -----------------------------------------------------------------------------------
	 * 3     P1       step3
	 * -----------------------------------------------------------------------------------
	 * 4     P2       step4
	 * -----------------------------------------------------------------------------------
	 * 5     P1       step5                                        SIGN_R（签名的R值）
	 *                                                             SIGN_S（签名的S值）
	 * -----------------------------------------------------------------------------------
	 * 6     P2       step6                                        SIGN_R（签名的R值）
	 *                                                             SIGN_S（签名的S值）
	 *
	 */
	public static void testECDSASign() {
		//声明计算参与方
		MpcSdk p1 = MpcSdk.getMpcSdk(ComputeEcdsaSign.PARTY_P1);
		MpcSdk p2 = MpcSdk.getMpcSdk(ComputeEcdsaSign.PARTY_P2);

//		如果集成的系统为分布式的，需要实现分布式的会话管理 （常用的为redis实现）
//		MpcSdk p2 = MpcSdk.getMpcSdk(ComputeEcdsaKeyGen.PARTY_P2, new SessionDao() {
//			@Override
//			public void update(Session session) {
//			}
//			@Override
//			public Session readSession(String sessionId) {
//				return null;
//			}
//			@Override
//			public void delete(Session session) {
//			}
//			@Override
//			public String create(Session session) {
//				return null;
//			}
//		});

		//生成计算的会话 step=0
		MpcContext mpcContext = p2.createSession(ComputeEcdsaSign.COMPUTE_KEY);

		String hash = "fdc93d07405854f4d2122ca88f73a11e400926ea43b9eb62b0a8ceea9da79563";

		//P1 计算 step=1
		Map<String, Object> p1_input = new HashMap<>();
//		p1_input.put(ComputeEcdsaSign.DATA_HASH, "A6E3C57DD01ABE90086538398355DD4C3B17AA873382B0F24D6129493D8AAD60");
		p1_input.put(ComputeEcdsaSign.DATA_HASH, hash);
		p1_input.put(ComputeEcdsaSign.P1_SK, "91929491211981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7");
		p1_input.put(ComputeEcdsaSign.MULT_PK, "D9BCB02D642185285E960E64B9BCF6DEFD90A12FB5879CDF711FBA09E4BD05A23E9185943985068FF70A8B21BEB76E8B3F073D2D5DDA5D1F70B1882F32636A80DA348DB00D8F1BF30A13F56E5F655CAB3E6DDEB20FC86B04F36577302962CA4C48C632690C68404C92350844C0A4963D822BB959CA8211AEECED31D710454A03");
		p1_input.put(ComputeEcdsaSign.MULT_SK, "D9BCB02D642185285E960E64B9BCF6DEFD90A12FB5879CDF711FBA09E4BD05A23E9185943985068FF70A8B21BEB76E8B3F073D2D5DDA5D1F70B1882F32636A7F020014D9E52E0BCB8A640B520894AFC969E7E8B9C230A11E998F33EB016F3F3DDE5DD973CF4AC34318F9D05AFD1A4A5B31C20D1B5FBAB4AD021B962EB9481610894B5AE2F6F4C12A86623C1AC5C97B0A46CED9B49541119D491E945284ABF36372D238BE63EAAACBACCFAE5B06CE2399F8AFA8E7178CB40D6A211A675EA2960E4C3443C9F40B66257310E3C273088F45202D57441D98D452AB853CF36367BDE3E621D4DB962A31706D2EDB0AB6A29D6D44D2460B59467C3F5223A1A9D020C353D9BCB02D642185285E960E64B9BCF6DEFD90A12FB5879CDF711FBA09E4BD05A23E9185943985068FF70A8B21BEB76E8B3F073D2D5DDA5D1F70B1882F32636A80DA348DB00D8F1BF30A13F56E5F655CAB3E6DDEB20FC86B04F36577302962CA4C48C632690C68404C92350844C0A4963D822BB959CA8211AEECED31D710454A03");
		mpcContext = p1.nextStep(mpcContext.getToOthers(),p1_input);

		//P2 计算 step=2
		Map<String, Object> p2_input = new HashMap<>();
		p2_input.put(ComputeEcdsaSign.DATA_HASH, hash);
		p2_input.put(ComputeEcdsaSign.P2_SK, "88998998899883FD0BC31D10FD780CFEBE6DB81B44828F310A301CA1DEC2F25A");
		p2_input.put(ComputeEcdsaSign.MULT_PK, "D9BCB02D642185285E960E64B9BCF6DEFD90A12FB5879CDF711FBA09E4BD05A23E9185943985068FF70A8B21BEB76E8B3F073D2D5DDA5D1F70B1882F32636A80DA348DB00D8F1BF30A13F56E5F655CAB3E6DDEB20FC86B04F36577302962CA4C48C632690C68404C92350844C0A4963D822BB959CA8211AEECED31D710454A03");
		mpcContext = p2.nextStep(mpcContext.getToOthers(),p2_input);

		//P1计算 step=3
		mpcContext = p1.nextStep(mpcContext.getToOthers());

		//P2计算 step=4
		mpcContext = p2.nextStep(mpcContext.getToOthers());

		//P1计算 step=5
		mpcContext = p1.nextStep(mpcContext.getToOthers());
		Log.e(TAG,"Sign P1 SIGN_R = "+ mpcContext.getResult().get(ComputeEcdsaSign.SIGN_R));
		Log.e(TAG,"Sign P1 SIGN_S = "+ mpcContext.getResult().get(ComputeEcdsaSign.SIGN_S));
		verifySign(mpcContext, hash);

		//P2计算 step=6
		mpcContext = p2.nextStep(mpcContext.getToOthers());
		Log.e(TAG,"Sign P2 SIGN_R = "+ mpcContext.getResult().get(ComputeEcdsaSign.SIGN_R));
		Log.e(TAG,"Sign P2 SIGN_S = "+ mpcContext.getResult().get(ComputeEcdsaSign.SIGN_S));
		verifySign(mpcContext, hash);
	}

	public static void verifySign(MpcContext mpcContext, String hash) {
		//签名后的数据
		byte[] sign = new byte[64];
		System.arraycopy(HexUtil.hexToByteArray((String)mpcContext.getResult().get(ComputeEcdsaSign.SIGN_R)), 0, sign, 0, 32);
		System.arraycopy(HexUtil.hexToByteArray((String)mpcContext.getResult().get(ComputeEcdsaSign.SIGN_S)), 0, sign, 32, 32);
		//验证签名
		ReturnVerify returnVerify = NativeECDSA.ECDSA_DiCo_Verify(HexUtil.hexToByteArray(hash),
				HexUtil.hexToByteArray("7A3F2EE6AC572696C65EA9DF45F44C7F6B6B7CD1CEA48F301F21EA8DBA82F2B7F26005A6155A142D8A6038E8B1CD2A589F98624DCA8692261770FC086D6C1DD3"),
				sign);
		if(returnVerify.getSuccess() != 1) {
			throw new RuntimeException("签名验证失败");
		}
	}

}
