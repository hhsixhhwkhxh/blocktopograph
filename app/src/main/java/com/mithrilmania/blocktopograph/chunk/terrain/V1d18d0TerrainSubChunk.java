package com.mithrilmania.blocktopograph.chunk.terrain;

import androidx.annotation.NonNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mithrilmania.blocktopograph.BuildConfig;
import com.mithrilmania.blocktopograph.Log;
import com.mithrilmania.blocktopograph.WorldData;
import com.mithrilmania.blocktopograph.block.Block;
import com.mithrilmania.blocktopograph.block.BlockTemplate;
import com.mithrilmania.blocktopograph.block.BlockTemplates;
import com.mithrilmania.blocktopograph.block.blockproperty.BlockProperty;
import com.mithrilmania.blocktopograph.chunk.ChunkTag;
import com.mithrilmania.blocktopograph.map.Dimension;
import com.mithrilmania.blocktopograph.nbt.convert.NBTInputStream;
import com.mithrilmania.blocktopograph.nbt.tags.ByteTag;
import com.mithrilmania.blocktopograph.nbt.tags.CompoundTag;
import com.mithrilmania.blocktopograph.nbt.tags.IntTag;
import com.mithrilmania.blocktopograph.nbt.tags.StringTag;
import com.mithrilmania.blocktopograph.util.LittleEndianOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class V1d18d0TerrainSubChunk extends TerrainSubChunk {

    public V1d18d0TerrainSubChunk(ByteBuffer raw){
        raw.order(ByteOrder.LITTLE_ENDIAN);
        //压缩版本
        byte storageVersion = raw.get();
        int bitsPerBlock = storageVersion >> 1;
        int blocksPerWord = Integer.SIZE / bitsPerBlock;
        int numints = (4095 / blocksPerWord) + 1;//int个数
        System.out.printf("要读取:%d个int%n", numints);
        System.out.printf("占用大小:%d(0x%X)%n", numints * Integer.BYTES, numints * Integer.BYTES);

        //先读取每条索引
        int[] maskValues = new int[numints];
        for (int j = 0; j < numints; j++) {
            maskValues[j] = raw.getInt();//小端int
        }

        //创建此分区4096个方块索引
        int[] indexes = new int[16 * 16 * 16];
        int blockMask = (1 << bitsPerBlock) - 1;//掩码
        //根据每条索引填充数据
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    //4096索引坐标
                    int index = (x << 8) | (z << 4) | y;
                    //计算在maskValues数组中的位置
                    int wordIndex = index / blocksPerWord;
                    int shift = (index % blocksPerWord) * bitsPerBlock;
                    //设置索引
                    indexes[wordIndex] = (maskValues[wordIndex] >> shift) & blockMask;
                    System.out.printf("(%d, %d, %d):%d%n", x, y, z, indexes[wordIndex]);
                }
            }
        }
    }
    public V1d18d0TerrainSubChunk(){

    }
    @NonNull
    @Override
    public BlockTemplate getBlockTemplate(int x, int y, int z, int layer) {
        return null;
    }

    @NonNull
    @Override
    public Block getBlock(int x, int y, int z, int layer) {
        return null;
    }

    @Override
    public void setBlock(int x, int y, int z, int layer, @NonNull Block block) {

    }

    @Override
    public int getBlockLightValue(int x, int y, int z) {
        return 0;
    }

    @Override
    public int getSkyLightValue(int x, int y, int z) {
        return 0;
    }

    @Override
    public void save(WorldData worldData, int chunkX, int chunkZ, Dimension dimension, int which) throws WorldData.WorldDBException, IOException {

    }
}