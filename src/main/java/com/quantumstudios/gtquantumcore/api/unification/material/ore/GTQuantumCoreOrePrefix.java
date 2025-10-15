package com.quantumstudios.gtquantumcore.api.unification.material.ore;

import com.quantumstudios.gtquantumcore.api.unification.material.info.GTQuantumCoreMaterialFlags;
import com.quantumstudios.gtquantumcore.api.unification.material.info.GTQuantumCoreMaterialIconType;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.info.MaterialIconType;
import gregtech.api.unification.ore.OrePrefix;

import static gregtech.api.GTValues.M;
import static gregtech.api.unification.ore.OrePrefix.Flags.ENABLE_UNIFICATION;

public class GTQuantumCoreOrePrefix {

    public static final OrePrefix mesh = new OrePrefix("mesh", M / 2, null, GTQuantumCoreMaterialIconType.mesh,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_MESH));

    public static final OrePrefix billet = new OrePrefix("billet", (M + 1) / 2 + 3
            , null, GTQuantumCoreMaterialIconType.billet,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_BILLET));

    public static final OrePrefix wireDense = new OrePrefix("wireDense", M, null, GTQuantumCoreMaterialIconType.wiredense,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_DENSE_WIRE));

    public static final OrePrefix ntmpipe = new OrePrefix("ntmpipe", M * 3, null, GTQuantumCoreMaterialIconType.ntmpipe,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_NTMPIPE));

    public static final OrePrefix shell = new OrePrefix("shell", M * 4, null, GTQuantumCoreMaterialIconType.shell,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_SHELL));

    public static final OrePrefix plateTriple = new OrePrefix("plateTriple", M * 3, null, GTQuantumCoreMaterialIconType.plateWelded,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_CAST_PLATE));

    public static final OrePrefix plateSextuple = new OrePrefix("plateSextuple", M * 6, null, GTQuantumCoreMaterialIconType.plateSextuple,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_WELDED_PLATE));

    public static final OrePrefix piston = new OrePrefix("piston", M * 4, null, GTQuantumCoreMaterialIconType.piston,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_PISTON));

    public static final OrePrefix hook = new OrePrefix("hook", M * 2, null, GTQuantumCoreMaterialIconType.hook,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_HOOK));

    public static final OrePrefix tablet = new OrePrefix("tablet", M * 8, null, GTQuantumCoreMaterialIconType.tablet,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_TABLET));

    public static final OrePrefix curvedplate = new OrePrefix("curvedplate", M, null, GTQuantumCoreMaterialIconType.curvedplate,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_CURVED_PLATE));

    public static final OrePrefix thread = new OrePrefix("thread", -1, null, GTQuantumCoreMaterialIconType.thread,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_THREAD));

    public static final OrePrefix yarn = new OrePrefix("yarn", -1, null, GTQuantumCoreMaterialIconType.yarn,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_YARN));

    public static final OrePrefix structural = new OrePrefix("structural", -1, null, GTQuantumCoreMaterialIconType.structural,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_STRUCTURAL));

    public static final OrePrefix structural_c = new OrePrefix("structural_c", -1, null, GTQuantumCoreMaterialIconType.structural_c,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_STRUCTURAL_C));

    public static final OrePrefix bar = new OrePrefix("bar", -1, null, GTQuantumCoreMaterialIconType.bar,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_BAR));

    public static final OrePrefix cake = new OrePrefix("cake", -1, null, GTQuantumCoreMaterialIconType.cake,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_CAKE));

    public static final OrePrefix briquette = new OrePrefix("briquette", -1, null, GTQuantumCoreMaterialIconType.briquette,
            ENABLE_UNIFICATION, mat -> mat.hasFlag(GTQuantumCoreMaterialFlags.GENERATE_BRIQUETTE));
}
