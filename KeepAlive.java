package exchanger;

import java.io.IOException;
import java.util.Base64;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

public class KeepAlive extends TimerTask{

	@Override
	public void run() {
		String BalanceParams = Globals.balance().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(BalanceParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		String Params = Exchange.CreatBasicParams() + temp;
		System.out.println(Params); // 调试用，输出参数设置
		String BalanceURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(BalanceURL); // 调试用，输出申请的网页地址
		HttpGet getBalance = new HttpGet(BalanceURL);
		getBalance.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchange.Exchanger.execute(getBalance);
//			System.out.println("alive"); //调试用
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
