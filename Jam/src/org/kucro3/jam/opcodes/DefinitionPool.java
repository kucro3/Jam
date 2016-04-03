package org.kucro3.jam.opcodes;

import java.util.*;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

public class DefinitionPool {
	public DefinitionPool()
	{
		
	}
	
	public Label newLabelOutOfPool()
	{
		return new Label();
	}
	
	public Label newLabel(String name)
	{
		Label label = new Label();
		labels.put(name, label);
		return label;
	}
	
	public void removeLabel(String name)
	{
		labels.remove(name);
	}
	
	public Label getLabel(String name)
	{
		return labels.get(name);
	}
	
	public Local getLocal(String name)
	{
		return locals.get(name);
	}
	
	public boolean defLocal(int index, String type, String name)
	{
		if(locals.containsKey(index))
			return false;
		Local local = new Local(index, type, name);
		locals.put(index, local);
		regLocal(index);
		return true;
	}
	
	public void visitLocals(MethodVisitor mv, Label codeStart, Label codeEnd)
	{
		for(Map.Entry<Integer, Local> entry : locals.entrySet())
		{
			Local local = entry.getValue();
			int index = entry.getKey();
			mv.visitLocalVariable(local.name, Type.getType(local.type).getDescriptor(),
					null, codeStart, codeEnd, index);
		}
	}
	
	public void regLocal(int index)
	{
		maxlocal = Math.max(maxlocal, index);
	}
	
	public int computeStack(int stack)
	{
		if(stack < 0xF000FFFF)
			return stack;
		int i = this.stack;
		i += stack;
		if(i < 0)
			return Opcode.STACK_UNDER_FLOW;
		this.stack = i;
		this.maxstack = Math.max(i, this.maxstack);
		return 0;
	}
	
	public int currentStack()
	{
		return stack;
	}
	
	public int maxLocal()
	{
		return maxlocal;
	}
	
	public int maxStack()
	{
		return maxstack;
	}
	
	private int maxlocal = 0;
	
	private int stack = 0;
	
	private int maxstack = 0;
	
	private final Map<Integer, Local> locals = new HashMap<>();
	
	private final Map<String, Label> labels = new HashMap<>();

	public class Local 
	{
		private Local(int index, String type, String name)
		{
			this.index = index;
			this.type = type;
			this.name = name;
		}
		
		public String getType()
		{
			return type;
		}
		
		public String getName()
		{
			return name;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		int index;
		
		String type;
		
		String name;
	}
}
