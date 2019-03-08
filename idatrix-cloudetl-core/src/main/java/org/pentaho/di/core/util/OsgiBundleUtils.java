/**
 * GDBD iDatrix CloudETL System.
 */
package org.pentaho.di.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Provides utilities interactive with OSGI bundle. - Load OSGI bundle (TODO.) -
 * Invoke OSGI bundle class method Since OSGI container has its own
 * BundleClassLoader, we need to reflect a bundle class to get its object and
 * then invoke its methods. Please do not instantiate a bundle class outside of
 * the OSGI container (Apache Karaf in this project)!
 * 
 * @author JW
 * @since 2017年6月20日
 *
 */
public class OsgiBundleUtils {

	public static final Log logger = LogFactory.getLog( OsgiBundleUtils.class );
	/**
	 * Invoke method in class loaded in OSGI bundle. !!! deprecated - It will cause
	 * problem if given null as the parameter!
	 * 
	 * @param obj
	 *            class | Object , class 获取静态方法,Object 获取对象方法
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object invokeOsgiMethod(Object obj, String name, Object... args) {
		if (obj == null) {
			return null;
		}
		Class<?>[] types = null;
		if (args != null) {
			types = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					// it will cause problem if given null as the parameter!
					types[i] = args[i].getClass();
				}
			}
		}
		return invokeOsgiMethod(obj, name, args, types);
	}

	/**
	 * Invoke method in class loaded in OSGI bundle.
	 * 
	 * @param obj
	 * @param name
	 * @param args
	 * @param argTypes
	 * @return
	 */
	public static Object invokeOsgiMethod(Object obj, String name, Object[] args, Class<?>[] argTypes) {

		if (obj == null) {
			return null;
		}
		Class<?> objClass;
		if (obj instanceof Class) {
			objClass = (Class<?>) obj;
			obj = null;
		} else if (obj instanceof String) {
			try {
				objClass = Class.forName((String) obj);
			} catch (ClassNotFoundException e) {
				logger.error("Invoke Osgi Method error, obj: " + obj + ", Class Not Found! " + e.getMessage());
				return null;
			}
			obj = null;
		} else {
			objClass = obj.getClass();
		}
		Method m;
		if (argTypes != null && argTypes.length > 0) {
			m = seekOsgiMethod(objClass, name, argTypes);
		} else {
			m = seekOsgiMethod(objClass, name);
		}
		if (m != null) {
			// 取消java语言访问检查以访问protected方法
			m.setAccessible(true);
			try {
				if (args != null && args.length == 1 && m.getParameterTypes() != null
						&& m.getParameterTypes().length == 1 && m.getParameterTypes()[0].isArray()) {
					Class<?> paramType = m.getParameterTypes()[0].getComponentType();
					Class<?> array = objClass.getClassLoader().loadClass("java.lang.reflect.Array");
					Object argsArr = array.getMethod("newInstance", Class.class, int.class).invoke(null, paramType,
							((Object[]) args[0]).length);// ((Object[])args[0]).length);
					for (int i = 0; i < ((Object[]) args[0]).length; i++) {
						array.getMethod("set", Object.class, int.class, Object.class).invoke(null, argsArr, i,
								((Object[]) args[0])[i]);
					}
					return m.invoke(obj, argsArr);
				} else {
					return m.invoke(obj, args);
				}
			} catch (ClassNotFoundException | NoSuchMethodException e) {
				logger.debug("Invoke Osgi Method error, name: " + m.getName() + ", error: " + e.getMessage());
			} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException
					| SecurityException e) {
				logger.error("Invoke Osgi Method error, name: " + m.getName() + ", error: " + e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * Create a new instance of class loaded in OSGI bundle. - It's better calling
	 * PluginRegistry to find the plugin and load class.
	 * 
	 * @param obj
	 * @param className
	 * @param args
	 * @return
	 */
	public static Object newOsgiInstance(Object obj, String className, Object... args) {
		Class<?>[] types = null;
		if (args != null && args.length > 0) {
			types = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					types[i] = args[i].getClass();
				}
			}
		}
		return newOsgiInstance(obj, className, types, args);
	}

	public static Object newOsgiInstance(Object obj, String className, Class<?>[] types, Object[] args) {

		try {
			Class<?> instanceClass;
			if (obj instanceof Class) {
				instanceClass = (Class<?>) obj;
			} else {
				instanceClass = obj.getClass().getClassLoader().loadClass(className);
			}

			if (args == null || args.length == 0) {
				return instanceClass.newInstance();
			} else {
				return instanceClass.getConstructor(types).newInstance(args);
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			logger.debug("New Osgi Instance error: " + e.getMessage());
		} catch (InstantiationException | InvocationTargetException | IllegalAccessException | IllegalArgumentException
				| SecurityException e) {
			logger.error("New Osgi Instance error: " + e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Get field declared in class loaded in OSGI bundle.
	 * 
	 * @param obj
	 * @param name
	 * @param isPrivate
	 * @return
	 */
	public static Object getOsgiField(Object obj, String name, boolean isPrivate) {
		try {

			if (obj == null) {
				return null;
			}
			Class<?> objClass;
			if (obj instanceof Class) {
				objClass = (Class<?>) obj;
				obj = null;
			} else if (obj instanceof String) {
				try {
					objClass = Class.forName((String) obj);
				} catch (ClassNotFoundException e) {
					logger.error(obj + " not found!");
					return null;
				}
				obj = null;
			} else {
				objClass = obj.getClass();
			}

			Field field = seekOsgiField(objClass, name, true);
			if (field == null) {
				logger.debug("Get Osgi Field error, name: " + name + " not exits!");
				return null;
			}

			if (isPrivate) {
				field.setAccessible(true);
			}
			return field.get(obj);
		} catch (Exception e) {
			logger.debug("Get Osgi Field error, name: " + name + ", error: " + e.getMessage());
			return null;
		}

	}

	/**
	 * Set field declared in class loaded in OSGI bundle.
	 * 
	 * @param obj
	 * @param name
	 * @param value
	 * @param isPrivate
	 */
	public static void setOsgiField(Object obj, String name, Object value, boolean isPrivate) {
		try {

			if (obj == null) {
				return;
			}
			Class<?> objClass;
			if (obj instanceof Class) {
				objClass = (Class<?>) obj;
				obj = null;
			} else {
				objClass = obj.getClass();
			}

			Field field = seekOsgiField(objClass, name, true);
			if (field == null) {
				logger.debug("Get Osgi Field error, name: " + name + " not exits!");
				return;
			}

			if (isPrivate) {
				field.setAccessible(true);
			}

			field.set(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			logger.error("Get Osgi Field error, name: " + name + ", error: " + e.getMessage(),e);
		}

	}

	public static List<String> getOsgiFieldNames(Object obj) {
		if (obj == null) {
			return null;
		}
		Class<?> objClass;
		if (obj instanceof Class) {
			objClass = (Class<?>) obj;
			obj = null;
		} else {
			objClass = obj.getClass();
		}
		List<String> result = seekOsgiFields(objClass).stream().map(field -> { return field.getName();}).collect(Collectors.toList());
		return result;
	}

	public static List<Field> seekOsgiFields(Class<?> objClass ) {
		if (objClass == null) {
			return  Lists.newArrayList();
		}
		
		List<Field>	result = Lists.newArrayList();
		
		Field[] fields = objClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field f : fields) {
				if(f.getModifiers() < 8) {
					//只需要 private protected,public 三种修饰符的域才获取
					result.add(f);
				}
			}
		}
		if (objClass.getGenericSuperclass() != null) {
			result.addAll(seekOsgiFields(objClass.getSuperclass()));
		}
		return result;
	}

	public static Field seekOsgiField(Class<?> clazz, String name, boolean isIgnoreCase) {
		try {
			if (!isIgnoreCase) {
				return clazz.getDeclaredField(name);
			} else {
				Field f = null;
				try {
					f = clazz.getDeclaredField(name);
				} catch (NoSuchFieldException s) {
					for (Field ff : clazz.getDeclaredFields()) {
						if (ff.getName().equalsIgnoreCase(name)) {
							f = ff;
							break;
						}
					}
				}
				if (f != null) {
					return f;
				} else {
					throw new NoSuchFieldException(name);
				}
			}

		} catch (Exception e) {
			if (clazz.getGenericSuperclass() != null) {
				// 查找父类
				return seekOsgiField(clazz.getSuperclass(), name, isIgnoreCase);
			}
			logger.debug("Get Osgi Field error, name: " + name + ", error: " + e.getMessage());
		}
		return null;
	}

	public static Map<Class<?>, Class<?>> baseTypeTransfor = Maps.newHashMap();
	static {
		baseTypeTransfor.put(Integer.class, int.class);
		baseTypeTransfor.put(int.class, Integer.class);
		baseTypeTransfor.put(Boolean.class, boolean.class);
		baseTypeTransfor.put(boolean.class, Boolean.class);
		baseTypeTransfor.put(Long.class, long.class);
		baseTypeTransfor.put(long.class, Long.class);
		baseTypeTransfor.put(Double.class, double.class);
		baseTypeTransfor.put(double.class, Double.class);
		baseTypeTransfor.put(Float.class, float.class);
		baseTypeTransfor.put(float.class, Float.class);
		baseTypeTransfor.put(Character.class, char.class);
		baseTypeTransfor.put(char.class, Character.class);
		baseTypeTransfor.put(Byte.class, byte.class);
		baseTypeTransfor.put(byte.class, Byte.class);
		baseTypeTransfor.put(Short.class, short.class);
		baseTypeTransfor.put(short.class, Short.class);
	}

	public static boolean isSameClass(Class<?> cl1 , Class<?> cl2) {
		
		if(cl1 == null && cl2 == null) {
			return true ;
		}else if( cl1 == null ||cl2 == null ) {
			return false ;
		}else {
			if( cl1 == cl2 || cl1.isAssignableFrom(cl2) ||  cl2.isAssignableFrom(cl1)) {
				return true ;
			}else if( baseTypeTransfor.containsKey(cl1) && baseTypeTransfor.get(cl1) == cl2){
				return true ;
			}
		}
		
		return false ;
	}
	
	public static List<String> seekOsgiMethodNames(Object obj,boolean isOnlyPublic) {
		if (obj == null) {
			return null;
		}
		Class<?> objClass;
		if (obj instanceof Class) {
			objClass = (Class<?>) obj;
			obj = null;
		} else {
			objClass = obj.getClass();
		}
		List<String> result = seekOsgiMethodNames(objClass,isOnlyPublic).stream().map(method -> { return method.getName();}).collect(Collectors.toList());
		return result;
	}

	public static List<Method> seekOsgiMethodNames(Class<?> objClass,boolean isOnlyPublic ) {
		if (objClass == null) {
			return  Lists.newArrayList();
		}
		
		List<Method>	result = Lists.newArrayList();
		
		Method[] methods = objClass.getDeclaredMethods();
		if (methods != null && methods.length > 0) {
			for (Method m : methods) {
				if( isOnlyPublic && m.getModifiers() == 1 ) {
					result.add(m);
				}else if( !isOnlyPublic && m.getModifiers() < 8) {
					//只需要 private protected,public 三种修饰符的域才获取
					result.add(m);
				}
			}
		}
		if (objClass.getGenericSuperclass() != null) {
			result.addAll(seekOsgiMethodNames(objClass.getSuperclass(),isOnlyPublic));
		}
		return result;
	}
	
	/**
	 * Seek method in class loaded in OSGI bundle.
	 * 
	 * @param clazz
	 * @param name
	 * @param argTypes
	 * @return
	 */
	public static Method seekOsgiMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		try {
			return clazz.getDeclaredMethod(name, argTypes);
		} catch (NoSuchMethodException e) {
			if (argTypes.length > 0) {
				List<Method> listMethod = Arrays.asList(clazz.getDeclaredMethods()).stream()
						.filter(method -> method.getName().equals(name)).collect(Collectors.toList());
				if (listMethod.size() == 1) {
					return listMethod.get(0);
				}
				for (Method method : listMethod) {
					Class<?>[] paramTypes = method.getParameterTypes();
					if (paramTypes != null && paramTypes.length == argTypes.length) {
						int i = 0;
						for (; i < paramTypes.length; i++) {
							
							if(  argTypes[i] == null ||
									paramTypes[i].equals(argTypes[i]) ||
									paramTypes[i].isAssignableFrom(argTypes[i]) || 
									(baseTypeTransfor.containsKey(paramTypes[i])&& baseTypeTransfor.get(paramTypes[i]).equals(argTypes[i])) ) {
								//参数为null 或者 类型相等 或者 子类赋值给父类 或者 基础类型转换 ,  匹配,检查下一个参数
								continue ;
							}
							if ((paramTypes[i].isArray() && argTypes[i].isArray())
									&& (paramTypes[i].getComponentType().equals(argTypes[i].getComponentType())
											||paramTypes[i].getComponentType().isAssignableFrom(argTypes[i].getComponentType())
											|| (baseTypeTransfor.containsKey(paramTypes[i].getComponentType())&& baseTypeTransfor.get(paramTypes[i].getComponentType()).equals(argTypes[i].getComponentType())))) {
								// 参数为数组,且类型一致,检查下一个参数
								continue;
							}
							// 参数不匹配,检查下一个方法
							break;
						}
						if (i == paramTypes.length) {
							// 循环到了 最后一个参数,即全部匹配
							return method;
						}
					}
				}
			}

			if (clazz.getGenericSuperclass() != null) {
				// 查找父类
				return seekOsgiMethod(clazz.getSuperclass(), name, argTypes);
			}
			logger.debug("Seek Osgi Method error, name:" + name + ", error: " + e.getMessage());
			return null;
		}
	}

}
