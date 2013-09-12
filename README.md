ExpressionFactory
=================

Object factory for any Resource that can be computed from an EL expression. 

This factory can be configured in a <DefaultContext> or <Context> element in your conf/server.xml configuration file. Some examples of factory configuration: 

Usage
-----

    <Resource
      name="/url/SomeService"
      factory="org.apache.naming.factory.ExpressionFactory"
      importClass="java.net.URI"
      expression="URI('http://some.service.url').toURL()"
    />

    <Resource
      name="/cache/CacheManager"
      factory="org.apache.naming.factory.ExpressionFactory"
      importClass="net.sf.ehcache.CacheManager,java.net.URI"
      importStatic="net.sf.ehcache.CacheManager.newInstance"
      expression="CacheManager.newInstance(URI('file:${catalina.base}/conf/ehcache.xml').toURL())"
      closeMethod="shutdown"
      singleton="true"
    />
