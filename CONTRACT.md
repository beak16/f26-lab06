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

No. I predict that the api module will compile and its updated tests will pass, but the consumer module will fail at compile time because the old positional createBooking methods will have been replaced by createBooking(BookingRequest).

**Where.** Name the call sites you expect to be affected, if any.

I expect FrontDesk.java:27 and FrontDesk.java:33 to fail because both call the existing four-argument createBooking method.

**What about the tests in `api/`, after you update them?** And whether their
result is evidence about the consumer.

I expect the updated API tests to pass because they will use BookingRequest. However, that does not show that the consumer is compatible, because the API tests do not compile or exercise FrontDesk.

### Step 1: after the fold

**What the build printed.** Paste it for each module, including file and
line for anything that failed.

lab06-api compiled and all 9 tests passed with 0 failures and 0 errors. lab06-consumer failed during compilation. FrontDesk.java:27 and FrontDesk.java:33 failed because createBooking now requires a BookingRequest, but the consumer still passes four positional arguments. The reactor ended with lab06-api SUCCESS, lab06-consumer FAILURE, and BUILD FAILURE.

**Which module's tests ran, and which did not.** And what that tells you about
who can detect a contract break.

All 9 api tests ran and passed. The 7 consumer tests did not run because the consumer failed during compilation before reaching the test phase. This shows that passing the API’s own tests does not prove compatibility with an external consumer; the contract break was detected only when the consumer was compiled.

### Step 2: the deprecation path

**What you added.** The signatures that came back, and what they delegate to.

I added two @Deprecated default methods to BookingApi with the old signatures: createBooking(String, long, long, String) and createBooking(String, long, long, String, String). Both build a BookingRequest and delegate to createBooking(BookingRequest), so the booking logic remains in one place.

**The warnings.** Paste one deprecation warning line from the build log (from
a `mvn -B clean test` run, since a rerun with nothing to compile prints none).

[WARNING] /Users/beakim/Desktop/17-214/Assignments/f26-lab06/consumer/src/main/java/edu/cmu/cs214/frontdesk/FrontDesk.java:[27,19] createBooking(java.lang.String,long,long,java.lang.String) in edu.cmu.cs214.booking.BookingApi has been deprecated

**What the deprecation path resolves.** Who can now build that could not build
during step 1, and who is on which schedule.

The untouched consumer can build again. FrontDesk.java:27 and :33 compile, and all 7 consumer tests pass. The API can move to BookingRequest now while the consumer can migrate later, before the deprecated overloads are eventually removed.

**What the warnings accomplish that a README note would not.** Be concrete
about where the warning shows up and who sees it without looking for it.

The warnings appear directly in the consumer’s compile output at the deprecated call sites, including the file and line number. This tells the consumer exactly what needs to migrate without requiring them to find and read a README.

---

## Milestone 3: The misuse critique

Not coded. One misuse, one redesign, one cost. Discuss it with your TA.

### The misuse

**What is easy to get wrong.** One specific thing about the API surface.

The fourth argument of createBooking uses null to encode behavior: null means do not waitlist on conflict, while a non-null string means waitlist using that key. A caller cannot understand this distinction from the method call alone.

**The call site.** File and line in `consumer/`, with the call. Show the
code that a reader cannot understand without opening the javadoc, or that a
caller could get wrong with the compiler still happy.

consumer/src/main/java/edu/cmu/cs214/frontdesk/FrontDesk.java:27

return api.createBooking(roomId, startMinute, endMinute, null);

A reader has to open the BookingApi javadoc to know what null means.

**What goes wrong when it happens.** Silent bad behavior, wrong data, a crash
somewhere far away?

The failure can be silent. If a caller passes null when it intended to waitlist, a conflicting booking is not created or waitlisted and the method returns null.

### The redesign

**The proposal.** Types, enums, factories, or whatever you are proposing. Show
the new signature and the new call site.

Replace the null-based policy with two explicit methods: bookIfFree(BookingRequest) and bookOrWaitlist(BookingRequest, WaitlistKey). WaitlistKey validates that its value is non-null and non-blank.

**Why the mistake is now hard or impossible to make.** Point at the mechanism,
such as the compiler, a validating constructor, or an exhaustive switch.

The method name makes the conflict policy explicit, and bookOrWaitlist requires a validated WaitlistKey. The caller no longer uses null as a hidden switch between two behaviors.

### One tradeoff

**What it costs.** Something real, such as caller ceremony, migration burden
against the deprecation path you just built, or more types for a newcomer to
learn. "No real downside" does not count.

The API becomes larger and migration becomes more expensive. Existing consumers must learn and migrate to the new methods and WaitlistKey type, while deprecated APIs may need to remain temporarily for compatibility.

**When the price is worth paying.** A condition under which it is.

The cost is worth paying when silent misuse could cause lost reservations and when many consumers use the API, because the safer design makes the intended behavior explicit and compiler-checkable.
