# Contract Worksheet

One section per milestone. Fill each one in as you go, in order. Write each
prediction before you run anything. That is the part a TA asks about.

Keep it short and specific. Point at methods, call sites, and error text.

---

## Milestone 1: The notes overload

### Prediction (write this before you run the build, and you can deliberate with your agent)

**Will the consumer, untouched, still compile and pass?** Yes or no.

Yes. I predict that the untouched consumer will still compile and pass its tests. The existing four-argument createBooking method will remain unchanged, while the new method is an additional five-argument overload with notes. Therefore, the existing calls in FrontDesk can still use the original four-argument method. This is an additive change because it adds new API surface without removing or changing what the existing consumer relies on.

**Why.** What does the compiler do with the consumer's existing call sites once
the new overload exists?

The compiler will continue to match the existing four-argument calls in FrontDesk to the original four-argument createBooking method. The new five-argument overload is not selected because those call sites only provide four arguments.

### What happened

**The result.** What the build printed for each module.

**If your prediction was wrong,** say what you missed.

**Is an additive change always safe in Java?** One case where adding something
to an API still breaks a caller, if you can name one.

The result. lab06-api: 9 tests passed. lab06-consumer: 7 tests passed with no source changes. All reactor modules succeeded and the build ended with BUILD SUCCESS.

If your prediction was wrong. My prediction was correct. The existing four-argument calls in FrontDesk still resolved to the original four-argument overload.

Is an additive change always safe in Java? No. For example, adding an abstract method to an interface can break existing implementations because they must implement the new method.
---

## Milestone 2: The request object

### Prediction (write this before you run the build)

**Will the untouched consumer still compile and pass?** Yes or no, and if no,
which module goes red and whether at compile time or test time.

**Where.** Name the call sites you expect to be affected, if any.

**What about the tests in `api/`, after you update them?** And whether their
result is evidence about the consumer.

Will the untouched consumer still compile and pass?
No. I predict that the api module will compile and its updated tests will pass, but the consumer module will fail at compile time because the old positional createBooking methods will have been replaced by createBooking(BookingRequest).

Where.
I expect FrontDesk.java:27 and FrontDesk.java:33 to fail because both call the existing four-argument createBooking method.

What about the tests in api/, after you update them?
I expect the updated API tests to pass because they will use BookingRequest. However, that does not show that the consumer is compatible, because the API tests do not compile or exercise FrontDesk.

### Step 1: after the fold

**What the build printed.** Paste it for each module, including file and
line for anything that failed.

**Which module's tests ran, and which did not.** And what that tells you about
who can detect a contract break.

### Step 2: the deprecation path

**What you added.** The signatures that came back, and what they delegate to.

**The warnings.** Paste one deprecation warning line from the build log (from
a `mvn -B clean test` run, since a rerun with nothing to compile prints none).

**What the deprecation path resolves.** Who can now build that could not build
during step 1, and who is on which schedule.

**What the warnings accomplish that a README note would not.** Be concrete
about where the warning shows up and who sees it without looking for it.

---

## Milestone 3: The misuse critique

Not coded. One misuse, one redesign, one cost. Discuss it with your TA.

### The misuse

**What is easy to get wrong.** One specific thing about the API surface.

**The call site.** File and line in `consumer/`, with the call. Show the
code that a reader cannot understand without opening the javadoc, or that a
caller could get wrong with the compiler still happy.

**What goes wrong when it happens.** Silent bad behavior, wrong data, a crash
somewhere far away?

### The redesign

**The proposal.** Types, enums, factories, or whatever you are proposing. Show
the new signature and the new call site.

**Why the mistake is now hard or impossible to make.** Point at the mechanism,
such as the compiler, a validating constructor, or an exhaustive switch.

### One tradeoff

**What it costs.** Something real, such as caller ceremony, migration burden
against the deprecation path you just built, or more types for a newcomer to
learn. "No real downside" does not count.

**When the price is worth paying.** A condition under which it is.
