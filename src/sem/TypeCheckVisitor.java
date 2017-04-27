package sem;

import ast.*;
import java.util.LinkedList;

public class TypeCheckVisitor extends BaseSemanticVisitor<Void> {

	
	public LinkedList<String> structNames;
	
	public TypeCheckVisitor() {
		structNames = new LinkedList<String>();
	}

	@Override
	public Void visitVarDecl(VarDecl vd) {
		
		if (vd.type == BaseType.VOID) {
			
			error("Cannot declare a variable with type VOID.");
			
		}
		
		return null;
	}
	
	
    @Override
    public Void visitBlock(Block b) {

        for (VarDecl d : b.varDecls) {

        	d.accept(this);

        }
        
        for (Stmt d : b.statements) {
        	

                d.accept(this);
        	

        }
        
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
    	
    	if (fd.type != null) {
    		fd.type.accept(this);
    	}
    	else {
    		error("Could not find function type (possibly undeclared).");
    	}
    	
        for (VarDecl vd : fd.params) {
        	
    		vd.accept(this);

        }
        fd.block.accept(this);

        return null;
    }

    @Override
    public Void visitProgram(Program p) {

    	for (StructType st : p.structTypes) {
    		/*
    		int i = 0;
    		for (String s : structNames) {
    			
    			
    			if (s == st.name) {
    				error("Two structs may not share a name!");
    				i = 1;
    			}
    			    			
    		}
    		
			if (i == 0) {
				structNames.add(st.name);
			}*/
    		
    	}
    	
        for (StructType st : p.structTypes) {

        	
            st.accept(this);
            

        }
        
        for (VarDecl vd : p.varDecls) {

            vd.accept(this);
            
        }
        
        for (FunDecl fd : p.funDecls) {
        	
        	

            fd.accept(this);
            

        }

        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
    	if (v.vd != null) {
	
	    	if (v.vd.type != null) {
	    		v.type = v.vd.type;
	    	}
	    	else {
	    		
	    	}
    	}
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {

    	return null;
    }

    @Override
    public Void visitStructType(StructType st) {

        for (VarDecl d : st.varDecls) {
       	
            d.accept(this);
            
        }

    	
        
        return null;
    }

	@Override
	public Void visitIntLiteral(IntLiteral il) {
		il.type = BaseType.INT;
		return null;
	}

	@Override
	public Void visitCharLiteral(CharLiteral c) {
		c.type = BaseType.CHAR;
		return null;
	}

	@Override
	public Void visitStringLiteral(StringLiteral s) {
		s.type = new ArrayType(BaseType.CHAR, s.s.length()+1);
		return null;
	}

	@Override
	public Void visitBinOp(BinOp bo) {

		bo.lhs.accept(this);

		bo.rhs.accept(this);
		
		if (bo.op == Op.ADD) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of an addition have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.SUB) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a substraction have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.MUL) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a multiplication have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.DIV) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a division have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.MOD) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a modular operation have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.OR) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of an \"or\" comparison have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.AND) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of an \"and\" comparison have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.GT) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a \"greater than\" comparison have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.LT) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a \"less than\" comparison have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.GE) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a \"greater than or equal\" comparison have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.LE) {
			boolean bool1 = (bo.lhs.type == BaseType.INT);
			boolean bool2 = (bo.rhs.type == BaseType.INT);
			
			if (bool1 && bool2) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a \"less than or equal\" comparison have to be integers or integer typed expressions!");
			}
			
		}
		else if (bo.op == Op.NE) {
			
			
			
			if (bo.rhs.type.equals1(bo.lhs.type)) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of a \"not equals\" comparison have to be same-type expressions!");
			}
			
		}
		else if (bo.op == Op.EQ) {
			
		
			if (bo.rhs.type.equals1(bo.lhs.type)) {
				bo.type = BaseType.INT;
			}
			else {
				error("Both operands of an \"equals\" comparison have to be same-type expressions!");
			}
			
		}
		
		
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {

		at.arrayType.accept(this);
		

		return null;
	}

	@Override
	public Void visitPointerType(PointerType pt) {

		pt.pointsTo.accept(this);

		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fc) {
		
		
		fc.type = fc.decl.type;
				
        for (Expr e : fc.expressions) {

            e.accept(this);
        }
        
        if (fc.expressions.size() != fc.decl.params.size()) {
        	
        	error("Amount of expressions does not match function arguments.");
        	
        }
        else {/*
        	
        	for (int i = 0; i < fc.expressions.size(); i++) {
        		if (fc.expressions.get(i).type != null && fc.decl.params.get(i).type != null) {
	        		if (!fc.expressions.get(i).type.equals1(fc.decl.params.get(i).type)) {
	        			
	        			error("Function arguments do not match in type.");
	        			
	        		}
        		}
        	}*/
        	
        }
		
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aa) {
		
		aa.exp1.accept(this);

		aa.exp2.accept(this);
		
		boolean bool1 = (aa.exp2.type == BaseType.INT);

		if (bool1) {
			
		}
		else {
			error("Arrays can only be accessed at an integer!");
		}

		
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fa) {
		
		fa.exp.accept(this);
		
		boolean bool1 = (fa.exp.type instanceof StructType);

		if (bool1) {
			
		}
		else {
			//error("Can only perform field access on a structure.");
		}
		
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr va) {

		va.exp.accept(this);


		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr so) {

		so.ty.accept(this);
		
		so.type = BaseType.INT;

		return null;
	}

	@Override
	public Void visitTypeCastExpr(TypeCastExpr so) {
		
		so.ty.accept(this);

		so.exp.accept(this);
		
		so.type = so.ty;
	
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
			

		es.e.accept(this);

		
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		

		w.e.accept(this);

		w.t.accept(this);

		boolean bool1 = (w.e.type == BaseType.INT);

		if (bool1) {
			
		}
		else {
			error("Must compare on an integer expression! (WHILE)");
		}
		
		return null;
	}

	@Override
	public Void visitIf(If i) {

		i.e.accept(this);
		
		boolean bool1 = (i.e.type == BaseType.INT);

		if (bool1) {
			
		}
		else {
			error("Must compare on an integer expression! (IF)");
		}

		i.tif.accept(this);
		
		if (i.telse != null) {
			
			i.telse.accept(this);
			
		}
		
		
		return null;
	}


	
	@Override
	public Void visitReturn(Return r) {
		


		if (r.e != null) {
			
			r.e.accept(this);
			
		}

		
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		
		if (a.lhs instanceof VarExpr || a.lhs instanceof FieldAccessExpr || a.lhs instanceof ArrayAccessExpr || a.lhs instanceof ValueAtExpr) {
			
			
			
		}
		else {
			error("Left-hand side of assignment invalid!");
		}
		

		a.lhs.accept(this);

		a.rhs.accept(this);
		
		

		
		return null;
	}

}

