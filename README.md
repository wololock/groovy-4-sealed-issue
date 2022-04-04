# Groovy 4 Sealed classes in package problem in Java 17

This repository contians an exemplary Groovy scripts that document the problem with packaged sealed classes in Java 17.
This problem does not exist when running on Java 8 or Java 11.

## What is the problem?

Here is an exemplary Groovy script that uses sealed classes introduced in Groovy 4.0 release.

```groovy
import groovy.transform.Canonical

sealed interface Tree<T> {}

@Singleton final class Empty implements Tree {
    String toString() { 'Empty' }
}

@Canonical final class Node<T> implements Tree<T> {
    T value
    Tree<T> left, right

    String toString() {
        "Node($value, $left, $right)"
    }
}

Tree<Integer> tree = new Node<Integer>(42, new Node<>(0, Empty.instance, Empty.instance), Empty.instance)
assert tree.toString() == 'Node(42, Node(0, Empty, Empty), Empty)'

println "IT WORKS!"
```

As long as this script is in the default package, all works fine.

```
$ ./gradlew -q runScript -PmainClass=working 
> Running working.groovy script...
IT WORKS!
```

But if the same script is added to the `example` (or any other) package, the execution fails with the following stack trace:

```
$ ./gradlew -q runScript -PmainClass=example.notworking
> Running example.notworking.groovy script...
Exception in thread "main" java.lang.ClassFormatError: Illegal class name "example.Empty" in class file example/Tree
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at example.notworking.run(notworking.groovy:20)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:568)
        at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:343)
        at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:328)
        at groovy.lang.MetaClassImpl.doInvokeMethod(MetaClassImpl.java:1369)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1103)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1009)
        at org.codehaus.groovy.runtime.InvokerHelper.invokePogoMethod(InvokerHelper.java:610)
        at org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(InvokerHelper.java:593)
        at org.codehaus.groovy.runtime.InvokerHelper.runScript(InvokerHelper.java:413)
        at org.codehaus.groovy.runtime.InvokerHelper$runScript.callStatic(Unknown Source)
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallStatic(CallSiteArray.java:54)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:217)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:240)
        at example.notworking.main(notworking.groovy)

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':runScript'.
> Process 'command '/home/wololock/.sdkman/candidates/java/17.0.2.8.1-amzn/bin/java'' finished with non-zero exit value 1

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 898ms
```

The same problem exists when the `Tree<T>`, `Empty`, and `Node<T>` classes are defined in separate groovy files:

```
/gradlew -q runScript -PmainClass=example.more.run  
> Running example.more.run.groovy script...
Exception in thread "main" java.lang.ClassFormatError: Illegal class name "example.more.Empty" in class file example/more/Tree
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at example.more.run.run(run.groovy:3)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:568)
        at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:343)
        at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:328)
        at groovy.lang.MetaClassImpl.doInvokeMethod(MetaClassImpl.java:1369)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1103)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1009)
        at org.codehaus.groovy.runtime.InvokerHelper.invokePogoMethod(InvokerHelper.java:610)
        at org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(InvokerHelper.java:593)
        at org.codehaus.groovy.runtime.InvokerHelper.runScript(InvokerHelper.java:413)
        at org.codehaus.groovy.runtime.InvokerHelper$runScript.callStatic(Unknown Source)
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallStatic(CallSiteArray.java:54)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:217)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:240)
        at example.more.run.main(run.groovy)

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':runScript'.
> Process 'command '/home/wololock/.sdkman/candidates/java/17.0.2.8.1-amzn/bin/java'' finished with non-zero exit value 1

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 900ms
```

## How to run examples?

There are three examples in the repository, and they can be run with the following commands:

```
./gradlew -q runScript -PmainClass=working
```

```
./gradlew -q runScript -PmainClass=example.notworking
```

```
./gradlew -q runScript -PmainClass=example.more.run
```

## Run all examples

There is also additional `./runall.sh` script that runs them all and displays the information about the used Java version.

For Java 1.8 it produces the output like this:

```
$ ./runall.sh

Your current Java version is
-------------------------------------------------
openjdk version "1.8.0_292"
OpenJDK Runtime Environment Corretto-8.292.10.1 (build 1.8.0_292-b10)
OpenJDK 64-Bit Server VM Corretto-8.292.10.1 (build 25.292-b10, mixed mode)

> Running working.groovy script...
IT WORKS!
> Running example.notworking.groovy script...
IT WORKS!
> Running example.more.run.groovy script...
IT WORKS!
```

For Java 11 it produces the output like this:

```
$ ./runall.sh                  

Your current Java version is
-------------------------------------------------
openjdk version "11.0.10" 2021-01-19 LTS
OpenJDK Runtime Environment Corretto-11.0.10.9.1 (build 11.0.10+9-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.10.9.1 (build 11.0.10+9-LTS, mixed mode)

> Running working.groovy script...
IT WORKS!
> Running example.notworking.groovy script...
IT WORKS!
> Running example.more.run.groovy script...
IT WORKS!
```

For Java 17 it produces the output like this:

```
/runall.sh                  

Your current Java version is
-------------------------------------------------
openjdk version "17.0.2" 2022-01-18 LTS
OpenJDK Runtime Environment Corretto-17.0.2.8.1 (build 17.0.2+8-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.2.8.1 (build 17.0.2+8-LTS, mixed mode, sharing)

> Running working.groovy script...
IT WORKS!
> Running example.notworking.groovy script...
Exception in thread "main" java.lang.ClassFormatError: Illegal class name "example.Empty" in class file example/Tree
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at example.notworking.run(notworking.groovy:20)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:568)
        at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:343)
        at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:328)
        at groovy.lang.MetaClassImpl.doInvokeMethod(MetaClassImpl.java:1369)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1103)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1009)
        at org.codehaus.groovy.runtime.InvokerHelper.invokePogoMethod(InvokerHelper.java:610)
        at org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(InvokerHelper.java:593)
        at org.codehaus.groovy.runtime.InvokerHelper.runScript(InvokerHelper.java:413)
        at org.codehaus.groovy.runtime.InvokerHelper$runScript.callStatic(Unknown Source)
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallStatic(CallSiteArray.java:54)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:217)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:240)
        at example.notworking.main(notworking.groovy)

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':runScript'.
> Process 'command '/home/wololock/.sdkman/candidates/java/17.0.2.8.1-amzn/bin/java'' finished with non-zero exit value 1

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 869ms
> Running example.more.run.groovy script...
Exception in thread "main" java.lang.ClassFormatError: Illegal class name "example.more.Empty" in class file example/more/Tree
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at java.base/java.lang.ClassLoader.defineClass1(Native Method)
        at java.base/java.lang.ClassLoader.defineClass(ClassLoader.java:1012)
        at java.base/java.security.SecureClassLoader.defineClass(SecureClassLoader.java:150)
        at java.base/jdk.internal.loader.BuiltinClassLoader.defineClass(BuiltinClassLoader.java:862)
        at java.base/jdk.internal.loader.BuiltinClassLoader.findClassOnClassPathOrNull(BuiltinClassLoader.java:760)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClassOrNull(BuiltinClassLoader.java:681)
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:639)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:520)
        at example.more.run.run(run.groovy:3)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:568)
        at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:343)
        at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:328)
        at groovy.lang.MetaClassImpl.doInvokeMethod(MetaClassImpl.java:1369)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1103)
        at groovy.lang.MetaClassImpl.invokeMethod(MetaClassImpl.java:1009)
        at org.codehaus.groovy.runtime.InvokerHelper.invokePogoMethod(InvokerHelper.java:610)
        at org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(InvokerHelper.java:593)
        at org.codehaus.groovy.runtime.InvokerHelper.runScript(InvokerHelper.java:413)
        at org.codehaus.groovy.runtime.InvokerHelper$runScript.callStatic(Unknown Source)
        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCallStatic(CallSiteArray.java:54)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:217)
        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:240)
        at example.more.run.main(run.groovy)

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':runScript'.
> Process 'command '/home/wololock/.sdkman/candidates/java/17.0.2.8.1-amzn/bin/java'' finished with non-zero exit value 1

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 834ms
```

## Java example

There is also a pure Java example that runs with no issues.

```
$ ./gradlew -q runJavaExample                 
Note: /home/wololock/workspace/groovy-4-sealed-issue/src/main/java/example/Main.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
Node(42, Node(0, Empty, Empty), Empty)

IT WORKS!
```