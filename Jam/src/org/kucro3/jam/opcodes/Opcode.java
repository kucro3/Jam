package org.kucro3.jam.opcodes;

import org.objectweb.asm.MethodVisitor;

public final class Opcode {
	public Opcode(String opcode, int code, OpcodeInsn insn, String arg3, int stack)
	{
		this.opcode = opcode;
		this.code = code;
		this.insn = insn;
		this.a = arg3;
		this.stack = stack;
	}
	
	public String opcode()
	{
		return opcode;
	}
	
	public int code()
	{
		return code;
	}
	
	public OpcodeInsn insn()
	{
		return insn;
	}
	
	public String a()
	{
		return a;
	}
	
	public int computeStack(int currentStack, String line)
	{
		if(stack == STACK_DYNC)
			return insn.computeStack(code, currentStack, line);
		return stack;
	}
	
	public boolean handle(MethodVisitor visitor, String line, DefinitionPool pool)
	{
		return insn.handle(visitor, code, line, pool);
	}
	
	public static final int STACK_COMPUTE_FAILURE = 0xF1FFFFFF;
	
	public static final int STACK_UNDER_FLOW = 0xF0FFFFFF;
	
	public static final int STACK_DYNC_UNSUPPORTED = 0x7FFFFFFF;
	
	public static final int STACK_DYNC = 0x7EFFFFFF;
	
	private final int stack;
	
	private final String opcode;
	
	private final int code;
	
	private final OpcodeInsn insn;
	
	private final String a;
}
