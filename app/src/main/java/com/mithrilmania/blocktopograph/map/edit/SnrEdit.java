package com.mithrilmania.blocktopograph.map.edit;

import androidx.annotation.NonNull;

import com.mithrilmania.blocktopograph.block.Block;
import com.mithrilmania.blocktopograph.block.OldBlock;
import com.mithrilmania.blocktopograph.chunk.Chunk;


public class SnrEdit implements EditTarget.RandomAccessEdit {

    private final SnrConfig config;
    private SnrConfig.SearchConditionBlock b1;
    private SnrConfig.SearchConditionBlock b2;
    private Block b3;
    private Block b4;

    SnrEdit(@NonNull SnrConfig cfg) {
        config = cfg;
        switch (cfg.searchMode) {
            case 1:
            case 2:
            case 3:
                b1 = cfg.searchBlockMain;
                break;
            case 4:
                b1 = cfg.searchBlockMain;
                b2 = cfg.searchBlockSub;
                break;
        }
        switch (cfg.placeMode) {
            case 1:
            case 2:
                b3 = cfg.placeOldBlockMain;
                break;
            case 3:
                b3 = cfg.placeOldBlockMain;
                b4 = cfg.placeOldBlockSub;
                break;
        }
    }

    @Override
    public int edit(Chunk chunk, int x, int y, int z) {
        if (
                (config.searchMode == 1 && b1.matches(chunk.getBlock(x, y, z, 1))
                ) || (config.searchMode == 2 && b1.matches(chunk.getBlock(x, y, z))
                ) || (config.searchMode == 3 && (
                        b1.matches(chunk.getBlock(x, y, z))
                                || b1.matches(chunk.getBlock(x, y, z, 1)))
                ) || (config.searchMode == 4 && (
                        b1.matches(chunk.getBlock(x, y, z))
                                && b2.matches(chunk.getBlock(x, y, z, 1)))
                )
        ) {
            switch (config.placeMode) {
                case 1:
                    chunk.setBlock(x, y, z, 1, b3);
                    break;
                case 2:
                    chunk.setBlock(x, y, z, 0, b3);
                    break;
                case 3:
                    chunk.setBlock(x, y, z, 0, b3);
                    chunk.setBlock(x, y, z, 1, b4);
                    break;
            }
        }
        return 0;
    }

}
