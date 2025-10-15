package com.quantumstudios.gtquantumcore.api.unification.material.info;

import gregtech.api.unification.material.info.MaterialFlag;
import gregtech.api.unification.material.info.MaterialFlags;
import gregtech.api.unification.material.properties.PropertyKey;

public class GTQuantumCoreMaterialFlags {
    public static final MaterialFlag GENERATE_MESH = new MaterialFlag.Builder("generate_mesh")
            .requireProps(PropertyKey.WIRE)
            .requireFlags(MaterialFlags.GENERATE_FINE_WIRE)
            .build();

    public static final MaterialFlag GENERATE_BILLET = new MaterialFlag.Builder("generate_billet")
            .requireProps(PropertyKey.INGOT)
            .build();

    public static final MaterialFlag GENERATE_SHELL = new MaterialFlag.Builder("generate_shell")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_NTMPIPE = new MaterialFlag.Builder("generate_ntmpipe")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_DENSE_WIRE = new MaterialFlag.Builder("generate_dense_wire")
            .requireProps(PropertyKey.WIRE)
            .requireFlags(MaterialFlags.GENERATE_FINE_WIRE)
            .build();

    public static final MaterialFlag GENERATE_CAST_PLATE = new MaterialFlag.Builder("generate_cast_plate")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_WELDED_PLATE = new MaterialFlag.Builder("generate_welded_plate")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_PISTON = new MaterialFlag.Builder("generate_piston")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .requireFlags(MaterialFlags.GENERATE_RING)
            .requireFlags(MaterialFlags.GENERATE_ROD)
            .build();

    public static final MaterialFlag GENERATE_HOOK = new MaterialFlag.Builder("generate_hook")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_BOLT_SCREW)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_TABLET = new MaterialFlag.Builder("generate_tablet")
            .requireProps(PropertyKey.INGOT)
            .build();


    public static final MaterialFlag GENERATE_CURVED_PLATE = new MaterialFlag.Builder("generate_curved_plate")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .build();

    public static final MaterialFlag GENERATE_THREAD = new MaterialFlag.Builder("generate_thread")
            .build();

    public static final MaterialFlag GENERATE_YARN = new MaterialFlag.Builder("generate_yarn")
            .build();

    public static final MaterialFlag GENERATE_CAKE = new MaterialFlag.Builder("generate_cake")
            .build();

    public static final MaterialFlag GENERATE_STRUCTURAL = new MaterialFlag.Builder("generate_structure")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_BOLT_SCREW)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .requireFlags(MaterialFlags.GENERATE_ROD)
            .build();

    public static final MaterialFlag GENERATE_BAR = new MaterialFlag.Builder("generate_bar")
            .requireProps(PropertyKey.INGOT)
            .requireFlags(MaterialFlags.GENERATE_LONG_ROD)
            .build();

    public static final MaterialFlag GENERATE_STRUCTURAL_C = new MaterialFlag.Builder("generate_structure_c")
            .requireFlags(MaterialFlags.GENERATE_BOLT_SCREW)
            .requireFlags(MaterialFlags.GENERATE_PLATE)
            .requireFlags(MaterialFlags.GENERATE_LONG_ROD)
            .build();

    public static final MaterialFlag GENERATE_BRIQUETTE = new MaterialFlag.Builder("generate_briquette")
            .build();

}
