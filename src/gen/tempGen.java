package gen;

import ast.*;

import sem.ProcSymbol;
import sem.Scope;
import sem.Symbol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.EmptyStackException;
import java.util.Stack;

import java.util.ArrayList;

public class tempGen implements ASTVisitor<Register> {

    /*
     * Simple register allocator.
     */

    // contains all the free temporary registers
    private Stack<Register> freeRegs = new Stack<Register>();

    public tempGen() {
        freeRegs.addAll(Register.tmpRegs);
        
        global = new ArrayList<VarDecl>();
        
        functions = new ArrayList<String>();
        
        frames = new ArrayList<Integer>();
        
        outer = true;
        
        assigning = false;
        
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
    public ArrayList<Integer> frames;
    public Program program;
    public boolean outer;
    public int stackpointer;
    public int framepointer;
    public int funpointer;
    public ArrayList<String> functions;
    public boolean assigning;
    
    
    
    
    
    @Override
    public Register visitProgram(Program p) {
    	
    	this.program = p;
    	
    	outer = true;
    	
    	writer.print(".data\n\n\n");        
     
        
        for (VarDecl vd : p.varDecls) {

            vd.accept(this);
            global.add(vd);
            writer.print("\n");
            
        }
        
        outer = false;
        
        writer.print("\n\n.text\n");
        
        for (FunDecl fd : p.funDecls) {
        	
        	if (fd.name.equals("main")) {
        		//System.out.println(fd.name);
        		
        		writer.print("\nmain: \n\n");
        		
        		fd.accept(this);
        	}
        	
        	
        }
        
        writer.print("\n\nli $v0, 10\nsyscall");
    	
        return null;
    }
    
    
    
    
    @Override
    public Register visitVarDecl(VarDecl vd) {
        
    	if (outer) {
    	
    		if (!(vd.type instanceof StructType)) {
		    	writer.print(vd.varName + ":");
		    	
		    	vd.type.accept(this);
		    	
		    	writer.print("\n");
    		} else {
        		currentname = vd.varName;
        		vd.type.accept(this);
        		writer.print("\n");
        		
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
			
			writer.print(" 0:" + at.arraySize);
		
		}
		else {
			
			funpointer+= 4*at.arraySize;
			stackpointer += 4*at.arraySize;
			writer.print(-4*at.arraySize);			
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
		}
		return null;
	}
	
	
    
	
	
	
	
	
    @Override
    public Register visitFunDecl(FunDecl p) {
        
    	functions.add(new String(p.name));
    	
    	framepointer = stackpointer;
    	
    	frames.add(new Integer(framepointer));
    	
    	funpointer = 0;
    	
    	p.visited = true;
    	
    	p.addresses.add(new Integer(stackpointer));
    	
    	for (VarDecl d : p.params) {
    		
    		d.accept(this);
    		
    	}
    	
    	for (VarDecl d : p.block.varDecls) {
    		
    		d.accept(this);
    		
    	}
    	
    	p.block.accept(this);
    
        return null;
    }
	
    
    
    
    
    
    
    @Override
    public Register visitBlock(Block b) {
        
    	for (Stmt s : b.statements) {
    		
    		if (s instanceof Block) {
    			
    		}
    		
    		else if (s instanceof Assign) {
    			
    			assigning = true;
    			s.accept(this);
    			
    			
    		}
    		
    		else {
    			s.accept(this);
    		}
    		
    	}
    	
    	
        return null;
    }

    
    
    




	@Override
	public Register visitExprStmt(ExprStmt es) {
		
		writer.print("\n");
		es.e.accept(this);
		writer.print("\n");
		
		return null;
	}

	@Override
	public Register visitWhile(While w) {
		
		
		
		return null;
	}

	@Override
	public Register visitIf(If i) {
		
		
		
		return null;
	}

	@Override
	public Register visitReturn(Return r) {
		
		
		
		return null;
	}

	@Override
	public Register visitAssign(Assign a) {
		
		Register value;
		Register result;
		
		value = a.lhs.accept(this);
		
		assigning = false;
		
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
		
		i = Character.getNumericValue(c.c);
				
		Register result = getRegister();
		writer.print("\nli " + result.toString() + ", " + i + "\n");
				
		return result;
	}

	@Override
	public Register visitStringLiteral(StringLiteral s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Register visitBinOp(BinOp bo) {
		
		Register lhs;
		Register rhs;
		
		Register result = getRegister();
		
		lhs = bo.lhs.accept(this);
		rhs = bo.rhs.accept(this);
		
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
		
		
		
		else if (bo.op.equals(Op.GT)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.LT)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.GE)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.LE)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.NE)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.EQ)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.OR)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}
		else if (bo.op.equals(Op.AND)) {
			writer.print("\nXXX " + result.toString() + ", " + lhs.toString() + ", " + rhs.toString() + "\n");
		}


		
		freeRegister(lhs);
		freeRegister(rhs);
		
		return result;
	}
	
	
	

	@Override
	public Register visitFunCallExpr(FunCallExpr fc) {
		
		writer.print("\nsw $sp, $fp\n");
		
		writer.print("\nj " + fc.name + "\n");
		
		return null;
	}
	
	
	
	
    @Override
    public Register visitVarExpr(VarExpr v) {
        
    	if (assigning) {
    		
    		assigning = false;
	    	Register result = getRegister();
	    	
	    	if (global.contains(v.vd)) {
	    		
	    		writer.print("\nla " + result.toString() + ", " +  v.name + "\n");
	    		
	    	} else {
	    		
	    		int curraddress = v.vd.address.get(v.vd.address.size()-1);
	    		int tempstack = stackpointer - curraddress;
	    		
	    		System.out.println(curraddress);
	    		System.out.println(stackpointer);
	    		System.out.println(tempstack);
	    		
	    		writer.print("\nla " + result.toString() + ", " + tempstack +"($sp)\n");
	    		
	    	}
	    	
	        return result;
    	}
    	else {
    		
    		return null;
    		
    	}
    	
    }
	
	
	

	@Override
	public Register visitArrayAccessExpr(ArrayAccessExpr aa) {
		
    	if (assigning) {
    		
	    	Register result = getRegister();
	    		
    		Register curraddress = aa.exp1.accept(this);
    		
    		assigning = false;
    		
    		Register arroffset = aa.exp2.accept(this);
    		
    		
    		writer.print("\nmul " + arroffset.toString() + ", " + arroffset.toString() + ", -4\n");
    		writer.print("\nadd " + result.toString() + ", " + curraddress.toString() + ", " + arroffset.toString() + "\n");
    		
	    	
	    	freeRegister(curraddress);
	    	freeRegister(arroffset);
	    	
	    	assigning = true;
	        return result;
	        
    	}
    	else {
    		
    		return null;
    		
    	}
		
	}

	
	
	
	
	@Override
	public Register visitFieldAccessExpr(FieldAccessExpr fa) {
		/*
		if (assigning) {
			
			assigning = false;
			
			Register result = getRegister();
			
			Register left = fa.exp.accept(this);
			
			assigning = true;
			
			
			
			writer.print("\n\n");

			freeRegister(left);
			
			return result;
			
		}
		
		else {
			
			return null;
			
		}
		*/
		return null;

	}
	
	
	
	

	@Override
	public Register visitValueAtExpr(ValueAtExpr va) {
		
		if (assigning) {
			
			Register result = getRegister();
			
			Register value = va.exp.accept(this);
			
			writer.print("\nla " + result.toString() + ", (" + value.toString() + ")\n");
			
			freeRegister(value);
			
			return result;
			
		}
		
		else {
			
			return null;
			
		}
		
		
	}
	
	
	
	

	@Override
	public Register visitSizeOfExpr(SizeOfExpr so) {
	
		Register result = getRegister();
		
		int size = 0;
		
		if (so.ty instanceof BaseType) {
			
			size = 4;
			
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
