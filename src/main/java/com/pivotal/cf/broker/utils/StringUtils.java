package com.pivotal.cf.broker.utils;

import static java.lang.Math.*;

import java.util.Random;

public class StringUtils {
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static Random rnd = new Random();

	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++){
			if(i==0)
				sb.append(AB.charAt(10+rnd.nextInt(AB.length()-10)));
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		return sb.toString();
	}
	
	
}
