package ast;

import a5.Critter;

public class Arithmetic extends Binary<Expression, Arithmetic.Op> implements
		Expression {

	public enum Op {
		PLUS {
			@Override
			public String toString() {
				return "+";
			}
		},
		MINUS {
			@Override
			public String toString() {
				return "-";
			}
		},
		MUL {
			@Override
			public String toString() {
				return "*";
			}
		},
		DIV {
			@Override
			public String toString() {
				return "/";
			}
		},
		MOD {
			@Override
			public String toString() {
				return "mod";
			}
		};
	}

	public Arithmetic(Expression left, Op op, Expression right) {
		super(left, op, right);
	}
            
    @Override
    public int eval(Critter c) {
    	int l = left.eval(c);
        int r = right.eval(c);
        switch (op) {
        case PLUS: return l+r;
        case MINUS: return l-r;
        case MUL: return l*r;
        case DIV: return l/r;
        case MOD: return l%r;
        default: return 0;
        }
    }
            
	@Override
	public boolean handleRemove() {
		Expression randomArg = Mutation.randomArg(this);
		Mutation.replaceExpr(this, randomArg);
		return true;
	}

	@Override
	public Arithmetic dup(RichNode dupParent) {
		Arithmetic dup = new Arithmetic(null, op, null);
		dup.setParent(dupParent);
		dup.left = left.dup(dup);
		dup.right = right.dup(dup);
		return dup;
	}

	@Override
	String leftGroup() {
		return "(";
	}

	@Override
	String rightGroup() {
		return ")";
	}
	
	public boolean equals(Arithmetic a){
		return left.equals(a.left) && op.equals(a.op) && right.equals(a.right);
	}

	@Override
	public boolean equals(Expression e) {
		if (e instanceof Arithmetic){
			Arithmetic temp = dup(e);
			return equals(temp);
		} else {
			return false;
		}
	}
}
