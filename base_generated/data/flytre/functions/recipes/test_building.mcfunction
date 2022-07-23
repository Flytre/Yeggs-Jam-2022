####################################################################################################
#Java enhanced file
#Created: 07/23/2022 14:14
####################################################################################################


execute as @e[type=item,nbt={Item:{id:"minecraft:stone"}}] if score @s item_count matches 32.. run tag @s add stack_0
execute as @e[type=item,nbt={Item:{id:"minecraft:oak_log"}}] if score @s item_count matches 32.. run tag @s add stack_1
execute as @e[type=item,tag=stack_0] at @s if entity @e[type=item,distance=..1,tag=stack_1] run summon minecraft:marker ~ ~ ~ {Tags:[p_test_building]}
execute as @e[type=marker,tag=p_test_building] if entity @e[type=marker,tag=test_building,distance=..12] run tag @s add invalid
execute as @e[type=marker,tag=p_test_building,tag=!invalid] at @s as @e[tag=stack_0,distance=..1,limit=1] run scoreboard players remove @s item_count 32
execute as @e[type=marker,tag=p_test_building,tag=!invalid] at @s as @e[tag=stack_0,distance=..1] store result entity @s Item.Count byte 1 run scoreboard players get @s item_count
execute as @e[type=marker,tag=p_test_building,tag=!invalid] at @s as @e[tag=stack_1,distance=..1,limit=1] run scoreboard players remove @s item_count 32
execute as @e[type=marker,tag=p_test_building,tag=!invalid] at @s as @e[tag=stack_1,distance=..1] store result entity @s Item.Count byte 1 run scoreboard players get @s item_count
execute as @e[type=marker,tag=p_test_building,tag=!invalid] at @s as @e[tag=stack_0,distance=..1,limit=1] run tag @s add key_stack
execute as @a run function flytre:recipes/test_building_player
execute as @e[type=marker,tag=p_test_building,tag=!invalid] run summon minecraft:marker ~ ~ ~ {Tags:[test_building]}
execute as @e[type=marker,tag=p_test_building,tag=!invalid] run say @e[tag=summoner_player] SPAWNED test_building
kill @e[type=marker,tag=p_test_building]
tag @e[type=item] remove stack_0
tag @e[type=item] remove stack_1
tag @e[type=item] remove key_stack
tag @e[type=player] remove summoner_player
