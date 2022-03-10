package com.hpa.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {

    public static String doGet(String url) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        String result = "";
        try {
            // 通过址默认配置创建一个httpClient实例
            httpClient = HttpClients.createDefault();
            // 创建httpGet远程连接实例
            HttpGet httpGet = new HttpGet(url);
            // 设置请求头信息，鉴权
            httpGet.setHeader("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 设置配置请求参数
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(35000)// 连接主机服务超时时间
                    .setConnectionRequestTimeout(35000)// 请求超时时间
                    .setSocketTimeout(60000)// 数据读取超时时间
                    .build();
            // 为httpGet实例设置配置
            httpGet.setConfig(requestConfig);
            // 执行get请求得到返回对象
            response = httpClient.execute(httpGet);
            // 通过返回对象获取返回数据
            HttpEntity entity = response.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != response) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;

    }


    public static String doPost(String httpUrl, String param) {

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开连接
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接请求方式
            connection.setRequestMethod("POST");
            // 设置连接主机服务器超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取主机服务器返回数据超时时间：60000毫秒
            connection.setReadTimeout(60000);

            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            // 设置传入参数的格式:请求参数应该是 name1=value1&name2=value2 的形式。
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置鉴权信息：Authorization: Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0
            connection.setRequestProperty("Authorization", "Bearer da3efcbf-0845-4fe3-8aba-ee040be542c0");
            // 通过连接对象获取一个输出流
            os = connection.getOutputStream();
            // 通过输出流对象将参数写出去/传输出去,它是通过字节数组写出的
            os.write(param.getBytes());
            // 通过连接对象获取一个输入流，向远程读取
            if (connection.getResponseCode() == 200) {

                is = connection.getInputStream();
                // 对输入流对象进行包装:charset根据工作项目组的要求来设置
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                StringBuffer sbf = new StringBuffer();
                String temp = null;
                // 循环遍历一行一行读取数据
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 断开与远程地址url的连接
            connection.disconnect();
        }
        return result;
    }




    private static final String URL = "172.16.2.30";
    private static final String CONTENT = "网络的世界里，每个人都是主角，你怎么样，中国便怎么样。每一份呼之欲出的中国正能量，都将助力一个更加美好的中国；每一份激动人心的中国正能量，都将成就一个更加强大的中..." ;
    private static final int PORT = 9999 ;

    public static String sendHTTP(String news){
        String res = "";
        try {
            Socket socket = new Socket(URL,PORT); //建立TCP/IP链接
            OutputStream out = socket.getOutputStream() ;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            out.write(news.getBytes());  //发送数据
            int d = -1 ;
            int l = 0;
            while((d=in.read())!=-1){       //接收
                res = res + (char)d;
                l ++;
                if(l==2){
                    break;
                }
            }
            System.out.println(res);
            out.close();
            in.close();
            socket.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }




    public static void main(String[] args) {
        //System.out.println(doGet("http://172.16.2.107:32359/api/v1/query?query=http_server_requests_seconds_count{exception=\"None\",instance=\"172.16.2.107:32000\",job=\"test-metric\",method=\"GET\",outcome=\"SUCCESS\",status=\"200\",uri=\"/histogram\"}"));

        String exception = "None";
        String instance = "172.16.2.106:32000";
        String job = "test-metric";
        String method = "GET";
        String outcome = "SUCCESS";
        String status = "200";
        String uri = "/histogram";


        String uuu = "http_server_requests_seconds_count{exception=\"None\",method=\"GET\",outcome=\"SUCCESS\",status=\"200\",uri=\"/histogram\"}";


        //String u = "http://172.16.2.107:32359/api/v1/query?query=http_server_requests_seconds_count{exception=\"None\",method=\"GET\",outcome=\"SUCCESS\",status=\"200\",uri=\"/histogram\"}";
        String u = "http://172.16.2.107:31527/api/v1/query?query=rate(http_server_requests_seconds_count[1m])*60";
        //System.out.println(doGet("http://172.16.2.107:32359/api/v1/query?query="+uuu));
        String result =doGet(u);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject js = jsonObject.getJSONObject("data");
        JSONArray  array= js.getJSONArray("result");
        System.out.println(array);
        JSONObject obj =(JSONObject)array.get(2);
      System.out.println(obj);
        JSONArray obj1 = obj.getJSONArray("value");
      System.out.println(obj1);
        Object object = obj1.get(1);
        Double res = Double.valueOf((String)object);
        System.out.println(res);
    }


    public static Double getMetric(){
        String exception = "None";
        String instance = "172.16.2.107:32000";
        String job = "test-metric";
        String method = "GET";
        String outcome = "SUCCESS";
        String status = "200";
        String uri = "/histogram";

        String uuu = "http_server_requests_seconds_count{exception=\"None\",method=\"GET\",outcome=\"SUCCESS\",status=\"200\",uri=\"/histogram\"}";

        //String u = "http://172.16.2.107:32359/api/v1/query?query=http_server_requests_seconds_count{exception=\"None\",method=\"GET\",outcome=\"SUCCESS\",status=\"200\",uri=\"/histogram\"}";
        String u = "http://172.16.2.107:31527/api/v1/query?query=rate(http_server_requests_seconds_count[1m])*60";
        //System.out.println(doGet("http://172.16.2.107:32359/api/v1/query?query="+uuu));
        String result =doGet(u);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject js = jsonObject.getJSONObject("data");
        JSONArray  array= js.getJSONArray("result");
        // System.out.println(array);
        JSONObject obj =(JSONObject)array.get(2);
        // System.out.println(obj);
        JSONArray obj1 = obj.getJSONArray("value");
        // System.out.println(obj1);
        Object object = obj1.get(1);

        Double res = Double.valueOf((String)object);

        System.out.println(object);

        return res;

    }



}