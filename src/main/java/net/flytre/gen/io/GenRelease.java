package net.flytre.gen.io;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;

public class GenRelease {

    private static final String SRC = "/Users/aaron/IdeaProjects/Realms saves/Bingo 1.19";
    private static final String END = "/Users/aaron/Library/Application Support/minecraft/saves/bingo_release_1_19";


    public static void main(String[] args) throws IOException {

        deleteDir(new File(END));
        File src = new File(SRC);
        File end = new File(END);
        copyDirectory(src, end);

        //remove all the extraneous files
        deleteDir(new File(END + "/advancements"));
        deleteDir(new File(END + "/stats"));
        deleteDir(new File(END + "/playerdata"));
        deleteDir(new File(END + "/.git"));
        deleteDir(new File(END + "/level.dat_old"));
        deleteDir(new File(END + "/datapacks/.datapack"));
        deleteDir(new File(END + "/datapacks/.git"));
        deleteDir(new File(END + "/datapacks/.idea"));
        deleteDir(new File(END + "/datapacks/.datapack"));
        deleteDir(new File(END + "/datapacks/datapacks.iml"));
        deleteDir(new File(END + "/datapacks/README.md"));
        deleteDir(new File(END + "/datapacks/out"));
        deleteDir(new File(END + "/datapacks/resources"));
        deleteDir(new File(END + "/datapacks/json-simple-1.1.1.jar"));
        deleteDir(new File(END + "/datapacks/out"));
        deleteDir(new File(END + "/datapacks/src"));


        //IMPORTANT
        deleteDir(new File(END + "/datapacks/ocean_set"));


        deleteDir(new File(END + "/datapacks/todo.txt"));
        deleteDir(new File(END + "/data/fabricRegistry.dat"));
        deleteDir(new File(END + "/data/fabricRegistry.dat.1"));
        deleteDir(new File(END + "/data/fabricRegistry.dat.2"));
        deleteDir(new File(END + "/config.json"));
        deleteDir(new File(END + "/pom.xml"));
        deleteDir(new File(END + "/bingo.iml"));
        deleteDir(new File(END + "/.idea"));

        deleteDir(new File(END + "/datapacks/.gradle"));
        deleteDir(new File(END + "/datapacks/build"));
        deleteDir(new File(END + "/datapacks/run"));
        deleteDir(new File(END + "/datapacks/build"));
        deleteDir(new File(END + "/datapacks/out"));
        deleteDir(new File(END + "/out"));
        deleteDir(new File(END + "/datapacks/config.json"));
        deleteDir(new File(END + "/datapacks/build.gradle"));
        deleteDir(new File(END + "/datapacks/gradle.properties"));
        deleteDir(new File(END + "/datapacks/gradlew"));
        deleteDir(new File(END + "/datapacks/gradlew.bat"));
        deleteDir(new File(END + "/datapacks/settings.gradle"));

        deleteDir(new File(END + "datapacks/flytre_custom_items/data/flytre/functions/testing.mcfunction"));


        prepareLevelDat();


    }


    public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
        if (sourceDir.isDirectory()) {
            copyDirectoryRecursively(sourceDir, targetDir);
        } else {
            Files.copy(sourceDir.toPath(), targetDir.toPath());
        }
    }

    private static void copyDirectoryRecursively(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }
        for (String child : source.list()) {
            copyDirectory(new File(source, child), new File(target, child));
        }
    }

    private static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public static void prepareLevelDat() {
        File f = new File(END + "/level.dat");
        try {
            CompoundTag nbtCompound = NbtIo.readCompressed(f);
            CompoundTag data = nbtCompound.getCompound("Data");
            data.putLong("LastPlayed", (new Date()).getTime());
            data.remove("Player");
            data.getCompound("GameRules").putString("sendCommandFeedback", "false");
            data.getCompound("GameRules").putString("logAdminCommands", "false");
            data.getCompound("GameRules").putString("spectatorsGenerateChunks", "false");
            NbtIo.writeCompressed(nbtCompound, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
