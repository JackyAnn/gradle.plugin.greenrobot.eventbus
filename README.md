# gradle.plugin.greenrobot.eventbus
[![JitPack](https://jitpack.io/v/JackyAnn/gradle.plugin.greenrobot.eventbus.svg)](https://jitpack.io/#JackyAnn/gradle.plugin.greenrobot.eventbus)

gradle.plugin.greenrobot.eventbus is a gradle plugin for [EventBus](https://github.com/greenrobot/EventBus) when use proguard.
* is supports [EventBus](https://github.com/greenrobot/EventBus) version from 3.0 and above.
* is supports [com.android.tools.build:gradle](https://bintray.com/android/android-tools/com.android.tools.build.gradle) version from 1.5 and above.
* is supports android AnnotationProcessor
* is supports [android-apt](https://bitbucket.org/hvisser/android-apt)


## Process
*1. hook gradle proguard task.*

*2. parse mapping.txt.*

*3. analyse class file.*

*4. replace method name.*

## Integration

### How to use
*1. integration [EventBus](https://github.com/greenrobot/EventBus) and [EventBusAnnotationProcessor](https://github.com/greenrobot/EventBus/tree/master/EventBusAnnotationProcessor)*

*2. add the jitpack repository and plugin dependency to your project build file*
```gradle
buildscript {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath 'com.github.JackyAnn:gradle.plugin.greenrobot.eventbus:v1.0'
    }
}
```

*3. apply plugin to you module build file*
```gradle
apply plugin: 'com.tobelinker.greenrobot.eventbus'
```

## Sample
[gradle.plugin.greenrobot.eventbus.sample](https://github.com/JackyAnn/gradle.plugin.greenrobot.eventbus.sample)

* source code generate by [EventBusAnnotationProcessor](https://github.com/greenrobot/EventBus/tree/master/EventBusAnnotationProcessor) 

```java
package com.sample.index;

import org.greenrobot.eventbus.meta.SimpleSubscriberInfo;
import org.greenrobot.eventbus.meta.SubscriberMethodInfo;
import org.greenrobot.eventbus.meta.SubscriberInfo;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/** This class is generated by EventBus, do not edit. */
public class BusIndex implements SubscriberInfoIndex {
    private static final Map<Class<?>, SubscriberInfo> SUBSCRIBER_INDEX;

    static {
        SUBSCRIBER_INDEX = new HashMap<Class<?>, SubscriberInfo>();

        putIndex(new SimpleSubscriberInfo(com.tobelinker.greenrobot.eventbus.sample.BaseActivity.class, true,
                new SubscriberMethodInfo[] {
            new SubscriberMethodInfo("showMessage", com.tobelinker.greenrobot.eventbus.sample.Message.class),
            new SubscriberMethodInfo("showMessage", com.tobelinker.greenrobot.eventbus.sample.EventMessage.class),
        }));

        putIndex(new SimpleSubscriberInfo(com.tobelinker.greenrobot.eventbus.sample.MainActivity.class, true,
                new SubscriberMethodInfo[] {
            new SubscriberMethodInfo("showMessage", com.tobelinker.greenrobot.eventbus.sample.Message.class),
            new SubscriberMethodInfo("showMessage", com.tobelinker.greenrobot.eventbus.sample.EventMessage.class),
            new SubscriberMethodInfo("showMessage", String.class),
            new SubscriberMethodInfo("showMessage", Object.class),
        }));

    }

    private static void putIndex(SubscriberInfo info) {
        SUBSCRIBER_INDEX.put(info.getSubscriberClass(), info);
    }

    @Override
    public SubscriberInfo getSubscriberInfo(Class<?> subscriberClass) {
        SubscriberInfo info = SUBSCRIBER_INDEX.get(subscriberClass);
        if (info != null) {
            return info;
        } else {
            return null;
        }
    }
}
```


* compiled code whith proguard

```java
package com.a.a;

import java.util.*;
import a.a.a.a.*;
import com.tobelinker.greenrobot.eventbus.sample.*;

public class a implements d
{
    private static final Map<Class<?>, c> a;
    
    private static void a(final c c) {
        com.a.a.a.a.put(c.a(), c);
    }
    
    @Override
    public c a(final Class<?> clazz) {
        final c c = com.a.a.a.a.get(clazz);
        if (c != null) {
            return c;
        }
        return null;
    }
    
    static {
        a = new HashMap<Class<?>, c>();
        a(new a.a.a.a.b(MainActivity.class, true, new e[] { new e("showMessage", com.tobelinker.greenrobot.eventbus.sample.c.class), new e("showMessage", b.class), new e("showMessage", String.class), new e("showMessage", Object.class) }));
        a(new a.a.a.a.b(com.tobelinker.greenrobot.eventbus.sample.a.class, true, new e[] { new e("showMessage", com.tobelinker.greenrobot.eventbus.sample.c.class), new e("showMessage", b.class) }));
    }
}
```

* the mapping.txt

```java
com.sample.index.BusIndex -> com.a.a.a:
    java.util.Map SUBSCRIBER_INDEX -> a
    void <init>() -> <init>
    void putIndex(org.greenrobot.eventbus.meta.SubscriberInfo) -> a
    org.greenrobot.eventbus.meta.SubscriberInfo getSubscriberInfo(java.lang.Class) -> a
    void <clinit>() -> <clinit>
com.tobelinker.greenrobot.eventbus.sample.BaseActivity -> com.tobelinker.greenrobot.eventbus.sample.a:
    void <init>() -> <init>
    void showMessage(com.tobelinker.greenrobot.eventbus.sample.Message) -> a
    void showMessage(com.tobelinker.greenrobot.eventbus.sample.EventMessage) -> a
com.tobelinker.greenrobot.eventbus.sample.EventMessage -> com.tobelinker.greenrobot.eventbus.sample.b:
    java.lang.String message -> a
    void <init>(java.lang.String) -> <init>
    java.lang.String toString() -> toString
com.tobelinker.greenrobot.eventbus.sample.MainActivity -> com.tobelinker.greenrobot.eventbus.sample.MainActivity:
    org.greenrobot.eventbus.EventBus eventBus -> a
    void <init>() -> <init>
    void onCreate(android.os.Bundle) -> onCreate
    void onDestroy() -> onDestroy
    void showMessage(com.tobelinker.greenrobot.eventbus.sample.Message) -> a
    void showMessage(com.tobelinker.greenrobot.eventbus.sample.EventMessage) -> a
    void showMessage(java.lang.String) -> a
com.tobelinker.greenrobot.eventbus.sample.Message -> com.tobelinker.greenrobot.eventbus.sample.c:
    java.lang.String message -> a
    void <init>(java.lang.String) -> <init>
    java.lang.String toString() -> toString
```

* the final code after processed


```java
package com.a.a;

import java.util.*;
import a.a.a.a.*;
import com.tobelinker.greenrobot.eventbus.sample.*;

public class a implements d
{
    private static final Map<Class<?>, c> a;
    
    private static void a(final c c) {
        com.a.a.a.a.put(c.a(), c);
    }
    
    @Override
    public c a(final Class<?> clazz) {
        final c c = com.a.a.a.a.get(clazz);
        if (c != null) {
            return c;
        }
        return null;
    }
    
    static {
        a = new HashMap<Class<?>, c>();
        a(new a.a.a.a.b(com.tobelinker.greenrobot.eventbus.sample.a.class, true, new e[] { new e("a", com.tobelinker.greenrobot.eventbus.sample.c.class), new e("a", b.class) }));
        a(new a.a.a.a.b(MainActivity.class, true, new e[] { new e("a", com.tobelinker.greenrobot.eventbus.sample.c.class), new e("a", b.class), new e("a", String.class), new e("showMessage", Object.class) }));
    }
}
```

## Notice
none

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Copyright (c) 2016, tobelinker.com
