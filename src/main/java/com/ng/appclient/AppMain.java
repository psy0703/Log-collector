package com.ng.appclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ng.bean.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Administrator on 2019/1/18 0018.
 */
public class AppMain {

    private final static Logger logger = LoggerFactory.getLogger(AppMain.class);
    static Random rand = new Random();

    /**
     * 1.appkey
     * 2.循环遍历次数
     * 3.uid的长度，默认是4
     * 4.商品id的长度，默认是4
     *
     * @param args
     */
    public static void main(String[] args) {

//      appkey的名称
        String appkey = args.length > 0 ? args[0] : "gmall";
//		循环遍历次数
        int loop_len = args.length > 1 ? Integer.parseInt(args[1]) : 100 * 100;
//		mid的长度
        int mid_length = args.length > 2 ? Integer.parseInt(args[2]) : 4;
//		uid的长度
        int uid_length = args.length > 3 ? Integer.parseInt(args[3]) : 4;
//		商品id的长度
        int newsid_length = args.length > 4 ? Integer.parseInt(args[4]) : 4;

        //kafka 配置
        Properties props = new Properties();
        //Kafka 服务器的主机名和端口号
        props.put("bootstrap.servers","psy831:9092,psy832:9092,psy833:9092");
        // 等待所有副本节点的应答
        props.put("acks","all");
        // 重试最大次数
        props.put("retries",0);
        // 批消息处理大小
        props.put("batch.size",16384);
        // 请求延时
        props.put("linger.ms",1);
        // 发送缓存区内存大小
        props.put("buffer.memory",33554432);
        // key序列化
        props.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        // value序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);


        for (int i = 0; i < loop_len; i++) {

            JSONObject json = new JSONObject();
            json.put("ap", appkey);
            json.put("cm", generateComFields(mid_length,uid_length));

            JSONArray eventsArray = new JSONArray();

            int flag = rand.nextInt(2);
            switch (flag) {
                case (0):
                    //应用启动
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateStart());
                    }
                    break;

                case (1):
                    // 事件日志
                    // 商品点击，展示
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateDisplay(newsid_length));
                    }

                    // 商品详情页
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateNewsDetail(newsid_length));
                    }

                    // 商品列表页
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateNewList());
                    }

                    // 广告
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateAd());
                    }

                    // 消息通知
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateNotification());
                    }

                    // 用户前台活跃
                    if (rand.nextBoolean()) {
                        eventsArray.add(generatbeforeground());
                    }

                    // 用户后台活跃
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateBackground());
                    }

                    //故障日志
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateError());
                    }

                    // 用户评论
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateComment());
                    }

                    // 用户收藏
                    if (rand.nextBoolean()) {
                        eventsArray.add(generateFavorites());
                    }

                    // 用户点赞
                    if (rand.nextBoolean()) {
                        eventsArray.add(generatePraise());
                    }
                    break;
            }

            json.put("et", eventsArray);

//          时间
            long millis = System.currentTimeMillis();

//          控制台打印
            logger.info(millis + "|" + json.toJSONString());

            //kafka 生产者 发送消息
            producer.send(new ProducerRecord<String, String>("omall_test",
                    i + "",millis + "|" + json.toJSONString()));

            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 数据发送完毕后关闭生产者与kafka服务器的连接
        producer.close();
    }

    /**
     * 公共字段设置
     *
     * @param uid_length
     * @return
     */
    static JSONObject generateComFields(int mid_length, int uid_length) {

        AppBase appBase = new AppBase();

//      设备id
        appBase.setMid('m' + getRandomDigits(mid_length));
//      用户id
        appBase.setUid('u' + getRandomDigits(uid_length));
//		程序版本号 5,6等
        appBase.setVc("" + rand.nextInt(20));
//		程序版本名 v1.1.1
        appBase.setVn("1." + rand.nextInt(4) + "." + rand.nextInt(10));
//      安卓系统版本
        appBase.setOs("8." + rand.nextInt(3) + "." + rand.nextInt(10));

//		语言  es,en,pt
        int flag = rand.nextInt(3);
        switch (flag) {
            case (0):
                appBase.setL("es");
                break;
            case (1):
                appBase.setL("en");
                break;
            case (2):
                appBase.setL("pt");
                break;
        }

//      渠道号   从哪个渠道来的
        appBase.setSr(getRandomChar(1));

//      区域
        flag = rand.nextInt(2);
        switch (flag) {
            case 0:
                appBase.setAr("BR");
            case 1:
                appBase.setAr("MX");
        }

//      手机品牌 ba ,手机型号 md，就取2位数字了
        flag = rand.nextInt(3);
        switch (flag) {
            case 0:
                appBase.setBa("Sumsung");
                appBase.setMd("sumsung-" + rand.nextInt(20));
                break;
            case 1:
                appBase.setBa("Huawei");
                appBase.setMd("Huawei-" + rand.nextInt(20));
                break;
            case 2:
                appBase.setBa("Mi");
                appBase.setMd("Mi-" + rand.nextInt(20));
                break;
        }

//      嵌入sdk的版本
        appBase.setSv("V2." + rand.nextInt(10) + "." + rand.nextInt(10));
//      gmail
        appBase.setG(getRandomCharAndNumr(8) + "@gmail.com");

//      屏幕宽高 hw
        flag = rand.nextInt(4);
        switch (flag) {
            case 0:
                appBase.setHw("640*960");
                break;
            case 1:
                appBase.setHw("640*1136");
                break;
            case 2:
                appBase.setHw("750*1134");
                break;
            case 3:
                appBase.setHw("1080*1920");
                break;
        }

//      客户端产生日志时间
        long millis = System.currentTimeMillis();
        appBase.setT("" + (millis - rand.nextInt(99999999)));

//		手机网络模式 3G,4G,WIFI
        flag = rand.nextInt(3);
        switch (flag) {
            case 0:
                appBase.setNw("3G");
                break;
            case 1:
                appBase.setNw("4G");
                break;
            case 2:
                appBase.setNw("WIFI");
                break;
        }

//      拉丁美洲 西经34°46′至西经117°09；北纬32°42′至南纬53°54′
//      经度
        appBase.setLn((-34 - rand.nextInt(83) - rand.nextInt(60) / 10.0) + "");
//      纬度
        appBase.setLa((32 - rand.nextInt(85) - rand.nextInt(60) / 10.0) + "");

        JSONObject common = (JSONObject) JSON.toJSON(appBase);

        return common;
    }

    /**
     * 商品展示事件
     *
     * @return
     */
    static JSONObject generateDisplay(int newsid_length) {

        AppDisplay appDisplay = new AppDisplay();

        boolean boolFlag = rand.nextInt(10) < 7 ? true : false;
//	    动作：曝光商品=1，点击商品=2，
        if (boolFlag) {
            appDisplay.setAction("1");
        } else {
            appDisplay.setAction("2");
        }

//      商品id
        String newsId = 'n' + getRandomDigits(newsid_length);
        appDisplay.setNewsid(newsId);

//      顺序  设置成6条吧
        int flag = rand.nextInt(6);
        appDisplay.setPlace("" + flag);

//      曝光类型
        flag = 1 + rand.nextInt(2);
        appDisplay.setExtend1("" + flag);

//      分类
        flag = 1 + rand.nextInt(100);
        appDisplay.setCategory("" + flag);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appDisplay);

        return packEventJson("display", jsonObject);
    }

    /**
     * 商品详情页
     *
     * @param newsid_length
     * @return
     */
    static JSONObject generateNewsDetail(int newsid_length) {

        AppNewsDetail appNewsDetail = new AppNewsDetail();

//      页面入口来源
        int flag = 1 + rand.nextInt(3);
        appNewsDetail.setEntry(flag + "");

//      动作
        appNewsDetail.setAction("" + (rand.nextInt(4) + 1));

//      商品id
        appNewsDetail.setNewsid('n' + getRandomDigits(newsid_length));

//      商品来源类型
        flag = 1 + rand.nextInt(3);
        appNewsDetail.setShowtype(flag + "");

//      商品样式
        flag = rand.nextInt(6);
        appNewsDetail.setShowtype("" + flag);

//      页面停留时长
        flag = rand.nextInt(10) * rand.nextInt(7);
        appNewsDetail.setNews_staytime(flag + "");

//      加载时长
        flag = rand.nextInt(10) * rand.nextInt(7);
        appNewsDetail.setLoading_time(flag + "");

//       加载失败码
        flag = rand.nextInt(10);
        switch (flag) {
            case 1:
                appNewsDetail.setType1("102");
                break;
            case 2:
                appNewsDetail.setType1("201");
                break;
            case 3:
                appNewsDetail.setType1("325");
                break;
            case 4:
                appNewsDetail.setType1("433");
                break;
            case 5:
                appNewsDetail.setType1("542");
                break;
            default:
                appNewsDetail.setType1("");
                break;
        }

//      分类
        flag = 1 + rand.nextInt(100);
        appNewsDetail.setCategory("" + flag);

        JSONObject eventJson = (JSONObject) JSON.toJSON(appNewsDetail);

        return packEventJson("newsdetail", eventJson);
    }

    /**
     * 商品列表
     *
     * @return
     */
    static JSONObject generateNewList() {

        AppLoading appLoading = new AppLoading();

//      动作
        int flag = rand.nextInt(3) + 1;
        appLoading.setAction(flag + "");

//      加载时长
        flag = rand.nextInt(10) * rand.nextInt(7);
        appLoading.setLoading_time(flag + "");

//      失败码
        flag = rand.nextInt(10);
        switch (flag) {
            case 1:
                appLoading.setType1("102");
                break;
            case 2:
                appLoading.setType1("201");
                break;
            case 3:
                appLoading.setType1("325");
                break;
            case 4:
                appLoading.setType1("433");
                break;
            case 5:
                appLoading.setType1("542");
                break;
            default:
                appLoading.setType1("");
                break;
        }

//      页面  加载类型
        flag = 1 + rand.nextInt(2);
        appLoading.setLoading_way("" + flag);

//      扩展字段1
        appLoading.setExtend1("");

//      扩展字段2
        appLoading.setExtend2("");

//      用户加载类型
        flag = 1 + rand.nextInt(3);
        appLoading.setType("" + flag);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appLoading);

        return packEventJson("loading", jsonObject);
    }

    /**
     * 广告相关字段
     *
     * @return
     */
    static JSONObject generateAd() {

        AppAd appAd = new AppAd();

//      入口
        int flag = rand.nextInt(3) + 1;
        appAd.setEntry(flag + "");

//      动作
        flag = rand.nextInt(5) + 1;
        appAd.setAction(flag + "");

//      状态
        flag = rand.nextInt(10) > 6 ? 2 : 1;
        appAd.setContent(flag + "");

//      失败码
        flag = rand.nextInt(10);
        switch (flag) {
            case 1:
                appAd.setDetail("102");
                break;
            case 2:
                appAd.setDetail("201");
                break;
            case 3:
                appAd.setDetail("325");
                break;
            case 4:
                appAd.setDetail("433");
                break;
            case 5:
                appAd.setDetail("542");
                break;
            default:
                appAd.setDetail("");
                break;
        }

//      广告来源
        flag = rand.nextInt(4) + 1;
        appAd.setSource(flag + "");

//      用户行为
        flag = rand.nextInt(2) + 1;
        appAd.setBehavior(flag + "");

//      商品类型
        flag = rand.nextInt(10);
        appAd.setNewstype("" + flag);

//      展示样式
        flag = rand.nextInt(6);
        appAd.setShow_style("" + flag);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appAd);

        return packEventJson("ad", jsonObject);
    }

    /**
     * 启动日志
     *
     * @return
     */
    static JSONObject generateStart() {

        AppStart appStart = new AppStart();

//      入口
        int flag = rand.nextInt(5) + 1;
        appStart.setEntry(flag + "");

//      开屏广告类型
        flag = rand.nextInt(2) + 1;
        appStart.setOpen_ad_type(flag + "");

//      状态
        flag = rand.nextInt(10) > 8 ? 2 : 1;
        appStart.setAction(flag + "");

//      加载时长
        appStart.setLoading_time(rand.nextInt(20) + "");

//      失败码
        flag = rand.nextInt(10);
        switch (flag) {
            case 1:
                appStart.setDetail("102");
                break;
            case 2:
                appStart.setDetail("201");
                break;
            case 3:
                appStart.setDetail("325");
                break;
            case 4:
                appStart.setDetail("433");
                break;
            case 5:
                appStart.setDetail("542");
                break;
            default:
                appStart.setDetail("");
                break;
        }

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appStart);

        return packEventJson("start", jsonObject);
    }

    /**
     * 消息通知
     *
     * @return
     */
    static JSONObject generateNotification() {

        AppNotification appNotification = new AppNotification();

        int flag = rand.nextInt(4) + 1;

//      动作
        appNotification.setAction(flag + "");

//      通知id
        flag = rand.nextInt(4) + 1;
        appNotification.setType(flag + "");

//      客户端弹时间
        appNotification.setAp_time((System.currentTimeMillis() - rand.nextInt(99999999)) + "");

//      备用字段
        appNotification.setContent("");

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appNotification);

        return packEventJson("notification", jsonObject);
    }

    /**
     * 前台活跃
     *
     * @return
     */
    static JSONObject generatbeforeground() {

        AppActive_foreground appActive_foreground = new AppActive_foreground();

//      推送消息的id
        int flag = rand.nextInt(2);
        switch (flag) {
            case 1:
                appActive_foreground.setAccess(flag + "");
                break;
            default:
                appActive_foreground.setAccess("");
                break;
        }

//      1.push 2.icon 3.其他
        flag = rand.nextInt(3) + 1;
        appActive_foreground.setPush_id(flag + "");

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appActive_foreground);

        return packEventJson("active_foreground", jsonObject);
    }

    /**
     * 后台活跃
     *
     * @return
     */
    static JSONObject generateBackground() {

        AppActive_background appActive_background = new AppActive_background();

//      启动源
        int flag = rand.nextInt(3) + 1;
        appActive_background.setActive_source(flag + "");

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appActive_background);

        return packEventJson("active_background", jsonObject);
    }


    /**
     * 错误日志数据
     *
     * @return
     */
    static JSONObject generateError() {

        AppErrorLog appErrorLog = new AppErrorLog();

        String[] errorBriefs = {"at cn.lift.dfdf.web.AbstractBaseController.validInbound(AbstractBaseController.java:72)", "at cn.lift.appIn.control.CommandUtil.getInfo(CommandUtil.java:67)"};        //错误摘要
        String[] errorDetails = {"java.lang.NullPointerException\\n    " + "at cn.lift.appIn.web.AbstractBaseController.validInbound(AbstractBaseController.java:72)\\n " + "at cn.lift.dfdf.web.AbstractBaseController.validInbound", "at cn.lift.dfdfdf.control.CommandUtil.getInfo(CommandUtil.java:67)\\n " + "at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\\n" + " at java.lang.reflect.Method.invoke(Method.java:606)\\n"};        //错误详情

        //错误摘要
        appErrorLog.setErrorBrief(errorBriefs[rand.nextInt(errorBriefs.length)]);
        //错误详情
        appErrorLog.setErrorDetail(errorDetails[rand.nextInt(errorDetails.length)]);

        JSONObject jsonObject = (JSONObject) JSON.toJSON(appErrorLog);

        return packEventJson("error", jsonObject);
    }


    /**
     * 为各个事件类型的公共字段（时间、事件类型、Json数据）拼接
     *
     * @param eventName
     * @param jsonObject
     * @return
     */
    static JSONObject packEventJson(String eventName, JSONObject jsonObject) {

        JSONObject eventJson = new JSONObject();

        eventJson.put("ett", (System.currentTimeMillis() - rand.nextInt(99999999)) + "");
        eventJson.put("en", eventName);
        eventJson.put("kv", jsonObject);

        return eventJson;
    }

    /**
     * 获取定长度的数字
     *
     * @param leng
     * @return
     */
    static String getRandomDigits(int leng) {

        String result = "";
        for (int i = 0; i < leng; i++) {
            result += rand.nextInt(10);
        }

        return result;
    }

    /**
     * 获取随机字母组合
     *
     * @param length 字符串长度
     * @return
     */
    public static String getRandomChar(Integer length) {

        String str = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            // 字符串
            str += (char) (65 + random.nextInt(26));// 取得大写字母
        }

        return str;
    }

    /**
     * 获取随机字母数字组合
     *
     * @param length 字符串长度
     * @return
     */
    public static String getRandomCharAndNumr(Integer length) {

        String str = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {

            boolean b = random.nextBoolean();

            if (b) { // 字符串
                // int choice = random.nextBoolean() ? 65 : 97; 取得65大写字母还是97小写字母
                str += (char) (65 + random.nextInt(26));// 取得大写字母
            } else { // 数字
                str += String.valueOf(random.nextInt(10));
            }
        }

        return str;
    }

    /**
     * 收藏
     * @return
     */
    static public JSONObject generateFavorites() {

        AppFavorites favorites = new AppFavorites();

        favorites.setCourse_id(rand.nextInt(10));
        favorites.setUserid(rand.nextInt(10));
        favorites.setAdd_time((System.currentTimeMillis() - rand.nextInt(99999999))+"");

        JSONObject jsonObject = (JSONObject) JSON.toJSON(favorites);

        return packEventJson("favorites",jsonObject);
    }

    /**
     * 点赞
     * @return
     */
    static public JSONObject generatePraise() {

        AppPraise praise=new AppPraise();

        praise.setId(rand.nextInt(10));
        praise.setUserid(rand.nextInt(10));
        praise.setTarget_id(rand.nextInt(10));
        praise.setType(rand.nextInt(4)+1);
        praise.setAdd_time((System.currentTimeMillis() - rand.nextInt(99999999))+"" );

        JSONObject jsonObject = (JSONObject) JSON.toJSON(praise);

        return packEventJson("praise",jsonObject);
    }

    /**
     * 评论
     * @return
     */
    static public JSONObject generateComment() {

        AppComment comment = new AppComment();

        comment.setComment_id(rand.nextInt(10));
        comment.setUserid(rand.nextInt(10));
        comment.setP_comment_id(rand.nextInt(5));

        comment.setContent(getCONTENT());
        comment.setAddtime((System.currentTimeMillis() - rand.nextInt(99999999))+"");

        comment.setOther_id(rand.nextInt(10));
        comment.setPraise_count(rand.nextInt(1000));
        comment.setReply_count(rand.nextInt(200));

        JSONObject jsonObject = (JSONObject) JSON.toJSON(comment);

        return packEventJson("comment",jsonObject);
    }

    /**
     * 生成单个汉字
     *
     * @return
     */
    private static char getRandomChar() {

        String str = "";
        int hightPos; //
        int lowPos;

        Random random = new Random();

        //随机生成汉子的两个字节
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("错误");
        }

        return str.charAt(0);
    }

    /**
     * 拼接成多个汉字
     * @return
     */
    static public String getCONTENT() {

        String str = "";

        for (int i = 0; i < rand.nextInt(100);i++) {
            str+=getRandomChar();
        }

        return str;
    }
}