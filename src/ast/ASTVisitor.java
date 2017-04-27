package ast;

public interface ASTVisitor<T> {
	
	//VISIT TYPES
    public T visitBaseType(BaseType bt);
    public T visitArrayType(ArrayType at);
    public T visitPointerType(PointerType pt);
    
    
    //-----
    public T visitStructType(StructType st);
    public T visitBlock(Block b);
    public T visitFunDecl(FunDecl p);
    public T visitProgram(Program p);
    public T visitVarDecl(VarDecl vd);
    
    
    //STATEMENTS
    public T visitExprStmt(ExprStmt es);
    public T visitWhile(While w);
    public T visitIf(If i);
    public T visitReturn(Return r);
    public T visitAssign(Assign a);
    
    
    //(EXPRESSIONS) LITERALS
    public T visitIntLiteral(IntLiteral il);
    public T visitCharLiteral(CharLiteral c);
    public T visitStringLiteral(StringLiteral s);
    
    
    //EXPRESSIONS
    public T visitBinOp(BinOp bo);
    public T visitFunCallExpr(FunCallExpr fc);
    public T visitArrayAccessExpr(ArrayAccessExpr aa);
    public T visitFieldAccessExpr(FieldAccessExpr fa);
    public T visitValueAtExpr(ValueAtExpr va);
    public T visitSizeOfExpr(SizeOfExpr so);
    public T visitTypeCastExpr(TypeCastExpr tc);
    
    public T visitVarExpr(VarExpr v);
    
    
}
