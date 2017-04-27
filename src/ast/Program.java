package ast;

import java.util.List;

public class Program implements ASTNode {

    public final List<StructType> structTypes;
    public final List<VarDecl> varDecls;
    public final List<FunDecl> funDecls;

    public Program(List<StructType> structTypes, List<VarDecl> varDecls, List<FunDecl> funDecls) {
        this.structTypes = structTypes;
	    this.varDecls = varDecls;
	    this.funDecls = funDecls;
    }

    public <T> T accept(ASTVisitor<T> v) {
	return v.visitProgram(this);
    }
}
