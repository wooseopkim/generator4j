# generator4j

This is a new (or should I say "modern") rewrite of [`java-generator-functions`](https://github.com/mherrmann/java-generator-functions).

### Major changes to the original source

- Now you pass a `(Context) -> void` to the `Generator` constructor instead of overriding `Generator.run()`.
- You can provide your own thread via `threadProvider` constructor parameter. This is mainly for testability, but you might find it useful.
- By default, `Generator` uses a virtual `Thread`.
- Supports `ctx.finish(T value)` for finishing iteration like Python's `return` or C#'s `yield break`.
- Implements `AutoCloseable` instead of overriding `finalize` for finishing `Thread`s.
- Supports `Generator.stream()`.

### Minimum Java version

Since this package utilizes virtual `Thread`s, you need to be using Java 21 or later.

### Cautions

I have no idea how Java threading and thus this works. Anything I can assure is that the test code passes if it does. I'm not sure if `Generator` class is safe or performant. APIs are subject to change, PRs are welcome.

### Credits

Of course, kudos to Michael Herrmann. Below is the license copy of the original repo.

```
The MIT License (MIT)

Copyright (c) 2014 Michael Herrmann.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
