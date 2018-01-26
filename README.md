# RxiOSMOE: Reactive Extensions for MOE (iOS)

Multi-OS Engine (MOE) specific bindings for RxJava 2.

This module adds the minimum classes to RxJava that make writing reactive components in MOE applications easy and hassle-free. More specifically, it provides a Scheduler that schedules on the main thread or any given Looper.

# Binaries

  ``` gradle
  buildscript {
      repositories {
          jcenter()
      }
  }
  
  ```

```groovy
compile 'com.github.devjn:rxiosmoe:2.0.1'
// It is recommended you also explicitly depend on RxJava's latest version for bug fixes and new features.
compile 'io.reactivex.rxjava2:rxjava:2.1.9'
```

###### For Gradle 4+

```groovy
implementation 'com.github.devjn:rxiosmoe:2.0.1'
implementation 'io.reactivex.rxjava2:rxjava:2.1.9'
```

* RxiOSMOE:  [ ![JCenter](https://api.bintray.com/packages/devjn/maven/rxiosmoe/images/download.svg) ](https://bintray.com/devjn/maven/rxiosmoe/_latestVersion)
* RxJava: <a href='http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.reactivex.rxjava2%22%20a%3A%22rxjava%22'><img src='http://img.shields.io/maven-central/v/io.reactivex.rxjava2/rxjava.svg'></a>

# Usage

## Observing on the main thread

One of the most common operations when dealing with asynchronous tasks is to observe the task's
result or outcome on the main thread. With RxJava you would declare your `Observable` to be observed on the main thread:

```java
Observable.just("one", "two", "three", "four", "five")
        .subscribeOn(Schedulers.newThread())
        .observeOn(IOSSchedulers.mainThread())
        .subscribe(/* an Observer */);
```

This will execute the `Observable` on a new thread, and emit results through `onNext` on the main thread.

## LICENSE

    Copyright 2017 devjn

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
