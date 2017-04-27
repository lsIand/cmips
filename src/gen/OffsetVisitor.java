package gen;

import ast.*;

public class OffsetVisitor implements ASTVisitor<Integer>{
	
	public int addr;
	
	public int funaddr;
	
	public Program program;
	
	public String currentfunc;
	
	public OffsetVisitor() {
		
		addr = 0;
	
	}
	
    public void checkOffsets(Program program) {   

    	this.program = program;
    	
        visitProgram(program);

    }
	
	@Override
	public Integer visitProgram(Program p) {
		
		for (StructType s : p.structTypes) {
			
			addr += s.accept(this);
			
		
		}
		
		for (FunDecl s : p.funDecls) {
			
			s.accept(this);

			
		}
		
		
		
		
		
		return null;
	}


	@Override
	public Integer visitStructType(StructType st) {
		
		int i = 0;
		
		
		
		for (VarDecl d : st.varDecls) {
			
			if (d.type instanceof StructType) {
				
				d.offset = i;
				
				StructType tempstruct = (StructType) d.type;
				
				for (StructType s : program.structTypes) {
					
					
					
					if (s.name.equals(tempstruct.name)) {
						
						tempstruct.refersto = s;
						
						d.type = tempstruct;
						
					}
					
				}
				
				i += tempstruct.refersto.accept(this);
				
				
				
			} else {
				
				d.offset = i;
				
				i+= d.type.accept(this);
				
				
				
			}
	
		}
		
		//System.out.println(i);
		
		st.structSize = i;
		
		return i;
	}

	@Override
	public Integer visitBaseType(BaseType bt) {
		
		
		
		return 4;
	}

	@Override
	public Integer visitArrayType(ArrayType at) {
		
		
		
		return at.arraySize*4;
	}

	@Override
	public Integer visitPointerType(PointerType pt) {
		
		
		
		
		return 4;
	}
	
	
	
	
	
	

	@Override
	public Integer visitBlock(Block b) {
		
		int k;
		int h;
		h = 0;
		
		for (VarDecl v : b.varDecls) {
				
			v.offset = funaddr;
			v.funname = currentfunc;
			k = v.type.accept(this);
			h += k;
			funaddr += k;
			
			//System.out.println(v.offset);
				
		}
		
		for (Stmt s : b.statements) {
			
			s.accept(this);
			
		}
		
		b.varSize = h;
		
		funaddr -= h;
		
		return null;
	}

	@Override
	public Integer visitFunDecl(FunDecl p) {
		
		currentfunc = p.name; 
		
		funaddr = 0;
		
		for (VarDecl v : p.params) {
			
			v.offset = funaddr;
			v.funname = currentfunc;
			funaddr += v.type.accept(this);
			
		}
		
		for (VarDecl v : p.block.varDecls) {
			
			v.offset = funaddr;
			v.funname = currentfunc;
			funaddr += v.type.accept(this);
			
		}
		
		p.block.varSize = funaddr;
		
		for (Stmt v : p.block.statements) {

			v.accept(this);
			
		}
		
		return null;
	}
	
	
	
	
	
	@Override
	public Integer visitVarDecl(VarDecl vd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitExprStmt(ExprStmt es) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitWhile(While w) {
		
		w.t.accept(this);
		
		return null;
	}

	@Override
	public Integer visitIf(If i) {
		
		i.tif.accept(this);
		
		if (i.telse != null) {
			
			i.telse.accept(this);
			
		}
		
		return null;
	}

	@Override
	public Integer visitReturn(Return r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitAssign(Assign a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitIntLiteral(IntLiteral il) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitCharLiteral(CharLiteral c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitStringLiteral(StringLiteral s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitBinOp(BinOp bo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitFunCallExpr(FunCallExpr fc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitArrayAccessExpr(ArrayAccessExpr aa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitFieldAccessExpr(FieldAccessExpr fa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitValueAtExpr(ValueAtExpr va) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitSizeOfExpr(SizeOfExpr so) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitTypeCastExpr(TypeCastExpr tc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer visitVarExpr(VarExpr v) {
		// TODO Auto-generated method stub
		return null;
	}

}
