package exchanger;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import cn.skypark.code.MyCheckCodeTool;

public class Exchange { // 所有交易用到的方法
	static CloseableHttpClient Exchanger = HttpClientBuilder.create().build();

	public static void Login() { // 自动登录账号信息，包含登录需要的，向登录页请求，向验证码页请求，
		Globals.getUserInfo(); //获取登录需要的账号信息
		HttpGet getLoginPage = new HttpGet(Globals.login_page);
		try {
			Exchanger.execute(getLoginPage); // 发起对登录页的GET请求
			// System.out.println(" "); // 调试用断点位置
			// HttpResponse responseLoginPage = Exchanger.execute(getLoginPage);
			// // 调试用，发起对登录页的GET请求
			// System.out.println("result = " +
			// EntityUtils.toString(responseLoginPage.getEntity(), "utf-8")); //
			// 调试用，获取登录页的内容
			// System.out.println("resCode = " +
			// responseLoginPage.getStatusLine().getStatusCode()); // 调试用，获取响应码
			List<NameValuePair> params = Globals.login();
			HttpPost postLoginInfo = new HttpPost(Globals.login_api);
			try {
				postLoginInfo.setEntity(new UrlEncodedFormEntity(params, "utf-8")); // 设置发送的登录信息
				// HttpEntity postEntity = postLoginInfo.getEntity(); // 调试用
				// if (postEntity != null) { // 调试用
				// System.out.println("Post content: " +
				// EntityUtils.toString(postEntity, "utf-8")); //
				// 调试用，显示向登录页传递的账号信息
				// } // 调试用
				HttpResponse responseLogin = Exchanger.execute(postLoginInfo); // 向登录页传递账号信息
				HttpEntity responseEntity = responseLogin.getEntity();
				if (responseEntity != null) {
					String LoginInfo = EntityUtils.toString(responseEntity, "utf-8");
					// System.out.println("Response content: " + LoginInfo); //
					// 调试用，获取已登录的页
					int i = LoginInfo.indexOf("欢迎您");
					if (i != -1) {
						GetTradeInfo();
					} else {
						JOptionPane.showMessageDialog(null, "登录失败！", "消息提示", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public static String getVerifyCode() { // 通过调用getcode_jdk1.8.jar包获取验证码
		Random random = new Random();
		int r = random.nextInt();
		HttpGet getVerifyImage = new HttpGet(Globals.verify_code_api + "?" + r);
		String VerifyCode = null;
		try {
			HttpResponse responseVerifyImage = Exchanger.execute(getVerifyImage); // 发起GET请求
			InputStream inputVerifyImage = responseVerifyImage.getEntity().getContent();
			FileOutputStream outputVerifyImage = new FileOutputStream("VerifyCode.jpg"); // 将GET到的验证码保存为图片
			byte[] data = new byte[1024];
			int len = 0;
			while ((len = inputVerifyImage.read(data)) != -1) {
				outputVerifyImage.write(data, 0, len);
			}
			outputVerifyImage.close();
			VerifyCode = Image2Code(); // 运行验证码识别包
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return VerifyCode;
	}

	public static String Image2Code() { // 将验证码保存为图片存于文件目录
		String result = null;
		String CodeImage = "VerifyCode.jpg"; // 识别验证码的包的命令
		String[] getCodeArg = new String[] { CodeImage };
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bao);
			PrintStream so = System.out;
			System.setOut(ps);
			MyCheckCodeTool.main(getCodeArg); // 截取命令行返回结果
			result = bao.toString();
			System.setOut(so);
			result = result.substring(26, 30); // 仅保留验证码部分
			// System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void GetTradeInfo() { // 获取交易用的uid 和 password信息
		HttpGet getTradeInfo = new HttpGet(Globals.trade_info_page);
		String TradeInfo = null;
		try {
			HttpResponse responseTradeInfo = Exchanger.execute(getTradeInfo); // 发起对登录信息页的GET请求
			HttpEntity responseEntity = responseTradeInfo.getEntity();
			if (responseEntity != null) {
				TradeInfo = EntityUtils.toString(responseEntity, "utf-8");
				// System.out.println("Response content: " + TradeInfo); //
				// 调试用，获取登录信息页
			}
			String JsonBase64 = "	var data = \"([/=\\w\\+]+)\""; // 得到base64的正则表达式
			Pattern Base64Pattern = Pattern.compile(JsonBase64);
			Matcher MatchBase64 = Base64Pattern.matcher(TradeInfo); // 匹配交易信息中的正则表达式
			MatchBase64.find();
			String temp = MatchBase64.toString();
			// System.out.println(temp); // 调试用，输出base64的交易信息
			int TradeInfoJsonLength = temp.length(); // 输出匹配后的base64的交易信息长度
			// System.out.println(TradeInfoJsonLength); // 调试用，输出交易信息长度
			String TradeInfoJsonBase64 = temp.substring(95, TradeInfoJsonLength - 2); // 使用偏移量移除匹配的var
																						// data
																						// =
																						// 字段
//			System.out.println(TradeInfoJsonBase64); // 调试用，输出base64编码的的交易信息
			byte[] TradeInfoJsonByte = Base64.getDecoder().decode(TradeInfoJsonBase64); // 将base64格式解码
			String TradeInfoJson = null;
			try {
				TradeInfoJson = new String(TradeInfoJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
//			System.out.println(TradeInfoJson); // 调试用，输出json格式的交易信息
			JSONObject jo = JSONObject.fromObject(TradeInfoJson);
			JSONArray ja = jo.getJSONArray("item");
			JSONObject shanghai = ja.getJSONObject(0); // 读取上海交易账号信息
			Globals.sh_exchange_type = shanghai.getString("exchange_type");
			Globals.sh_stock_account = shanghai.getString("stock_account");
			JSONObject shenzhen = ja.getJSONObject(1); // 读取深圳交易账号信息
			Globals.sz_exchange_type = shenzhen.getString("exchange_type");
			Globals.sz_stock_account = shenzhen.getString("stock_account");
			Globals.client_risklevel = jo.getString("branch_no");
			Globals.op_station = jo.getString("op_station");
			Globals.trdpwd = jo.getString("trdpwd");
			Globals.uid = jo.getString("uid");
			Globals.branch_no = jo.getString("branch_no");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String GetPosition(){ //获取账户持仓状况
		String Position = null;
		String PositionParams = Globals.position().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(PositionParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		String Params = CreatBasicParams() + temp;
//		System.out.println(Params); // 调试用，输出参数设置
		String PositionURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(PositionURL); // 调试用，输出申请的网页地址
		HttpGet getPosition = new HttpGet(PositionURL);
		getPosition.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchanger.execute(getPosition);
//			System.out.println(" "); // 调试用断点位置
			HttpResponse responseGetPosition = Exchanger.execute(getPosition); // 调试用，发起对登录页的GET请求
			String PositionJsonBase64 = EntityUtils.toString(responseGetPosition.getEntity(), "utf-8"); // 获取登录页的内容
			byte[] PositionJsonByte = Base64.getDecoder().decode(PositionJsonBase64); // 将base64格式解码
			String PositionJson = null;
			try {
				PositionJson = new String(PositionJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
				Position = PositionJson;
				//处理json
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Position;
	}
	
	public static String GetBalance(){ //获取账户资金状况
		String Balance = null;
		String BalanceParams = Globals.balance().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(BalanceParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		String Params = CreatBasicParams() + temp;
//		System.out.println(Params); // 调试用，输出参数设置
		String BalanceURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(BalanceURL); // 调试用，输出申请的网页地址
		HttpGet getBalance = new HttpGet(BalanceURL);
		getBalance.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchanger.execute(getBalance);
//			System.out.println(" "); // 调试用断点位置
			HttpResponse responseGetBalance = Exchanger.execute(getBalance); // 调试用，发起对登录页的GET请求
			String BalanceJsonBase64 = EntityUtils.toString(responseGetBalance.getEntity(), "utf-8"); // 获取登录页的内容
			byte[] BalanceJsonByte = Base64.getDecoder().decode(BalanceJsonBase64); // 将base64格式解码
			String BalanceJson = null;
			try {
				BalanceJson = new String(BalanceJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
				Balance = BalanceJson;
				//处理json
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Balance;
	}

	public static String GetEntrust(){ //获取当日委托单
		String Entrust = null;
		String EntrustParams = Globals.entrust().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(EntrustParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		String Params = CreatBasicParams() + temp;
//		System.out.println(Params); // 调试用，输出参数设置
		String EntrustURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(EntrustURL); // 调试用，输出申请的网页地址
		HttpGet getEntrust = new HttpGet(EntrustURL);
		getEntrust.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchanger.execute(getEntrust);
//			System.out.println(" "); // 调试用断点位置
			HttpResponse responseGetEntrust = Exchanger.execute(getEntrust); // 调试用，发起对登录页的GET请求
			String EntrustJsonBase64 = EntityUtils.toString(responseGetEntrust.getEntity(), "utf-8"); // 获取登录页的内容
			byte[] EntrustJsonByte = Base64.getDecoder().decode(EntrustJsonBase64); // 将base64格式解码
			String EntrustJson = null;
			try {
				EntrustJson = new String(EntrustJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
				Entrust = EntrustJson;
				//处理json
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Entrust;
	}
	
	public static String CancelEntrust(int EntrustNo){ //取消委托单
		String CancelEntrust = null;
		String CancelEntrustParams = Globals.cancel_entrust().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(CancelEntrustParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		String password = Globals.trdpwd;
		int entrust_no = EntrustNo; //要取消的委托单号
		temp = temp + "&" + "password=" + password + "&" + "entrust_no=" + entrust_no;
		String Params = CreatBasicParams() + temp;
//		System.out.println(Params); // 调试用，输出参数设置
		String CancelEntrustURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(CancelEntrustURL); // 调试用，输出申请的网页地址
		HttpGet getCancelEntrust = new HttpGet(CancelEntrustURL);
		getCancelEntrust.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchanger.execute(getCancelEntrust);
//			System.out.println(" "); // 调试用断点位置
			HttpResponse responseGetCancelEntrust = Exchanger.execute(getCancelEntrust); // 调试用，发起对登录页的GET请求
			String CancelEntrustJsonBase64 = EntityUtils.toString(responseGetCancelEntrust.getEntity(), "utf-8"); // 获取登录页的内容
			byte[] CancelEntrustJsonByte = Base64.getDecoder().decode(CancelEntrustJsonBase64); // 将base64格式解码
			String CancelEntrustJson = null;
			try {
				CancelEntrustJson = new String(CancelEntrustJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
				CancelEntrust = CancelEntrustJson;
				//处理json
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return CancelEntrust;
	}
	
	public static String Buy(int StockCode, int Price){ // 买
		String Buy = null;
		String BuyParams = Globals.buy().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(BuyParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		int stock_code = StockCode; // 股票代码
		int exchange_type = selectSHSZ(stock_code); //选择上海深圳
		String stock_account = null;
		if (exchange_type == 1){
			stock_account = Globals.sh_stock_account;
		}
		else {
			stock_account = Globals.sz_stock_account;
		}
		int price = Price; // 买入价格
		int entrust_prop = 0; // 委托类型，暂未实现，默认为限价委托
		temp = temp + "&" + "stock_account=" + stock_account + "&" + "exchange_type=" + exchange_type + "&" + "fund_account=" + Globals.fund_account + "&" + "client_risklevel=" + Globals.branch_no + "&" + "op_station=" + Globals.op_station + "&" + "trdpwd=" + Globals.trdpwd  + "&" + "uid=" + Globals.uid + "&" + "branch_no=" + Globals.branch_no + "&" + "entrust_prop=" + entrust_prop + "&" + "stock_code =" + stock_code + "&" + "entrust_price=" + price;
		String Params = CreatBasicParams() + temp;
//		System.out.println(Params); // 调试用，输出参数设置
		String BuyURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(BuyURL); // 调试用，输出申请的网页地址
		HttpGet getBuy = new HttpGet(BuyURL);
		getBuy.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchanger.execute(getBuy);
//			System.out.println(" "); // 调试用断点位置
			HttpResponse responseGetBuy = Exchanger.execute(getBuy); // 调试用，发起对登录页的GET请求
			String BuyJsonBase64 = EntityUtils.toString(responseGetBuy.getEntity(), "utf-8"); // 获取登录页的内容
			byte[] BuyJsonByte = Base64.getDecoder().decode(BuyJsonBase64); // 将base64格式解码
			String BuyJson = null;
			try {
				BuyJson = new String(BuyJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
				Buy = BuyJson;
				//处理json
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Buy;
	}
	
	public static String Sell(int StockCode, int Price){ // 卖
		String Sell = null;
		String SellParams = Globals.sell().toString();
		Pattern dot = Pattern.compile(", ");
		Matcher MatchDot = dot.matcher(SellParams); // 把,_改成&并且去掉[]
		String temp = MatchDot.replaceAll("&");
		temp = temp.substring(1, temp.length() - 1);
		int stock_code = StockCode; // 股票代码
		int exchange_type = selectSHSZ(stock_code); //选择上海深圳
		String stock_account = null;
		if (exchange_type == 1){
			stock_account = Globals.sh_stock_account;
		}
		else {
			stock_account = Globals.sz_stock_account;
		}
		int price = Price; // 卖出价格
		int entrust_prop = 0; // 委托类型，暂未实现，默认为限价委托
		temp = temp + "&" + "stock_account=" + stock_account + "&" + "exchange_type=" + exchange_type + "&" + "fund_account=" + Globals.fund_account + "&" + "client_risklevel=" + Globals.branch_no + "&" + "op_station=" + Globals.op_station + "&" + "trdpwd=" + Globals.trdpwd  + "&" + "uid=" + Globals.uid + "&" + "branch_no=" + Globals.branch_no + "&" + "entrust_prop=" + entrust_prop + "&" + "stock_code =" + stock_code + "&" + "entrust_price=" + price;
		String Params = CreatBasicParams() + temp;
//		System.out.println(Params); // 调试用，输出参数设置
		String SellURL = Globals.prefix + Base64.getEncoder().encodeToString(Params.getBytes()); 
//		System.out.println(SellURL); // 调试用，输出申请的网页地址
		HttpGet getSell = new HttpGet(SellURL);
		getSell.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko");
		try {
			Exchanger.execute(getSell);
//			System.out.println(" "); // 调试用断点位置
			HttpResponse responseGetSell = Exchanger.execute(getSell); // 调试用，发起对登录页的GET请求
			String SellJsonBase64 = EntityUtils.toString(responseGetSell.getEntity(), "utf-8"); // 获取登录页的内容
			byte[] SellJsonByte = Base64.getDecoder().decode(SellJsonBase64); // 将base64格式解码
			String SellJson = null;
			try {
				SellJson = new String(SellJsonByte, "utf-8"); // 对解码后使用utf-8编码得到json格式交易信息
				Sell = SellJson;
				//处理json
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Sell;
	}
	
	public static String CreatBasicParams() { // 返回交易所需的基本参数
		Random random = new Random();
		int r = random.nextInt();
		String op_branch_no = Globals.branch_no;
		String BasicParams = "uid=" + Globals.uid + "&" + "version=" + Globals.version  + "&" + "custid="  + Globals.custid + "&" + "op_branch_no=" + op_branch_no + "&" + "branch_no=" + Globals.branch_no + "&" 
				+ "op_entrust_way=" + Globals.op_entrust_way + "&" + "op_station=" + Globals.op_station + "&" + "fund_account=" + Globals.fund_account  + "&" + "password=" + Globals.password
				 + "&" + "identity_type=" + Globals.identity_type + "&" + "ram=" + r;
//		System.out.println(BasicParams); // 调试用，输出基本参数
		return BasicParams;
	}

	public static int selectSHSZ(int StockCode){ // 识别股票代码首字符判断深沪市
		String stockcode = Integer.toString(StockCode);
		String temp = stockcode.substring(0);
		if (temp.equals(5)||temp.equals(6)||temp.equals(9)){
			return 1; // 1是上海
		}
		else{
			return 2; // 2是深圳
		}
	}
	
	public static void keepAlive(){ //每30秒尝试获取一次账户资金信息以保持会话
		Timer timer = new Timer(); 
	    timer.schedule(new KeepAlive(), 30 * 1000, 30 * 1000);
	}
	
}