package com.beanUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class BeanFactory {

	public static Object getBean(String id) {
		try {

			// 1.创建xml解析器
			// 2.解析文档
			// 3.获得元素---xpath规则
			// 4.获得元素的属性值
			// 5.反射创建对象
			String className=Person.class.getName();
			Class clazz = Class.forName(className);
			Constructor constructor = clazz.getConstructor(null);
			Object newInstance = constructor.newInstance(null);
			// Element rootElement = doc.getRootElement();
			return newInstance;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static <T>T getBean(String id, Class<T> clazz) {
		try {
			String path = BeanFactory.class.getClassLoader().getResource("Bean.xml").getPath();
			String className=null;
			return (T) Class.forName(className).getConstructor(null).newInstance(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;

	}





	public static void main(String[] args) {
		Person person = getBean2(Person.class);
		System.out.println(person);
//		Object person = getBean("");
////		System.out.println("person = " + person);
//		if(person instanceof Person){
//			Person p =(Person) person;
//			System.out.println("p = " + p);
//		}
		List<Long> list = getList(1l);
		List<Double> list1 = getList(1.2);
		List<Float> list2 = getList(2.3f);
		List<Boolean> list3 = getList(false);
		List<Class<Person>> list4 = getList(Person.class);
		List<Person> list5 = getList(new Person());

	}

	private static <T>T getBean2(Class<T> clazz) {

		try {
			return (T)Class.forName(clazz.getName()).getConstructor(null).newInstance(null);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static <T>List<T> getList(T t){
		List<T> list=new ArrayList<T>();
		return list;
	}


}
