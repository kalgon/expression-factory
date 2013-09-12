# ExpressionFactory

## Introduction

Implementation of a javax.naming.spi.ObjectFactory for any Resource that can be computed from an EL 3.0 expression. 

This factory can be configured in a `<DefaultContext>` or `<Context>` element in your conf/server.xml configuration file.

## Remark

Try to avoid overloaded methods/constructors as much as possible in EL 3.0 expression as the ELProcessor will use the first one it will find. 

## Usage

### Creating an URL resource

In web.xml:

    <resource-ref>
      <res-ref-name>url/SomeService</res-ref-name>
      <res-type>java.net.URL</res-type>
      <res-auth>Container</res-auth>
      <res-sharing-scope>Shareable</res-sharing-scope>
	</resource-ref>

In context.xml or server.xml:

    <Resource
      name="url/SomeService"
      type="java.net.URL"
      factory="org.apache.naming.factory.ExpressionFactory"
      importClass="java.net.URI"
      expression="URI('http://some.service.url').toURL()"
    />
    
### Creating an EHCache CacheManager with a configuration relative to $CATALINA_BASE

In web.xml:

    <resource-ref>
      <res-ref-name>cache/CacheManager</res-ref-name>
      <res-type>net.sf.ehcache.CacheManager</res-type>
      <res-auth>Container</res-auth>
      <res-sharing-scope>Shareable</res-sharing-scope>
	</resource-ref>

In context.xml or server.xml:

    <Resource
      name="cache/CacheManager"
      type="net.sf.ehcache.CacheManager"
      factory="org.apache.naming.factory.ExpressionFactory"
      importClass="net.sf.ehcache.CacheManager,java.net.URI"
      importStatic="net.sf.ehcache.CacheManager.newInstance"
      expression="CacheManager.newInstance(URI('file:${catalina.base}/conf/ehcache.xml').toURL())"
      closeMethod="shutdown"
      singleton="true"
    />
