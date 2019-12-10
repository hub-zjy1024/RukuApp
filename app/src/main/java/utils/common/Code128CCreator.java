package utils.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import utils.net.ftp.FTPUtils;


public class Code128CCreator {
	private static final String CODE = "utf-8";
	private static final int BLACK = 0xff000000;
	private static final int WHITE = 0xFFFFFFFF;
	String[][] code128 = { { " ", " ", "00", "212222", "11011001100" }, { "!", "!", "01", "222122", "11001101100" },
			{ "\"", "\"", "02", "222221", "11001100110" }, { "#", "#", "03", "121223", "10010011000" },
			{ "$", "$", "04", "121322", "10010001100" }, { "%", "%", "05", "131222", "10001001100" },
			{ "&", "&", "06", "122213", "10011001000" }, { "'", "'", "07", "122312", "10011000100", },
			{ "(", "(", "08", "132212", "10001100100" }, { ")", ")", "09", "221213", "11001001000" },
			{ "*", "*", "10", "221312", "11001000100" }, { "+", "+", "11", "231212", "11000100100" },
			{ ",", ",", "12", "112232", "10110011100" }, { "-", "-", "13", "122132", "10011011100" },
			{ ".", ".", "14", "122231", "10011001110" }, { "/", "/", "15", "113222", "10111001100" },
			{ "0", "0", "16", "123122", "10011101100" }, { "1", "1", "17", "123221", "10011100110" },
			{ "2", "2", "18", "223211", "11001110010", }, { "3", "3", "19", "221132", "11001011100" },
			{ "4", "4", "20", "221231", "11001001110", }, { "5", "5", "21", "213212", "11011100100" },
			{ "6", "6", "22", "223112", "11001110100" }, { "7", "7", "23", "312131", "11101101110" },
			{ "8", "8", "24", "311222", "11101001100" }, { "9", "9", "25", "321122", "11100101100" },
			{ ":", ":", "26", "321221", "11100100110" }, { ";", ";", "27", "312212", "11101100100" },
			{ "<", "<", "28", "322112", "11100110100" }, { "=", "=", "29", "322211", "11100110010" },
			{ ">", ">", "30", "212123", "11011011000" }, { "?", "?", "31", "212321", "11011000110" },
			{ "@", "@", "32", "232121", "11000110110" }, { "A", "A", "33", "111323", "10100011000" },
			{ "B", "B", "34", "131123", "10001011000" }, { "C", "C", "35", "131321", "10001000110" },
			{ "D", "D", "36", "112313", "10110001000" }, { "E", "E", "37", "132113", "10001101000" },
			{ "F", "F", "38", "132311", "10001100010" }, { "G", "G", "39", "211313", "11010001000" },
			{ "H", "H", "40", "231113", "11000101000" }, { "I", "I", "41", "231311", "11000100010" },
			{ "J", "J", "42", "112133", "10110111000" }, { "K", "K", "43", "112331", "10110001110" },
			{ "L", "L", "44", "132131", "10001101110" }, { "M", "M", "45", "113123", "10111011000" },
			{ "N", "N", "46", "113321", "10111000110" }, { "O", "O", "47", "133121", "10001110110" },
			{ "P", "P", "48", "313121", "11101110110" }, { "Q", "Q", "49", "211331", "11010001110" },
			{ "R", "R", "50", "231131", "11000101110" }, { "S", "S", "51", "213113", "11011101000" },
			{ "T", "T", "52", "213311", "11011100010" }, { "U", "U", "53", "213131", "11011101110" },
			{ "V", "V", "54", "311123", "11101011000" }, { "W", "W", "55", "311321", "11101000110" },
			{ "X", "X", "56", "331121", "11100010110" }, { "Y", "Y", "57", "312113", "11101101000" },
			{ "Z", "Z", "58", "312311", "11101100010" }, { "[", "[", "59", "332111", "11100011010" },
			{ "\\", "\\", "60", "314111", "11101111010" }, { "]", "]", "61", "221411", "11001000010" },
			{ "^", "^", "62", "431111", "11110001010" }, { "_", "_", "63", "111224", "10100110000" },
			{ "NUL", "`", "64", "111422", "10100001100" }, { "SOH", "a", "65", "121124", "10010110000" },
			{ "STX", "b", "66", "121421", "10010000110" }, { "ETX", "c", "67", "141122", "10000101100" },
			{ "EOT", "d", "68", "141221", "10000100110" }, { "ENQ", "e", "69", "112214", "10110010000" },
			{ "ACK", "f", "70", "112412", "10110000100" }, { "BEL", "g", "71", "122114", "10011010000" },
			{ "BS", "h", "72", "122411", "10011000010" }, { "HT", "i", "73", "142112", "10000110100" },
			{ "LF", "j", "74", "142211", "10000110010" }, { "VT", "k", "75", "241211", "11000010010" },
			{ "FF", "I", "76", "221114", "11001010000" }, { "CR", "m", "77", "413111", "11110111010" },
			{ "SO", "n", "78", "241112", "11000010100" }, { "SI", "o", "79", "134111", "10001111010" },
			{ "DLE", "p", "80", "111242", "10100111100" }, { "DC1", "q", "81", "121142", "10010111100" },
			{ "DC2", "r", "82", "121241", "10010011110" }, { "DC3", "s", "83", "114212", "10111100100" },
			{ "DC4", "t", "84", "124112", "10011110100" }, { "NAK", "u", "85", "124211", "10011110010" },
			{ "SYN", "v", "86", "411212", "11110100100" }, { "ETB", "w", "87", "421112", "11110010100" },
			{ "CAN", "x", "88", "421211", "11110010010" }, { "EM", "y", "89", "212141", "11011011110" },
			{ "SUB", "z", "90", "214121", "11011110110" }, { "ESC", "{", "91", "412121", "11110110110" },
			{ "FS", "|", "92", "111143", "10101111000" }, { "GS", "},", "93", "111341", "10100011110" },
			{ "RS", "~", "94", "131141", "10001011110" }, { "US", "DEL", "95", "114113", "10111101000" },
			{ "FNC3", "FNC3", "96", "114311", "10111100010" }, { "FNC2", "FNC2", "97", "411113", "11110101000" },
			{ "SHIFT", "SHIFT", "98", "411311", "11110100010" }, { "CODEC", "CODEC", "99", "113141", "10111011110" },
			{ "CODEB", "FNC4", "CODEB", "114131", "10111101110" },
			{ "FNC4", "CODEA", "CODEA", "311141", "11101011110" }, { "FNC1", "FNC1", "FNC1", "411131", "11110101110" },
			{ "StartA", "StartA", "StartA", "211412", "11010000100" },
			{ "StartB", "StartB", "StartB", "211214", "11010010000" },
			{ "StartC", "StartC", "StartC", "211232", "11010011100" },
			{ "Stop", "Stop", "Stop", "2331112", "1100011101011" } };

	enum CodeTYPE {
		CODE128A, CODE128B, CODE128C,
	}

	/**
	 * 生产Code128的条形码的code
	 * 
	 * @param barCode
	 * @param encode
	 * @return
	 */
	public String getCode(String barCode, String encode) {
		String _Text = "";// 返回的参数
		List<Integer> dataList = new ArrayList<Integer>();// 2截取位的组合
		int codeC = 105; // 首位
		// 编码不能是奇数
		if (!((barCode.length() & 1) == 0))
			return "";
		while (barCode.length() != 0) {
			int _Temp = 0;
			try {
				// Code128 编码必须为数字
				_Temp = Integer.valueOf(barCode.substring(0, 2));
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			// 获得条纹
			_Text += getValue(_Temp);
			dataList.add(_Temp);
			// 条码截取2个就需要去掉用过的前二位
			barCode = barCode.substring(2);
		}
		if (dataList.size() == 0) {
			return "";
		}
		_Text = getValue(codeC) + _Text; // 获取开始位
		for (int i = 0; i != dataList.size(); i++) {
			codeC += dataList.get(i) * (i + 1);
		}
		codeC = codeC % 103; // 获得校验位
		_Text += getValue(codeC); // 获取校验位
		_Text += "1100011101011"; // 结束位
		return _Text;
	}
	public String getCodeC(String barCode) {
		String _Text = "";// 返回的参数
		List<Integer> dataList = new ArrayList<Integer>();// 2截取位的组合
		int codeC = 105; // 首位
		// 编码不能是奇数
		if (!((barCode.length() & 1) == 0))
			return "";
		while (barCode.length() != 0) {
			int _Temp = 0;
			try {
				// Code128 编码必须为数字
				_Temp = Integer.valueOf(barCode.substring(0, 2));
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			// 获得条纹
			_Text += getValue(_Temp);
			dataList.add(_Temp);
			// 条码截取2个就需要去掉用过的前二位
			barCode = barCode.substring(2);
		}
		if (dataList.size() == 0) {
			return "";
		}
		_Text = getValue(codeC) + _Text; // 获取开始位
		for (int i = 0; i != dataList.size(); i++) {
			codeC += dataList.get(i) * (i + 1);
		}
		codeC = codeC % 103; // 获得校验位
		_Text += getValue(codeC); // 获取校验位
		_Text += "1100011101011"; // 结束位
		return _Text;
	}

	/**
	 * 生产Code128的条形码的code
	 * {@link #getCodeC(String)}sadfasdfasdfasdfasdf
	 * @author js
	 * @param barCode
	 * @return
	 * @throws Exception
	 */
	public String getCodeA(String barCode, int type) throws Exception {
		String _Text = "";// 返回的参数
		String start = "StartA"; // 首位
		switch (type) {
		case 0:
			start = "StartA";
			break;
		case 1:
			start = "StartB";
			break;
		case 2:
			start = "StartC";
			break;
		default:
			start = "StartA";
			break;
		}
		char[] charArray = barCode.toCharArray();
		_Text += getValue(start, type); // 获取开始位
		int checkCode = 0;
		for (int i = 0; i < charArray.length; i++) {
			String value = String.valueOf(charArray[i]);
			if (type == 2) {
				if (i + 1 >= charArray.length) {
					break;
				}
				value = charArray[i] + "" + charArray[i + 1];
			}
			_Text += getValue(value , type);
			checkCode += getCheckID(value, type) * (i + 1);
		}
		checkCode = (getCheckID(start, type) + checkCode) % 103;// 获取校验位
		_Text += code128[checkCode][4];
		_Text += "1100011101011"; // 结束位
		return _Text;
	}

	/**
	 * 根据编号获得条纹
	 * 
	 * @param p_Value
	 * @param type
	 * @return
	 */
	private String getValue(String p_Value, int type) {
		if (type > 2) {
			System.out.println("type 只能为 0 1 2");
			return "";
		}
		for (int i = 0; i < code128.length; i++) {
			String s = code128[i][type];
			if (s.equals(p_Value)) {
				return code128[i][4];
			}
		}
		return "";
	}

	/**
	 * @param p_Value 
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private int getCheckID(String p_Value, int type) throws Exception { for (int i = 0; i < code128.length; i++) {
			String s = code128[i][type];
			if (s.equals(p_Value)) {
				return i;
			}
		}
		throw new Exception("no such Char " + p_Value);
	}

	// / <summary>
	// / 根据编号获得条纹
	// / </summary>
	// / <param name="p_CodeId"></param>
	// / <returns></returns>
	private String getValue(int p_CodeId) {
		return code128[p_CodeId][4];
	}

	public static void main(String[] args) throws Exception {
		String content0 = "118842807789";
		int width = 100;
		int height = 50;
		Code128CCreator c = new Code128CCreator();
		String barCode = c.getCode(content0, "");
		c.kiCode128C(barCode, 2, 35, "d:/dyj/code128c.png");
		FileOutputStream fos = new FileOutputStream("d:/dyj/code128.png");
		fos.close();
		// content0 = "12u!^&*($@$*)~!_%(";
		String codeA = c.getCodeA(content0, 1);
		c.kiCode128C(codeA, 2, 35, "d:/dyj/code128A.png");

	}

	// / <summary>
	// / 生成条码
	// / </summary>
	// / <param name="BarString">条码模式字符串</param>
	// / <param name="Height">生成的条码高度</param>
	// / <returns>条码图形</returns>
	public void create128c(String codeContent, String path) {
		int desity = 1;
		int height = 50;
		create128c(codeContent, desity, height, path);
	}

	public void create128c(String codeContent, int density, int Height, String path) {
		try {
			String barStr = getCodeA(codeContent, 2);
//			String barStr = getCode(codeContent, "");

			kiCode128C(barStr, density, Height, path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void kiCode128C(String barString, int density, int Height, String path) {
		try {
			File myPNG = new File(path);
			OutputStream out = new FileOutputStream(myPNG);
			if (null == barString || null == out || 0 == barString.length()) {
				out.close();
				return;
			}
			Log.e("zjy", getClass() + "->kiCode128C(): barString==" + barString);
			char[] cs = barString.toCharArray();
			int len = density;
			int width = len * cs.length;
			int height = Height;

			Bitmap mBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			Canvas s = new Canvas(mBackgroundBitmap);
			Paint mPaint = new Paint();
			for (int i = 0; i < cs.length; i++) {
				if ("1".equals(cs[i] + "")) {
					mPaint.setColor(Color.BLACK);
				}else {
					mPaint.setColor(Color.WHITE);
				}
				s.drawRect(i * len, 0, (i + 1) * len, Height, mPaint);
			}
			s.save();
			s.restore();
			mBackgroundBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
//			FtpUploader mUPloader = new FtpUploader(FTPUtils.mainAddress);
			String remote = "/testCodec.jpeg";
			FTPUtils.uploadToLocalFTP(path, remote);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
