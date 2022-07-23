####################################################################################################
#Java enhanced file
#Created: 07/23/2022 14:14
####################################################################################################


data modify storage flytre:uuid uuid set from entity @s UUID
execute store result score modif item_count run data modify storage flytre:uuid uuid set from entity @e[tag=key_stack,limit=1] Thrower
execute if score modif item_count matches 0 run tag @s add summoner_player
