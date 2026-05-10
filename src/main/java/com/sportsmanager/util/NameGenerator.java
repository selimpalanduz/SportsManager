package com.sportsmanager.util;

import java.util.Random;

public class NameGenerator {

    private static final String[] FIRST_NAMES = {
            "Ahmet", "Mehmet", "Ali", "Veli", "Hasan", "Hüseyin", "Mustafa",
            "İbrahim", "Yusuf", "Emre", "Burak", "Arda", "Can", "Cem", "Eren",
            "Furkan", "Kerem", "Murat", "Onur", "Oğuz", "Ozan", "Selim", "Serkan",
            "Tolga", "Umut", "Volkan", "Yiğit", "Berkay", "Caner", "Deniz",
            "Doğan", "Ekrem", "Erdal", "Gökhan", "Halil", "Hakan", "İlhan",
            "Kaan", "Levent", "Mert", "Okan", "Recep", "Salih", "Tayfun",
            "Tuncay", "Uğur", "Cihan", "Hamza", "Barış", "Sefa"
    };

    private static final String[] LAST_NAMES = {
            "Yılmaz", "Kaya", "Demir", "Şahin", "Çelik", "Yıldız", "Yıldırım",
            "Öztürk", "Aydın", "Özdemir", "Arslan", "Doğan", "Kılıç", "Aslan",
            "Çetin", "Kara", "Koç", "Kurt", "Özkan", "Şimşek", "Polat",
            "Erdoğan", "Korkmaz", "Çakır", "Acar", "Bulut", "Karaca",
            "Şen", "Avcı", "Ergin", "Tunç", "Güneş", "Bal", "Aksoy",
            "Türk", "Yavuz", "Alkan", "Erol", "Aktaş", "Bilgin",
            "Tan", "Sezer", "Gül", "Uslu", "Demirel"
    };

    private final Random random;

    public NameGenerator() {
        this.random = new Random();
    }

    public NameGenerator(long seed) {
        this.random = new Random(seed);
    }

    public String generate() {
        String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String last  = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }

    public String generateFirst() {
        return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    public String generateCoachName() {
        return generate();
    }
}