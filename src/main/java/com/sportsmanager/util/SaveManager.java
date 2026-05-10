package com.sportsmanager.util;

import com.sportsmanager.core.SportFactory;
import com.sportsmanager.interfaces.ISport;
import com.sportsmanager.model.common.StandingsEntry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveManager {

    public static final String DEFAULT_FILE_NAME = "career.sav";

    public static File defaultSaveFile() {
        String home = System.getProperty("user.home");
        File dir = new File(home, ".sportsmanager");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, DEFAULT_FILE_NAME);
    }

    public static void save(GameState state) throws IOException {
        save(state, defaultSaveFile());
    }

    public static void save(GameState state, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(state);
        }
    }

    public static GameState load() throws IOException, ClassNotFoundException {
        return load(defaultSaveFile());
    }

    public static GameState load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            GameState state = (GameState) ois.readObject();
            reattachSport(state);
            return state;
        }
    }

    public static boolean hasSave() {
        return defaultSaveFile().exists();
    }

    public static boolean deleteSave() {
        File f = defaultSaveFile();
        if (!f.exists()) return false;
        return f.delete();
    }

    private static void reattachSport(GameState state) {
        if (state == null || state.getLeague() == null) return;
        ISport sport = SportFactory.createSport(state.getSportType());
        if (state.getLeague().getStandings() != null) {
            for (StandingsEntry e : state.getLeague().getStandings()) {
                e.setSport(sport);
            }
        }
    }
}