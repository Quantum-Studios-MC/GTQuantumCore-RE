package com.quantumstudios.gtquantumcore.api;

public interface IBellowHatch {
    /**
     * Gets the parallel processing bonus (e.g., 4 for 4x parallel recipes)
     */
    int getParallelBonus();

    /**
     * Returns true if the bellow is active
     */
    boolean isActive();
}
