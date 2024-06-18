package com.consol.control;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Randomizer {

    private final Random random = new Random();
    private final String[] firstNames = {"Hans", "Peter", "Gabi"};
    private final String[] lastNames = {"Meier", "Schmitt", "Bauer"};

    public String getRandomFirstName() {
        return firstNames[random.nextInt(firstNames.length)];
    }

    public String getRandomLastName() {
        return lastNames[random.nextInt(lastNames.length)];
    }

    public int getRandomAge() {
        return random.nextInt(100);
    }
}
