package com.jfinalshop.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;

//import java.io.File;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.StringBody;


public class HttpUtils {
	/**
	 * 以Post方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带的参数
	 * @return String 返回结果
	 * @throws Exception
	 */
	public static String POSTMethod(String url, Map<String, Object> argsMap)throws Exception {
		byte[] dataByte = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		// 设置参数
		UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
		httpPost.setEntity(encodedFormEntity);
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpPost);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpPost.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}
	
	/**
	 * 以Post方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param map
     * @return String 返回结果
	 * @throws Exception
	 */
	/*public static String POSTMethod(String url, Map<String, String> map)throws Exception {
		byte[] dataByte = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);		
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpPost);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpPost.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}*/

	/**
	 * 以Get方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @return String
	 * @throws Exception
	 */
	public static String GETMethod(String url, Map<String, Object> argsMap)	throws Exception {
		byte[] dataByte = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 为GET请求链接构造参数
		url = formatGetParameter(url, argsMap);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpGet.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}

	/**
	 * 以Put方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带的参数
	 * @return String
	 * @throws Exception
	 */
	public static String PUTMethod(String url, Map<String, Object> argsMap)	throws Exception {
		byte[] dataByte = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(url);
		// 设置参数
		UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
		httpPut.setEntity(encodedFormEntity);
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpPut);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpPut.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}

	/**
	 * @Description: POST文件
	 * @param @param url
	 * @param @param fileName
	 * @param @param token
	 * @param @return
	 * @param @throws Exception   
	 * @return String
	 * @author 李红元
	 * @date 2015-7-11 下午1:57:31
	 */
	/*public static String POSTMethodImage(String url, String fileName, String token) throws Exception {
		byte[] dataByte = null;
		
		HttpPost post = new HttpPost(url);
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);		
		builder.addPart("upfile", new FileBody(new File(fileName),ContentType.DEFAULT_BINARY));
		builder.addPart("token", new StringBody(token, ContentType.MULTIPART_FORM_DATA));
		
		HttpEntity entity = builder.build();		
		post.setEntity(entity);
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse httpResponse = client.execute(post);
		
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			post.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
		
	}*/
	
	/**
	 * 构造GET请求地址的参数拼接
	 * 
	 * @param url
	 * @param argsMap
	 * @return String
	 */
	private static String formatGetParameter(String url,Map<String, Object> argsMap) {
		if (url != null && url.length() > 0) {
			if (!url.endsWith("?")) {
				url = url + "?";
			}

			if (argsMap != null && !argsMap.isEmpty()) {
				Set<Entry<String, Object>> entrySet = argsMap.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					if (entry != null) {
						String key = entry.getKey();
						String value = (String) entry.getValue();
						url = url + key + "=" + value;
						if (iterator.hasNext()) {
							url = url + "&";
						}
					}
				}
			}
		}
		return url;
	}

	/**
	 * 获取数据
	 * 
	 * @param httpEntity
	 * @return
	 * @throws Exception
	 */
	private static byte[] getData(HttpEntity httpEntity) throws Exception {
		BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bufferedHttpEntity.writeTo(byteArrayOutputStream);
		byte[] responseBytes = byteArrayOutputStream.toByteArray();
		return responseBytes;
	}

	/**
	 * 设置HttpPost请求参数
	 * 
	 * @param argsMap
	 * @return BasicHttpParams
	 */
	private static List<BasicNameValuePair> setHttpParams(Map<String, Object> argsMap) {
		List<BasicNameValuePair> nameValuePairList = new ArrayList<BasicNameValuePair>();
		// 设置请求参数
		if (argsMap != null && !argsMap.isEmpty()) {
			Set<Entry<String, Object>> set = argsMap.entrySet();
			Iterator<Entry<String, Object>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
				nameValuePairList.add(basicNameValuePair);
			}
		}
		return nameValuePairList;
	}

	/**
	 * 将字节数组转换成字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static String bytesToString(byte[] bytes)throws UnsupportedEncodingException {
		if (bytes != null) {
			String returnStr = new String(bytes, "utf-8");
			return returnStr;
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		String url = "http://192.168.9.43:8086/api/receiver/update.jhtml";
		Map<String, Object> argsMap = new HashMap<String, Object>();
		argsMap.put("receiver.consignee", "李元吉");
		argsMap.put("receiver.phone", "13798064110");
		argsMap.put("receiver.area_id", "10");
		argsMap.put("receiver.id", "377");
		argsMap.put("receiver.address", "1橦203房");
		argsMap.put("token", "add9ac5e7ab84ceead4977cc69a145bd2oseCL");
		String result = POSTMethod(url, argsMap);
		System.out.println(result);

	}

	/**
	 * 发送表单
	 * @param getUrl
	 * @param body
	 * @return
	 */
	public static String send(String getUrl,String body){
		String result = null;
		InputStream in = null;
		try {
			URL url = new URL(getUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.connect();

//            String body = "userName=zhangsan&password=123456";
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(body);
			writer.close();

			int responseCode = connection.getResponseCode();
			System.out.println(responseCode);
//            if(responseCode == HttpURLConnection.HTTP_OK){
//                InputStream inputStream = connection.getInputStream();
//                result = convertStreamToString(inputStream);//将流转换为字符串。
//                System.out.println(result);
//            }
			in = connection.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(
					in));
			StringBuffer bs = new StringBuffer();
			String line = null;
			while ((line = buffer.readLine()) != null) {
				bs.append(line);
			}
			result = bs.toString();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return result;
	}

}