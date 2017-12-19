package no1s.premier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Twitter {
	private static final String HTTP_METHOD = "GET";
	private static final String ENDPOINT = "https://api.twitter.com/1.1/statuses/user_timeline.json";
	private static final String SCREEN_NAME = "@realDonaldTrump";
	private static final String COUNT = "10";
	private static final String TWEET_MODE = "extended";
	
	private static final String CONSUMER_KEY = "UhuBsj54DbRnnHGQYaPywHmAi";
	private static final String COUSUMER_SECRET = "e1zSaGoQorVKzyW5W7U8LaAmkUsf3nnjdIHCxh45Y03bnnBj86";
	private static final String ACCESS_TOKEN = "42566916-5CI2BDtmiUQdWFKtTY6ajgxmLlYjURI4ftW2K88h3";
	private static final String ACCESS_TOKEN_SECRET = "YHfdFB1RJxIcHQ6XOriX0GZhmZmFpNmI5wegaBIRDflIU";
	private static final String NONCE = String.valueOf(Math.random());
	private static final String TIME_STAMP = String.valueOf(System.currentTimeMillis() / 1000L);
	private static final String SIGN_METHOD = "HMAC-SHA1";
	private static final String VERSION = "1.0";
	
	public static void main(String... args) {
		// リクエストURL
		String endpointWithParam = ENDPOINT + "?screen_name=" + SCREEN_NAME + "&count=" + COUNT + "&tweet_mode=" + TWEET_MODE;
				
		String signature = null;
		// key生成
		try {
			String signature1 = HTTP_METHOD;
		    String signature2 = ENDPOINT;
		    String signature3Tmp = "count=%s&oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s&screen_name=%s&tweet_mode=%s";
		    String signature3 = String.format(signature3Tmp, COUNT, CONSUMER_KEY, NONCE, SIGN_METHOD, 
		    		 						TIME_STAMP,ACCESS_TOKEN, VERSION, SCREEN_NAME, TWEET_MODE);

		    String signatureTmp = "%s&%s&%s";
		    String signatureStr =  String.format(signatureTmp, urlEncoding(signature1), 
		    									urlEncoding(signature2), urlEncoding(signature3));
		    
		    String keyStr = urlEncoding(COUSUMER_SECRET) + "&" + urlEncoding(ACCESS_TOKEN_SECRET);			

		    signature = makeSignature(keyStr, signatureStr);
			String encodedSignature = urlEncoding(signature);
			
			String authHeaderTmp = "OAuth oauth_consumer_key=\"%s\", oauth_nonce=\"%s\", oauth_signature=\"%s\", oauth_signature_method=\"%s\", oauth_timestamp=\"%s\", oauth_token=\"%s\", oauth_version=\"%s\"";
		    String authHeader = String.format(authHeaderTmp, CONSUMER_KEY, NONCE, encodedSignature,
		                                      SIGN_METHOD, TIME_STAMP, ACCESS_TOKEN, VERSION);			
			
			// 実行
			URL url = new URL(endpointWithParam);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", authHeader);
			con.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			StringBuilder sb = new StringBuilder();
			String s = null;
			while ((s = reader.readLine()) != null) {
				sb.append(s);
			}
			
			ArrayList<TwitterBean> twitterList = parseJson(sb.toString());
			
			for(TwitterBean bean : twitterList) {
				System.out.println(bean.getCreated_at());
				System.out.println(bean.getFull_text());
				System.out.println("----------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String urlEncoding(String text) throws UnsupportedEncodingException {
		return URLEncoder.encode(text, "UTF-8");
	}
	
	private static String makeSignature(String keyStr, String signatureStr) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec key = new SecretKeySpec(keyStr.getBytes(), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);
		byte[] rawHmac = mac.doFinal(signatureStr.getBytes());
		return Base64.getEncoder().encodeToString(rawHmac);
	}
	
	private static ArrayList<TwitterBean> parseJson(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		
		return mapper.readValue(json, new TypeReference<List<TwitterBean>>() {});
	}
}
