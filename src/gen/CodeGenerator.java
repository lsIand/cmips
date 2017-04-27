package gen;

import ast.*;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.EmptyStackException;

import java.util.Stack;

import java.util.ArrayList;

public class CodeGenerator implements ASTVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();

    public CodeGenerator() {
        freeRegs.addAll(Register.tmpRegs);
        
        global = new ArrayList<VarDecl>();
        
        currentblock = new ArrayList<Block>();
        
        
        outer = true;
        
        addressAccessAssign = false;
        
        stackpointer = 0;
        
        framepointer = 0;

    }

    @SuppressWarnings("serial")
	private class RegisterAllocationError extends Error {}

    private Register getRegister() {
        try {
            return freeRegs.pop();
        } catch (EmptyStackException ese) {
            throw new RegisterAllocationError(); // no more free registers, bad luck!
        }
    }

    private void freeRegister(Register reg) {
        freeRegs.push(reg);
    }



    private PrintWriter writer; // use this writer to output the assembly instructions


    public void emitProgram(Program program, File outputFile) throws FileNotFoundException {
        
    	ov = new OffsetVisitor();
    	
    	ov.checkOffsets(program);
    	
    	writer = new PrintWriter(outputFile);       

        visitProgram(program);
        
        writer.close();
        
    }
    
    
    
    
    
    
    
    public OffsetVisitor ov;
    public ArrayList<VarDecl> global;

    public Program program;
    public boolean outer;
    public int stackpointer;
    public int framepointer;
    public int funpointer;

    public boolean addressAccessAssign;
    
    public String currentfunc;
    public int currenttag;
    public int itertag;
    
    public boolean main;
    
    public ArrayList<Block> currentblock;
    
    public int currentMemory;
    
    public int memPLUS;    
    
    
    
    @Override
    public Register visitProgram(Program p) {
    	
    	this.program = p;
    	
    	outer = true;
    	main = true;
    	
    	writer.print(".data\n\n\n");        
     
        
        for (VarDecl vd : p.varDecls) {

            vd.accept(this);
            global.add(vd);
            writer.print("\n");
            
        }
        
        outer = false;
        
        writer.print("\n\n.text\n");
        
        writer.print("\naddi $fp, $sp, 0\n");
        
        writer.print("\naddi $a3, $sp, 4\n");
        
        writer.print("\naddi $a1, $sp, 4\n");
        
        for (FunDecl fd : p.funDecls) {
        	
        	if (fd.name.equals("main")) {
        		
        		currentfunc = new String(fd.name);        		
        		
        		fd.accept(this);
        	}
        	
        	
        }
        
        writer.print("\n\nli $v0, 10\nsyscall");
        
        main = false;
        
        for (FunDecl fd : p.funDecls) {
        	
        	if (!fd.visited) {
        		
        		currentfunc = new String(fd.name);        		
        		
        		fd.accept(this);
        	}
        	
        	
        }
        
        
    	
        return null;
    }
    
    
    
    
    @Override
    public Register visitVarDecl(VarDecl vd) {
        
    	if (outer) {
    	
    		if (!(vd.type instanceof StructType)) {
		    	writer.print(vd.varName + ":");
		    	
		    	vd.type.accept(this);
		    	
		    	writer.print(" 66\n");
    		} else {
        		currentname = vd.varName;
        		
        		vd.type.accept(this);
        		
        		
        	}
    	}
    	else {
    		
    		vd.address.add(new Integer(stackpointer));
    		
    		writer.print("\naddi $sp, $sp, ");
    		vd.type.accept(this);
    		writer.print("\n");

    	}
    	
        return null;
    }

    
    
    
    @Override
    public Register visitBaseType(BaseType bt) {
    	
    	if (outer) {
    	
	    	if (bt.equals(BaseType.INT)) {
	    		
	    		writer.print(" .word");
	    		
	    	}
	    	else if (bt.equals(BaseType.CHAR)) {
	    		
	    		writer.print(" .word");
	    		
	    	}
    	
    	}
    	else {
    		
    		funpointer+= 4;
    		stackpointer += 4;
    		writer.print(-4);
    		currentMemory = 4;
    		paramsize += 4;
    		
    	}
    	
        return null;
    }
    
    
    
    public String currentname;
    
    @Override
    public Register visitStructType(StructType st) {
    	
    	if (outer) {
    		
    		StructType tempStruct;
    		
    		for (StructType pt : program.structTypes) {
    			
    			if (pt.name.equals(st.name)) {
    				
    				st.refersto = pt;
    				
    			}
    			
    		}
    	
	    	for (VarDecl var : st.refersto.varDecls) {
	    		
	    		if (var.type instanceof StructType) {
	    			
	    			tempStruct = (StructType) var.type;
	    					
	    			visitSubStruct(tempStruct.refersto, "\n" + currentname + "_" + var.varName);
	    			
	    			
	    		}
	   
	    		else {
	    				    	    		
    	    		writer.print("\n" + currentname + "_");
    	    		var.accept(this);
					    			    			
	    		}		
	    	}	    	
    	} 
    	
    	else {    		
    		
    		for (StructType ss : program.structTypes) {
    			
    			if (st.name.equals(ss.name)) {
    				
    				st.refersto = ss;
    				
    			}
    			
    		}
    		
    		funpointer+= st.refersto.structSize;
    		stackpointer += st.refersto.structSize;
    		writer.print(-st.refersto.structSize);
    		currentMemory = st.refersto.structSize;
    		paramsize += st.refersto.structSize;
    		
    	}
    	
        return null;
    }   
    
    public void visitSubStruct(StructType st, String s) {
    	
		StructType tempStruct;
		
		

    	for (VarDecl var : st.varDecls) {
    		
    		if (var.type instanceof StructType) {
    			
    			tempStruct = (StructType) var.type;
    			
    			visitSubStruct(tempStruct.refersto, s + "_" + var.varName);
    		}
   
    		else {
    				    	    		
	    		writer.print(s + "_");
	    		var.accept(this);
				    			    			
    		}		
    	}	
    }


	@Override
	public Register visitArrayType(ArrayType at) {
		
		if (outer) {
			
			at.arrayType.accept(this);
			
			for (int i = 0; i< at.arraySize-1; i++) {
				
				writer.print(" 66,");
				
			}
		
		}
		else {
			
			funpointer+= 4*at.arraySize;
			stackpointer += 4*at.arraySize;
			writer.print(-4*at.arraySize);	
			currentMemory = 4*at.arraySize;
			paramsize += 4*at.arraySize;
		}	
		return null;
	}
	

	@Override
	public Register visitPointerType(PointerType pt) {
		
		if (outer) {
			
			writer.print(" .word");
			
		}
		else {
			
			funpointer+= 4;
			stackpointer+= 4;
			writer.print(-4);		
			currentMemory = 4;
			paramsize += 4;
		}
		return null;
	}
	
	
    
	
	
	public static final int constantFset = 80;
	public static final int charsize = 4;
	
	int paramsize = 0;
	
	FunDecl current;
	
	
    @Override
    public Register visitFunDecl(FunDecl p) {
        
    	current = p;
    	
    	memPLUS = 0;
    	
    	writer.print("\n" + p.name + ":\n\n");

    		
    		writer.print("\nsw $fp, ($sp)\n");
    		
    		stackpointer += constantFset;
    		
    		writer.print("\naddi $sp, $sp, -" + constantFset + "\n");   
    		
    		writer.print("\naddi $fp, $sp, 0\n");
    		
    		
    		
    	
    	funpointer = 0;
    	
    	paramsize = 0;

    	
    	p.visited = true;
    	
    	for (VarDecl d : p.params) {
    		
    		d.accept(this);
    		
    	}
    	

    	
    	if (!p.name.equals("print_s") && !p.name.equals("print_i") && !p.name.equals("print_c") && !p.name.equals("read_c") && !p.name.equals("read_i") && !p.name.equals("mcmalloc")) {
    		
    		p.block.accept(this);
    		
    	}
    	else {
    		
    		if (p.name.equals("print_i")) {
    			
    			writer.print("\nlw $a0, ($fp)\n");
    			
    			writer.print("\nli $v0, 1\n");
    			
    			writer.print("\nsyscall\n");
    			
    			writer.print("\naddi $sp, $sp, 4\n");
    			
    		}
    		
    		else if (p.name.equals("print_c")) {
    			
    			writer.print("\nlb $a0, ($fp)\n");
    			
    			writer.print("\nli $v0, 11\n");
    			
    			writer.print("\nsyscall\n");
    			
    			writer.print("\naddi $sp, $sp, 4\n");
    			
    		}
    		
    		else if (p.name.equals("print_s")) {
    			
    			//writer.print("\naddi $sp, $sp, -4\n");
    			
    			writer.print("\nlw $a2, ($fp)\n");
    			
    			writer.print("\nlw $a0, ($a2)\n");
    			
    			currenttag += 2;
    			
    			int ttag = currenttag;
    			
    			int ttag2 = currenttag + 1;
    			
    			
    			
    			writer.print("\nbeq $a0, 0, tag" + ttag2 + "\n");
    			
    			writer.print("\ntag" + ttag + ":\n");
    			
    			
    			
    			
    			
    			writer.print("\nli $v0, 11\n");
    			
    			writer.print("\nsyscall\n");
    			
    			
    			writer.print("\naddi $a2, $a2, -4\n");
    			
    			writer.print("\nlw $a0, ($a2)\n");
    			
    			writer.print("\nbne $a0, 0, tag" + ttag + "\n");
    			
    			
    			writer.print("\ntag" + ttag2 + ":\n");
    			
    			
    			
    			
    			//writer.print("\nli $v0, 11\n");
    			
    			//writer.print("\nsyscall\n");
    			
    			writer.print("\naddi $sp, $sp, 4\n");
    			
    		}
    		
    		else if (p.name.equals("read_c")) {
    			
    			writer.print("\naddi $sp, $sp, -4\n");
    			
    			writer.print("\nli $v0, 12\n");
    			
    			writer.print("\nsyscall\n");

    			
    			int blocksizes = 0;
    			
    			blocksizes+=4;

    			writer.print("\naddi $fp, $fp, " + constantFset + "\n");   
    			
    			
    			writer.print("\nlw $fp, ($fp)\n");
    			
    			writer.print("\naddi $sp, $sp, " + constantFset + "\n"); 
    			
    			
    			writer.print("\naddi $sp, $sp, " + blocksizes + "\n"); 

    			
    			//System.out.println(blocksizes);
    			

    				writer.print("\njr $ra\n");
    			
    		}
    		
    		else if (p.name.equals("read_i")) {
    			
    			writer.print("\naddi $sp, $sp, -4\n");
    			
    			writer.print("\nli $v0, 5\n");
    			
    			writer.print("\nsyscall\n");
    			
    			
    			int blocksizes = 0;
    			
    			blocksizes+=4;

    			writer.print("\naddi $fp, $fp, " + constantFset + "\n");   
    			
    			
    			writer.print("\nlw $fp, ($fp)\n");
    			
    			writer.print("\naddi $sp, $sp, " + constantFset + "\n"); 
    			
    			
    			writer.print("\naddi $sp, $sp, " + blocksizes + "\n"); 
    			

    			
    			//System.out.println(blocksizes);
    			

    				writer.print("\njr $ra\n");
    		}
    		
    		else if (p.name.equals("mcmalloc")) {
    			

    			
    			writer.print("\nlw $a0, ($fp)\n");
    			
    			writer.print("\nli $v0, 9\n");
    			
    			writer.print("\nsyscall\n");
    			
    			
    			

    			writer.print("\naddi $fp, $fp, " + constantFset + "\n");   
    			
    			
    			writer.print("\nlw $fp, ($fp)\n");
    			
    			writer.print("\naddi $sp, $sp, " + constantFset + "\n"); 
    			
    			writer.print("\naddi $sp, $sp, 4\n"); 
    			
    			
    			

    			
    			//System.out.println(blocksizes);
    			

    				writer.print("\njr $ra\n");
    		}
    		
    		
    		
    	}
    	
    
    	
    		writer.print("\naddi $fp, $fp, " + constantFset + "\n");
    		
    		writer.print("\nlw $fp, ($fp)\n");
    		
    		writer.print("\naddi $sp, $sp, " + constantFset + "\n"); 
    		 
    		
    		if (!main) {
    			
    			writer.print("\njr $ra\n");
    			
    		}
 	
        return null;
    }
	
    
    
    
    
    
    
    @Override
    public Register visitBlock(Block b) {
    	
    	if (!currentblock.isEmpty()) {
    		
    		b.father = currentblock.get(currentblock.size()-1);
    		
    	}
    	
    	currentblock.add(b);
    	
    	for (VarDecl vd: b.varDecls) {
    		
    		vd.accept(this);
    		
    	}
        
    	for (Stmt s : b.statements) {
    		
    		if (s instanceof Block) {
    			
    			s.accept(this);
    			
    		}
    		
    		else if (s instanceof Assign) {

    			s.accept(this);
    			
    			
    		}
    		
    		else if (s instanceof Return) {
    			s.accept(this);
    		}
    		
    		else if (s instanceof If) {
    			s.accept(this);
    		}
    		
    		else if (s instanceof While) {
    			s.accept(this);
    		}
    		
    		else if (s instanceof ExprStmt) {
    			s.accept(this);
    		}
    		
    	}
    	
		writer.print("\naddi $sp, $sp, " + b.varSize + "\n"); 
    	
    	currentblock.remove(b);
    	
    	
        return null;
    }

    
    
    




	@Override
	public Register visitExprStmt(ExprStmt es) {
		
		Register free;
		
		writer.print("\n");
		free = es.e.accept(this);
		writer.print("\n");
		
		if (free != null) {
			freeRegister(free);
		}
		return null;
	}

	@Override
	public Register visitWhile(While w) {
		
		Register check;
		
		int ttag = currenttag;
		
		int ttag2 = currenttag + 1;
		
		currenttag += 2;
		
		
		
		
		check = w.e.accept(this);
		
		
		writer.print("\nbeq " + check.toString() + ", 0, tag" + ttag2 + "\n");
		
		writer.print("\ntag" + ttag + ":\n");
		
		w.t.accept(this);
		
		check = w.e.accept(this);
		
		writer.print("\nbne " + check.toString() + ", 0, tag" + ttag + "\n");
		
		writer.print("\ntag" + ttag2 + ":\n");
		
		
		
		
		freeRegister(check);
		
		return null;
	}

	@Override
	public Register visitIf(If i) {
		
		
		Register check;
		
		
		check = i.e.accept(this);
		
		
		int ttag = currenttag;
		
		int ttag2 = currenttag + 1;
		
		int ttag3 = currenttag + 2;
		
		currenttag += 3;
		
		
		writer.print("\nbne " + check.toString() + ", 1, tag" + ttag2 + "\n");
		

		writer.print("\ntag" + ttag + ":\n");
		
		i.tif.accept(this);
		
		writer.print("\nj tag" + ttag3 + "\n");
		


		writer.print("\ntag" + ttag2 + ":\n");
		
		if (i.telse != null) {
			
			i.telse.accept(this);
			
		}


		writer.print("\ntag" + ttag3 + ":\n");
		
		
		freeRegister(check);
		
		return null;
	}

	@Override
	public Register visitReturn(Return r) {
		
		int blocksizes = 0;
		
		Block enclosing = currentblock.get(currentblock.size()-1);
		
		Register result;
		
		if (r.e != null) {
			
			result = r.e.accept(this);
			
			writer.print("\naddi $v0, " + result.toString() + ", 0\n");   
			
		}
		
		while (enclosing.father != null) {

			blocksizes += enclosing.varSize;
			
			enclosing = enclosing.father;
			
		}
		
		blocksizes+=enclosing.varSize;

		writer.print("\naddi $fp, $fp, " + constantFset + "\n");   
		
		
		writer.print("\nlw $fp, ($fp)\n");
		
		writer.print("\naddi $sp, $sp, " + constantFset + "\n"); 
		
		
		writer.print("\naddi $sp, $sp, " + blocksizes + "\n"); 
		
		
		//System.out.println(blocksizes);
		

			
			writer.print("\njr $ra\n");

		
		return null;
	}

	@Override
	public Register visitAssign(Assign a) {
		
		Register value;
		Register result;
		
		addressAccessAssign = true;
		
		value = a.lhs.accept(this);
		
		addressAccessAssign = false;
		
		result = a.rhs.accept(this);
		
		writer.print("\nsw " + result.toString() + ", (" + value.toString() + ")\n");
		
		freeRegister(value);
		
		freeRegister(result);
		
		return null;
	}

	@Override
	public Register visitIntLiteral(IntLiteral il) {
		
		Register result = getRegister();
		writer.print("\nli " + result.toString() + ", " + il.i + "\n");
				
		return result;
	}

	@Override
	public Register visitCharLiteral(CharLiteral c) {
		int i;
		
		i = (int) c.c;
				
		Register result = getRegister();
		writer.print("\nli " + result.toString() + ", " + i + "\n");
				
		return result;
	}

	
	int absoluteMemory = 0;
	
	
	@Override
	public Register visitStringLiteral(StringLiteral s) {
		
		Register result = getRegister();
		
		String padded = s.s + "\0";
		
		int stringIndex = padded.length()*charsize;
		int strLen = padded.length();
		int realOffset = stringIndex - charsize;
		
		
		
		
		writer.print("\nli $a0, " + stringIndex + "\n");
		
		writer.print("\nli $v0, 9\n");
		
		writer.print("\nsyscall\n");
		
		
		writer.print("\naddi $v0, $v0, " + stringIndex + "\n");
		
		for (int i = 0; i < strLen; i++) {
			writer.print("\naddi $v0, $v0, -" + charsize + "\n");
			
			int tempChar = (int) padded.charAt(i);
			
			writer.print("\nli " + result.toString() + ", " + tempChar + "\n");
			
			writer.print("\nsw " + result.toString() + ", ($v0)\n");
			
			
		}
		
		
		
		writer.print("\naddi " + result.toString() + ", $v0, " + realOffset + "\n");

	
		
		/*
		writer.print("\naddi $a3, $a3, " + stringIndex + "\n");
		
		for (int i = 0; i < strLen; i++) {
			writer.print("\naddi $a3, $a3, -" + charsize + "\n");
			int tempChar = (int) padded.charAt(i);
			
			writer.print("\nli " + result.toString() + ", " + tempChar + "\n");
			
			writer.print("\nsw " + result.toString() + ", ($a3)\n");
			
			
		}
		
		
		
		writer.print("\naddi " + result.toString() + ", $a3, " + realOffset + "\n");
		
		writer.print("\naddi $a3, $a3, " + stringIndex + "\n");
		*/
		
		return result;
		

	}

	@Override
	public Register visitBinOp(BinOp bo) {
		
		Register lhs;
		Register rhs;
		
		Register result = getRegister();
		
		lhs = bo.lhs.accept(this);
		rhs = bo.rhs.accept(this);
		
		int ttag = currenttag;
		
		int ttag2 = currenttag + 1;
		
		
		currenttag += 2;
		
		if (bo.op.equals(Op.ADD)) {
			writer.print("\nadd " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.MUL)) {
			writer.print("\nmul " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.SUB)) {
			writer.print("\nsub " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.DIV)) {
			writer.print("\ndiv " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.MOD)) {
			writer.print("\ndiv " + lhs.toString() + ", " + rhs.toString() + "\n");
			writer.print("\nmfhi " + result.toString() + "\n");
		}
		
		
		
		else if (bo.op.equals(Op.GT)) {

			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nble " + lhs.toString() + ", " + rhs.toString() + ", tag" + ttag + "\n");
			writer.print("\nli " + result.toString() + ", 1\n");
			writer.print("\ntag" + ttag + ":\n");
		
		}
		else if (bo.op.equals(Op.LT)) {

			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nbge " + lhs.toString() + ", " + rhs.toString() + ", tag" + ttag + "\n");
			writer.print("\nli " + result.toString() + ", 1\n");
			writer.print("\ntag" + ttag + ":\n");
		}
		else if (bo.op.equals(Op.GE)) {
			currenttag++;
			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nblt " + lhs.toString() + ", " + rhs.toString() + ", tag" + ttag + "\n");
			writer.print("\nli " + result.toString() + ", 1\n");
			writer.print("\ntag" + ttag + ":\n");		}
		else if (bo.op.equals(Op.LE)) {
			currenttag++;
			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nbgt " + lhs.toString() + ", " + rhs.toString() + ", tag" + ttag + "\n");
			writer.print("\nli " + result.toString() + ", 1\n");
			writer.print("\ntag" + ttag + ":\n");		
		}
		else if (bo.op.equals(Op.NE)) {
			currenttag++;
			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nbeq " + lhs.toString() + ", " + rhs.toString() + ", tag" + ttag + "\n");
			writer.print("\nli " + result.toString() + ", 1\n");
			writer.print("\ntag" + ttag + ":\n");	
		}
		else if (bo.op.equals(Op.EQ)) {
			currenttag++;
			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nbne " + lhs.toString() + ", " + rhs.toString() + ", tag" + ttag + "\n");
			writer.print("\nli " + result.toString() + ", 1\n");
			writer.print("\ntag" + ttag + ":\n");	
		}
		else if (bo.op.equals(Op.OR)) {

			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nbeq " + lhs.toString() + ", 1, tag" + ttag + "\n");
			writer.print("\nbeq " + rhs.toString() + ", 1, tag" + ttag + "\n");

			writer.print("\nj tag" + (ttag2) + "\n");	
			writer.print("\ntag" + ttag + ":\n");	
			writer.print("\nli " + result.toString() + ", 1\n");

			writer.print("\ntag" + ttag2 + ":\n");
		}
		else if (bo.op.equals(Op.AND)) {

			currenttag++;
			writer.print("\nli " + result.toString() + ", 0\n");
			writer.print("\nbne " + lhs.toString() + ", 1, tag" + ttag + "\n");
			writer.print("\nbne " + rhs.toString() + ", 1, tag" + ttag + "\n");

			writer.print("\nj tag" + (ttag2) + "\n");	
			writer.print("\ntag" + ttag + ":\n");	
			writer.print("\nli " + result.toString() + ", 1\n");

			writer.print("\ntag" + ttag2 + ":\n");
		}


		
		freeRegister(lhs);
		freeRegister(rhs);
		
		return result;
	}
	
	
	

	@Override
	public Register visitFunCallExpr(FunCallExpr fc) {
		
		int totalMemory = 0;
		
		writer.print("\naddi $sp, $sp, -4\n");
		
		writer.print("\nsw $ra, ($sp)\n");
		
		writer.print("\naddi $sp, $sp, 4\n");
		
		writer.print("\naddi $sp, $sp, -" + constantFset + "\n");
		
		
		Register current;
		
		for (int i = 0; i < fc.expressions.size(); i++) {
			

			
			current = fc.expressions.get(i).accept(this);
			
			writer.print("\nsw " + current.toString() + ", ($sp)\n");
			
			fc.decl.params.get(i).accept(this);
			
			totalMemory += currentMemory;
			
			freeRegister(current);
	
		}
		
		
		
		writer.print("\naddi $sp, $sp, " + totalMemory + "\n");
		
		writer.print("\naddi $sp, $sp, " + constantFset + "\n");
		
		
		writer.print("\naddi $sp, $sp, -" + 8 + "\n");
		
		for (int ct = 0; ct < 18; ct ++) {
			
			writer.print("\nsw " + Register.tmpRegs.get(ct).toString() + ", ($sp)\n");
			
			writer.print("\naddi $sp, $sp, -" + 4 + "\n");	
			
		}
		
		
		writer.print("\naddi $sp, $sp, " + constantFset + "\n");
		
		//writer.print("\naddi $sp, $sp, " + 8 + "\n");
		
		writer.print("\njal " + fc.name + "\n");		
		
		writer.print("\naddi $sp, $sp, -4\n");
		
		writer.print("\nlw $ra, ($sp)\n");
		
		writer.print("\naddi $sp, $sp, 4\n");
		
		writer.print("\naddi $sp, $sp, -" + 8 + "\n");
		
		for (int ct = 0; ct < 18; ct ++) {
			
			writer.print("\nlw " + Register.tmpRegs.get(ct).toString() + ", ($sp)\n");
			
			writer.print("\naddi $sp, $sp, -" + 4 + "\n");	
			
		}
		
		
		writer.print("\naddi $sp, $sp, " + constantFset + "\n");
		
		Register result = getRegister();
		
		writer.print("\naddi " + result.toString() + ", $v0, 0\n");
		
		return result;
	}
	
	
	
	
    @Override
    public Register visitVarExpr(VarExpr v) {
        
    	if (addressAccessAssign) {
    		
    		//addressAccessAssign = false;
	    	Register result = getRegister();
	    	
	    	if (global.contains(v.vd)) {
	    		
	    		if (v.vd.type instanceof ArrayType) {
	    			
	    			ArrayType arr = (ArrayType) v.vd.type;
	    			
	    			int thatset = (arr.arraySize-1)*4;
	    			
	    			writer.print("\nla " + result.toString() + ", " +  v.name + " + " + thatset + "\n");
	    			
	    		}
	    		else {
	    		
	    			writer.print("\nla " + result.toString() + ", " +  v.name + "\n");
	    		
	    		}
	    		
	    	} else {
	    		
	    		
	    		writer.print("\nla " + result.toString() + ", " + -1*v.vd.offset + "($fp)\n");
	    		
	    	}
	    	
	        return result;
    	}
    	else {
    		
	    	Register result = getRegister();
	    	
	    	if (global.contains(v.vd)) {
	    		
	    		if (v.vd.type instanceof ArrayType) {
	    			ArrayType arr = (ArrayType) v.vd.type;
	    			
	    			int thatset = (arr.arraySize-1)*4;
	    			
	    			writer.print("\nla " + result.toString() + ", " +  v.name + " + " + thatset + "\n");
	    		
	    		}
	    		else {
	    		
	    			writer.print("\nla " + result.toString() + ", " +  v.name + "\n");
	    		
	    		}
	    		
	    	} else {
	    		
	    		
	    		writer.print("\nla " + result.toString() + ", " + -1*v.vd.offset +"($fp)\n");
	    		
	    	}
	    	
	    	writer.print("\nlw " + result.toString() + ", (" + result.toString() + ")\n");
	    	
	        return result;
    		
    	}
    	
    }
	
	
	

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aa) {
		
    	if (addressAccessAssign) {
    		
	    	Register result = getRegister();
	    		
    		Register curraddress = aa.exp1.accept(this);
    		
    		addressAccessAssign = false;
    		
    		Register arroffset = aa.exp2.accept(this);
    		
    		//writer.print("\nmul " + arroffset.toString() + ", " + arroffset.toString() + ", -4\n");
    		writer.print("\nmul " + arroffset.toString() + ", " + arroffset.toString() + ", -4\n");
    		writer.print("\nadd " + result.toString() + ", " + curraddress.toString() + ", " + arroffset.toString() + "\n");
    		
	    	
	    	freeRegister(curraddress);
	    	freeRegister(arroffset);
	    	
	    	addressAccessAssign = true;
	        return result;
	        
    	}
    	else {
    		
	    	Register result = getRegister();
	    	
	    	addressAccessAssign = true;
    		
    		Register curraddress = aa.exp1.accept(this);
    		
    		addressAccessAssign = false;
    		
    		Register arroffset = aa.exp2.accept(this);
    		
    		
    		writer.print("\nmul " + arroffset.toString() + ", " + arroffset.toString() + ", -4\n");
    		writer.print("\nadd " + result.toString() + ", " + curraddress.toString() + ", " + arroffset.toString() + "\n");
    		
    		writer.print("\nlw " + result.toString() + ", (" + result.toString() + ")\n");
	    	
	    	freeRegister(curraddress);
	    	freeRegister(arroffset);
	    	
	        return result;
    		
    	}
		
	}

	
	
	
	
	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fa) {
		

		StringLiteral sl;
		
		FieldAccessExpr e;
		
		
		if (addressAccessAssign && (fa.exp instanceof FieldAccessExpr || fa.exp instanceof VarExpr)) {
			
			if (fa.exp instanceof FieldAccessExpr) {
				
				e = (FieldAccessExpr) fa.exp;
				
				sl = (StringLiteral) fa.name;
				
				Register result = getRegister();
				
				writer.print("\nla " + result.toString() +", ");
				
				String s = sl.s;
				
				writer.print(paintStructAccess(e));	
				
				writer.print("_" + s);
				
				
				writer.print("\n\n");
				
				return result;
				
			} else {
				
				VarExpr froe = (VarExpr) fa.exp;
				
				sl = (StringLiteral) fa.name;
				
				Register result = getRegister();
				
				writer.print("\nla " + result.toString() +", ");
				
				String s = sl.s;
				
				writer.print(froe.name);	
				
				writer.print("_" + s);
				
				
				writer.print("\n\n");
				
				return result;
				
			}
			
			
			

			
		}
		
		else if (fa.exp instanceof FieldAccessExpr || fa.exp instanceof VarExpr) {
			
			if (fa.exp instanceof FieldAccessExpr) {
				
				e = (FieldAccessExpr) fa.exp;
				
				sl = (StringLiteral) fa.name;
				
				Register result = getRegister();
				
				writer.print("\nlw " + result.toString() +", ");
				
				String s = sl.s;
				
				writer.print(paintStructAccess(e));	
				
				writer.print("_" + s);
				
				
				writer.print("\n\n");
				
				return result;
				
			} else {
				
				VarExpr froe = (VarExpr) fa.exp;
				
				sl = (StringLiteral) fa.name;
				
				Register result = getRegister();
				
				writer.print("\nlw " + result.toString() +", ");
				
				String s = sl.s;
				
				writer.print(froe.name);	
				
				writer.print("_" + s);
				
				
				writer.print("\n\n");
				
				return result;
				
			}

		}
		else {
			
			return Register.gp;
			
		}

	}
	
	public String paintStructAccess(FieldAccessExpr e) {
		
		String s;
		
		if (e.exp instanceof FieldAccessExpr) {
			
			StringLiteral sl = (StringLiteral) e.name;
			
			FieldAccessExpr em = (FieldAccessExpr) e.exp;
			
			s = paintStructAccess(em);
			
			return s + "_" + sl.s;
			

		} else if (e.exp instanceof VarExpr) {
			
			VarExpr v = (VarExpr) e.exp;
			
			StringLiteral sl = (StringLiteral) e.name;
			
			
			
			return v.name + "_" + sl.s;
			
		} else {
			
			return "";
		}
		
		
	}
	
	
	
	

	@Override
	public Register visitValueAtExpr(ValueAtExpr va) {
		
		if (addressAccessAssign) {
			
			Register result = getRegister();
			
			Register value = va.exp.accept(this);
			
			writer.print("\nla " + result.toString() + ", (" + value.toString() + ")\n");
			
			freeRegister(value);
			
			return result;
			
		}
		
		else {
			
			//addressAccessAssign = true;
			
			Register result = getRegister();
			
			Register value = va.exp.accept(this);
			
			//addressAccessAssign = false;
			
			writer.print("\nlw " + result.toString() + ", (" + value.toString() + ")\n");
			
			freeRegister(value);
			
			
			
			return result;
			
		}
		
		
	}
	
	
	
	

	@Override
	public Register visitSizeOfExpr(SizeOfExpr so) {
	
		Register result = getRegister();
		
		int size = 0;
		
		if (so.ty instanceof BaseType) {
			
			if (so.ty.equals(BaseType.INT)) {
				
				size = 4;
				
			}
			else {
				
				size = 4;
				
			}
			
		}
		
		else if (so.ty instanceof PointerType) {
			
			size = 4;
			
		}
		
		else if (so.ty instanceof ArrayType) {
			
			ArrayType t = (ArrayType) so.ty;
			
			size = 4*t.arraySize;
			
		}
		
		else if (so.ty instanceof StructType) {
			
			StructType t = (StructType) so.ty;
			
			size = t.structSize;
			
		}
		
		writer.print("\nli " + result.toString() + ", " + size + "\n");
		
		return result;
	}

	@Override
	public Register visitTypeCastExpr(TypeCastExpr tc) {
		
		Register result = tc.exp.accept(this);
		
		return result;
	}
}
