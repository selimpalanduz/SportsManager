package com.sportsmanager.util;

import java.util.Random;

public class NameGenerator {

    private static final String[] MALE_FIRST_NAMES = {
            
            "Ahmet", "Mehmet", "Ali", "Veli", "Hasan", "Hüseyin", "Mustafa",
            "Yusuf", "Emre", "Burak", "Arda", "Can", "Eren", "Furkan", "Kerem",
            "Onur", "Selim", "Volkan", "Hakan", "Kaan", "Mert", "Hamza", "Barış",
            
            "Klaus", "Hans", "Jürgen", "Friedrich", "Dieter", "Wolfgang", "Stefan",
            "Lukas", "Maximilian",
            
            "James", "John", "William", "Harry", "Oliver", "Jack", "Thomas",
            "George", "Charles",
            
            "Pierre", "Jean", "Antoine", "Lucas", "Hugo", "Mathieu", "Olivier",
            "Sébastien",
            
            "Marco", "Luca", "Carlos", "Diego", "Sergio", "Pablo", "Alejandro",
            "Matteo", "Giovanni",
            
            "Rafael", "Bruno", "Gabriel", "Thiago", "Ronaldo", "Felipe", "Marcelo",
            
            "Wei", "Jian", "Hao", "Lei", "Ming", "Tao", "Chen"
    };

    private static final String[] FEMALE_FIRST_NAMES = {
            
            "Ayşe", "Fatma", "Zeynep", "Elif", "Merve", "Esra", "Büşra",
            "Selin", "Deniz", "Ezgi", "Burcu", "Damla", "Eda", "Gizem",
            "Hande", "Melis", "Yasemin", "Aslı", "Cansu",
           
            "Anna", "Greta", "Hannah", "Lena", "Sophie", "Lisa", "Emma",
           
            "Olivia", "Amelia", "Isla", "Ava", "Mia", "Charlotte", "Grace",
            
            "Camille", "Manon", "Léa", "Chloé", "Juliette", "Margaux",
            
            "Sofia", "Lucia", "Valentina", "Isabella", "Carmen", "Paola",
            "Gabriella",
            
            "Beatriz", "Larissa", "Camila", "Mariana", "Luana",
            
            "Mei", "Lin", "Xiu", "Yan", "Hua", "Jing"
    };

    private static final String[] LAST_NAMES = {
            
            "Yılmaz", "Kaya", "Demir", "Şahin", "Çelik", "Yıldız", "Öztürk",
            "Aydın", "Arslan", "Doğan", "Kılıç", "Çetin", "Kara", "Koç",
            "Şimşek", "Erdoğan", "Çakır", "Acar", "Şen", "Bal", "Türk",
            
            "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Wagner",
            "Becker", "Hoffmann",
            
            "Smith", "Johnson", "Williams", "Brown", "Taylor", "Wilson", "Davies",
            
            "Dubois", "Martin", "Bernard", "Petit", "Moreau", "Laurent",
            
            "García", "Rodríguez", "Martínez", "López", "Rossi", "Romano",
            "Ferrari", "Bianchi",
            
            "Silva", "Santos", "Oliveira", "Souza", "Costa", "Pereira",
            
            "Wang", "Li", "Zhang", "Liu", "Chen", "Yang"
    };

    private final Random random;
    private final boolean female;

    public NameGenerator() {
        this.random = new Random();
        this.female = false;
    }

    public NameGenerator(boolean female) {
        this.random = new Random();
        this.female = female;
    }

    public NameGenerator(long seed) {
        this.random = new Random(seed);
        this.female = false;
    }

    public String generate() {
        String[] firstNames = female ? FEMALE_FIRST_NAMES : MALE_FIRST_NAMES;
        String first = firstNames[random.nextInt(firstNames.length)];
        String last = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }

    public String generateFirst() {
        String[] firstNames = female ? FEMALE_FIRST_NAMES : MALE_FIRST_NAMES;
        return firstNames[random.nextInt(firstNames.length)];
    }

    public String generateCoachName() {
        return generate();
    }
}