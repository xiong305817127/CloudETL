package com.idatrix.metacube.service;

public class xh_test {

	public static void main(String[] args) {

		String type = "bigint" ;
//		String type = "bigint(20)" ;
//		String type = "bigint(20,5)" ;
//		String type = "bigint unsigned" ;
//		String type = "bigint(20) unsigned" ;
//		String type = "bigint(20,3) unsigned" ;

		// (?<=^\S{3})\S{3} 
		//\S{3}(?=\S{3}$)
		String[] types =type.split("\\s");
		System.out.println(types[0].replaceAll("(?<=^.*)[^\\w].*", ""));
		System.out.println(types[0].replaceAll(".*\\((?=\\d*)", "").replaceAll("(?<=^\\d*)\\D.*", ""));
		System.out.println(types[0].replaceAll(".*,(?=\\d*)", "").replaceAll("(?<=^\\d*)\\D.*", ""));
		System.out.println(types.length>1?types[1]:"");

	}

}
