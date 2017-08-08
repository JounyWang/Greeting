package com.sihuatech.sensetime.demo;

import java.util.HashSet;
import java.util.Set;

public class CatSet {
	Set<Cat> cats = new HashSet<Cat>();
	private Cat a = new Cat("A", 1);
	private Cat b = new Cat("B", 2);

	public void ff() {
		cats.add(this.a);
		cats.add(this.b);
		cats.add(new Cat("C", 1));
		System.out.println("size: " + cats.size());
	}

	public void f2() {
		a.name = "A1";
		a.age = 3;
	}

	public static void main(String[] args) {
		CatSet cc = new CatSet();
		cc.ff();
		cc.f2();
		cc.ff();

	}

}

class Cat {
	String name;
	int age;

	public Cat(String _name, int _age) {
		name = _name;
		age = _age;
	}
}