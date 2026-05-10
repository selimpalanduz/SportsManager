package com.sportsmanager.core;

import com.sportsmanager.model.common.GameState;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SaveManager {

    private SaveManager() { }

    public static void save(File file, GameState state) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeObject(state);
        }
    }

    public static GameState load(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object obj = in.readObject();
            if (!(obj instanceof GameState)) {
                throw new IOException("Invalid save file: not a GameState object");
            }
            return (GameState) obj;
        }
    }
}