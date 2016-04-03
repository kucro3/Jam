package org.kucro3.jam.opcodes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.kucro3.ini.*;

public class OpcodeMap {
	public static void init(Object obj)
	{
		if(!init) try {
			IniProfile ini;
			
			if(obj instanceof IniProfile)
				ini = (IniProfile)obj;
			else if(obj instanceof InputStream)
				ini = new IniProfile((InputStream)obj);
			else if(obj instanceof File)
				ini = new IniProfile((File)obj);
			else
				throw new IllegalArgumentException();
			
			IniSection sec = ini.getSection("Opcodes");
			for(Map.Entry<String, String> entry : sec.getProperties().entrySet())
			{
				String opcode = entry.getKey();
				String[] info = entry.getValue().split(",");
				int code, stack;
				OpcodeInsn insn;
				String extra;
				
				/* Parse hex code */ {
					String s0 = info[0];
					if(s0.startsWith("0x"))
						code = Integer.parseInt(s0.substring(2), 16);
					else
						code = Integer.parseInt(s0);
				}
				
				/* Parse insn */ {
					insn = OpcodeInsn.valueOf(info[1]);
				}
				
				/* Parse extra */ {
					extra = info[2];
				}
				
				/* Parse stack */ {
					String stk;
					switch(stk = info[3])
					{
					case "DYNC":
						stack = Opcode.STACK_DYNC;
						break;
					default:
						stack = Integer.parseInt(stk);
					}
				}
				
				init(new Opcode(opcode, code, insn, extra, stack));
			}
			init = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void init(Opcode opcode)
	{
		put(opcode.opcode(), opcode);
	}
	
	private static void put(String str, Opcode opcode)
	{
		int offset = hash(str);
		
		pool[offset] = new OpcodeElement(str, opcode);
	}
	
	public static Opcode getOpcode(String key, boolean hashHack)
	{
		OpcodeElement element = pool[hash(key)];
		
		if(element == null)
			return null;
		
		if(hashHack || element.key.equals(key))
			return element.value;
		
		return null;
	}
	
	public static Opcode getOpcode(String key)
	{
		return getOpcode(key, false);
	}
	
	static int hash(Object obj)
	{
		int hash = obj.hashCode();
		return ((Math.abs(hash ^ (hash >>> 3)) % 0x515) - 21) & 0x7FFF;
	}
	
	static {
		pool = new OpcodeElement[0x4F5];
	}
	
	private static volatile boolean init;
	
	private static final OpcodeElement[] pool;

	static final class OpcodeElement
	{
		OpcodeElement(String key, Opcode value)
		{
			this.key = key;
			this.value = value;
		}
		
		Opcode value;
		
		String key;
	}
}
