package es.uned.si3.util;

import org.apache.log4j.Logger;

public class LoggerHelper {
	private static Logger defaultLogger= null;
	public static Logger getDefaultLogger() {
		if( defaultLogger == null)
			defaultLogger= Logger.getLogger("A");
		
		return defaultLogger;
	}

}
