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

import java.util.Hashtable;

import javax.el.ELProcessor;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

/**
 * <p>
 * Object factory for any Resource that can be computed from an EL expression.
 * </p>
 * 
 * <p>
 * This factory can be configured in a &lt;DefaultContext&gt; or &lt;Context&gt;
 * element in your conf/server.xml configuration file. Some examples of factory
 * configuration:
 * </p>
 * 
 * <pre>
 * &lt;Resource
 *   name=&quot;/url/SomeService&quot;
 *   factory=&quot;org.apache.naming.factory.ExpressionFactory&quot;
 *   importClass=&quot;java.net.URI&quot;
 *   expression=&quot;URI('http://some.service.url').toURL()&quot;
 * /&gt;
 * </pre>
 * 
 * <pre>
 * &lt;Resource
 *   name=&quot;/cache/CacheManager&quot;
 *   factory=&quot;org.apache.naming.factory.ExpressionFactory&quot;
 *   importClass=&quot;net.sf.ehcache.CacheManager,java.net.URI&quot;
 *   importStatic=&quot;net.sf.ehcache.CacheManager.newInstance&quot;
 *   expression=&quot;CacheManager.newInstance(URI('file:${catalina.base}/conf/ehcache.xml').toURL())&quot;
 *   closeMethod=&quot;shutdown&quot;
 *   singleton=&quot;true&quot;
 * /&gt;
 * </pre>
 * 
 * @author Xavier Dury
 */
public class ExpressionFactory implements ObjectFactory {

  @Override
  public Object getObjectInstance(Object referenceObject, Name name,
      Context nameCtx, Hashtable<?, ?> environment) throws Exception {
    if (referenceObject instanceof Reference) {
      Reference reference = (Reference) referenceObject;
      RefAddr expressionRefAddr = reference.get("expression");
      if (expressionRefAddr == null) {
        throw new IllegalStateException("Expression parameter is required");
      }
      ELProcessor processor = new ELProcessor();
      for (String importClass : split(reference, "importClass")) {
        processor.getELManager().importClass(importClass);
      }
      for (String importPackage : split(reference, "importPackage")) {
        processor.getELManager().importPackage(importPackage);
      }
      for (String importStatic : split(reference, "importStatic")) {
        processor.getELManager().importStatic(importStatic);
      }
      processor.defineBean("name", name);
      processor.defineBean("reference", reference);
      processor.defineBean("nameContext", nameCtx);
      processor.defineBean("environment", environment);
      return processor.eval(expressionRefAddr.getContent().toString());
    }
    return null;
  }

  private String[] split(Reference reference, String name) {
    RefAddr refAddr = reference.get(name);
    return refAddr == null ? new String[0] : refAddr.getContent().toString()
        .split("[^\\w\\.\\$]+");
  }
}
