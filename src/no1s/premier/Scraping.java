package no1s.premier;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * �X�N���C�s���O�����{����N���X
 * @author seo
 */
public class Scraping {
	// div class="rc"��h3 class="r"��original_target�����������N��A<a>�̒l���^�C�g��
	public static final String SEARCH_URL = "https://www.google.co.jp/search?q=%E6%B2%96%E7%B8%84%E3%80%80%E9%AB%98%E7%B4%9A%E3%83%9B%E3%83%86%E3%83%AB";
	public static final String EXP_TITLE = "//div[@class='rc']/h3[@class='r']/a";
	public static final String EXP_LINK = "//div[@class='rc']/h3[@class='r']/a/@data-href";
	
	public static void main(String... args) {
		ArrayList<String> responseList = null;
		
		String pattern = "<h3 class=\"r\">(.*?)</a>";
		
		Pattern p = Pattern.compile(pattern);
	    
		try {
			// URL���烌�X�|���X���擾
			responseList = getResponse();
			for(String response : responseList) {
				Matcher m = p.matcher(response);
				if(m.find()) {
					// <h3 class="r">�ȉ���<a>�̗v�f����^�C�g���ƃ����N��𒊏o
					String matchResp = m.group(1);
					String cutHeadResp = matchResp.substring(16);
					String[] splitRespList = cutHeadResp.split("&amp");
					
					// �^�C�g��
					String cutTailResp = splitRespList[splitRespList.length - 1];
					String[] splitTailList = cutTailResp.split("\">");
					String titleStrWithBTag = splitTailList[1];
					
					// b�^�O���폜
					String titleStr = titleStrWithBTag.replaceAll("<b>", "").replaceAll("</b>", "");
					System.out.println(titleStr);
					
					// �����N��
					String linkStr = splitRespList[0];
					System.out.println(linkStr);
					
					System.out.println("----------------------------");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ArrayList<String> getResponse() throws IOException, ParserConfigurationException, SAXException {
		URLConnection con = new URL(SEARCH_URL).openConnection();
		con.setRequestProperty("User-agent","Mozilla/5.0");        
        InputStream is = con.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        ArrayList<String> responseList = new ArrayList<String>();
        String s = null;
        while ((s = reader.readLine()) != null) {
        	responseList.add(s);
        }
        
		return responseList;
	}
}
