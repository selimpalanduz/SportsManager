package com.sportsmanager.util;

import java.util.Random;

public final class NameGenerator {

    private static final String[] MALE_FIRST = {
            "Ahmet", "Mehmet", "Ali", "Veli", "Hasan", "Huseyin", "Mustafa",
            "Ibrahim", "Yusuf", "Emre", "Burak", "Arda", "Cenk", "Hakan",
            "Kerem", "Salih", "Okan", "Caner", "Selim", "Deniz", "Mert",
            "Eren", "Baris", "Tolga", "Sinan", "Furkan", "Berkay", "Ozan",
            "Umut", "Berke", "Anil", "Cengiz", "Doruk", "Ege", "Goktug",
            "Kaan", "Onur", "Sarp", "Tarik", "Yigit"
    };

    private static final String[] FEMALE_FIRST = {
            "Ayse", "Fatma", "Zeynep", "Elif", "Merve", "Selin", "Ece",
            "Ipek", "Asli", "Ceren", "Defne", "Gizem", "Hande", "Nazli",
            "Pelin", "Sevgi", "Tugce", "Yasemin", "Zehra", "Bahar",
            "Esra", "Gamze", "Inci", "Lale", "Melike", "Nehir", "Petek",
            "Sila", "Tuana", "Ayla"
    };

    private static final String[] LAST = {
            "Yilmaz", "Kaya", "Demir", "Sahin", "Celik", "Yildiz", "Aydin",
            "Ozturk", "Arslan", "Dogan", "Kilic", "Aslan", "Cetin", "Kara",
            "Koc", "Kurt", "Polat", "Akin", "Cakir", "Erdogan", "Ucar",
            "Kaplan", "Bulut", "Tas", "Toprak", "Acar", "Avci", "Bayram",
            "Coskun", "Eren", "Guler", "Korkmaz", "Sari", "Tekin", "Ulus",
            "Yavuz", "Aksoy", "Bilgin", "Candan", "Demirel"
    };

    private final Random rng;

    public NameGenerator() {
        this(new Random());
    }

    public NameGenerator(long seed) {
        this(new Random(seed));
    }

    public NameGenerator(Random random) {
        this.rng = random;
    }

    public String maleName() {
        return MALE_FIRST[rng.nextInt(MALE_FIRST.length)]
                + " " + LAST[rng.nextInt(LAST.length)];
    }

    public String femaleName() {
        return FEMALE_FIRST[rng.nextInt(FEMALE_FIRST.length)]
                + " " + LAST[rng.nextInt(LAST.length)];
    }

    public String anyName() {
        return rng.nextBoolean() ? maleName() : femaleName();
    }

    public String coachName() {
        return "Coach " + maleName();
    }
}