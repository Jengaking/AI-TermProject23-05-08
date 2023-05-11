package v1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PapaGoMod {
	private static String clientId = "9_H84l7TirwUA5MDMeMU";// 애플리케이션 클라이언트 아이디값
	private static String clientSecret = "EK2lJZ6foc";// 애플리케이션 클라이언트 시크릿값

	public static String doTranslation(String input) {
		String text = "";
		String res = "";
		try {
			text = URLEncoder.encode(input, "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			
			con.setRequestMethod("POST");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			// post request
			String postParams = "source=en&target=ko&text=" + text;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			res = response.toString();
			int category = res.indexOf("\"translatedText\"");
			res = res.substring(category + "\"translatedText\"".length() + 1, res.indexOf(",\"engineType\""));
			
			System.out.println("PPG : Translated text : " + res);
		} catch (Exception e) {
			System.out.println(e);
		}
		return res;
	}
}
