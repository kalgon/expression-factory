package org.apache.naming.factory;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionFactoryTest {
	
	private final ObjectFactory factory = new ExpressionFactory();

	@Test
	public void importClassShouldWork() throws Exception {
		Reference reference = new Reference(null);
		reference.add(new StringRefAddr("expression", "URI('file:./').toURL()"));
		reference.add(new StringRefAddr("importClass", "java.net.URI"));
		URL url = (URL) this.factory.getObjectInstance(reference, null, null, null);
		Assert.assertEquals(new URI("file:./").toURL(), url);
	}
	
	@Test
	public void importMultipleClassesShouldWork() throws Exception {
		Reference reference = new Reference(null);
		reference.add(new StringRefAddr("expression", "File(URI('file:/test.txt'))"));
		reference.add(new StringRefAddr("importClass", "java.net.URI,java.io.File"));
		Assert.assertTrue(this.factory.getObjectInstance(reference, null, null, null) instanceof File);
	}
	
	@Test
	public void importPackageShouldWork() throws Exception {
		Reference reference = new Reference(null);
		reference.add(new StringRefAddr("expression", "URI('file:./').toURL()"));
		reference.add(new StringRefAddr("importPackage", "java.net"));
		URL url = (URL) this.factory.getObjectInstance(reference, null, null, null);
		Assert.assertEquals(new URI("file:./").toURL(), url);
	}
	
	@Test
	public void importStaticShouldWork() throws Exception {
		Reference reference = new Reference(null);
		reference.add(new StringRefAddr("expression", "randomUUID()"));
		reference.add(new StringRefAddr("importStatic", "java.util.UUID.randomUUID"));
		Assert.assertTrue(this.factory.getObjectInstance(reference, null, null, null) instanceof UUID);
	}
	
	@Test(expected = IllegalStateException.class)
	public void missingExpressionShouldFail() throws Exception {
		this.factory.getObjectInstance(new Reference(null), null, null, null);
	}
	
	@Test
	public void multipleExpressionsShouldWork() throws Exception {
		Reference reference = new Reference(null);
		reference.add(new StringRefAddr("expression", "f='file:./';u=URI(f);u.toURL()"));
		reference.add(new StringRefAddr("importClass", "java.net.URI"));
		this.factory.getObjectInstance(reference, null, null, null);
	}
}
