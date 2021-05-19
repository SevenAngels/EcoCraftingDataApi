package com.apex;

import com.apex.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static com.apex.db.DbService.*;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        EcoDataResponse ecoData = EcoDataResponse.builder()
                .craftingTables(getAllCraftingTables())
                .upgradeModules(getAllUpgradeModules())
                .skills(getAllSkills())
                .items(getAllItems())
                .ingredients(getAllIngredients())
                .outputs(getAllOutputs())
                .recipes(getAllRecipes())
                .build();

        //Select some random skills
        Set<Skill> selectedSkills = new TreeSet<>(Comparator.comparing(Skill::getName));
        List<String> skillNames = Arrays.asList("Logging", "Carpentry");
        for (Skill s : ecoData.getSkills()) {
            if (skillNames.contains(s.getName())) {
                selectedSkills.add(s);
            }
        }

        getUniqueIngredientsForSkills(ecoData, selectedSkills);
        getRecipeOutputsForSkills(ecoData, selectedSkills);

    }

    private static Collection<Recipe> getRecipeOutputsForSkills(EcoDataResponse ecoData, Collection<Skill> selectedSkills) {
        LOGGER.info("Selected skills:");
        for (Skill s : selectedSkills) {
            LOGGER.info(s.getName());
        }

        Set<Recipe> recipes = new TreeSet<>();
        for (Skill s : selectedSkills) {
            recipes.addAll(ecoData.getRecipes().stream()
                    .filter(recipe -> recipe.getSkill().getNameID().equals(s.getNameID()))
                    .filter(recipe -> {
                        boolean shouldAdd = true;
                        for (Output o : recipe.getOutputs()) {
                            String searchString = recipe.getSkill().getName().replace(" ", "");
                            if (o.getItem().getNameID().contains(searchString)) {
                                shouldAdd = false;
                                break;
                            }
                        }
                        return shouldAdd;
                    })
                    .collect(Collectors.toSet()));
        }

        LOGGER.info("Final recipe list for display:");

        for (Recipe r : recipes) {
            LOGGER.info(r.getName());
        }

        return recipes;
    }

    private static Set<Item> getUniqueIngredientsForSkills(EcoDataResponse ecoData, Collection<Skill> selectedSkills) {
        LOGGER.info("Selected skills:");
        for (Skill s : selectedSkills) {
            LOGGER.info(s.getName());
        }

        //Get all recipes for the selected skills
        Set<Recipe> recipes = new HashSet<>();
        for (Skill s : selectedSkills) {
            recipes.addAll(ecoData.getRecipes().stream()
                    .filter(recipe -> recipe.getSkill().getNameID().equals(s.getNameID()))
                    .collect(Collectors.toSet()));
        }
        Set<Item> ingredients = new TreeSet<>(Comparator.comparing(Item::getName));
        for (Recipe r : recipes) {
            for (Ingredient i : r.getIngredients()) {
                if (!i.getItem().getNameID().contains("Lvl4")) {
                    ingredients.add(i.getItem());
                }
            }
        }


        Set<Item> removedIngredients = new HashSet<>();
        //Check each recipe and see if any ingredients are outputs in other recipes
        for (Recipe r : recipes) {
            for (Ingredient i : r.getIngredients()) {
                for (Recipe r2 : recipes) {
                    if (!r.equals(r2)) {
                        for (Output o : r2.getOutputs()) {
                            if (!removedIngredients.contains(o.getItem()) && o.getItem().equals(i.getItem())) {
                                LOGGER.info("Removing ingredient {} from recipe {} since it is an output of recipe {} " +
                                        "in a valid selected skill.", i.getItem().getName(), r.getName(), r2.getName());
                                ingredients.remove(i.getItem());
                                removedIngredients.add(i.getItem());
                            }
                        }
                    }
                }
            }
        }

        LOGGER.info("Final item ingredient list for display:");

        for (Item i : ingredients) {
            String name = i.isTag() ? i.getName() + " (Tag)" : i.getName();
            LOGGER.info(name);
        }

        return ingredients;
    }

    private static int getRandomInteger(int bound) {
        return new Random().nextInt(bound);
    }
}
