package org.vanbart;

import static org.vanbart.ExecutionTimer.time;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Trying out Java 8 streams / lambdas
 */
public class Java8Tests {

private Collection<Person> persons;

    public Java8Tests() {
        persons = new ArrayList<>();
        persons.add(new Person("Dirk", 110, 1.90f));
        persons.add(new Person("Piet", 75, 1.73f));
        persons.add(new Person("Kees", 93, 1.80f));
        persons.add(new Person("Bubba", 140, 1.75f));
        persons.add(new Person("Tweety", 45, 1.65f));
    }

    public static void main(String[] args) throws IOException {
        Java8Tests tryout = new Java8Tests();
        tryout.streamSet();
        tryout.streamFile();
        tryout.testCustomFunctional();
        tryout.acceptBiOperator((x, y) -> x * y);
        tryout.testMultilineClosure();
        tryout.testExecutionTimer();

        tryout.createsString(String::toUpperCase, "hello");
    }

    /**
     * Test custom @functionalinterface.
     */
    public void testCustomFunctional() {
        Foo foo = str -> "Hello, "+str;
        System.out.println(foo.bar("World"));
    }

    /**
     * Test multi statement closure.
     */
    public void testMultilineClosure() {
        System.out.println(createsString(s -> {
            s = s.toUpperCase();
            s = s.toLowerCase();
            return s;
        }, "Hello"));
        acceptsFunction(s -> s.toUpperCase().substring(2), "dia");
        
    }

    public String createsString(Function<String, String> strCreator, String argument) {
        return strCreator.apply(argument);
    }

    /**
     * Test higher order function.
     * @param func a function which accepts a String and returns another one.
     * @param argument the argument on which the first argument is applied.
     */
    public void acceptsFunction(Function<String, String> func, String argument) {
        log("The result has length %s", func.apply(argument).length());
    }

    public void acceptBiOperator(DoubleBinaryOperator dbi) {
        System.out.println(dbi.applyAsDouble(2,2));

    }

    /**
     * Streaming the in memory Set, serial and parallel.
     */
    public void streamSet() throws IOException {
        // lambda expression
        Optional<Person> heaviest = persons.stream().min(Comparator.comparing(p -> p.getWeight()));
        heaviest.ifPresent(p -> log("lightest = " + p.getName()));

        // method reference
        Optional<Person> bmi = persons.stream().max(Comparator.comparing(Person::calcBmi));
        bmi.ifPresent(p -> log("heighest BMI: %s", p.getName()));
        long start, time;

        // parallel map / reduce
        System.out.println("parallel");
        start = System.currentTimeMillis();
                Integer total2 = persons.parallelStream().map(Person::getWeight).reduce(0, (w1, w2) -> w1+w2);
        time = System.currentTimeMillis() - start;
        log("total weight in parallel = %s (%s ms)", total2, time);

        // map/reduce serial
        start = System.currentTimeMillis();
        Integer total = persons.stream().map(Person::getWeight).reduce(0, (w1,w2) -> w1+w2);
        time = System.currentTimeMillis() - start;
        log("total weight of all persons combined = %s (%s ms)", total, time);
    }

    /**
     * Test the {@link ExecutionTimer}.
     */
    public void testExecutionTimer() {
        log("testExecutionTimer");

        log("serial");
        Function<Collection<Person>, Integer> serial = coll -> coll.stream().map(Person::getWeight).reduce(0, (w1,w2) -> w1+w2);
        int total1 = time(serial, persons);
        log("total 1 = %s", total1);

        // even shorter syntax:
        log("parallel");
        int total2 = time((coll -> coll.parallelStream().map(Person::getWeight).reduce(0, (w1, w2) -> w1 + w2)), persons);
        log("total 2 = %s", total2);

        log("Calling method inside log:" + time(serial, persons));

        log("Calling closure inside log:" + time((coll -> coll.parallelStream().map(Person::getWeight).reduce(0, (w1, w2) -> w1 + w2)), persons));

    }

    /**
     * Try streaming a file from the classpath, uses try-with-resources.
     * @throws IOException
     */
    public void streamFile() throws IOException {
        URL url = getClass().getClassLoader().getResource("testfile.txt");
        Path path = FileSystems.getDefault().getPath(url.getPath());

        try (Stream<String> lines = Files.lines(path)) {
            Optional<String> longest = lines.max(Comparator.comparing(String::length));
            longest.ifPresent(l -> log("Longest line in file: '%s' (%s)", l, l.length()));
        }

        try (Stream<String> lines = Files.lines(path)) {
            log("There are %s lines longer than 35 chars.", lines.filter(s -> s.length() > 35).count());
        }
    }

    private static void log(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    /**
     * Mini inner domain class.
     */
    static class Person {
        final String name;
        final Integer weight; // kg
        final Float length; // m

        Person(String name, Integer weight, Float length) {
            this.name = name;
            this.weight = weight;
            this.length = length;
        }

        private String getName() {
            return name;
        }

        private Integer getWeight() {
            // artifially slow method...
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignored
            }
            return weight;
        }

        Float calcBmi() {
            return weight / (length * length);
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}
