package com.kms.demo;


import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Test {

    public static void main(String[] args) throws Exception {
//        //设置需要的矿工费
//        BigInteger GAS_PRICE = BigInteger.valueOf(22_000_000_000L);
//        BigInteger GAS_LIMIT = BigInteger.valueOf(4_300_000);
//
//        String home = System.getProperty("user.home");
//
//        File file = new File(home + File.separator + "Desktop" + File.separator + "wallet.json");
//
//        //调用的是kovan测试环境，这里使用的是infura这个客户端
//        Web3j web3j = Web3jFactory.build(new HttpService("https://rinkeby.infura.io/v3/c22152c1fa194298a92f8c064c95a565"));
//        //转账人账户地址
//        String ownAddress = "0xdc8f9fd2a4064ea0f37cfd99425ce460dbff2d8f";
//        //被转人账户地址
//        String toAddress = "0xaa66603233499d72b365261877df423d42a886d5";
//        //转账人私钥
//        Credentials credentials = WalletUtils.loadCredentials(
//                "987654321",
//                file.getAbsolutePath());
//
//        //getNonce（这里的Nonce我也不是很明白，大概是交易的笔数吧）
//        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
//                ownAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
//        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
//
//        //创建交易，这里是转0.5个以太币
//        BigInteger value = Convert.toWei("0.5", Convert.Unit.ETHER).toBigInteger();
//        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
//                nonce, GAS_PRICE, GAS_LIMIT, toAddress, value);
//
//        //签名Transaction，这里要对交易做签名
//        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//        String hexValue = Numeric.toHexString(signedMessage);
//
//        //发送交易
//        EthSendTransaction ethSendTransaction =
//                web3j.ethSendRawTransaction(hexValue).sendAsync().get();
//        String transactionHash = ethSendTransaction.getTransactionHash();
//
//        //获得到transactionHash后就可以到以太坊的网站上查询这笔交易的状态了
//        System.out.println(transactionHash);

        Flowable.just(1)
                .map(new Function<Integer, String>() {

                    @Override
                    public String apply(Integer integer) throws Exception {
                        System.out.println("第一个map执行线程： " + Thread.currentThread().getName());
                        return String.valueOf(integer);
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        System.out.println("第二个map执行线程： " + Thread.currentThread().getName());
                        return s;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("Consumer执行线程：  " + Thread.currentThread().getName());
                    }
                });

    }
}