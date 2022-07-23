package net.flytre.yeggs_jam;

import net.flytre.gen.io.FileHandler;
import net.flytre.gen.io.FunctionWriter;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class StructureToSetblock {

    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_ENTRY_TO_STRING_FUNCTION = new Function<Map.Entry<Property<?>, Comparable<?>>, String>() {

        @Override
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            }
            Property<?> property = entry.getKey();
            return property.getName() + "=" + this.getName(property, entry.getValue());
        }

        private <T extends Comparable<T>> String getName(Property<T> property, Comparable<?> comparable) {
            return property.getName((T) comparable);
        }
    };

    private final List<String> palette = new ArrayList<>();
    private final Map<Integer, List<BlockRecord>> blocks = new TreeMap<>();
    private final List<StructureTemplate.StructureEntityInfo> entityInfoList = new ArrayList<>();


    record BlockRecord(BlockPos pos, int paletteIndex, @Nullable CompoundTag nbt) {

    }

    public StructureToSetblock(File in) throws IOException {
        CompoundTag nbt = NbtIo.readCompressed(in);
        ListTag palette = nbt.getList(StructureTemplate.PALETTE_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < palette.size(); i++) {
            BlockState state = NbtUtils.readBlockState(palette.getCompound(i));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Registry.BLOCK.getKey(state.getBlock()));
            if (!state.getValues().isEmpty()) {
                stringBuilder.append('[');
                stringBuilder.append(state.getValues().entrySet().stream().map(PROPERTY_ENTRY_TO_STRING_FUNCTION).collect(Collectors.joining(",")));
                stringBuilder.append(']');
            }
            this.palette.add(stringBuilder.toString());
        }
        ListTag blocks = nbt.getList(StructureTemplate.BLOCKS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            var listPos = block.getList("pos", Tag.TAG_INT);
            BlockPos pos = new BlockPos(listPos.getInt(0), listPos.getInt(1), listPos.getInt(2));
            BlockRecord record = new BlockRecord(pos, block.getInt("state"), block.contains("nbt") ? block.getCompound("nbt") : null);
            this.blocks.computeIfAbsent(pos.getY(), __ -> new ArrayList<>()).add(record);
        }
        ListTag entities = nbt.getList(StructureTemplate.ENTITIES_TAG, 10);
        for (int i = 0; i < entities.size(); ++i) {
            CompoundTag entity = entities.getCompound(i);
            ListTag posList = entity.getList("pos", 6);
            Vec3 pos = new Vec3(posList.getDouble(0), posList.getDouble(1), posList.getDouble(2));
            ListTag blockPosList = entity.getList(StructureTemplate.ENTITY_TAG_BLOCKPOS, 3);
            BlockPos blockPos = new BlockPos(blockPosList.getInt(0), blockPosList.getInt(1), blockPosList.getInt(2));
            if (!entity.contains("nbt")) continue;
            CompoundTag entityNbt = entity.getCompound("nbt");
            this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(pos, blockPos, entityNbt));
        }
    }

    public void generate(String id) {

        FunctionWriter.makeFunctionAndSetLoc("structure/generate_loop");

        FunctionWriter.state("execute as @e[type=marker] at @s if score @s " + id + "_t matches 1.. run function flytre:" + id + "/base");

        FunctionWriter.makeFunction(id + "/base");
        FunctionWriter.addObjective(id + "_t");
        int i = 0;
        for (var entry : blocks.entrySet()) {
            FunctionWriter.makeFunctionAndSetLoc(id + "/layer_" + entry.getKey());
            FunctionWriter.addStatement(id + "/base", "execute if score @s " + id + "_t matches " + ((i++) * 3 + 1) + " run function flytre:" + id + "/layer_" + entry.getKey());
            for (BlockRecord record : entry.getValue()) {
                FunctionWriter.state(String.format("setblock ~%d ~%d ~%d %s%s", record.pos().getX(), record.pos().getY(), record.pos().getZ(), palette.get(record.paletteIndex), record.nbt() == null ? "" : record.nbt()));
            }
        }

        FunctionWriter.makeFunctionAndSetLoc(id + "/entities");
        for(var structureEntityInfo : entityInfoList) {
            CompoundTag compoundTag = structureEntityInfo.nbt.copy();
            ListTag listTag = new ListTag();
            listTag.add(DoubleTag.valueOf(structureEntityInfo.pos.x));
            listTag.add(DoubleTag.valueOf(structureEntityInfo.pos.y));
            listTag.add(DoubleTag.valueOf(structureEntityInfo.pos.z));
            compoundTag.put("Pos", listTag);
            compoundTag.remove("UUID");
            Optional<EntityType<?>> entity = EntityType.by(compoundTag);
            if(entity.isEmpty())
                continue;
            EntityType<?> entityType = entity.get();
            String idStr = Registry.ENTITY_TYPE.getKey(entityType).toString();
            FunctionWriter.state(String.format("summon %s ~%f ~%f ~%f %s",idStr,structureEntityInfo.pos.x,structureEntityInfo.pos.y,structureEntityInfo.pos.z,compoundTag));
        }
        FunctionWriter.addStatement(id + "/base", "execute if score @s " + id + "_t matches " + (i * 3 - 2) + " run function flytre:" + id + "/entities");
        FunctionWriter.addStatement(id + "/base", "execute if score @s " + id + "_t matches " + (i * 3 - 2) + " run scoreboard players set @s " + id + "_t 0");
        FunctionWriter.addStatement(id + "/base", "execute if score @s " + id + "_t matches 1.. run scoreboard players add @s " + id + "_t 1");

    }

    public static void main(String[] args) throws IOException {
        FileHandler.deleteDirectory("structure_setblock");
        FileHandler.createDatapack("structure_setblock");
        FunctionWriter.setName("structure_setblock");


        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        StructureToSetblock s2s = new StructureToSetblock(new File("/Users/aaron/IdeaProjects/MinecraftCore/resources/data/minecraft/structures/ruined_portal/giant_portal_1.nbt"));
        System.out.println(s2s.palette);
        for (var entry : s2s.blocks.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        s2s.generate("test_struct");
    }
}
