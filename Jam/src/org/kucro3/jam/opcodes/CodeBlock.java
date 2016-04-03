package org.kucro3.jam.opcodes;

import org.objectweb.asm.*;

public class CodeBlock implements Opcodes {
	public CodeBlock(MethodVisitor visitor, DefinitionPool pool)
	{
		this.dpool = pool;
		this.visitor = visitor;
		this.translator = new TranslatorDefault();
	}
	
	public CodeBlock(ClassVisitor cv, int modifier, String name, String desc)
	{
		this(cv, modifier, name, desc, new DefinitionPool());
	}
	
	public CodeBlock(ClassVisitor cv, int modifier, String name, String desc, DefinitionPool pool)
	{
		this(cv.visitMethod(modifier, name, desc, null, null), pool);
	}
	
	public CodeBlock(MethodVisitor visitor)
	{
		this(visitor, new DefinitionPool());
	}
	
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	public int cancel()
	{
		throw new UnsupportedOperationException();
	}
	
	public int cancel(int count)
	{
		throw new UnsupportedOperationException();
	}
	
	public CodeBlock next(String strline)
	{
		translator.translate(this, strline, ++line);
		return this;
	}

	public void insn(String command, String argument)
	{
		Opcode opcode = OpcodeMap.getOpcode(command);
		opcode.handle(visitor, argument, dpool);
		if(dpool.computeStack(opcode.computeStack(dpool.currentStack(), argument)) ==
				Opcode.STACK_UNDER_FLOW)
			throw new IllegalStateException("Stack Under Flow");
	}
	
	public void maxs()
	{
		visitor.visitMaxs(dpool.maxStack(), dpool.maxLocal());
	}
	
	public void code()
	{
		visitor.visitCode();
	}
	
	public void end()
	{
		visitor.visitEnd();
	}
	
	public int line()
	{
		return line;
	}
	
	public final MethodVisitor visitor;
	
	public final DefinitionPool dpool;
	
	private final Translator translator;
	
	int line = 0;
	
	public interface Translator
	{
		void translate(CodeBlock self, String strline, int line);
	}
	
	public static class TranslatorDefault implements Translator
	{
		public void translate(CodeBlock self, String strline, int line)
		{
			String[] ls = strline.split(" ", 2);
			self.insn(ls[0], ls.length > 1 ? ls[1] : null);
		}
	}
}
