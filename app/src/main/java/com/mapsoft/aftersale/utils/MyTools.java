package com.mapsoft.aftersale.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyTools {
	private static double Rc = 6378137; // 赤道半径
	private static double Rj = 6356725; // 极半径
	public static class Result{
		public int angle;
		public int distance;
		public Result(int angel,int distance) {
			this.angle=angel;
			this.distance=distance;
		}
	}
	private static class CJWD
	{
		double m_LoDeg, m_LoMin, m_LoSec; // longtitude 经度
		double m_LaDeg, m_LaMin, m_LaSec;
		double m_Longitude, m_Latitude;
		double m_RadLo, m_RadLa;
		double Ec;
		double Ed;
		public CJWD(double longitude, double latitude){
			m_LoDeg = (int)longitude;
			m_LoMin = (int)((longitude - m_LoDeg)*60);
			m_LoSec = (longitude - m_LoDeg - m_LoMin/60.)*3600;

			m_LaDeg = (int)(latitude);
			m_LaMin = (int)((latitude - m_LaDeg)*60);
			m_LaSec = (latitude - m_LaDeg - m_LaMin/60.)*3600;

			m_Longitude = longitude;
			m_Latitude = latitude;
			m_RadLo = longitude * 3.14159265/180.;
			m_RadLa = latitude * 3.14159265/180.;
			Ec = Rj + (Rc - Rj) * (90.0-m_Latitude) / 90.;
			Ed = Ec * Math.cos(m_RadLa);
		}

	}
	/**
	 * 获取角度
	 * @param A
	 * @param B
	 * @return
	 */
	public static Result getAngle(double longitude1, double latitude1,double longitude2, double latitude2) {
		CJWD A=new CJWD(longitude1, latitude1);
		CJWD B=new CJWD(longitude2, latitude2);
		return angle(A, B);
	}
	//获取角度
	private static Result angle(CJWD A, CJWD B) {
		double angle;
		double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
		double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
		double out = Math.sqrt(dx * dx + dy * dy);
		angle = Math.atan( Math.abs(dx/dy))*180./3.14159265;
		// 判断象限
		double dLo = B.m_Longitude - A.m_Longitude;
		double dLa = B.m_Latitude - A.m_Latitude;

		if(dLo > 0 && dLa <= 0)
		{
			angle = (90.0 - angle) + 90.;
		}
		else if(dLo <= 0 && dLa < 0)
		{
			angle = angle + 180.;
		}
		else if(dLo < 0 && dLa >= 0)
		{
			angle = (90.0 - angle) + 270;
		}
		Result result=new Result((int)angle,(int)out);
		return result;
	}

	/// <summary>
	/// 取得一个文本文件流的编码方式。
	/// </summary>
	/// <param name="stream">文本文件流。</param>
	/// <param name="defaultEncoding">默认编码方式。当该方法无法从文件的头部取得有效的前导符时，将返回该编码方式。</param>
	/// <returns></returns>
	public static String GetEncoding(String filepath)
	{
		File file = new File(filepath);
		InputStream in;
		try {
			in = new java.io.FileInputStream(file);
			byte[] b = new byte[3];
			in.read(b);
			in.close();
			if(b[0] == -1 && b[1] ==-2&& b[2] != 0xFF)
				return "Unicode";
			if(b[0] == -2 && b[1] == -1)
				return "UTF-16BE";
			if (b[0] == -17 && b[1] == -69 && b[2] == -65)
				return "UTF-8";
			else
				return "UTF-8";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "UTF-8";
	}
}
