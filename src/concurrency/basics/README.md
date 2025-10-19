# VaultCrackerThreads

A Java multithreading practice project demonstrating basic concurrency concepts through a simulated vault-cracking scenario.

## Purpose

This project is designed to practice fundamental Java multithreading and concurrency concepts, including:
- Thread creation and lifecycle management
- Thread synchronization
- Atomic operations with `AtomicBoolean`
- Race conditions and thread safety
- Thread interruption handling

## Overview

The program simulates a race between two hacker threads trying to crack a vault's password while a police thread counts down to their capture. The vault has a random password between 0 and 999, and the hackers must guess it before the police arrive.

## Components

### Vault
- Stores a randomly generated password (0-999)
- Provides `isCorrectPassword(int guess)` method with a 5ms delay to simulate password checking
- The delay makes the brute-force attack take time, allowing the police thread to potentially intervene

### AscendingHackerThread
- Attempts to crack the password by trying values from 0 to 999 in ascending order
- Uses high priority (`Thread.MAX_PRIORITY`)
- Stops immediately when any thread finds the password or police arrive

### DescendingHackerThread
- Attempts to crack the password by trying values from 999 to 0 in descending order
- Uses high priority (`Thread.MAX_PRIORITY`)
- Stops immediately when any thread finds the password or police arrive

### PoliceThread
- Counts down from 10 seconds (one second per count)
- If the countdown completes before hackers find the password, the game is over
- Monitors the shared `found` flag to detect if hackers succeeded first

## Key Concurrency Concepts

### AtomicBoolean for Thread Coordination
The `found` flag is shared between all threads to coordinate their actions:
- Hackers check `found.get()` in their loops to stop if password is found or police arrive
- Uses `compareAndSet(false, true)` to ensure only one thread claims victory
- Police sets flag if countdown completes without password being found

### Thread Synchronization
- All threads start simultaneously and are joined at the end
- Main thread waits for all threads to complete before printing final message
- Demonstrates proper thread lifecycle management

### Race Conditions
The program naturally creates race conditions:
- Two hackers competing to find the password first
- Hackers racing against the police countdown
- Multiple threads checking and modifying shared state

## Running the Program

```bash
javac concurrency/basics/Main.java
java concurrency.basics.Main
```

## Sample Output

```
Vault created with password---->542
Prepared thread AscendingHackerThread
Prepared thread DescendingHackerThread
Prepared thread PoliceThread
AscendingHackerThread started.
DescendingHackerThread started.
PoliceThread started.
Police countdown: 10
Police countdown: 9
Police countdown: 8
AscendingHackerThread guessed the password 542
AscendingHackerThread finished.
DescendingHackerThread finished.
Police: someone already found the password before I arrived.
PoliceThread finished.
The program has ended.
```

## Learning Outcomes

By studying and running this code, you will understand:
- How to create and start multiple threads
- How to use `Thread.join()` to wait for thread completion
- How to share state safely between threads using atomic variables
- How thread priority works (though it doesn't guarantee execution order)
- How to handle thread interruption properly
- Race conditions in concurrent programming
- The importance of checking shared flags in loops for coordination

## Notes

- Thread priority (`Thread.MAX_PRIORITY`) suggests to the JVM scheduler which threads are more important, but doesn't guarantee execution order
- The 5ms sleep in `isCorrectPassword()` simulates realistic password checking delay
- Password range is 0-999, giving hackers reasonable chance to succeed within 10 seconds
- The program demonstrates cooperative thread termination using the shared `found` flag