package ast;

import a5.Critter;

public class Comparison extends Binary<Expression, Comparison.Op> implements
		Condition {

	public enum Op {
		LT {
			@Override
			public String toString() {
				return "<";
			}
		},
		LE {
			@Override
			public String toString() {
				return "<=";
			}
		},
		EQ {
			@Override
			public String toString() {
				return "=";
			}
		},
		GE {
			@Override
			public String toString() {
				return ">=";
			}
		},
		GT {
			@Override
			public String toString() {
				return ">";
			}
		},
		NE {
			@Override
			public String toString() {
				return "!=";
			}
		};
	}

	public Comparison(Expression left, Op op, Expression right) {
		super(left, op, right);
	}
            
    @Override
    public boolean eval(Critter c) {
        int l = left.eval(c);
        int r = right.eval(c);
        switch (op) {
        case LT: 
        	if (l < r) return true;
        	return false;
        case LE: 
        	if (l <= r) return true;
        	return false;
        case EQ: 
        	if (l == r) return true;
        	return false;
        case GE: 
        	if (l >= r) return true;
        	return false;
        case GT: 
        	if (l > r) return true;
        	return false;
        case NE: 
        	if (l != r) return true;
        	return false;
        default:
        	return false;
        }
    }

	@Override
	public boolean handleRemove() {
		return false;
	}

	@Override
	public Comparison dup(RichNode dupParent) {
		Comparison dup = new Comparison(null, op, null);
		dup.setParent(dupParent);
		dup.left = left.dup(dup);
		dup.right = right.dup(dup);
		return dup;
	}

	@Override
	String leftGroup() {
		return "";
	}

	@Override
	String rightGroup() {
		return "";
	}
	
	public boolean equals(Comparison c){
		return left.equals(c.left) && op.equals(c.op) && right.equals(c.right);
	}

	@Override
	public boolean equals(Condition c) {
		if (c instanceof Comparison){
			Comparison temp = dup(c);
			return equals(temp);
		} else {
			return false;
		}
	}

}
