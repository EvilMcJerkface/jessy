/**
 * 
 */
package fr.inria.jessy.transaction;

import fr.inria.jessy.LocalJessy;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Masoud Saeida Ardekani
 *
 */
public class LocalJessy_NMSI {

	LocalJessy jessy;
	SampleTransaction1 st;
	
	public static Test suite() {
		TestSuite suite = new TestSuite(LocalJessy_NMSI.class.getName());
		//$JUnit-BEGIN$

		//$JUnit-END$
		return suite;
	}

}