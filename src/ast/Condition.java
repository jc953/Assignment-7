package ast;

import a5.Critter;

/**
 * An interface representing a Boolean condition in a critter program.
 */
public interface Condition extends RichNode {

	/**
	 * Evaluates the Boolean value of this condition.
	 * 
	 * @param c The critter to be evaluated for
	 * @return The Boolean value of this condition
	 */
	boolean eval(Critter c);

	Condition dup(RichNode dupParent);
	
	boolean equals(Condition c);

}
