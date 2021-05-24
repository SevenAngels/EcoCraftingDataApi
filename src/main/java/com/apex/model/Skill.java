package com.apex.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Skill implements Comparable<Skill> {
    private String name;
    private String nameID;
    private boolean basicUpgrade;
    private boolean advancedUpgrade;
    private boolean modernUpgrade;
    private boolean lavishWorkspace;

    @Override
    public int compareTo(Skill o) {
        return name.compareTo(o.getName());
    }
}
