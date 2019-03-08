package com.ys.test.histo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;


public class testMain {

	public static void main(String[] args) throws Exception {

		String fileRoot = "C:\\Users\\Administrator\\Desktop\\" ;
		
		String new_fileName = "histo.txt";
		String old_fileName = "histo_old.txt";
		
		File out = new File(fileRoot,"histo_sort.txt");
		out.delete();
		
		File in =  new File(fileRoot,new_fileName) ;
		File compare_in = new File(fileRoot,old_fileName) ;
		
		// num:0     #instances:1   #bytes:2  class name:3
		sort( 1 , in ,compare_in , out);
	}
	
	public static void sort( int index, File in,  File compare_in , File out ) throws Exception {
		
		
		Map<String, List<String>> inMap;
		if( in.exists() ) {
			inMap = getClassMaps( getLines(in) );
		}else {
			inMap = Maps.newLinkedHashMap() ;
		}
		Map<String, List<String>>  in_compareMap ;
		if( compare_in.exists() ) {
			in_compareMap =  getClassMaps( getLines(compare_in) ) ;
			
		}else {
			in_compareMap = Maps.newLinkedHashMap() ;
		}
		
		int classLength = inMap.keySet().stream().max(( a,b) ->  (a!= null && b!=null)?a.length()- b.length(): 1  ).orElse("").length()+5;
		int instancesLength = 40 ;
		int bytesLength = 40 ;
		
		
		String title =  format("#instances",instancesLength)  +format("#bytes",bytesLength)+format("className",classLength) ;
		FileUtils.write(out,title+"\n\n" , true);
		
		inMap.entrySet().stream().filter(en ->{
				if( en.getValue().size() > 2 ) {
					return !Utils.isEmpty( Const.getDigitsOnly( en.getValue().get(1) ) ) && !Utils.isEmpty( Const.getDigitsOnly( en.getValue().get(2) ) );
				}
				return false ;
			} ).map( entry -> {
				String classStr = entry.getKey() ;
				List<String> new_list = entry.getValue() ;
				List<String> old_list = in_compareMap.get(classStr);
				String  instances = new_list.get(1)+":" ;
				String  bytes = new_list.get(2)+":" ;
				if( old_list != null && old_list.size() > 2 ) {
					instances = instances + old_list.get(1)+":" + ( Long.valueOf(Const.getDigitsOnly(  new_list.get(1) )) - Long.valueOf(Const.getDigitsOnly(  old_list.get(1) )) ) ;
					bytes = bytes + old_list.get(2)+":" +( Long.valueOf(Const.getDigitsOnly(  new_list.get(2) )) - Long.valueOf(Const.getDigitsOnly(  old_list.get(2) )) );
					
					new_list.add(instances);
					new_list.add(bytes);
					new_list.add( ( Long.valueOf(Const.getDigitsOnly(  new_list.get(index) )) - Long.valueOf(Const.getDigitsOnly(  old_list.get(index) )) )+"" );
				}else {
					new_list.add(instances+"无");
					new_list.add(bytes+"无");
					new_list.add( new_list.get(index) );
				}
				
				in_compareMap.remove(classStr);
				
				return entry ;
				
			}).sorted( ( a ,b ) -> { 
				return ((Long)( Long.valueOf(Const.getDigitsOnly(b.getValue().get(b.getValue().size()-1))) - Long.valueOf(Const.getDigitsOnly(a.getValue().get(a.getValue().size()-1))) )).intValue() ; 
			}).forEach(entry -> {
				try {
					String outStr =   format(entry.getValue().get(entry.getValue().size()-3),instancesLength)  + format(entry.getValue().get(entry.getValue().size()-2),bytesLength)+format( entry.getValue().get(0),5)+ format(entry.getKey(),classLength) ;
					FileUtils.write(out, outStr+"\n" , true);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			
			}); 
		
		if( in_compareMap.size() >0 ) {
			
			FileUtils.write(out, "=================删除的对象========================\n" , true);
			for( Entry<String, List<String>> entry : in_compareMap.entrySet() ) {
				String outStr =  format( entry.getValue().get(1),instancesLength)  + format( entry.getValue().get(2),bytesLength)+format( entry.getValue().get(0),5)+ format(entry.getKey(),classLength)  ;
				FileUtils.write(out, outStr+"\n" , true);
			}
			
			
		}
		
		System.out.println("sort 完成:"+inMap.size());
		
	}
	
	public static String format(String str , int length ) {
		
		return String.format("%-"+length+"s", str) ;
		
	}
	
	public static List<String> getLines( File in ) throws Exception {
		
		BufferedReader fin = new BufferedReader( new FileReader(in));
		
		List<String> result = Lists.newArrayList() ;
		
		String line;
		while( (line = fin.readLine()) != null ) {
			if( Utils.isEmpty(line) ) {
				continue ;
			}
			result.add(line) ;
		 }
		
		fin.close();
		
		return result;
	}
			
		
	public static Map<String , List<String> > getClassMaps( List<String> lines ) throws Exception {
		
		Map<String , List<String> > classMaps = Maps.newLinkedHashMap() ;
		for( String line : lines ) {
			if( Utils.isEmpty(line) ) {
				continue ;
			}
			List<String> lineInfo = Lists.newArrayList() ;
			if( line.contains("instances")) {
				continue ;
			}
			String[] ss = StringUtils.split(line);
			if( ss == null || ss.length <= 2 ) {
				continue ;
			}
			
			String classStr = null;
			int i =0 ;
			for(String s : ss) {
				if( Utils.isEmpty(s) ) {
					continue ;
				}
				i++;
				lineInfo.add(s);
				if( i == 4 ) {
					classStr = s ;
				}
			}
			classMaps.put(classStr, lineInfo);
		 }
		return classMaps;
	}

}
