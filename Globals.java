package exchanger; //都是交易用的网址和参数

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import net.sf.json.JSONObject;

public class Globals { // 全局变量信息
	public static String login_page = "https://service.htsc.com.cn/service/login.jsp";
	public static String login_api = "https://service.htsc.com.cn/service/loginAction.do?method=login";
	public static String trade_info_page = "https://service.htsc.com.cn/service/flashbusiness_new3.jsp?etfCode=";
	public static String verify_code_api = "https://service.htsc.com.cn/service/pic/verifyCodeImage.jsp";
	public static String prefix = "https://tradegw.htsc.com.cn/?";
	public static String version = "1";
	public static String userName = null;
	public static String trdpwdEns = null;
	public static String custid = null;
	public static String op_entrust_way = "7";
	public static String password = null;
	public static String servicePwd = null;
	public static String identity_type = "";
	public static String sh_exchange_type = null;
	public static String sh_stock_account = null;
	public static String sz_exchange_type = null;
	public static String sz_stock_account = null;
	public static String fund_account = null;
	public static String client_risklevel = null;
	public static String op_station = null;
	public static String trdpwd = null;
	public static String uid = null;
	public static String branch_no = null;

	public static List<NameValuePair> login() {
		String ip = getIp(); // 得到ip地址
		String mac = getMac(); // 得到mac地址
		String verifycode = Exchange.getVerifyCode(); // 得到验证码
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("loginEvent", "1")); // easytrader发送的头
		params.add(new BasicNameValuePair("topath", "null"));
		params.add(new BasicNameValuePair("accountType", "1"));
		params.add(new BasicNameValuePair("userType", "jy"));
		params.add(new BasicNameValuePair("userName", userName));
		params.add(new BasicNameValuePair("trdpwd", trdpwd));
		params.add(new BasicNameValuePair("trdpwdEns", trdpwdEns));
		params.add(new BasicNameValuePair("servicePwd", servicePwd));
		params.add(new BasicNameValuePair("macaddr", mac));
		params.add(new BasicNameValuePair("lipInfo", ip));
		params.add(new BasicNameValuePair("vcode", verifycode));
		return params;
	}

	public static List<NameValuePair> position() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cssweb_type", "GET_STOCK_POSITION"));
		params.add(new BasicNameValuePair("function_id", "403"));
		params.add(new BasicNameValuePair("exchange_type", ""));
		params.add(new BasicNameValuePair("stock_account", ""));
		params.add(new BasicNameValuePair("stock_code", ""));
		params.add(new BasicNameValuePair("query_direction", ""));
		params.add(new BasicNameValuePair("query_mode", "0"));
		params.add(new BasicNameValuePair("request_num", "100"));
		params.add(new BasicNameValuePair("position_str", ""));
		return params;
	}

	public static List<NameValuePair> balance() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cssweb_type", "GET_FUNDS"));
		params.add(new BasicNameValuePair("function_id", "405"));
		params.add(new BasicNameValuePair("identity_type", ""));
		params.add(new BasicNameValuePair("money_type", ""));
		return params;
	}

	public static List<NameValuePair> entrust() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cssweb_type", "GET_CANCEL_LIST"));
		params.add(new BasicNameValuePair("function_id", "401"));
		params.add(new BasicNameValuePair("exchange_type", ""));
		params.add(new BasicNameValuePair("stock_account", ""));
		params.add(new BasicNameValuePair("stock_code", ""));
		params.add(new BasicNameValuePair("query_direction", ""));
		params.add(new BasicNameValuePair("sort_direction", "0"));
		params.add(new BasicNameValuePair("request_num", "100"));
		params.add(new BasicNameValuePair("position_str", ""));
		return params;
	}

	public static List<NameValuePair> buy() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cssweb_type", "STOCK_BUY"));
		params.add(new BasicNameValuePair("function_id", "302"));
		params.add(new BasicNameValuePair("exchange_type", ""));
		params.add(new BasicNameValuePair("stock_account", ""));
		params.add(new BasicNameValuePair("stock_code", ""));
		params.add(new BasicNameValuePair("query_direction", ""));
		params.add(new BasicNameValuePair("sort_direction", "0"));
		params.add(new BasicNameValuePair("request_num", "100"));
		params.add(new BasicNameValuePair("identity_type", ""));
		params.add(new BasicNameValuePair("entrust_bs", "1"));
		return params;
	}

	public static List<NameValuePair> sell() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cssweb_type", "STOCK_SALE"));
		params.add(new BasicNameValuePair("function_id", "302"));
		params.add(new BasicNameValuePair("exchange_type", ""));
		params.add(new BasicNameValuePair("stock_account", ""));
		params.add(new BasicNameValuePair("stock_code", ""));
		params.add(new BasicNameValuePair("query_direction", ""));
		params.add(new BasicNameValuePair("sort_direction", "0"));
		params.add(new BasicNameValuePair("request_num", "100"));
		params.add(new BasicNameValuePair("identity_type", ""));
		params.add(new BasicNameValuePair("entrust_bs", "2"));
		return params;
	}

	public static List<NameValuePair> cancel_entrust() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("cssweb_type", "STOCK_CANCEL"));
		params.add(new BasicNameValuePair("function_id", "304"));
		params.add(new BasicNameValuePair("exchange_type", ""));
		params.add(new BasicNameValuePair("stock_code", ""));
		params.add(new BasicNameValuePair("identity_type", ""));
		params.add(new BasicNameValuePair("entrust_bs", "2"));
		params.add(new BasicNameValuePair("batch_flag", "0"));
		return params;
	}

	public static String getIp() { // 获取本机ip
		String sIP = null;
		try {
			InetAddress address = InetAddress.getLocalHost();
			sIP = address.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sIP;
	}

	public static String getMac() { // 获取本机mac
		String sMAC = null;
		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(address);
			byte[] mac = ni.getHardwareAddress();
			Formatter formatter = new Formatter();
			for (int i = 0; i < mac.length; i++) {
				sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "")
						.toString();

			}
			formatter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sMAC;
	}

	public static void getUserInfo() { // 获取用户登录账号密码信息
		String UserInfo = "UserInfo.txt"; // 识别验证码的包的命令
		File file = new File(UserInfo);
		if (file.isFile() && file.exists()) { // 判断文件是否存在
			InputStreamReader read = null;
			try {
				read = new InputStreamReader(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			BufferedReader bufferedReader = new BufferedReader(read);
			String UserInfoJson = null;
			try {
				UserInfoJson = bufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.out.println(UserInfoJson); //调试用
			JSONObject jo = JSONObject.fromObject(UserInfoJson);
//			System.out.println(jo.toString()); //调试用
			userName  = jo.getString("userName");
			servicePwd = jo.getString("servicePwd");
			trdpwd = jo.getString("trdpwd");
//			System.out.println(" userName:"+ userName + " servicePwd:" + servicePwd + " trdpwd:" + trdpwd); //调试用
			custid = userName;
			fund_account = userName;
			trdpwdEns = trdpwd;
			password = trdpwd;
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
				}
			 else {
			System.out.println("找不到指定的文件");
		}

	}
}
