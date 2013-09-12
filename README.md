# ExpressionFactory

## Introduction

Implementation of a `javax.naming.spi.ObjectFactory` for any Resource that can be computed from an EL 3.0 expression (JSR-341). 

This factory can be configured in a `<DefaultContext>` or `<Context>` element in your conf/server.xml configuration file.

## Remark

Try to avoid overloaded methods/constructors as much as possible in EL 3.0 expression as the `ELProcessor` will use the first one it will find by reflection. 

## Usage

    <Resource
      name="some/jndi/name"
      type="fully.qualified.class.name"
      factory="org.apache.naming.factory.ExpressionFactory"
      importClass="list.of.classes, which.will.be.used, in.the.expression"
      importPackage="list.of.packages, which.will.be.used, in.the.expression"
      importStatic="list.of.static.methods, which.will.be.used, in.the.expression"
      expression="your EL 3.0 expression"
    />
    
### Properties

| Name | Presence | Description |
|------|----------|-------------|
| name | required | The local JNDI name where the result will be bound |
| factory required | Must be `org.apache.naming.factory.ExpressionFactory` |
| expression | required | The EL 3.0 expression whose result will be returned by the `ObjectFactory.getInstance()` method |
| type | required? | The result type of the EL expression |
| importClass | optional | Which classes should be imported to the `ELProcessor`. Use FQCN separated by spaces or commas |
| importPackage | optional | Which packages should be imported to the `ELProcessor`. Use FQPN separated by spaces or commas |
| importStatic | optional | Which static methods should be imported to the `ELProcessor`. Use FQMN separated by spaces or commas |

## Examples

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
    
### Creating an EHCache CacheManager Resource with a configuration file relative to $CATALINA_BASE

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
