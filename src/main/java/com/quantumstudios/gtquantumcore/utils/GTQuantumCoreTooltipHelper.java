package com.quantumstudios.gtquantumcore.utils;

import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static gregtech.api.GTValues.CLIENT_TIME;

public class GTQuantumCoreTooltipHelper extends TooltipHelper {

    private static final List<GTQuantumCoreFormatCode> CODES = new ArrayList<>();

    public static GTQuantumCoreFormatCode createNewCode(int rate, TextFormatting[]... codes) {
        GTQuantumCoreFormatCode code = new GTQuantumCoreFormatCode(rate, codes);
        CODES.add(code);
        return code;
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CODES.forEach(GTQuantumCoreFormatCode::updateIndex);
        }
    }

    public static class GTQuantumCoreFormatCode {

        private final int rate;
        private final TextFormatting[][] codes;
        private int index = 0;

        private GTQuantumCoreFormatCode(int rate, TextFormatting[]... codes) {
            this.rate = rate;
            this.codes = codes;
        }

        private void updateIndex() {
            if (CLIENT_TIME % rate == 0 && !ConfigHolder.client.preventBlinkingTooltips) {
                if (index + 1 >= codes.length) index = 0;
                else index++;
            }
        }

        @Override
        public String toString() {
            return Arrays.stream(codes[index]).map(TextFormatting::toString).collect(Collectors.joining());
        }
    }

    public static class SFormatCode {

        private final int rate;
        private final TextFormatting[][] codes;
        private int index = 0;

        private SFormatCode(int rate, TextFormatting[]... codes) {
            this.rate = rate;
            this.codes = codes;
        }

        private void updateIndex() {
            if (CLIENT_TIME % rate == 0 && !ConfigHolder.client.preventBlinkingTooltips) {
                if (index + 1 >= codes.length) index = 0;
                else index++;
            }
        }

        @Override
        public String toString() {
            return Arrays.stream(codes[index]).map(TextFormatting::toString).collect(Collectors.joining());
        }
    }

    // Generic multiblock tooltips
    public static void addGTQuantumCoreInformation(List<String> tooltip) {
//        tooltip.add(GTQuantumCoreValues.FORMAT_IRIS_1 + I18n.format("gtquantumcore.author.iris.1") +
//                GTQuantumCoreValues.FORMAT_IRIS_2 + I18n.format("serendustry.machine.author.iris.2") +
//                GTQuantumCoreValues.FORMAT_IRIS_3 + I18n.format("serendustry.machine.author.iris.3"));
    }

}