package ast;

import util.*;

import java.util.*;

/**
 * A representation of a critter program.
 */
public class Program extends AbstractNode {

	public List<Rule> rules;

	public Program() {
		rules = new ArrayList<Rule>();
	}

	public Program(List<Rule> rules) {
		this.rules = rules;
	}

	@Override
	public int size() {
		int size = 0;
		for (Rule rule : rules) {
			size += rule.size();
		}
		return size + 1;
	}

	@Override
	public boolean mutate(Mutation.Type type) {
		switch (type) {
		case COPY:
		case COPY_TREE:
		case CREATE_PARENT:
		case REMOVE:
			return false;
		case SWAP:
			return Mutation.swap2(rules);
		case REPLICATE:
			if (rules.isEmpty()) {
				return false;
			}
			Rule replicate = Utils.randomlySelect(rules);
			Rule replicated = replicate.dup(this);
			rules.add(replicated);
			return true;
		default:
			throw new AssertionError();
		}
	}

	@Override
	public void prettyPrint(StringBuffer sb) {
		if (!rules.isEmpty()) {
			for (Rule rule : rules) {
				rule.prettyPrint(sb);
				sb.append("\n");
			}
			int len = sb.length();
			sb.delete(len - 2, len);
		}
	}

	@Override
	public void query(F<Node, Boolean> p, List<Node> acc) {
		if (p.f(this)) {
			acc.add(this);
		}
		for (Rule rule : rules) {
			rule.query(p, acc);
		}
	}

	@Override
	public Program dup(RichNode dupParent) {
		Program dup = new Program();
		for (Rule rule : rules) {
			dup.rules.add((Rule) rule.dup(dup));
		}
		return dup;
	}

	public Program init() {
		return dup(null);
	}
	
	public boolean equals(Program p){
		if (rules.size() != p.rules.size()) return false;
		for (int i = 0; i < rules.size() && i < p.rules.size(); i++){
			if (!rules.get(i).equals(p.rules.get(i))){
				return false;
			}
		}
		return true;
	}

}
