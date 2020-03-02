package es.uned.si3.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author cyague
 */
public final class Utils {

	/**
	 * Comprueba si el valor de una clave del HashMap <code>userData</code> es
	 * de tipo <code>String</code> y no es vacío
	 * 
	 * @param userData
	 * @param sessionKey
	 * @return
	 */
	public static String isValidString(HashMap<String, Object> userData,
			String sessionKey) {
		String result = null;

		Object stringParamObj = userData.get(sessionKey);

		if (stringParamObj != null && stringParamObj instanceof String) {
			result = (String) stringParamObj;
		}

		return result;
	}

	/**
	 * Comprueba si el valor de una clave del HashMap <code>userData</code> es
	 * de tipo Date y no es vacío
	 * 
	 * @param userData
	 * @param sessionKey
	 * @return
	 */
	public static Date isValidDate(HashMap<String, Object> userData,
			String sessionKey) {
		Date result = null;

		Object dateParamObj = userData.get(sessionKey);

		if (dateParamObj != null && dateParamObj instanceof Date) {
			result = (Date) dateParamObj;
		}

		return result;
	}

	/**
	 * Método que calcula la fecha y hora actual
	 * 
	 * @return la fecha y hora actual
	 */
	public static Date getCurrentDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * Comprueba si la fecha <code>date</code> corresponde al día de hoy
	 * 
	 * @param date
	 * @return
	 */
	public static Boolean compareAreToday(Date date) {
		return compareAreToday(date, getCurrentDate());
	}

	/**
	 * Compara si ambas fechas corresponden al mismo día
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Boolean compareAreToday(Date date1, Date date2) {

		Calendar calendar = Calendar.getInstance();
		int year1, month1, day1;
		int year2, month2, day2;

		calendar.setTime(date1);
		year1 = calendar.get(Calendar.YEAR);
		month1 = calendar.get(Calendar.MONTH);
		day1 = calendar.get(Calendar.DAY_OF_MONTH);

		calendar.setTime(date2);
		year2 = calendar.get(Calendar.YEAR);
		month2 = calendar.get(Calendar.MONTH);
		day2 = calendar.get(Calendar.DAY_OF_MONTH);

		if (year1 != year2) {
			return Boolean.FALSE;
		}

		if (month1 != month2) {
			return Boolean.FALSE;
		}

		if (day1 != day2) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}
}
