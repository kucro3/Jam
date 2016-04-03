package org.kucro3.tool.asm;

import java.util.*;

import org.kucro3.clazz.util.ClassDef;
import org.kucro3.jam.opcodes.CodeBlock;
import org.kucro3.jam.opcodes.OpcodeMap;
import org.objectweb.asm.*;

public class ASM implements Opcodes {
	public static void asm(String... lines) throws InstantiationException, IllegalAccessException
	{
		CompiledCodeBlock ccb = compile(lines);
		if(ccb != null)
			ccb.run();
	}
	
	public static void asm(Object cacheHandle, String... lines) throws InstantiationException, IllegalAccessException
	{
		CompiledCodeBlock ccb;
		if((ccb = cache.get(cacheHandle)) == null)
			cache.put(cacheHandle, ccb = compile(lines));
		if(ccb != null)
			ccb.run();
	}
	
	private static final CompiledCodeBlock compile(String... lines) throws InstantiationException, IllegalAccessException
	{
		if(lines.length == 0)
			return null;
		
		String name = CLASS_NAME_HEADER + (count++);
		ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		cv.visit(V1_7, ACC_PUBLIC + ACC_FINAL, name, 
				null, "org/kucro3/tool/asm/ASM$CompiledCodeBlock", null);
		constructor(cv);
		CodeBlock cb = new CodeBlock(cv, ACC_PUBLIC + ACC_FINAL, "run", "()V");
		cb.code();
		for(int i = 0; i < lines.length; i++)
			cb.next(lines[i]);
		cb.maxs();
		cb.end();
		cv.visitEnd();
		byte[] byts = cv.toByteArray();
		Class<?> clz = ClassDef.defClass(name, byts, 0, byts.length);
		return (CompiledCodeBlock)clz.newInstance();
	}
	
	private static void constructor(ClassWriter cw)
	{
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "org/kucro3/tool/asm/ASM$CompiledCodeBlock"
				, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
	
	private static final Map<Object, CompiledCodeBlock> cache = new HashMap<>();
	
	private static volatile int count;
	
	private static final String CLASS_NAME_HEADER = "COMPILED_CODE_BLOCK_";
	
	public static abstract class CompiledCodeBlock
	{
		public abstract void run();
	}
	
	static {
		OpcodeMap.init(ASM.class.getClassLoader()
				.getResourceAsStream("org/kucro3/jam/opcodes/res/opcodes.ini"));
	}
}
