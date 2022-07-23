package net.flytre.gen.io;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FunctionWriter {

    public static String name = "flytre_custom_items";
    public static String dataLoc = name + "/data";
    public static String currentLoc = name + "/data";


    static void deleteOld() {
        FileHandler.deleteDirectory(name);
    }

    public static void setName(String newName) {
        name = newName;
        dataLoc = name + "/data";
    }


    static void createDatapack() {
        FileHandler.createDatapack(name);
    }

    static void makeTickJSON() {
        FileHandler.setOutput(dataLoc + "/minecraft/tags/functions/tick.json");
        FileHandler.print("""
                {
                  "replace": false,
                  "values": [
                  ]
                }""");
    }

    static void makeLoadJSON() {
        FileHandler.setOutput(dataLoc + "/minecraft/tags/functions/load.json");
        FileHandler.print("""
                {
                  "replace": false,
                  "values": [
                    "flytre:init_items"
                  ]
                }""");
    }

    public static void makeFunction(String name) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/" + name + ".mcfunction");
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        FileHandler.print(String.format("""
                ####################################################################################################
                #Java enhanced file
                #Created: %s
                ####################################################################################################
                                
                """, dateFormat.format(new Date())));

    }

    public static void makeFunctionWithStatement(String name, String statement) {
        makeFunction(name);
        addStatement(name, statement);
    }

    public static void makeFunctionAndSetLoc(String name) {
        makeFunction(name);
        setLoc(name);
    }

    public static void addStatement(String func, String statement) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/" + func + ".mcfunction");
        FileHandler.print(statement);
    }

    public static void setLoc(String loc) {
        currentLoc = loc;
    }

    public static void section(boolean large) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/" + currentLoc + ".mcfunction");

        if (!large)
            FileHandler.print("");
        else
            for (int i = 0; i < 3; i++)
                FileHandler.print("");
    }

    public static void state(String statment) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/" + currentLoc + ".mcfunction");

        FileHandler.print(statment);
    }

    public static void comment(String comment) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/" + currentLoc + ".mcfunction");

        FileHandler.print("#" + comment);
    }

    public static void commentWithBreakBefore(String comment) {

        section(false);
        comment(comment);
    }


    public static void addObjective(String name) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/init.mcfunction");

        FileHandler.print("scoreboard objectives add " + name + " dummy");
    }

    static void addObjective(String name, String criteria) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/init_items.mcfunction");

        FileHandler.print("scoreboard objectives add " + name + " " + criteria);

    }

    static void addInitFunctionComment(String comment) {
        FileHandler.setOutput(dataLoc + "/flytre/functions/init_items.mcfunction");
        FileHandler.print("#" + comment);
    }


}
