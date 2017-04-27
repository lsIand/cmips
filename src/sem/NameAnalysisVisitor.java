package sem;

import ast.*;

public class NameAnalysisVisitor extends BaseSemanticVisitor<Void> {

	Scope scope;
	public NameAnalysisVisitor(Scope c) {
		this.scope = c;
		
	}


	@Override
	public Void visitVarDecl(VarDecl vd) {
		
		Symbol s = scope.lookupCurrent(vd.varName);
		
		if (s != null) {
			error("Variable already declared in scope.");
		}
		else {
			scope.put(new VarSymbol(vd));
		}
		
		return null;
	}
    @Override
    public Void visitBlock(Block b) {

        for (VarDecl d : b.varDecls) {

        	d.accept(this);

        }
        
        for (Stmt d : b.statements) {
        	
        	if (d instanceof Block) {
        		this.scope = new Scope(this.scope);
        		d.accept(this);
                this.scope = this.scope.getOuter();
        	}
        	else {
                d.accept(this);
        	}

        }
        
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {

        fd.type.accept(this);
        
               
		
		this.scope = new Scope(this.scope);

        for (VarDecl vd : fd.params) {
        	
    		vd.accept(this);

        }
        
        fd.block.accept(this);

        return null;
    }

    @Override
    public Void visitProgram(Program p) {

    	
    	for (StructType st : p.structTypes) {
    		Symbol s = scope.lookupCurrent(st.name);
    		
    		
    		if (s != null) {
    			error("Struct " + st.name + " illegaly shadows a function, struct, or variable.");
    		}
    		else {
    			scope.put(new VarSymbol(new VarDecl(st, st.name)));
    		}
    	}
    	
        for (StructType st : p.structTypes) {

        	this.scope = new Scope(this.scope);
        	
            st.accept(this);
            
            this.scope = this.scope.getOuter();
        }
        for (VarDecl vd : p.varDecls) {

            vd.accept(this);
        }
        
        for (FunDecl fd : p.funDecls) {
        	
    		Symbol s = scope.lookupCurrent(fd.name);
    		
    		
    		if (s != null) {
    			error("Function " + fd.name + " illegaly shadows a variable or function.");
    		}
    		else {
    			scope.put(new ProcSymbol(fd));
    		}
        	
        }
        
        for (FunDecl fd : p.funDecls) {
        	
        	

            fd.accept(this);
            
            this.scope = this.scope.getOuter();
        }

        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
    	
    	if (this.scope.lookup(v.name) == null) {
    		
    		error(v.name + " is not a declared variable.");
    		
    	}
    	else {
    		
    		v.vd = this.scope.lookup(v.name).vd;
    		
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

		return null;
	}

	@Override
	public Void visitCharLiteral(CharLiteral c) {

		return null;
	}

	@Override
	public Void visitStringLiteral(StringLiteral s) {

		return null;
	}

	@Override
	public Void visitBinOp(BinOp bo) {

		bo.lhs.accept(this);

		bo.rhs.accept(this);

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
		
		
    	if (this.scope.lookup(fc.name) == null) {
    		
    		error(fc.name + " is not a declared function.");
    		
    	}
    	else {
    		
    		fc.decl = this.scope.lookup(fc.name).fd;
    		
    	}
		
        for (Expr e : fc.expressions) {

            e.accept(this);
        }
		
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aa) {
		
		aa.exp1.accept(this);

		aa.exp2.accept(this);

		
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fa) {
		
		fa.exp.accept(this);
		
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

		return null;
	}

	@Override
	public Void visitTypeCastExpr(TypeCastExpr so) {
		
		so.ty.accept(this);

		so.exp.accept(this);
	
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

		
		return null;
	}

	@Override
	public Void visitIf(If i) {

		i.e.accept(this);

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
		

		a.lhs.accept(this);

		a.rhs.accept(this);

		
		return null;
	}

}
