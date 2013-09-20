/*
 * #%L
 * expression-factory
 * %%
 * Copyright (C) 2013 Apache Software Foundation
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.apache.naming.factory;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.el.ELProcessor;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;

import org.junit.Assert;
import org.junit.Test;

public class ExpressionFactoryTest {

  private final ObjectFactory factory = new ExpressionFactory();

  @Test
  public void importClassShouldWork() throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    map.put("expression", "URI('file:./').toURL()");
    map.put("importClass", "java.net.URI");
    URL url = (URL) invokeFactory(map);
    Assert.assertEquals(new URI("file:./").toURL(), url);
  }

  @Test
  public void importMultipleClassesShouldWork() throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    map.put("expression", "File(URI('file:/test.txt'))");
    map.put("importClass", "java.net.URI,java.io.File");
    Assert.assertTrue(invokeFactory(map) instanceof File);
  }

  @Test
  public void importPackageShouldWork() throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    map.put("expression", "URI('file:./').toURL()");
    map.put("importPackage", "java.net");
    URL url = (URL) invokeFactory(map);
    Assert.assertEquals(new URI("file:./").toURL(), url);
  }

  @Test
  public void importStaticShouldWork() throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    map.put("expression", "randomUUID()");
    map.put("importStatic", "java.util.UUID.randomUUID");
    Assert.assertTrue(invokeFactory(map) instanceof UUID);
  }

  @Test(expected = IllegalStateException.class)
  public void missingExpressionShouldFail() throws Exception {
    invokeFactory(Collections.<String, String> emptyMap());
  }

  @Test
  public void multipleExpressionsShouldWork() throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    map.put("expression", "f='file:./';u=URI(f);u.toURL()");
    map.put("importClass", "java.net.URI");
    invokeFactory(map);
  }

  @Test
  public void overloadedConstructorsShouldWork() throws Exception {
    ELProcessor processor = new ELProcessor();
    processor.getELManager().importClass("java.io.File");
    processor.getELManager().importClass("java.net.URI");
    File file = (File) processor.eval("File(URI('file:/'));File('./')");
    Assert.assertEquals(new File("./").getCanonicalPath(),
        file.getCanonicalPath());
  }

  @Test
  public void overloadedMethodsShouldWork() throws Exception {
    ELProcessor processor = new ELProcessor();
    processor.getELManager().importClass("java.io.File");
    String result = (String) processor
        .eval("StringBuilder().append('a').append(42).toString()");
    Assert.assertEquals(result, "a42");
  }

  private Object invokeFactory(Map<String, String> refAddrs) throws Exception {
    Reference reference = new Reference(null);
    for (Map.Entry<String, String> entry : refAddrs.entrySet()) {
      reference.add(new StringRefAddr(entry.getKey(), entry.getValue()));
    }
    return this.factory.getObjectInstance(reference, null, null, null);
  }
}
