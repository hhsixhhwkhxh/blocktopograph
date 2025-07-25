package com.mithrilmania.blocktopograph.map.edit;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.common.collect.Streams;
import com.mithrilmania.blocktopograph.block.Block;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class SnrConfig implements Serializable {

    private static final String KEY_SEARCH_IN = "search_in";
    private static final String KEY_PLACE_IN = "place_in";
    private static final String KEY_SEARCH_ANY = "search_any";
    private static final String KEY_SEARCH_BG = "search_bg";
    private static final String KEY_SEARCH_FG = "search_fg";
    private static final String KEY_PLACE_ANY = "place_any";
    private static final String KEY_PLACE_BG = "place_bg";
    private static final String KEY_PLACE_FG = "place_fg";

    public boolean ignoreSubId;
    public int searchMode;
    public int placeMode;
    public SearchConditionBlock searchBlockMain;
    public SearchConditionBlock searchBlockSub;
    public Block placeOldBlockMain;
    public Block placeOldBlockSub;

    //savedInstanceState.getInt(KEY_SEARCH_IN, 0)
    //savedInstanceState.getInt(KEY_PLACE_IN, 0)
    //savedInstanceState.getSerializable(KEY_SEARCH_ANY)
    //savedInstanceState.getSerializable(KEY_SEARCH_BG)
    //savedInstanceState.getSerializable(KEY_SEARCH_FG)
    //savedInstanceState.getSerializable(KEY_PLACE_ANY)
    //savedInstanceState.getSerializable(KEY_PLACE_BG)
    //savedInstanceState.getSerializable(KEY_PLACE_FG)


//        bundle.putInt(KEY_SEARCH_IN, getSearchInCode());
//        bundle.putInt(KEY_PLACE_IN, getPlaceInCode());
//        bundle.putSerializable(KEY_SEARCH_ANY, mBinding.searchBlockAny.getBlock());
//        bundle.putSerializable(KEY_SEARCH_BG, mBinding.searchBlockBg.getBlock());
//        bundle.putSerializable(KEY_SEARCH_FG, mBinding.searchBlockFg.getBlock());
//        bundle.putSerializable(KEY_PLACE_ANY, mBinding.replaceBlockAny.getBlock());
//        bundle.putSerializable(KEY_PLACE_BG, mBinding.replaceBlockBg.getBlock());
//        bundle.putSerializable(KEY_PLACE_FG, mBinding.replaceBlockFg.getBlock());

    public static class SearchConditionBlock implements Serializable {
        private final Block examplar;
        private final boolean matchNameOnly;
        private final boolean allowExtraStates;

        public SearchConditionBlock(@NonNull Block block, boolean matchNameOnly, boolean allowExtraStates) {
            this.examplar = block;
            this.matchNameOnly = matchNameOnly;
            this.allowExtraStates = allowExtraStates;
        }

        public boolean matches(Block block) {
            if (!examplar.getName().equals(block.getName())) return false;
            var examplarKnownProperties = examplar.getKnownProperties();
            if (examplarKnownProperties != null && Streams.zip(Arrays.stream(examplarKnownProperties),
                    Arrays.stream(block.getKnownProperties()), Pair::new)
                    .anyMatch(pair -> pair.first != null && !Objects.equals(pair.first, pair.second))) {
                return false;
            }
            var examplarCustomProperties = examplar.getCustomProperties();
            var blockCustomProperties = block.getCustomProperties();
            if (!allowExtraStates && blockCustomProperties.size() > examplarCustomProperties.size())
                return false;
            for (var pair : examplarCustomProperties.entrySet())
                if (!Objects.equals(pair.getValue(), blockCustomProperties.get(pair.getKey())))
                    return false;
            return true;
        }
    }
}
