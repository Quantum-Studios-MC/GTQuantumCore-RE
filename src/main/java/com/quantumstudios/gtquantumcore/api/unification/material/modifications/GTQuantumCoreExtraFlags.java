package com.quantumstudios.gtquantumcore.api.unification.material.modifications;

import com.quantumstudios.gtquantumcore.api.unification.material.info.GTQuantumCoreMaterialFlags;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialFlag;

import java.util.List;

import static gregtech.api.unification.material.Materials.*;

public class GTQuantumCoreExtraFlags {

    public static void setFlags(Material[] materials, MaterialFlag... flags) {
        for (Material material : materials) {
            material.addFlags(flags);
        }
    }

    public static void setFlags(List<Material> materials, MaterialFlag... flags) {
        for (Material material : materials) {
            material.addFlags(flags);
        }
    }

    public static void register() {
        mesh();
        billet();
        shell();
        densewire();
        ntmpipe();
        plateTriple();
        plateSextuple();
        piston();
        hook();
        tablet();
        curvedplate();
        thread();
        yarn();
        structural();
        bar();
        structural_c();
        cake();
        briquette();
    }

    private static void mesh() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_MESH);
    }

    private static void billet() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_BILLET);
    }

    private static void shell() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_SHELL);
    }

    private static void densewire() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_DENSE_WIRE);
    }

    private static void ntmpipe() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_NTMPIPE);
    }

    private static void plateTriple() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_CAST_PLATE);
    }

    private static void plateSextuple() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_WELDED_PLATE);
    }

    private static void piston() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};

        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_PISTON);
    }

    private static void hook() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_HOOK);
    }

    private static void tablet() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_TABLET);
    }

    private static void curvedplate() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_CURVED_PLATE);
    }

    private static void thread() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_THREAD);
    }

    private static void yarn() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_YARN);
    }

    private static void cake() {
        Material[] materials = {RedAlloy, WroughtIron, Iron, Steel, Gold, Silver, Uranium235};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_CAKE);
    }

    private static void structural() {
        Material[] materials = {RedAlloy, Iron, Steel, Gold};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_STRUCTURAL);
    }

    private static void bar() {
        Material[] materials = {Iron, Steel};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_BAR);

    }

    private static void structural_c() {
        Material[] materials = {Iron};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_STRUCTURAL_C);

    }

    private static void briquette() {
        Material[] materials = {Coal, Coke, Charcoal};
        setFlags(materials, GTQuantumCoreMaterialFlags.GENERATE_BRIQUETTE);

    }

}