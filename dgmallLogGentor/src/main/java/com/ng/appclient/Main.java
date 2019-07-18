package com.ng.appclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ng.bean.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

/**
 * @Author: Cedaris
 * @Date: 2019/7/17 13:39
 */
public class Main {
    private final static Logger logger = LoggerFactory.getLogger(AppMain.class);
    public static void main(String[] args) {
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

        for (int i = 0; i < 100000; i++) {
            JSONObject commonFields = generaCommonFields();

            int flag = rand.nextInt(6);
            switch (flag) {
                case (0):
                    commonFields.put("Type","watch_video");
                    commonFields.put("Event","play");
                    commonFields.put("Properties",generaWatch());
                    break;
                case (1):
                    commonFields.put("Type","view");
                    commonFields.put("Event","view");
                    commonFields.put("Properties",generaView());
                    break;
                case (2):
                    commonFields.put("Type","click");
                    commonFields.put("Event","click");
                    commonFields.put("Properties",generaClick());
                    break;
                case (3):
                    commonFields.put("Type","behavior");
                    commonFields.put("Event","behavior");
                    commonFields.put("Properties",generaBehavior());
                    break;
                case (4):
                    commonFields.put("Type","search_click");
                    commonFields.put("Event","search_click");
                    commonFields.put("Properties",generaSearch());
                    break;
                case (5):
                    commonFields.put("Type","gift");
                    commonFields.put("Event","gift");
                    commonFields.put("Properties",generaGift());
                    break;

            }

//          时间
            long millis = System.currentTimeMillis();

//          控制台打印
            logger.info(millis + "|" + commonFields.toJSONString());

            //kafka 生产者 发送消息
            producer.send(new ProducerRecord<String, String>("dgmall_log",
                    i + "",commonFields.toJSONString()));

            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
    static Random rand = new Random();

    /**
     * 为各个事件类型的公共字段（时间、事件类型、Json数据）拼接
     *
     * @param eventName
     * @param jsonObject
     * @return
     */
    static JSONObject packEventJson(String eventName, String type,JSONObject jsonObject) {

        JSONObject eventJson = new JSONObject();

        eventJson.put("Type",type);
        eventJson.put("Event", eventName);
        eventJson.put("Properties", jsonObject);

        return eventJson;
    }

    /**
     * 生成公共字段 时间和页面标识
     * @return
     */
    static JSONObject generaCommonFields(){
        AppBaseField baseField = new AppBaseField();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        //时间
        baseField.setTime(sdf.format(new Date(System.currentTimeMillis())));
        //页面标识
        int flag = rand.nextInt(3);
        switch (flag) {
            case (0):
                baseField.setDistinct_id("Andriod+" + UUID.randomUUID());
                break;
            case (1):
                baseField.setDistinct_id("ios+" + UUID.randomUUID());
                break;
            case (2):
                baseField.setDistinct_id("js+" + UUID.randomUUID());
                break;
        }
        JSONObject jsonObject = (JSONObject) JSON.toJSON(baseField);
        return jsonObject;
    }

    /**
     * 生成观看视频日志
     * @return
     */
    static JSONObject generaWatch(){
        AppWatch appWatch = new AppWatch();
        appWatch.setTrace_id("app" + rand.nextInt(3)+".scenes" + rand.nextInt(10)+ ".plan" + rand.nextInt(3)+".bucket" + rand.nextInt(4));
        appWatch.setAlg_match("match"+rand.nextInt(4));
        appWatch.setAlg_rank("rank" +rand.nextInt(3));
        appWatch.setRule("rule" + rand.nextInt(5));
        appWatch.setBhv_amt(Integer.toString(rand.nextInt(5)));
        appWatch.setUser_id(getRandomDigits(4));
        appWatch.setVideo_id(getRandomDigits(5));
        appWatch.setVideo_user_id(getRandomDigits(4));
        appWatch.setVideo_desc(getCONTENT(6));
        appWatch.setVideo_tag(getCONTENT(2));
        appWatch.setWatch_time_long(getRandomDigits(2));
        appWatch.setVideo_long(getRandomDigits(3));
        appWatch.setMusic_name(getCONTENT(4));
        appWatch.setMusic_write(getCONTENT(3));
        appWatch.setVideo_topic(getCONTENT(3));
        appWatch.setVideo_address(getRandomCharAndNumr(8));
        appWatch.setIs_attention(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_like(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_comment(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_share_weixin(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_share_friendster(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_share_qq(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_save(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_get_red_packets(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_copy_site(Integer.toString(rand.nextInt(2)));
        appWatch.setIs_report("0");
        appWatch.setReport_content("...");
        appWatch.setIs_not_interested(Integer.toString(rand.nextInt(2)));
        appWatch.setShop_id(getRandomDigits(5));
        appWatch.setShop_name(getCONTENT(3) + "特产店");

        return  (JSONObject) JSON.toJSON(appWatch);
//        return  packEventJson("play","watch_video",jsonObject);
    }

    /**
     * 生成曝光日志
     * @return
     */
    static JSONObject generaView(){
        AppView appView = new AppView();
        appView.setAlg_match("match"+rand.nextInt(4));
        appView.setAlg_rank("rank" +rand.nextInt(3));
        appView.setRule("rule" + rand.nextInt(5));
        appView.setTrace_id("app" + rand.nextInt(3)+".scenes" + rand.nextInt(10)+ ".plan" + rand.nextInt(3)+".bucket" + rand.nextInt(4));
        appView.setUser_id(getRandomDigits(4));
        appView.setVideo_id(getRandomDigits(5));

        return (JSONObject) JSON.toJSON(appView);
//        return  packEventJson("view","view",jsonObject);
    }

    /**
     * 生成点击日志
     * @return
     */
    static JSONObject generaClick(){
        AppClick appClick = new AppClick();
        appClick.setAlg_match("match"+rand.nextInt(4));
        appClick.setAlg_rank("rank" +rand.nextInt(3));
        appClick.setRule("rule" + rand.nextInt(5));
        appClick.setTrace_id("app" + rand.nextInt(3)+".scenes" + rand.nextInt(10)+ ".plan" + rand.nextInt(3)+".bucket" + rand.nextInt(4));
        appClick.setUser_id(getRandomDigits(4));
        appClick.setVideo_id(getRandomDigits(5));
        appClick.setBhv_amt(Float.intBitsToFloat(rand.nextInt(3)));

        return (JSONObject) JSON.toJSON(appClick);
//        return  packEventJson("click","click",jsonObject);
    }

    /**
     * 生成 详情-浏览点击日志——浏览与点击并一起了
     * @return
     */
    static JSONObject generaBehavior(){
        AppBehavior appBehavior = new AppBehavior();
        appBehavior.setAlg_match("match"+rand.nextInt(4));
        appBehavior.setAlg_rank("rank" +rand.nextInt(3));
        appBehavior.setRule("rule" + rand.nextInt(5));
        appBehavior.setTrace_id("app" + rand.nextInt(3)+".scenes" + rand.nextInt(10)+ ".plan" + rand.nextInt(3)+".bucket" + rand.nextInt(4));
        appBehavior.setUser_id(getRandomDigits(4));
        appBehavior.setVideo_id(getRandomDigits(5));
        appBehavior.setBhv_amt(Float.intBitsToFloat(rand.nextInt(3)));
        int flag = rand.nextInt(2);
        switch (flag) {
            case (0):
                appBehavior.setBhv_type("detail_click");
                break;
            case (1):
                appBehavior.setBhv_type("detail_view");
                break;
        }
        return (JSONObject) JSON.toJSON(appBehavior);
//        return packEventJson("behavior","behavior",jsonObject);
    }

    /**
     * 生成搜索点击日志
     * @return
     */
    static JSONObject generaSearch(){
        AppSearch appSearch = new AppSearch();
        appSearch.setSearch_content(getCONTENT(4));
        appSearch.setUser_id(getRandomDigits(4));
        return (JSONObject) JSON.toJSON(appSearch);
//        return packEventJson("search_click","search_click",jsonObject);
    }

    /**
     * 生成送礼日志
     * @return
     */
    static JSONObject generaGift(){
        AppGift appGift = new AppGift();
        appGift.setContent(getCONTENT(4) + "特产店");
        appGift.setUser_id(getRandomDigits(4));
        appGift.setVideo_id(getRandomDigits(5));
        appGift.setTrace_id("app" + rand.nextInt(3)+".scenes" + rand.nextInt(10)+ ".plan" + rand.nextInt(3)+".bucket" + rand.nextInt(4));

        return (JSONObject) JSON.toJSON(appGift);
//        return packEventJson("gift","gift",jsonObject);
    }

    /**
     * 获取定长度的数字
     *
     * @param leng
     * @return
     */
    static String getRandomDigits(int leng) {

        String result = "";
        for (int i = 0; i <= leng; i++) {
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
    static public String getCONTENT(int len) {

        String str = "";

        for (int i = 0; i < rand.nextInt(len);i++) {
            str+=getRandomChar();
        }

        return str;
    }
}
