package ast;

public class ArrayType implements Type {
    
	public Type arrayType;
	public int arraySize;

	public ArrayType(Type t, int i) {
		
		this.arrayType = t;
		this.arraySize = i;
		
	}
	
	public String toString() {
		
		return ("arrayholder");
		
	}
	
    public <T> T accept(ASTVisitor<T> v) {
        return v.visitArrayType(this);
    }

    public boolean numEquals(ArrayType t) {
    	
    	return (t.arraySize == this.arraySize);
    	
    }
    
	@Override
	public boolean equals1(Type t) {				
		
		if (t instanceof ArrayType) {
			ArrayType s = (ArrayType) t;

			return (this.arrayType.equals1(s.arrayType) && s.arraySize == this.arraySize);
		
		}
		return false;
	}
}
