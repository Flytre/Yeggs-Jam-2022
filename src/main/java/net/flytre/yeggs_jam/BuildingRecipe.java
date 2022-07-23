package net.flytre.yeggs_jam;

import net.flytre.gen.io.FileHandler;
import net.flytre.gen.io.FunctionWriter;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class BuildingRecipe implements Generatable {


    public static final int MIN_DISTANCE = 12;

    //Spawns a marker indicating a building with this tag type
    private final String buildingTagId;

    //recipe for the building
    private final List<ItemStack> recipeComponents;

    public BuildingRecipe(String buildingTagId, List<ItemStack> recipeComponents) {
        this.buildingTagId = buildingTagId;
        this.recipeComponents = recipeComponents;
    }

    @Override
    public void generate() {
        FunctionWriter.addStatement("recipes/detect", String.format("function flytre:recipes/%s", buildingTagId));

        FunctionWriter.makeFunctionAndSetLoc(String.format("recipes/%s", buildingTagId));

        for (int i = 0; i < recipeComponents.size(); i++) {
            ItemStack stack = recipeComponents.get(i);
            FunctionWriter.state(String.format(
                    "execute as @e[type=item,nbt={Item:{id:\"%s\"%s}}] if score @s item_count matches %d.. run tag @s add stack_%d",
                    Registry.ITEM.getKey(stack.getItem()),
                    stack.hasTag() ? ", tag: " + stack.getTag() : "",
                    stack.getCount(),
                    i
            ));
        }

        StringBuilder detectCommand = new StringBuilder("execute as @e[type=item,tag=stack_0] at @s");
        for (int i = 1; i < recipeComponents.size(); i++) {
            detectCommand.append(String.format(" if entity @e[type=item,distance=..1,tag=stack_%d]", i));
        }
        detectCommand.append(String.format(" run summon minecraft:marker ~ ~ ~ {Tags:[p_%s]}", buildingTagId));
        FunctionWriter.state(detectCommand.toString());

        FunctionWriter.state(String.format("execute as @e[type=marker,tag=p_%1$s] if entity @e[type=marker,tag=%1$s,distance=..%2$d] run tag @s add invalid", buildingTagId, MIN_DISTANCE));

        for (int i = 0; i < recipeComponents.size(); i++) {
            FunctionWriter.state(String.format(
                    "execute as @e[type=marker,tag=p_%s,tag=!invalid] at @s as @e[tag=stack_%d,distance=..1,limit=1] run scoreboard players remove @s item_count %d",
                    buildingTagId,
                    i,
                    recipeComponents.get(i).getCount()
            ));
            FunctionWriter.state(String.format(
                    "execute as @e[type=marker,tag=p_%s,tag=!invalid] at @s as @e[tag=stack_%d,distance=..1] store result entity @s Item.Count byte 1 run scoreboard players get @s item_count",
                    buildingTagId,
                    i
            ));
        }

        FunctionWriter.state(String.format("execute as @e[type=marker,tag=p_%s,tag=!invalid] at @s as @e[tag=stack_0,distance=..1,limit=1] run tag @s add key_stack",buildingTagId));
        FunctionWriter.state(String.format("execute as @a run function flytre:recipes/%s_player",buildingTagId));
        FunctionWriter.makeFunctionAndSetLoc(String.format("recipes/%s_player",buildingTagId));
        FunctionWriter.state("data modify storage flytre:uuid uuid set from entity @s UUID");
        FunctionWriter.state("execute store result score modif item_count run data modify storage flytre:uuid uuid set from entity @e[tag=key_stack,limit=1] Thrower");
        FunctionWriter.state("execute if score modif item_count matches 0 run tag @s add summoner_player");


        FunctionWriter.setLoc(String.format("recipes/%s", buildingTagId));

        FunctionWriter.state(String.format("execute as @e[type=marker,tag=p_%1$s,tag=!invalid] run summon minecraft:marker ~ ~ ~ {Tags:[%1$s]}", buildingTagId));
        FunctionWriter.state(String.format("execute as @e[type=marker,tag=p_%1$s,tag=!invalid] run say @e[tag=summoner_player] SPAWNED %1$s", buildingTagId));

        FunctionWriter.state(String.format("kill @e[type=marker,tag=p_%s]", buildingTagId));


        for (int i = 0; i < recipeComponents.size(); i++) {
            FunctionWriter.state(String.format("tag @e[type=item] remove stack_%d", i));
        }
        FunctionWriter.state("tag @e[type=item] remove key_stack");
        FunctionWriter.state("tag @e[type=player] remove summoner_player");


    }

    public static void generateInit() {
        FunctionWriter.addObjective("item_count");
        FunctionWriter.makeFunctionAndSetLoc("recipes/detect");
        FunctionWriter.state("execute as @e[type=item] store result score @s item_count run data get entity @s Item.Count");
    }

    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        FileHandler.deleteDirectory("base_generated");
        FileHandler.createDatapack("base_generated");
        FunctionWriter.setName("base_generated");

        BuildingRecipe.generateInit();

        BuildingRecipe recipe = new BuildingRecipe("test_building", List.of(new ItemStack(Items.STONE, 32), new ItemStack(Items.OAK_LOG, 32)));
        recipe.generate();
    }
}
