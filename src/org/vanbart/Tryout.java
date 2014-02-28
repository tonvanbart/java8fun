package org.vanbart;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Trying out Java 8 streams / lambdas
 */
public class Tryout {

    private Set<Person> persons;

    public static void main(String[] args) throws IOException {
        Tryout tryout = new Tryout();
        tryout.streamSet();
        tryout.streamFile();
    }

    public Tryout() {
        persons = new HashSet<>();
        persons.add(new Person("Dirk", 110, 1.90f));
        persons.add(new Person("Piet", 75, 1.73f));
        persons.add(new Person("Kees", 93, 1.80f));
        persons.add(new Person("Bubba", 140, 1.75f));
        persons.add(new Person("Tweety", 45, 1.65f));
    }

    /**
     * Try streaming the in memory Set.
     */
    public void streamSet() throws IOException {
        // lambda expression
        Optional<Person> heaviest = persons.stream().min(Comparator.comparing(p -> p.getWeight()));
        heaviest.ifPresent(p -> log("lightest = " + p.getName()));

        // method reference
        Optional<Person> bmi = persons.stream().max(Comparator.comparing(Person::calcBmi));
        bmi.ifPresent(p -> log("heighest BMI: %s", p.getName()));

        // map/reduce
        Integer total = persons.stream().map(Person::getWeight).reduce(0, (w1,w2) -> w1+w2);
        log("total weight of all persons combined = %s", total);
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
