package ast;

import a5.Critter;

public class UnaryExpr extends Unary<UnaryExpr.Op> implements Expression {

	public enum Op {
		MEM, NEARBY, AHEAD, RANDOM;

		@Override
		public String toString() {
			return name().toLowerCase();
		}
	}

	public UnaryExpr(Op op, Expression expr) {
		super(op, expr);
	}

	@Override
	public int eval(Critter c) {
		switch (op){
		case MEM: 
			if (expr.eval(c) < 0 || expr.eval(c) >= c.mem.length){
				return 0;
			}
			return c.mem[expr.eval(c)];
		case NEARBY: return c.nearby(expr.eval(c));
		case AHEAD: return c.ahead(expr.eval(c));
		case RANDOM: return (int)(Math.random()*(expr.eval(c))); 
		default: return 0;
		}
	}

	@Override
	public boolean mutate(Mutation.Type type) {
		switch (type) {
		case SWAP:
		case REPLICATE:
			return false;
		case REMOVE:
			Mutation.replaceExpr(this, expr);
			return true;
		case COPY_TREE:
			return Mutation.copyExprTree(this);
		case COPY:
			return unaryCopy();
		case CREATE_PARENT:
			Mutation.createExprParent(this);
			return true;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public UnaryExpr dup(RichNode dupParent) {
		UnaryExpr dup = new UnaryExpr(op, null);
		dup.setParent(dupParent);
		dup.expr = expr.dup(dup);
		return dup;
	}

	public boolean equals(UnaryExpr u){
		return op == u.op && expr.equals(u.expr);
	}
	
	public boolean equals(Expression e){
		if (e instanceof UnaryExpr){
			UnaryExpr temp = dup(e);
			return equals(temp);
		} else {
			return false;
		}
	}
}
