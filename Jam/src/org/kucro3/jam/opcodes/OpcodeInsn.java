package org.kucro3.jam.opcodes;

import org.kucro3.jam.opcodes.DefinitionPool.Local;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public enum OpcodeInsn {
	INSN_FIELD {
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			String owner, name, desc;
			String[] arguments;
			if((arguments = line.split(":")).length != 2)
				return false;
			/* Parse owner & name */ {
				String ownerAndName = arguments[0];
				int ownerAndNameSplit = ownerAndName.lastIndexOf('/');
				owner = ownerAndName.substring(0, ownerAndNameSplit);
				name = ownerAndName.substring(ownerAndNameSplit + 1);
			}
			
			/* Parse desc */ {
				desc = arguments[1];
			}
			
			mv.visitFieldInsn(opcode, owner, name, desc);
			return true;
		}
	},
	
	INSN_IINC {
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			int inc;
			try {
				inc = Integer.parseInt(line);
			} catch (Throwable e) {
				return false;
			}
			mv.visitIincInsn(opcode, inc);
			return true;
		}
	},
	
	INSN_VOID {
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			mv.visitInsn(opcode);
			return true;
		}
	},
	
	INSN_INT {
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			int val;
			try {
				val = Integer.parseInt(line);
			} catch (Throwable e) {
				return false;
			}
			mv.visitIntInsn(opcode, val);
			return true;
		}
	},
	
	INSN_INVKDYNC,
	
	INSN_JMP {
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			Label label = pool.getLabel(line);
			if(label == null) 
				return false;
			mv.visitJumpInsn(opcode, label);
			return true;
		}
	},
	
	INSN_SWITCH_LKP,
	INSN_SWITCH_TBL,
	
	INSN_TYPE { // <opcode> <type>
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			mv.visitTypeInsn(opcode, line);
			return true;
		}
	},
	
	INSN_LDC { // <opcode> <type>:<value>
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			Object v;
			String[] splits = line.split(":", 2);
			if(splits.length < 2)
				return false;
			String // ->
				type = splits[0],
				value = splits[1];
			switch(type.toLowerCase())
			{
			case "int":
				v = Integer.parseInt(value);
				break;
			case "float":
				v = Float.parseFloat(value);
				break;
			case "long":
				v = Long.parseLong(value);
				break;
			case "double":
				v = Double.parseDouble(value);
				break;
			case "string":
				v = value;
				break;
			case "class":
				v = Type.getType(value);
				break;
			default:
				return false;
			}
			mv.visitLdcInsn(v);
			return true;
		}
	},
	
	INSN_METHOD { // <opcode> <method_name><descriptor>
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			return _PUBLIC_CODE_BLOCK_0_._handle(mv, opcode, line, pool, false);
		}
		
		@Override
		public int computeStack(int opcode, int currentStack, String line)
		{
			return _PUBLIC_CODE_BLOCK_0_._computeStack(opcode, currentStack, line);
		}
	},
	
	INSN_METHOD_IF {
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			return _PUBLIC_CODE_BLOCK_0_._handle(mv, opcode, line, pool, true);
		}
		
		@Override
		public int computeStack(int opcode, int currentStack, String line)
		{
			return _PUBLIC_CODE_BLOCK_0_._computeStack(opcode, currentStack, line);
		}
	},
	
	INSN_VAR { // <opcode> <index|$local_name>
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			int index;
			if(line.startsWith("$"))
			{
				Local local = pool.getLocal(line.substring(1));
				if(local == null)
					return false;
				index = local.getIndex();
			} 
			else try {
				index = Integer.parseInt(line);
			} catch (Throwable e) {
				return false;
			}
			
			mv.visitVarInsn(opcode, index);
			pool.regLocal(index);
			return true;
		}
	},
	
	INSN_MARRAY { // <opcode> <class>:<dimension>
		@Override
		public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool)
		{
			String[] args = line.split(":");
			if(args.length != 2)
				return false;
			String clazz = args[0];
			String dim = args[1];
			int dimension;
			try {
				dimension = Integer.parseInt(dim);
			} catch (Throwable e) {
				return false;
			}
			
			mv.visitMultiANewArrayInsn(clazz, dimension);
			return true;
		}
		
		@Override
		public int computeStack(int opcode, int currentStack, String line)
		{
			String dim = line.split(":")[1];
			int dimension = Integer.parseInt(dim);
			if(currentStack == 0)
				return Opcode.STACK_UNDER_FLOW;
			return Math.max(-dimension, -currentStack);
		}
	};
	
	private OpcodeInsn()
	{
		
	}
	
	public int computeStack(int opcode, int currentStack, String line)
	{
		return Opcode.STACK_DYNC_UNSUPPORTED;
	}
	
	public boolean handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool) 
	{
		return false;
	}
	
	private static class _PUBLIC_CODE_BLOCK_0_
	{
		private static boolean _handle(MethodVisitor mv, int opcode, String line, DefinitionPool pool
				,boolean arg)
		{
			for(int i = 0; i < line.length(); i++)
				switch(line.charAt(i))
				{
				case '(':
					String // ->
						name = line.substring(0, i),
						desc = line.substring(i, line.length());
					for(int j = name.length() - 1; j > -1; j--)
						switch(name.charAt(j))
						{
						case '/':
							String // ->
								fowner = name.substring(0, j),
								fname = name.substring(j + 1, name.length());
							mv.visitMethodInsn(opcode, fowner, fname, desc, arg);
							return true;
						default:
							continue;
						}
					break;
				default:
					continue;
				}
			return false;
		}
		
		private static int _computeStack(int opcode, int currentStack, String line)
		{
			int argcount = 0;
			boolean isVoid = line.endsWith("V");
			boolean isStatic = opcode == Opcodes.INVOKESTATIC;
			for(int i = 0; i < line.length(); i++)
			{
				switch(line.charAt(i))
				{
				case '(':
					LOOP_01: for(int j = i + 1; j < line.length(); j++)
						switch(line.charAt(j))
						{
						case ')':
							break LOOP_01;
						case 'Z':
						case 'B':
						case 'C':
						case 'S':
						case 'I':
						case 'J':
						case 'F':
						case 'D':
							argcount++;
							break;
						case '[':
						case 'L':
							LOOP_02: {
								for(j++; j < line.length(); j++)
									switch(line.charAt(j))
									{
									case ';':
										argcount++;
										break LOOP_02;
									default:
										continue;
									}
								return Opcode.STACK_COMPUTE_FAILURE;
							}
							break;
						default:
							return Opcode.STACK_COMPUTE_FAILURE;
						}
					break;
				default:
					continue;
				}
				return (isVoid ? 0 : 1) + (isStatic ? 0 : -1) - argcount;
			}
			throw new IllegalStateException("");
		}
	}
}