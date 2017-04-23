package de.mallox.eclipse.templates.util;

public class ByteArrayClassLoader extends ClassLoader {

	private String className;
	private byte[] byteArray;

	public ByteArrayClassLoader(ClassLoader pParentClassLoader, String pClassName, byte[] pByteArray) {
		super(pParentClassLoader);
		className = pClassName;
		byteArray = pByteArray;
	}

	public Class<?> get() {
		return defineClass(className, byteArray, 0, byteArray.length);
	}
}
