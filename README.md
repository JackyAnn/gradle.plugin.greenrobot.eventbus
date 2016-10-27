# gradle.plugin.greenrobot.eventbus
[![JitPack](https://jitpack.io/v/JackyAnn/gradle.plugin.greenrobot.eventbus.svg)](https://jitpack.io/#JackyAnn/gradle.plugin.greenrobot.eventbus)

gradle.plugin.greenrobot.eventbus is a gradle plugin for eventbus when use proguard.
* is supports [EventBus](https://github.com/greenrobot/EventBus) version from 3.0 and above.
* is supports com.android.tools.build:gradle version from 1.5 and above.
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
	* gradle
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
	* gradle
```gradle
apply plugin: 'com.tobelinker.greenrobot.eventbus'
```

## Sample
[gradle.plugin.greenrobot.eventbus.sample](https://github.com/JackyAnn/gradle.plugin.greenrobot.eventbus.sample)

## Notice
none

## License

[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

Copyright (c) 2016, tobelinker.com
