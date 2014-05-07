package ast;

import a5.Critter;

/**
 * A representation of a binary Boolean condition: 'and' or 'or'
 */
public class Logical extends Binary<Condition, Logical.Op> implements Condition {

	public enum Op {
		OR {
			@Override
			public String toString() {
				return "or";
			}
		},
		AND {
			@Override
			public String toString() {
				return "and";
			}
		};
	}

	public Logical(Condition left, Op op, Condition right) {
		super(left, op, right);
	}
    
    @Override
	public boolean eval(Critter c) {
    	boolean l = left.eval(c);
    	boolean r = right.eval(c);
    	switch (op){
    	case OR:
    		if (l || r) return true;
    		return false;
    	case AND:
    		if (l && r) return true;
    		return false;
    	default:
    		return false;	
    	}
	}
    
    
	@Override
	public boolean handleRemove() {
		Condition randomArg = Mutation.randomArg(this);
		Mutation.replaceCond(this, randomArg);
		return true;
	}

	@Override
	public Logical dup(RichNode dupParent) {
		Logical dup = new Logical(null, op, null);
		dup.setParent(dupParent);
		dup.left = left.dup(dup);
		dup.right = right.dup(dup);
		return dup;
	}

	@Override
	String leftGroup() {
		return "{";
	}

	@Override
	String rightGroup() {
		return "}";
	}
	
	public boolean equals(Logical l){
		return left.equals(l.left) && op.equals(l.op) && right.equals(l.right); 
	}

	@Override
	public boolean equals(Condition c) {
		if (c instanceof Logical){
			Logical temp = dup(c);
			return equals(temp);
		} else {
			return false;
		}
	}
}
