package ast;

public class PointerType implements Type {
    
	public Type pointsTo;	

	public PointerType(Type t) {
		
		this.pointsTo = t;
		
	}
	
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitPointerType(this);
    }

	@Override
	public boolean equals1(Type t) {
		
		if (t instanceof PointerType) {
			PointerType s = (PointerType) t;
			return (this.pointsTo.equals1(s.pointsTo));
		
		}
		return false;
	}
}
