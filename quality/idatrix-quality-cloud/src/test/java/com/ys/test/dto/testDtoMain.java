/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.test.dto;

import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.vfs2.FileObject;
import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.step.steps.redundance.SPRedundance;

/**
 *
 * @author JW
 * @since 2017年6月7日
 *
 */
public class testDtoMain {

	private static String type="Step";
	private static String types="steps";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args)  {

		type="Step";
		types="steps";
		String stepType = "XMLInputStream";
		Class<?> classMeta = null;// XMLInputStreamMeta.class;
		Class<?> classDto = SPRedundance.class;
		transMain(stepType, classMeta,classDto);


		//		type="Entry";
		//		types="entries";
		////		String className = "SimpleEval";
		//		String className = "aa".toLowerCase();
		//		Class<?> classTest = ConcatFieldsMeta.class;
		//		transMain((className.charAt(0) + "").toUpperCase() + className.substring(1), classTest);


	}


	public static void transMain(String stepType,Class<?> classMeta,Class<?> dtoClass) {
		
		if(dtoClass == null) {
			try {
				dtoClass = Class.forName("com.ys.idatrix.cloudetl.dto."+type.toLowerCase()+"."+types+".SP" + stepType);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("================createParam===================\n");
		createParam(stepType, classMeta);
		
		System.out.println("================ParameterObject===================\n");
		parameterObject(dtoClass);

		System.out.println("\n================encode===================\n");
		encodeGetMethom(stepType, classMeta, dtoClass);
		
		System.out.println("\n================decode===================\n");
		decodeGetMethom(stepType, classMeta, dtoClass);
		
		System.out.println("\n================print Json===================\n");
		printJson(dtoClass);
	}

	public static void printJson(Class<?> dtoClass) {

		try {
			Object obj = OsgiBundleUtils.newOsgiInstance(dtoClass,null);
			//对域赋值
			for( String fieldName : OsgiBundleUtils.getOsgiFieldNames(obj) ) {
				Field field = OsgiBundleUtils.seekOsgiField(dtoClass, fieldName, false);
				Class<?> fieldType =  field.getType() ;
				if( OsgiBundleUtils.baseTypeTransfor.containsKey(fieldType) ||  fieldType.isEnum()) {
					continue ;
				}else if (fieldType.getSimpleName().contains("List")) {
					List<Object> fieldValue = new ArrayList<>();
					Type fc = field.getGenericType();
					if (fc instanceof ParameterizedType) {
						ParameterizedType pt = (ParameterizedType) fc;
						Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
						fieldValue.add(genericClazz.newInstance());
					}
					OsgiBundleUtils.setOsgiField(obj, fieldName, fieldValue, true);
				}else if(fieldType.isArray()) {
					
					Class<?> targetdtoClass = Class.forName(fieldType.getCanonicalName().replaceAll("\\[\\]", ""));
					Object[] fieldValue = (Object[]) Array.newInstance(targetdtoClass, 1);
					fieldValue[0] = targetdtoClass.newInstance() ;
					OsgiBundleUtils.setOsgiField(obj, fieldName, fieldValue, true);
				}else if(fieldType.getSimpleName().contains("Map")){
					HashMap<String,Object> map = new HashMap<String,Object>();
					map.put("XXXkey", "XXXvalue");
					OsgiBundleUtils.setOsgiField(obj, fieldName,map , true);
				}else {
					OsgiBundleUtils.setOsgiField(obj, fieldName, fieldType.newInstance(), true);
				}
				
			}

			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(obj) ;
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void parameterObject(Class<?> dtoClass) {

		try {

			System.out.println("JSONObject jsonObj = JSONObject.fromObject(json);");
			boolean isSetMap = false;

			for (Field field : OsgiBundleUtils.seekOsgiFields(dtoClass)) {
				
				Class<?> type = field.getType();
				String fieldTypeStr = type.getSimpleName();
				
				if( type.isArray() ) {
					type = Class.forName( type.getCanonicalName().replaceAll("\\[\\]", ""));
					fieldTypeStr = type.getSimpleName() ;
				}
				
				if( fieldTypeStr.startsWith("List")) {
					type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
					fieldTypeStr = type.getSimpleName() ;
				}
				
				if( OsgiBundleUtils.baseTypeTransfor.containsKey(type) || fieldTypeStr.equalsIgnoreCase("String") ) {
					//基础类型
					continue ;
				}
				if (!isSetMap) {
					System.out.println("Map<String, Class<?>> classMap = new HashMap<>();");
					isSetMap = true;
				}
				System.out.println("classMap.put(\"" + field.getName() + "\", " + fieldTypeStr + ".class);");
			}

			System.out.println("return (" + dtoClass.getSimpleName() + ") JSONObject.toBean(jsonObj, " +  dtoClass.getSimpleName() + ".class"+ (isSetMap ? ", classMap" : "") + ");");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static <T> void encodeGetMethom(String stepType,Class<?> classMeta,Class<?> dtoClass) {
		try {

			String obj = dtoClass.getSimpleName().toLowerCase();
			String targetobj = classMeta.getSimpleName().toLowerCase();
			
			System.out.println(isStep()?"StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();":"");
			System.out.println( dtoClass.getSimpleName() + " " + obj + "= new " + dtoClass.getSimpleName() + "();");
			System.out.println(classMeta.getSimpleName() + " " + targetobj + "= (" + classMeta.getSimpleName() + " )"+type.toLowerCase()+"MetaInterface;");
			System.out.println();

			List<String> fileds = OsgiBundleUtils.getOsgiFieldNames(dtoClass);
			fileds.forEach(fieldName -> {
				
				Field dtoField = OsgiBundleUtils.seekOsgiField(dtoClass, fieldName, false);
				Method dtoMethod = OsgiBundleUtils.seekOsgiMethod(dtoClass, "set" + (fieldName.charAt(0) + "").toUpperCase()+ fieldName.substring(1), dtoField.getType());
				
				String metafieldName = fieldName;
				Field metaField = OsgiBundleUtils.seekOsgiField(classMeta, fieldName, false);
				if(metaField == null && fieldName.endsWith("s")) {
					metafieldName = fieldName.substring(0, fieldName.length()-1);
					metaField =  OsgiBundleUtils.seekOsgiField(classMeta, metafieldName , false);
				}
				String metafieldName1 = metafieldName ;
				List<Method> metaMethods =  OsgiBundleUtils.seekOsgiMethodNames(classMeta,false);
				Method metaMethod = metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+metafieldName1)).findAny().orElse(
						metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+metafieldName1)).findAny().orElse(
									metaMethods.stream().filter(method -> !method.getName().startsWith("set")&&method.getName().toLowerCase().contains(metafieldName1.toLowerCase())).findAny().orElse(null)
								)
						);
				if( metaMethod == null ) {
					System.out.println("//[WARN] "+fieldName+" meta Method 没有找到.");
				}
				
				if ( OsgiBundleUtils.baseTypeTransfor.containsKey(dtoField.getType()) || dtoField.getType().getSimpleName().equalsIgnoreCase("String")) {
					//普通类型,直接赋值
					System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." + ( metaMethod!= null?metaMethod.getName():fieldName)  + "() );");
				}else if( dtoField.getType().getSimpleName().contains("List") ){
					//List类型 
					System.out.println();
					Class<?> partDtoType = (Class<?>) ((ParameterizedType) dtoField.getGenericType()).getActualTypeArguments()[0];
					if(  OsgiBundleUtils.baseTypeTransfor.containsKey(partDtoType) || partDtoType.getSimpleName().equalsIgnoreCase("String")) {
						//List<普通类型>
						if(metaField != null && metaField.getType().isArray() ) {
							System.out.println("if("+targetobj + "." +( metaMethod!= null?metaMethod.getName():fieldName) + "() != null && "+targetobj + "." +( metaMethod!= null?metaMethod.getName():fieldName) + "().length > 0){");
							System.out.println(obj + "." + dtoMethod.getName() + "( Arrays.asList( " + targetobj + "." +( metaMethod!= null?metaMethod.getName():fieldName) + "() ) );");
							System.out.println("}");
						}else if( metaField != null && metaField.getType().getSimpleName().contains("List") ){
							System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." +( metaMethod!= null?metaMethod.getName():fieldName) + "() );");
						}else {
							//异常
							System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." +fieldName + ");");
						}
					}else {
						//List<partDto对象>,分 meta Array 和 Meta List
						System.out.println();
						String partDtoObj = partDtoType.getSimpleName().toLowerCase()+"List";
						System.out.println("List<" + partDtoType.getSimpleName() + "> " + partDtoObj + " = Lists.newArrayList();");
						Boolean isAddFor = false ;
						
						if(metaField!= null && metaField.getType().getSimpleName().contains("List")) {
							//partDto对象  ==  meta List
							isAddFor = true ;
							
							Class<?> metaPartType = (Class<?>) ((ParameterizedType) metaField.getGenericType()).getActualTypeArguments()[0];
							List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(metaPartType,false);
							
							String metaPartobj =  metaField.getName()+"MetaList" ;
							System.out.println(" List<"+metaPartType.getSimpleName()+"> "+metaPartobj+" = "+targetobj+"."+ metaMethod.getName()+"();");
							System.out.println(" for ( int i = 0; "+metaPartobj+" != null && i < "+metaPartobj+".size(); i++ ) { ");
							System.out.println( partDtoType.getSimpleName() + " tempobj = new "+ partDtoType.getSimpleName() + "();");
							 OsgiBundleUtils.seekOsgiFields(partDtoType).stream().forEach(field -> {
								//partDto set方法
								Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+field.getName())).findAny().orElse(
										metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+field.getName())).findAny().orElse(
												metaMethods.stream().filter(method ->!method.getName().startsWith("set")&& method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
												)
										);
								Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "set" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1), field.getType());
								
								System.out.println(" tempobj." + dtoPartMethod.getName()+"( "+metaPartobj+".get(i)."+( metaDtoMethod!= null?metaDtoMethod.getName():field.getName())+"() ); ");
							
							});
							System.out.println(partDtoObj+".add( tempobj ) ; ");
							System.out.println(" } ");
							System.out.println(obj + "." + dtoMethod.getName() + "(" + partDtoObj + " );");
							System.out.println();
							
						}else if(metaField!= null && metaField.getType().isArray()){
							
							//partDto对象  ==  meta Array
							isAddFor = true ;
							
							Class<?> metaPartType = null;
							try {
								metaPartType = Class.forName( metaField.getType().getCanonicalName().replaceAll("\\[\\]", ""));
							} catch (ClassNotFoundException e) { }
							
							if(  OsgiBundleUtils.baseTypeTransfor.containsKey(metaPartType) || metaPartType.getSimpleName().equalsIgnoreCase("String")) {
								
								List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(classMeta, true) ;
								Field metaField1 = metaField ;
								Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+metaField1.getName())).findAny().orElse(
										metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+metaField1.getName())).findAny().orElse(
												metaMethods.stream().filter(method -> !method.getName().startsWith("set")&&method.getName().toLowerCase().contains(metaField1.getName().toLowerCase())).findAny().orElse(null)
												)
										);
								
								String metaPartobj =  metaField.getName()+"MetaArray" ;
								System.out.println(" "+metaField.getType().getSimpleName()+" "+metaPartobj+" = "+ targetobj +"."+ metaDtoMethod.getName()+"();");
								System.out.println(" for ( int i = 0; "+metaPartobj+"!= null && i < "+metaPartobj+ ".length; i++ ) { ");
								System.out.println( partDtoType.getSimpleName() + " tempobj = new "+ partDtoType.getSimpleName() + "();");
								OsgiBundleUtils.seekOsgiFields(partDtoType).stream().forEach(field -> {
									//partDto set方法
									Method metaDtoMethod1 = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+field.getName())).findAny().orElse(
											metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+field.getName())).findAny().orElse(
													metaMethods.stream().filter(method -> !method.getName().startsWith("set")&&method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
													)
											);
									Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "set" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1), field.getType());
									System.out.println(" tempobj." + dtoPartMethod.getName()+"( "+targetobj+"."+( metaDtoMethod1!= null?metaDtoMethod1.getName():field.getName())+"()[i] ); ");
								});
								System.out.println(partDtoObj+".add( tempobj ) ; ");
								System.out.println(" } ");
								System.out.println(obj + "." + dtoMethod.getName() + "(" + partDtoObj + " );");
								System.out.println();
								
							}else {
								List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(metaPartType,false);
								
								String metaPartobj =  metaField.getName()+"MetaArray" ;
								System.out.println(" "+metaPartType.getSimpleName()+"[] "+metaPartobj+" = "+targetobj+"."+ metaMethod.getName()+"();");
								System.out.println(" for ( int i = 0; "+metaPartobj+" != null && i < "+metaPartobj+".length; i++ ) { ");
								System.out.println( partDtoType.getSimpleName() + " tempobj = new "+ partDtoType.getSimpleName() + "();");
								 OsgiBundleUtils.seekOsgiFields(partDtoType).stream().forEach(field -> {
									//partDto set方法
									Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+field.getName())).findAny().orElse(
											metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+field.getName())).findAny().orElse(
													metaMethods.stream().filter(method ->!method.getName().startsWith("set")&& method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
													)
											);
									Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "set" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1), field.getType());
									
									System.out.println(" tempobj." + dtoPartMethod.getName()+"( "+metaPartobj+"[i]."+( metaDtoMethod!= null?metaDtoMethod.getName():field.getName())+"() ); ");
								
								});
								System.out.println(partDtoObj+".add( tempobj ) ; ");
								System.out.println(" } ");
								System.out.println(obj + "." + dtoMethod.getName() + "(" + partDtoObj + " );");
								System.out.println();
							}
							
						}else{
							Field partDtoField = OsgiBundleUtils.seekOsgiFields(partDtoType).get(0);
							Field partMetaField = OsgiBundleUtils.seekOsgiField(classMeta, partDtoField.getName() , false);
							if(partMetaField != null && partMetaField.getType().isArray()) {
								//partDto对象  ==  meta Array
								isAddFor = true ;
								
								List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(classMeta, true) ;
								
								Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+partDtoField.getName())).findAny().orElse(
										metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+partDtoField.getName())).findAny().orElse(
												metaMethods.stream().filter(method -> !method.getName().startsWith("set")&&method.getName().toLowerCase().contains(partDtoField.getName().toLowerCase())).findAny().orElse(null)
												)
										);
								
								String metaPartobj =  partMetaField.getName()+"MetaArray" ;
								System.out.println(" "+partMetaField.getType().getSimpleName()+" "+metaPartobj+" = "+ targetobj +"."+ metaDtoMethod.getName()+"();");
								System.out.println(" for ( int i = 0; "+metaPartobj+"!= null && i < "+metaPartobj+ ".length; i++ ) { ");
								System.out.println( partDtoType.getSimpleName() + " tempobj = new "+ partDtoType.getSimpleName() + "();");
								OsgiBundleUtils.seekOsgiFields(partDtoType).stream().forEach(field -> {
									//partDto set方法
									Method metaDtoMethod1 = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+field.getName())).findAny().orElse(
											metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+field.getName())).findAny().orElse(
													metaMethods.stream().filter(method -> !method.getName().startsWith("set")&&method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
													)
											);
									Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "set" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1), field.getType());
									System.out.println(" tempobj." + dtoPartMethod.getName()+"( "+targetobj+"."+( metaDtoMethod1!= null?metaDtoMethod1.getName():field.getName())+"()[i] ); ");
								});
								System.out.println(partDtoObj+".add( tempobj ) ; ");
								System.out.println(" } ");
								System.out.println(obj + "." + dtoMethod.getName() + "(" + partDtoObj + " );");
								System.out.println();
								
							}
							
						}
						
						if( !isAddFor) {
							//异常
							System.out.println(" int length =  ;");
							System.out.println(" for ( int i = 0;  i <length; i++ ) { ");
							System.out.println( partDtoType.getSimpleName() + " tempobj = new "+ partDtoType.getSimpleName() + "();");
							OsgiBundleUtils.seekOsgiFields(partDtoType).stream().forEach(field -> {
								//partDto set方法
								Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "set" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1), field.getType());
								System.out.println(" tempobj." + dtoPartMethod.getName()+"( "+targetobj+".get ); ");
							});
							System.out.println(partDtoObj+".add( tempobj ) ; ");
							System.out.println(" } ");
							System.out.println(obj + "." + dtoMethod.getName() + "(" + partDtoObj + " );");
							System.out.println();
						}
						
					}
						
				}else if(  dtoField.getType().isArray() ) {
					//Array 类型
					Class<?> partDtoType = null;
					try {
						partDtoType = Class.forName( dtoField.getType().getCanonicalName().replaceAll("\\[\\]", ""));
					} catch (ClassNotFoundException e) { }
					
					if(  OsgiBundleUtils.baseTypeTransfor.containsKey(partDtoType) || partDtoType.getSimpleName().equalsIgnoreCase("String")) {
						//Array <普通类型>
						if(metaField != null && metaField.getType().isArray() ) {
							System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." +( metaMethod!= null?metaMethod.getName():fieldName) + "() );");
						}else if( metaField != null && metaField.getType().getSimpleName().contains("List") ){
							System.out.println(obj + "." + dtoMethod.getName() + "( " + targetobj + "." +( metaMethod!= null?metaMethod.getName():fieldName) + "().toArray(new "+partDtoType.getSimpleName()+"[0] ) ) );");
						}else {
							//异常
							System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." +fieldName + ");");
						}
					}else {
						// meta Array 和  meta List
						System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." +fieldName + ");");
					}
				}else {
					//普通DTO类型
					if(metaField != null &&  !OsgiBundleUtils.baseTypeTransfor.containsKey(metaField.getType()) && !metaField.getType().getSimpleName().equalsIgnoreCase("String")) {
						//非简单对象
						System.out.println();
						String dtoFieldObj= dtoField.getName()+"Dto";
						System.out.println( dtoField.getType().getSimpleName() + " "+dtoFieldObj+" = new "+ dtoField.getType().getSimpleName() + "();");
						String metaFieldObj= metaField.getName()+"MetaDto";
						System.out.println( metaField.getType().getSimpleName() + " "+metaFieldObj+" = "+ targetobj+"."+( metaMethod!= null?metaMethod.getName():fieldName)+"();");
						System.out.println("if(" + metaFieldObj+" != null){");
						List<Method> metaDtoMethods = OsgiBundleUtils.seekOsgiMethodNames(metaField.getType(),false);
						OsgiBundleUtils.seekOsgiFields(dtoField.getType()).stream().forEach(field -> {
							//partDto set方法
							Method metaDtoMethod1 = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("get"+field.getName())).findAny().orElse(
									metaMethods.stream().filter(method -> method.getName().equalsIgnoreCase("is"+field.getName())).findAny().orElse(
											metaMethods.stream().filter(method -> !method.getName().startsWith("set")&&method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
											)
									);
							Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(dtoField.getType(), "set" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1), field.getType());
							System.out.println(dtoFieldObj+"." + dtoPartMethod.getName()+"( "+metaFieldObj+"."+( metaDtoMethod1!= null?metaDtoMethod1.getName():field.getName())+"() ); ");
						});
						System.out.println("}");
						System.out.println(obj + "." + dtoMethod.getName() + "(" + dtoFieldObj + " );");
						System.out.println();
					}else {
						//使用简单对象赋值
						System.out.println(obj + "." + dtoMethod.getName() + "(" + targetobj + "." +fieldName + ");");	
					}
				}
						
			});	
			
			System.out.println("return " + obj + ";");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void decodeGetMethom(String stepType,Class<?> classMeta,Class<?> dtoClass) {
		try {

			String obj = dtoClass.getSimpleName().toLowerCase();
			String targetobj = classMeta.getSimpleName().toLowerCase();
			
			System.out.println(isStep()?"StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();":"");
			System.out.println(dtoClass.getSimpleName() + " " + obj + "= (" + dtoClass.getSimpleName() + ")po;");
			System.out.println(classMeta.getSimpleName() + "  " + targetobj + "= (" + classMeta.getSimpleName() + " )"+type.toLowerCase()+"MetaInterface;");
			System.out.println();

			List<String> fileds = OsgiBundleUtils.getOsgiFieldNames(dtoClass);
			fileds.forEach(fieldName -> {
				
				Field dtoField = OsgiBundleUtils.seekOsgiField(dtoClass, fieldName, false);
				Method dtoGetMethod = OsgiBundleUtils.seekOsgiMethod(dtoClass, "get" + (fieldName.charAt(0) + "").toUpperCase()+ fieldName.substring(1));
				if( dtoGetMethod == null ) {
					dtoGetMethod =  OsgiBundleUtils.seekOsgiMethod(dtoClass, "is" + (fieldName.charAt(0) + "").toUpperCase()+ fieldName.substring(1));
				}
				
				String metafieldName = fieldName;
				Field metaField = OsgiBundleUtils.seekOsgiField(classMeta, fieldName, false);
				if(metaField == null && fieldName.endsWith("s")) {
					metafieldName = fieldName.substring(0, fieldName.length()-1);
					metaField =  OsgiBundleUtils.seekOsgiField(classMeta, metafieldName , false);
				}
				String metafieldName1 = metafieldName ;
				List<Method> metaSetMethods = OsgiBundleUtils.seekOsgiMethodNames(classMeta,false);
				Method metaSetMethod = metaSetMethods.stream().filter(method -> method.getName().equalsIgnoreCase("set"+metafieldName1)).findAny().orElse(
						metaSetMethods.stream().filter(method -> !method.getName().startsWith("get")&&method.getName().toLowerCase().contains(metafieldName1.toLowerCase())).findAny().orElse(null)
						);
				if( metaSetMethod == null ) {
					System.out.println("//[WARN] "+fieldName+" meta Set Method 没有找到.");
				}
				
				if ( OsgiBundleUtils.baseTypeTransfor.containsKey(dtoField.getType()) || dtoField.getType().getSimpleName().equalsIgnoreCase("String")) {
					//普通类型,直接赋值
					System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "(" + obj + "."+ dtoGetMethod.getName()+ "());");
				}else if( dtoField.getType().getSimpleName().contains("List") ){
					//List类型 
					System.out.println();
					Class<?> partDtoType = (Class<?>) ((ParameterizedType) dtoField.getGenericType()).getActualTypeArguments()[0];
					if(  OsgiBundleUtils.baseTypeTransfor.containsKey(partDtoType) || partDtoType.getSimpleName().equalsIgnoreCase("String")) {
						//List<普通类型>
						if(metaField != null && metaField.getType().isArray() ) {
							System.out.println("if( "+obj+"."+dtoGetMethod.getName()+"() != null ){");
							System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "(" + obj + "."+ dtoGetMethod.getName()+ "().toArray(new "+partDtoType.getSimpleName()+"[0] ));");
							System.out.println("}");
						}else if( metaField != null && metaField.getType().getSimpleName().contains("List") ){
							System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "(" + obj + "."+ dtoGetMethod.getName()+ "() ));");
						}else {
							//异常
							System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "(" + obj + "."+ dtoGetMethod.getName()+ "() ));");
						}
					}else {
						//List<partDto对象>,分 meta Array 和 Meta List
						System.out.println();
						String partDtoObj = partDtoType.getSimpleName().toLowerCase()+"List";
						System.out.println("List<" + partDtoType.getSimpleName() + "> " + partDtoObj + " = "+obj + "."+ dtoGetMethod.getName()+"() ;");
						System.out.println("if( "+partDtoObj+"!= null && "+partDtoObj+".size() > 0 ){");
						Boolean isAddFor = false ;
						
						if(metaField!= null && metaField.getType().getSimpleName().contains("List")) {
							//partDto对象  ==  meta List
							isAddFor = true ;
							
							Class<?> metaPartType = (Class<?>) ((ParameterizedType) metaField.getGenericType()).getActualTypeArguments()[0];
							List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(metaPartType , false);
							
							String metaPartobj =  metaField.getName()+"MetaList" ;
							System.out.println(" List<"+metaPartType.getSimpleName()+"> "+metaPartobj+" = Lists.newArrayList()");
							System.out.println(" for ( int i = 0; "+partDtoObj+" != null && i < "+partDtoObj+".size(); i++ ) { ");
							System.out.println( metaPartType.getSimpleName() + " tempobj = new "+ metaPartType.getSimpleName() + "();");
							OsgiBundleUtils.seekOsgiFields( partDtoType ).stream().forEach(field -> {
								//partDto set方法
								Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("set"+field.getName())).findAny().orElse(
										metaDtoMethods.stream().filter(method -> !method.getName().startsWith("get")&&method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
									);
								Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "get" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
								if( dtoPartMethod == null ) {
									dtoPartMethod =  OsgiBundleUtils.seekOsgiMethod(partDtoType, "is" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
								}
								System.out.println(" tempobj." + ( metaDtoMethod!= null?metaDtoMethod.getName():field.getName())  + "(" + partDtoObj + ".get(i)."+ dtoPartMethod.getName()+ "() ));");
							});
							System.out.println(metaPartobj+".add( tempobj ) ; ");
							System.out.println(" } ");
							System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "(" + metaPartobj+ " ));");
							System.out.println();
							
						}else if(metaField!= null && metaField.getType().isArray()){
							
							//partDto对象  ==  meta Array
							isAddFor = true ;

							Class<?> metaPartType = null;
							try {
								metaPartType = Class.forName( metaField.getType().getCanonicalName().replaceAll("\\[\\]", ""));
							} catch (ClassNotFoundException e) { }
							
							if(  OsgiBundleUtils.baseTypeTransfor.containsKey(metaPartType) || metaPartType.getSimpleName().equalsIgnoreCase("String")) {
								
								for( Field dtofield :OsgiBundleUtils.seekOsgiFields( partDtoType )) {
									Field metaDtoField = OsgiBundleUtils.seekOsgiField(classMeta, dtofield.getName(), false);
									if(metaDtoField != null) {
										System.out.println(" "+metaDtoField.getType().getSimpleName()+" "+ dtofield.getName()+"metaArray = new "+metaDtoField.getType().getSimpleName().replaceAll("\\[\\]", "")+"["+partDtoObj+".size()];");
									}else {
										//[WARN] 找不到 meta dto 域方法
										System.out.println(" XXX[] "+ dtofield.getName()+"metaArray = new XXX["+partDtoObj+".size()];");

									}
								}
								
								System.out.println(" for ( int i = 0; "+partDtoObj+"!= null && i < "+partDtoObj+ ".size(); i++ ) { ");
								for( Field dtofield : OsgiBundleUtils.seekOsgiFields(partDtoType )) {
									Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "get" + (dtofield.getName().charAt(0) + "").toUpperCase()+ dtofield.getName().substring(1));
									if( dtoPartMethod == null ) {
										dtoPartMethod =  OsgiBundleUtils.seekOsgiMethod(partDtoType, "is" + (dtofield.getName().charAt(0) + "").toUpperCase()+ dtofield.getName().substring(1));
									}
									System.out.println(" "+dtofield.getName()+"metaArray[i] ="+ partDtoObj+".get(i)."+dtoPartMethod.getName()+"();");
								}
								System.out.println("}");
								//
								List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(classMeta,false);
								for( Field dtofield : OsgiBundleUtils.seekOsgiFields(partDtoType)) {

									Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("set"+dtofield.getName())).findAny().orElse(
											metaDtoMethods.stream().filter(method -> !method.getName().startsWith("get")&&method.getName().toLowerCase().contains(dtofield.getName().toLowerCase())).findAny().orElse(null)
											);
									System.out.println(targetobj+"." + ( metaDtoMethod!= null?metaDtoMethod.getName():dtofield.getName())  + "(" + dtofield.getName()+"metaArray"+ " );");
								}
								System.out.println();
								
							}else {
								
								List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(metaPartType , false);
								
								String metaPartobj =  metaField.getName()+"MetaArray" ;
								System.out.println(" "+metaPartType.getSimpleName()+"[] "+metaPartobj+" = new "+metaPartType.getSimpleName()+"["+partDtoObj+".size()] ;");
								System.out.println(" for ( int i = 0; "+partDtoObj+" != null && i < "+partDtoObj+".size(); i++ ) { ");
								System.out.println( metaPartType.getSimpleName() + " tempobj = new "+ metaPartType.getSimpleName() + "();");
								OsgiBundleUtils.seekOsgiFields( partDtoType ).stream().forEach(field -> {
									//partDto set方法
									Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("set"+field.getName())).findAny().orElse(
											metaDtoMethods.stream().filter(method -> !method.getName().startsWith("get")&&method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
										);
									Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "get" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
									if( dtoPartMethod == null ) {
										dtoPartMethod =  OsgiBundleUtils.seekOsgiMethod(partDtoType, "is" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
									}
									System.out.println(" tempobj." + ( metaDtoMethod!= null?metaDtoMethod.getName():field.getName())  + "(" + partDtoObj + ".get(i)."+ dtoPartMethod.getName()+ "() );");
								});
								System.out.println(metaPartobj+"[i]= tempobj  ; ");
								System.out.println(" } ");
								System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "(" + metaPartobj+ " );");
								System.out.println();
							}
							
						}else {
							Field partDtoField = OsgiBundleUtils.seekOsgiFields( partDtoType).get(0);
							Field partMetaField = OsgiBundleUtils.seekOsgiField(classMeta, partDtoField.getName() , false);
							if(partMetaField != null && partMetaField.getType().isArray()) {
								//partDto对象  ==  meta Array
								isAddFor = true ;
								
								for( Field dtofield :OsgiBundleUtils.seekOsgiFields( partDtoType )) {
									Field metaDtoField = OsgiBundleUtils.seekOsgiField(classMeta, dtofield.getName(), false);
									if(metaDtoField != null) {
										System.out.println(" "+metaDtoField.getType().getSimpleName()+" "+ dtofield.getName()+"metaArray = new "+metaDtoField.getType().getSimpleName().replaceAll("\\[\\]", "")+"["+partDtoObj+".size()];");
									}else {
										//[WARN] 找不到 meta dto 域方法
										System.out.println(" XXX[] "+ dtofield.getName()+"metaArray = new XXX["+partDtoObj+".size()];");

									}
								}
								
								System.out.println(" for ( int i = 0; "+partDtoObj+"!= null && i < "+partDtoObj+ ".size(); i++ ) { ");
								for( Field dtofield : OsgiBundleUtils.seekOsgiFields(partDtoType )) {
									Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "get" + (dtofield.getName().charAt(0) + "").toUpperCase()+ dtofield.getName().substring(1));
									if( dtoPartMethod == null ) {
										dtoPartMethod =  OsgiBundleUtils.seekOsgiMethod(partDtoType, "is" + (dtofield.getName().charAt(0) + "").toUpperCase()+ dtofield.getName().substring(1));
									}
									System.out.println(" "+dtofield.getName()+"metaArray[i] ="+ partDtoObj+".get(i)."+dtoPartMethod.getName()+"();");
								}
								System.out.println("}");
								//
								List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(classMeta,false);
								for( Field dtofield : OsgiBundleUtils.seekOsgiFields(partDtoType)) {

									Method metaDtoMethod = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("set"+dtofield.getName())).findAny().orElse(
											metaDtoMethods.stream().filter(method -> !method.getName().startsWith("get")&&method.getName().toLowerCase().contains(dtofield.getName().toLowerCase())).findAny().orElse(null)
											);
									System.out.println(targetobj+"." + ( metaDtoMethod!= null?metaDtoMethod.getName():dtofield.getName())  + "(" + dtofield.getName()+"metaArray"+ " );");
								}
								System.out.println();
							}
							
						}
						
						if( !isAddFor) {
							//异常
							System.out.println(" int length =  ;");
							System.out.println(" for ( int i = 0;  i <length; i++ ) { ");
							System.out.println( partDtoType.getSimpleName() + " tempobj = new "+ partDtoType.getSimpleName() + "();");
							OsgiBundleUtils.seekOsgiFields(partDtoType).stream().forEach(field -> {
								//partDto get方法
								Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(partDtoType, "get" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
								if( dtoPartMethod == null ) {
									dtoPartMethod =  OsgiBundleUtils.seekOsgiMethod(partDtoType, "is" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
								}
								System.out.println(" XXXX ="+ partDtoObj+".get(i)."+dtoPartMethod.getName()+"();");
							});
							System.out.println(" } ");
							System.out.println(targetobj + "." +( metaSetMethod!= null?metaSetMethod.getName():fieldName)  + "( XXX );");
							System.out.println();
						}
						
						System.out.println("}");
						System.out.println();
					}
						
				}else if(  dtoField.getType().isArray() ) {
					//Array 类型
					Class<?> partDtoType = null;
					try {
						partDtoType = Class.forName( dtoField.getType().getCanonicalName().replaceAll("\\[\\]", ""));
					} catch (ClassNotFoundException e) { }
					
					if(  OsgiBundleUtils.baseTypeTransfor.containsKey(partDtoType) || partDtoType.getSimpleName().equalsIgnoreCase("String")) {
						//Array <普通类型>
						if(metaField != null && metaField.getType().isArray() ) {
							System.out.println(targetobj + "." + ( metaSetMethod!= null?metaSetMethod.getName():fieldName) + "( " + obj + "." +dtoGetMethod.getName() + "() );");
						}else if( metaField != null && metaField.getType().getSimpleName().contains("List") ){
							System.out.println("if("+obj+ "." +dtoGetMethod.getName() + "() != null && "+obj+ "." +dtoGetMethod.getName() + "().length > 0){");
							System.out.println(targetobj + "." + ( metaSetMethod!= null?metaSetMethod.getName():fieldName) + "(  Arrays.asList(" + obj+ "." +dtoGetMethod.getName()+ "() ) );");
							System.out.println("}");
						}else {
							//异常
							System.out.println(targetobj + "." + ( metaSetMethod!= null?metaSetMethod.getName():fieldName) + "( " + obj + "." +dtoGetMethod.getName() + "() );");
						}
					}else {
						// meta Array 和  meta List
						System.out.println(targetobj + "." + ( metaSetMethod!= null?metaSetMethod.getName():fieldName) + "( " + obj + "." +dtoGetMethod.getName() + "() );");
					}
				}else {
					//普通DTO类型
					if(metaField != null &&  !OsgiBundleUtils.baseTypeTransfor.containsKey(metaField.getType()) && !metaField.getType().getSimpleName().equalsIgnoreCase("String")) {
						//非简单对象
						System.out.println();
						String dtoFieldObj = dtoField.getName()+"Dto";
						System.out.println( dtoField.getType().getSimpleName() + " "+dtoFieldObj+" = "+ obj+"."+dtoGetMethod.getName()+"();");
						System.out.println("if(" + dtoFieldObj+" != null){");
						String metaFieldObj= metaField.getName()+"MetaDto";
						System.out.println( metaField.getType().getSimpleName() + " "+metaFieldObj+" = new "+ metaField.getType().getSimpleName() + "();");
						
						List<Method> metaDtoMethods =  OsgiBundleUtils.seekOsgiMethodNames(metaField.getType(),false);
						OsgiBundleUtils.seekOsgiFields( dtoField.getType() ).stream().forEach(field -> {
							//partDto set方法
							Method metaDtoMethod1 = metaDtoMethods.stream().filter(method -> method.getName().equalsIgnoreCase("set"+field.getName())).findAny().orElse(
									metaDtoMethods.stream().filter(method -> !method.getName().startsWith("get")&&method.getName().toLowerCase().contains(field.getName().toLowerCase())).findAny().orElse(null)
									);
							Method dtoPartMethod = OsgiBundleUtils.seekOsgiMethod(dtoField.getType(), "get" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
							if( dtoPartMethod == null ) {
								dtoPartMethod =  OsgiBundleUtils.seekOsgiMethod(dtoField.getType(), "is" + (field.getName().charAt(0) + "").toUpperCase()+ field.getName().substring(1));
							}
							
							System.out.println(metaFieldObj+"." + ( metaDtoMethod1!= null?metaDtoMethod1.getName():field.getName()) +"( "+dtoFieldObj+"."+dtoPartMethod.getName()+"() ); ");
						});
						
						System.out.println(targetobj + "." + ( metaSetMethod!= null?metaSetMethod.getName():fieldName) + "( " + metaFieldObj + " );");
						System.out.println(" }");
					}else {
						//使用简单对象赋值
						System.out.println(targetobj + "." + ( metaSetMethod!= null?metaSetMethod.getName():fieldName) + "( " + obj + "." +dtoGetMethod.getName() + "() );");	
					}
				}
						
			});	

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static <T> void createParam(String stepType, Class<T> classMeta) {
		try {

			StringBuffer sb =  new StringBuffer();
			
			sb.append("package com.ys.idatrix.cloudetl.dto."+type.toLowerCase()+"."+types+";").append("\n");
			
			sb.append("\n").append("import java.util.HashMap;");
			sb.append("\n").append("import java.util.List;");
			sb.append("\n").append("import java.util.Map;");
			sb.append("\n").append("import "+classMeta.getName()+";");
			sb.append("\n").append("import org.springframework.context.annotation.Scope;");
			sb.append("\n").append("import org.springframework.stereotype.Component;");
			sb.append("\n").append(isStep()?"import org.pentaho.di.trans.step.StepMetaInterface;":"import org.pentaho.di.job.entry.JobEntryInterface;");
			sb.append("\n").append(isStep()?"import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;":"");
			sb.append("\n").append(isStep()?"import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;":"");
			sb.append("\n").append("import net.sf.json.JSONObject;");
			sb.append("\n").append("");
			sb.append("\n").append("/**");
			sb.append("\n").append(" * "+type+" - "+stepType+".");
			sb.append("\n").append(" * 转换  "+classMeta.getName());
			sb.append("\n").append(" * ");
			sb.append("\n").append(" * @author XH");
			sb.append("\n").append(" * @since "+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			sb.append("\n").append(" */");

			sb.append("\n").append("@Component(\"SP" + (isStep()?stepType:stepType.toLowerCase()) + "\")");
			sb.append("\n").append("@Scope(\"prototype\")");
			sb.append("\n").append("public class SP" + stepType + " implements "+(isStep()?"StepParameter, StepDataRelationshipParser ,ResumeStepDataParser":"EntryParameter")+"{\n\n");

			List<String> names = OsgiBundleUtils.getOsgiFieldNames(classMeta);

			BufferedReader br = new BufferedReader(new StringReader(getTemplete()));
			String line = "";
			String lastLine = "" ;
			boolean isFor = false;
			boolean isForBase = false;
			String partDtoName = "" ;
			String partFieldName = "";
			Class<?> partDtoClass = null ;
			while ((line = br.readLine()) != null) {

				if (isFor && ( line.trim().equals("}") || line.trim().equals("});")) ) {
					isFor = false;
					sb.append("\n").append("\n-----------" + partDtoName + "-End--------------\n");
					sb.append("\n").append("\n");
					continue;
				}
				
				String lineTemp = line.toLowerCase();
				String lastLineTemp = lastLine ;
				
				if (isFor) {
					
					if(isForBase) {
						List<String> nameOpt = names.stream().filter(n -> lineTemp.contains("XMLHandler.addTagValue".toLowerCase())&&lineTemp.contains(n.toLowerCase()) ).collect(Collectors.toList());
						String name = getPriorityFieldName(nameOpt);
						if( !Utils.isEmpty(name) ) {
							Field field = OsgiBundleUtils.seekOsgiField(classMeta, name, true);
							sb.append("\n	private ").append(field.getType().getSimpleName().replaceAll("\\[\\]", "") + " " + name + ";");
							names.remove(name);
						}
						
					}else {
						List<String> nameOpt;
						Class<?> whichClass = null ;
						if( partDtoClass != null ) {
							nameOpt = OsgiBundleUtils.getOsgiFieldNames(partDtoClass).stream().filter(n -> lineTemp.contains("XMLHandler.addTagValue".toLowerCase())&&lineTemp.contains(n.toLowerCase()) ).collect(Collectors.toList());
							whichClass = partDtoClass ;
						}else {
							nameOpt = names.stream().filter(n -> lineTemp.contains("XMLHandler.addTagValue".toLowerCase())&&lineTemp.contains(n.toLowerCase()) ).collect(Collectors.toList());
							whichClass = classMeta ;
						}
						String name = getPriorityFieldName(nameOpt);
						if( !Utils.isEmpty(name) ) {
							Field ff = OsgiBundleUtils.seekOsgiField(whichClass, name , false);
							sb.append("\n	private ").append(ff.getType().getSimpleName()+" " + name + ";");
						}
						
					}
					continue;
				}
				
				if (lineTemp.matches(".*for\\s\\(.*") || lineTemp.contains("foreach")) {
					isFor = true;
					List<String> nameOpt = names.stream().filter(n -> lineTemp.matches(".*"+n.toLowerCase()+".*length.*") || lineTemp.matches(".*"+n.toLowerCase()+".*size.*") ||
							lastLineTemp.matches(".*"+n.toLowerCase()+".*length.*") || lastLineTemp.matches(".*"+n.toLowerCase()+".*size.*") ).collect(Collectors.toList());
					String name = getPriorityFieldName(nameOpt);
					if (!Utils.isEmpty(name) ) {
						Field forField = OsgiBundleUtils.seekOsgiField(classMeta, name, true);
						if(forField.getType().isArray() || forField.getType().getSimpleName().contains("List")) {
							//数组类型
							if(OsgiBundleUtils.baseTypeTransfor.containsKey(forField.getType()) || forField.getType().getSimpleName().contains("String") ) {
								//单个域数组(多值)
								partDtoName = stepType+name+"Dto" ;
								isForBase = true;
							}else {
								//dto域数组(单值)
								partDtoName = stepType+forField.getType().getSimpleName().replaceAll("\\[\\]", "")+name+"Dto" ;
								isForBase = false;
								partDtoClass=  Class.forName(forField.getType().getCanonicalName().replaceAll("\\[\\]", ""));
							}
							sb.append("\n	private ").append("List<" + partDtoName + "> " + " " + (name.endsWith("s")?name:(name+"s")) + ";");
							sb.append("\n").append("\n-----------" + partDtoName + "-Start--------------\n");
						}else {
							
							partDtoName =  name+"Dto" ;
							isForBase = false;
							partDtoClass = forField.getType() ;
							sb.append("\n	private ").append("List<" + partDtoName + "> " + " " + (name.endsWith("s")?name:(name+"s")) + ";");
							sb.append("\n").append("\n-----------" + partDtoName + "-Start--------------\n");
							
						}
					}
				} 
				
				List<String> nameOpt = names.stream().filter(n -> lineTemp.contains("XMLHandler.addTagValue".toLowerCase())&&lineTemp.contains(n.toLowerCase()) ).collect(Collectors.toList());
				String name = getPriorityFieldName(nameOpt);
				if( !Utils.isEmpty(name) ) {
					//包含 field
					Field field = OsgiBundleUtils.seekOsgiField(classMeta, name, true);
					if( field.getName().equalsIgnoreCase(partFieldName)) {
						nameOpt = OsgiBundleUtils.getOsgiFieldNames(partDtoClass).stream().filter(n -> lineTemp.contains("XMLHandler.addTagValue".toLowerCase())&&lineTemp.contains(n.toLowerCase()) ).collect(Collectors.toList());
						name = getPriorityFieldName(nameOpt);
						if( !Utils.isEmpty(name) ) {
							Field ff = OsgiBundleUtils.seekOsgiField(partDtoClass, name, false);
							sb.append("\n	private ").append(ff.getType().getSimpleName()+" " + name + ";");
						}
						continue ;
					}else {
						if( !Utils.isEmpty(partFieldName)) {
							sb.append("\n").append("\n-----------" + partDtoName + "-End--------------\n");
							sb.append("\n").append("\n");
							
							names.remove(partFieldName);
							partFieldName =  null ;
						}
						if(OsgiBundleUtils.baseTypeTransfor.containsKey(field.getType()) || field.getType().getSimpleName().contains("String") ) {
							sb.append("\n	private ").append(field.getType().getSimpleName() + " "+ name + ";");
							names.remove(name);
						}else {
							partFieldName = name;
							
							if(field.getType().isArray()){
								partDtoClass = Class.forName(field.getType().getCanonicalName().replaceAll("\\[\\]", ""));
							}else if( field.getType().getSimpleName().contains("List")) { 
								partDtoClass = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
							}else{
								//非基础类型
								partDtoClass = field.getType() ;
							}
							partDtoName =  partDtoClass.getSimpleName()+"Dto" ;
							sb.append("\n").append("\n-----------" + partDtoName + "-Start--------------\n");
							sb.append("\n	private ").append(field.getType().getSimpleName() + " "+ name + ";");
							
							nameOpt = OsgiBundleUtils.getOsgiFieldNames(partDtoClass).stream().filter(n -> lineTemp.contains("XMLHandler.addTagValue".toLowerCase())&&lineTemp.contains(n.toLowerCase()) ).collect(Collectors.toList());
							name = getPriorityFieldName(nameOpt);
							if( !Utils.isEmpty(name) ) {
								Field ff = OsgiBundleUtils.seekOsgiField(partDtoClass, name, false);
								sb.append("\n	private ").append(ff.getType().getSimpleName()+" " + name + ";");
							}
						}
					
					}
				}
				lastLine = line ;
			}
			sb.append("\n").append("}");
			System.out.println(sb.toString());
			
			
			String targetPath = Class.forName("com.ys.idatrix.cloudetl.dto."+type.toLowerCase()+"."+types+"."+type+"Parameter").getResource("").getPath();
			targetPath = targetPath.replaceAll("target/classes", "src/main/java")+"SP" + stepType+".java" ;
			FileObject javaFile = KettleVFS.getFileObject(targetPath);
			if( !javaFile.exists() ) {
				javaFile.createFile();
				javaFile.getContent().getOutputStream().write(sb.toString().getBytes());
			}
			javaFile.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static boolean isStep() {
		return "Step".equalsIgnoreCase(type) ;
	}
	
	static Map<String,Integer> cache = Maps.newHashMap() ;
	private static String getPriorityFieldName(List<String> fields) {
		if(fields == null || fields.isEmpty()) {
			return null ;
		}
		if(fields.size() == 1) {
			if(cache.containsKey(fields.get(0))) {
				cache.put(fields.get(0), cache.get(fields.get(0))+1) ;
			}else {
				cache.put(fields.get(0), 1 );
			}
			return fields.get(0);
		}
		Optional<String> opt = fields.stream().sorted( (s1,s2) -> {
			if(cache.containsKey(s1) && cache.containsKey(s2) ) {
				int cc = cache.get(s1)- cache.get(s2) ;
				if(cc == 0 ) {
					return s1.length() > s2.length() ? -1 : 1;
				}else {
					return cc;
				}
			}else if( !cache.containsKey(s1) && cache.containsKey(s2) ) {
				return -1;
			}else if( cache.containsKey(s1) && !cache.containsKey(s2) ) {
				return 1;
			}else if( !cache.containsKey(s1) && !cache.containsKey(s2) ) {
				return s1.length() > s2.length() ? -1 : 1;
			}
			
			return -1 ;
			
		}).findFirst();
		
		String f = opt.get() ;
		if(cache.containsKey(f)) {
			cache.put(f, cache.get(f)+1) ;
		}else {
			cache.put(f, 1 );
		}
		
		return f;
	}

	public static String getTemplete() {

		return "  @Override\r\n" + 
				"  public String getXML() {\r\n" + 
				"    StringBuffer retval = new StringBuffer();\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"sourceFromInput\", sourceFromInput ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"sourceFieldName\", sourceFieldName ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"filename\", filename ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"addResultFile\", addResultFile ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"nrRowsToSkip\", nrRowsToSkip ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"rowLimit\", rowLimit ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"defaultStringLen\", defaultStringLen ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"encoding\", encoding ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"enableNamespaces\", enableNamespaces ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"enableTrim\", enableTrim ) );\r\n" + 
				"\r\n" + 
				"    // The fields in the output stream\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeFilenameField\", includeFilenameField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"filenameField\", filenameField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeRowNumberField\", includeRowNumberField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"rowNumberField\", rowNumberField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeDataTypeNumericField\", includeXmlDataTypeNumericField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"dataTypeNumericField\", xmlDataTypeNumericField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \"\r\n" + 
				"        + XMLHandler.addTagValue( \"includeDataTypeDescriptionField\", includeXmlDataTypeDescriptionField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"dataTypeDescriptionField\", xmlDataTypeDescriptionField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlLocationLineField\", includeXmlLocationLineField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlLocationLineField\", xmlLocationLineField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlLocationColumnField\", includeXmlLocationColumnField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlLocationColumnField\", xmlLocationColumnField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlElementIDField\", includeXmlElementIDField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlElementIDField\", xmlElementIDField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlParentElementIDField\", includeXmlParentElementIDField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlParentElementIDField\", xmlParentElementIDField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlElementLevelField\", includeXmlElementLevelField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlElementLevelField\", xmlElementLevelField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlPathField\", includeXmlPathField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlPathField\", xmlPathField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlParentPathField\", includeXmlParentPathField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlParentPathField\", xmlParentPathField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlDataNameField\", includeXmlDataNameField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlDataNameField\", xmlDataNameField ) );\r\n" + 
				"\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"includeXmlDataValueField\", includeXmlDataValueField ) );\r\n" + 
				"    retval.append( \"    \" + XMLHandler.addTagValue( \"xmlDataValueField\", xmlDataValueField ) );\r\n" + 
				"\r\n" + 
				"    return retval.toString();\r\n" + 
				"  }";
	}

}
