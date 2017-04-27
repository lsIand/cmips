package ast;

import java.io.PrintWriter;

public class ASTPrinter implements ASTVisitor<Void> {

    private PrintWriter writer;

    public ASTPrinter(PrintWriter writer) {
            this.writer = writer;
    }

    @Override
    public Void visitBlock(Block b) {
        writer.print("Block(");
        
        String delimiter = "";
        String Dlimit = "";
        if (!b.statements.isEmpty() && !b.varDecls.isEmpty()) {
        	Dlimit = ", ";
        }
        for (VarDecl d : b.varDecls) {
            writer.print(delimiter);
            d.accept(this);
            delimiter = ", ";
        }
        
        delimiter = "";

        writer.print(Dlimit);
        
        for (Stmt d : b.statements) {
            writer.print(delimiter);
            d.accept(this);
            delimiter = ", ";
        }
        
        writer.print(")");
        return null;
    }

    @Override
    public Void visitFunDecl(FunDecl fd) {
        writer.print("FunDecl(");
        fd.type.accept(this);

        writer.print(", "+fd.name+", ");

        for (VarDecl vd : fd.params) {
            vd.accept(this);
            writer.print(", ");
        }
        fd.block.accept(this);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitProgram(Program p) {
        writer.print("Program(");
        String delimiter = "";
        for (StructType st : p.structTypes) {
            writer.print(delimiter);
            delimiter = ", ";
            st.accept(this);
        }
        for (VarDecl vd : p.varDecls) {
            writer.print(delimiter);
            delimiter = ", ";
            vd.accept(this);
        }
        for (FunDecl fd : p.funDecls) {
            writer.print(delimiter);
            delimiter = ", ";
            fd.accept(this);
        }
        writer.print(")");
	    writer.flush();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl vd){
        writer.print("VarDecl(");

        
        vd.type.accept(this);
        writer.print(", ");
        writer.print(vd.varName);
        

        writer.print(")");
        return null;
    }

    @Override
    public Void visitVarExpr(VarExpr v) {
        writer.print("VarExpr(");
        writer.print(v.name);
        writer.print(")");
        return null;
    }

    @Override
    public Void visitBaseType(BaseType bt) {
    	writer.print(bt);
    	return null;
    }

    @Override
    public Void visitStructType(StructType st) {
    	writer.print("StructType(");
    	writer.print(st.name);
        String delimiter = ", ";
        for (VarDecl d : st.varDecls) {
            writer.print(delimiter);
            d.accept(this);
        }
    	writer.print(")");
    	
        return null;
    }

	@Override
	public Void visitIntLiteral(IntLiteral il) {
		writer.print("IntLiteral(");
		writer.print(il.i);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitCharLiteral(CharLiteral c) {
		writer.print("CharLiteral(");
		writer.println(c.c);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitStringLiteral(StringLiteral s) {
		writer.print("StringLiteral(");
		writer.println(s.s);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitBinOp(BinOp bo) {
		writer.print("BinOp(");
		bo.lhs.accept(this);
		writer.print(", ");
		writer.print(bo.op);
		writer.print(", ");
		bo.rhs.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitArrayType(ArrayType at) {
		writer.print("ArrayType(");
		at.arrayType.accept(this);
		writer.print(", ");
		writer.print(at.arraySize);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitPointerType(PointerType pt) {
		writer.print("PointerType(");
		pt.pointsTo.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitFunCallExpr(FunCallExpr fc) {
		writer.print("FunCallExpr(");
		writer.print(fc.name);

		String delimiter = ", ";
		
        for (Expr e : fc.expressions) {
            writer.print(delimiter);
            e.accept(this);
        }
		
		writer.print(")");
		return null;
	}

	@Override
	public Void visitArrayAccessExpr(ArrayAccessExpr aa) {
		
		writer.print("ArrayAccessExpr(");
		aa.exp1.accept(this);
		writer.print(", ");
		aa.exp2.accept(this);
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitFieldAccessExpr(FieldAccessExpr fa) {
		
		writer.print("FieldAccessExpr(");
		fa.exp.accept(this);
		writer.print(", ");
		writer.println(fa.name);
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitValueAtExpr(ValueAtExpr va) {
		writer.print("ValueAtExpr(");
		va.exp.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitSizeOfExpr(SizeOfExpr so) {
		writer.print("SizeOfExpr(");
		so.ty.accept(this);
		writer.print(")");
		return null;
	}

	@Override
	public Void visitTypeCastExpr(TypeCastExpr so) {
		
		writer.print("TypeCastExpr(");
		so.ty.accept(this);
		writer.print(", ");
		so.exp.accept(this);
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitExprStmt(ExprStmt es) {
		
		writer.print("ExprStmt(");
		
		es.e.accept(this);
		
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitWhile(While w) {
		
		writer.print("While(");
		w.e.accept(this);
		writer.print(", ");
		w.t.accept(this);
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitIf(If i) {
		writer.print("If(");
		i.e.accept(this);
		writer.print(", ");
		i.tif.accept(this);
		
		if (i.telse != null) {
			
			writer.print(", ");
			i.telse.accept(this);
			
		}
		
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitReturn(Return r) {
		
		writer.print("Return(");

		if (r.e != null) {
			
			r.e.accept(this);
			
		}
		
		writer.print(")");
		
		return null;
	}

	@Override
	public Void visitAssign(Assign a) {
		
		writer.print("Assign(");
		a.lhs.accept(this);
		writer.print(", ");
		a.rhs.accept(this);
		writer.print(")");
		
		return null;
	}
    
}
