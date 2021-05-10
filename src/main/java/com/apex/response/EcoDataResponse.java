package com.apex.response;

import com.apex.model.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EcoDataResponse {
    private List<CraftingTable> craftingTables;
    private List<UpgradeModule> upgradeModules;
    private List<Item> items;
    private List<Recipe> recipes;
    private List<Skill> skills;
    private List<Ingredient> ingredients;
    private List<Output> outputs;
}
