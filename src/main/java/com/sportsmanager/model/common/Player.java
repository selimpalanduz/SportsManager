package com.sportsmanager.model.common;

public abstract class Player {
    private String name;
    private int overallSkill;
    private int injuryDuration; // Kalan sakatlık maç sayısı

    public Player(String name, int overallSkill) {
        this.name = name;
        this.overallSkill = overallSkill;
        this.injuryDuration = 0; // Başlangıçta sakat değil
    }

    // Her spor dalı (Headball, Football) oyuncu pozisyonunu kendi belirleyecek
    public abstract String getPosition();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOverallSkill() {
        return overallSkill;
    }

    public void setOverallSkill(int overallSkill) {
        this.overallSkill = overallSkill;
    }

    public int getInjuryDuration() {
        return injuryDuration;
    }

    public void setInjuryDuration(int injuryDuration) {
        this.injuryDuration = injuryDuration;
    }

    public boolean isInjured() {
        return this.injuryDuration > 0;
    }

    public void decrementInjury() {
        if (this.injuryDuration > 0) {
            this.injuryDuration--;
        }
    }
}