package de.mallox.eclipse.templates.util.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;

import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

public class InMemoryCompiler {

	public Class<?> compileClass(String pJavaSource, String pClassName) {
		ByteArrayOutputStream tOutputStream = new ByteArrayOutputStream();
		try {
			JavaCompiler javac = new EclipseCompiler();
			StandardJavaFileManager sjfm = javac.getStandardFileManager(null, null, null);
			SpecialClassLoader cl = new SpecialClassLoader();
			SpecialJavaFileManager fileManager = new SpecialJavaFileManager(sjfm, cl);

			String tShortClassName = pClassName;
			int tLastDotIndex = pClassName.lastIndexOf('.');
			if (tLastDotIndex != -1 && tLastDotIndex + 1 < pClassName.length()) {
				tShortClassName = pClassName.substring(tLastDotIndex + 1);				
			}
			List<MemorySource> compilationUnits = Arrays.asList(new MemorySource(tShortClassName, pJavaSource));
			Writer out = new PrintWriter(tOutputStream);
			JavaCompiler.CompilationTask compile;
			try {
				compile = javac.getTask(out, fileManager, null, null, null,
						compilationUnits);
			} catch (Exception e) {
				throw new RuntimeException(tOutputStream.toString());
			}
			boolean res = compile.call();
			if (res) {
				return cl.findClass(pClassName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return null;
	}
}

class MemorySource extends SimpleJavaFileObject {
	private String src;

	public MemorySource(String name, String src) {
		super(URI.create("file:///" + name + ".java"), Kind.SOURCE);
		this.src = src;
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return src;
	}

	public OutputStream openOutputStream() {
		throw new IllegalStateException();
	}

	public InputStream openInputStream() {
		return new ByteArrayInputStream(src.getBytes());
	}
}

class SpecialJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	private SpecialClassLoader xcl;

	public SpecialJavaFileManager(StandardJavaFileManager sjfm, SpecialClassLoader xcl) {
		super(sjfm);
		this.xcl = xcl;
	}

	public JavaFileObject getJavaFileForOutput(Location location, String name, JavaFileObject.Kind kind,
			FileObject sibling) throws IOException {
		MemoryByteCode mbc = new MemoryByteCode(name);
		xcl.addClass(name, mbc);
		return mbc;
	}

	public ClassLoader getClassLoader(Location location) {
		return xcl;
	}
}

class MemoryByteCode extends SimpleJavaFileObject {
	private ByteArrayOutputStream baos;

	public MemoryByteCode(String name) {
		super(URI.create("byte:///" + name + ".class"), Kind.CLASS);
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		throw new IllegalStateException();
	}

	public OutputStream openOutputStream() {
		baos = new ByteArrayOutputStream();
		return baos;
	}

	public InputStream openInputStream() {
		throw new IllegalStateException();
	}

	public byte[] getBytes() {
		return baos.toByteArray();
	}
}

class SpecialClassLoader extends ClassLoader {
	private Map<String, MemoryByteCode> m = new HashMap<String, MemoryByteCode>();

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		MemoryByteCode mbc = m.get(name);
		if (mbc == null) {
			mbc = m.get(name.replace(".", "/"));
			if (mbc == null) {
				return super.findClass(name);
			}
		}
		return defineClass(name, mbc.getBytes(), 0, mbc.getBytes().length);
	}

	public void addClass(String name, MemoryByteCode mbc) {
		m.put(name, mbc);
	}
}
