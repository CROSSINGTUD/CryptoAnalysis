package main.prefined;

import main.prefined.A;
import main.prefined.B;

public class C {
	
	public byte[] attr1 = "Attr1".getBytes();
	public byte[] attr2 = "Attr2".getBytes();
	public byte[] attr3 = "Attr3".getBytes();

	public A ensurePred1OnReturnA() {
		return new A();
	}
	
	public A ensurePred2OnReturnA() {
		return new A();
	}
	
	public B ensurePred1OnReturnB() {
		return new B();
	}
	
	public B ensurePred2OnReturnB() {
		return new B();
	}
	
	public void ensurePred1OnThis() {
		return;
	}
	
	public void ensurePred2OnThis() {
		return;
	}

	public void ensurePred1OnAttr1() {
		return;
	}
	
	public void ensurePred1OnAttr2() {
		return;
	}
	
	public void ensurePred1OnAttr3() {
		return;
	}
	
	public void ensurePred2OnAttr1() {
		return;
	}
	
	public void ensurePred2OnAttr2() {
		return;
	}
	
	public void ensurePred2OnAttr3() {
		return;
	}
	
	public byte[] getAttr1() {
		return attr1;
	}
	
	public byte[] getAttr2() {
		return attr2;
	}
	
	public byte[] getAttr3() {
		return attr3;
	}

}