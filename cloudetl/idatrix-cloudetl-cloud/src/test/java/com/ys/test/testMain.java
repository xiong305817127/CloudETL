package com.ys.test;

import java.util.Date;

public class testMain {

	public static void main(String[] args) {

		
		Class<?>[] fields = new Class[] { int.class ,Integer.class,Long.class,long.class,double.class,Double.class,float.class,Float.class,Boolean.class,boolean.class,String.class,Date.class};
		for(Class<?> f : fields) {
			String name = f.getSimpleName();
			System.out.println("=name="+name+"=====f="+f);
			
		}
	}

}
