package concurrency.basics;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private static final int MAX_PASSWORD = 999;

    public static void main(String[] args) throws InterruptedException {
        Random random = new Random();
        Vault vault = new Vault(random.nextInt(MAX_PASSWORD));

        AtomicBoolean found = new AtomicBoolean(false);

        List<Thread> threads = List.of(
                new AscendingHackerThread(vault, found),
                new DescendingHackerThread(vault, found),
                new PoliceThread(found)
        );

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("The program has ended.");
    }

    private static class Vault {
        private final int password;

        public Vault(int password) {
            this.password = password;
            System.out.println("Vault created with password---->" + password);
        }

        public boolean isCorrectPassword(int guess) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return this.password == guess;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected final Vault vault;
        protected final AtomicBoolean found;

        public HackerThread(Vault vault, AtomicBoolean found) {
            this.vault = vault;
            this.found = found;
            this.setName(this.getClass().getSimpleName());
            this.setPriority(Thread.MAX_PRIORITY);
            System.out.println("Prepared thread " + this.getName());
        }

    }

    private static class AscendingHackerThread extends HackerThread {

        public AscendingHackerThread(Vault vault, AtomicBoolean found) {
            super(vault, found);
        }

        @Override
        public void run() {
            System.out.println(this.getName() + " started.");
            for (int guess = 0; guess <= MAX_PASSWORD && !found.get(); guess++) {
                if (vault.isCorrectPassword(guess)) {
                    if (found.compareAndSet(false, true)) {
                        System.out.println(this.getName() + " guessed the password " + guess);
                    }
                    break;
                }
            }
            System.out.println(this.getName() + " finished.");
        }
    }

    private static class DescendingHackerThread extends HackerThread {

        public DescendingHackerThread(Vault vault, AtomicBoolean found) {
            super(vault, found);
        }

        @Override
        public void run() {
            System.out.println(this.getName() + " started.");
            for (int guess = MAX_PASSWORD; guess >= 0 && !found.get(); guess--) {
                if (vault.isCorrectPassword(guess)) {
                    if (found.compareAndSet(false, true)) {
                        System.out.println(this.getName() + " guessed the password " + guess);
                    }
                    break;
                }
            }
            System.out.println(this.getName() + " finished.");
        }
    }

    private static class PoliceThread extends Thread {
        private final AtomicBoolean found;

        public PoliceThread(AtomicBoolean found) {
            this.found = found;
            this.setName("PoliceThread");
            System.out.println("Prepared thread " + this.getName());
        }

        @Override
        public void run() {
            System.out.println(this.getName() + " started.");
            for (int i = 10; i > 0 && !found.get(); i--) {
                try {
                    System.out.println("Police countdown: " + i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(this.getName() + " interrupted.");
                    break;
                }
            }

            if (!found.get()) {
                found.set(true);
                System.out.println("Game over for you hackers! (Police arrived)");
            } else {
                System.out.println("Police: someone already found the password before I arrived.");
            }
            System.out.println(this.getName() + " finished.");
        }
    }
}
