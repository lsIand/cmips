package ast;

public class VarExpr extends Expr {

    public final String name;
    public VarDecl vd; // to be filled in by the name analyser
    
    public VarExpr(String name){
		this.name = name;
		vd = null;
    }

    public <T> T accept(ASTVisitor<T> v) {
	    return v.visitVarExpr(this);
    }

	@Override
	public int eval() {
		
		return 0;
	}
}
