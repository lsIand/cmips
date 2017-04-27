package ast;

public enum BaseType implements Type {
    INT, CHAR, VOID;

    public <T> T accept(ASTVisitor<T> v) {
        return v.visitBaseType(this);
    }

	@Override
	public boolean equals1(Type t) {
		
		if (t instanceof BaseType) {
			return this.equals(t);
		}
		return false;
		
	}
	
}
