package utils.net.wsdelegate;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedHashMap;

public class RKServer{
	private static String serverName = WebserviceUtils.RKServer;
	public static String GetApplyCustomInfo(String codes)throws IOException, XmlPullParserException {
		LinkedHashMap<String, Object> properties = new LinkedHashMap<>();
		properties.put("codes", codes);
		return WebserviceUtils.getWcfResult(properties, "GetApplyCustomInfo", serverName);
	}

}