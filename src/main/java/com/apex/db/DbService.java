package com.apex.db;

import com.apex.model.*;
import org.apache.log4j.Logger;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("squid:S1192")
public class DbService {

    private static final Logger LOGGER = Logger.getLogger(DbService.class);

    private static final String JDBC_URL = System.getenv("jdbc_url");
    private static final String DB_USER = System.getenv("db_user");
    private static final String DB_PWD = System.getenv("db_pwd");

    private static final String SELECT_SKILLS_SQL = "SELECT SkillName, SkillNameID, BasicUpgrade, AdvancedUpgrade, " +
            "ModernUpgrade, LavishWorkspace FROM Skills";
    private static final String SELECT_TABLES_SQL = "SELECT Name, NameID, UpgradeModule FROM CraftingTables";
    private static final String SELECT_UPGRADES_SQL = "SELECT Name, TypeNameID, Modifier FROM UpgradeModules";
    private static final String SELECT_ITEMS_SQL = "SELECT Name, ItemNameID, Tag FROM ITEMS";
    private static final String SELECT_INGREDIENTS_SQL = "SELECT Quantity, Reducible, I.Name, I.Tag, I.ItemNameID " +
            "FROM Ingredients JOIN Items I on I.ItemNameID = Ingredients.ItemNameID";
    private static final String SELECT_OUTPUTS_SQL = "SELECT Quantity, Reducible, I.Name, I.Tag, I.ItemNameID " +
            "FROM Outputs JOIN Items I on I.ItemNameID = Outputs.ItemNameID";
    private static final String SELECT_RECIPES_SQL = "SELECT RecipeName, RecipeNameID, Level, Labor, S.SkillNameID, " +
            "SkillName, BasicUpgrade, AdvancedUpgrade, ModernUpgrade, " +
            "LavishWorkspace, CT.Name, NameID, UpgradeModule " +
            "FROM Recipes JOIN CraftingTables CT on CT.NameID = Recipes.CraftingTableNameID " +
            "JOIN Skills S on Recipes.SkillNameID = S.SkillNameID";
    private static final String SELECT_INGREDIENTS_BY_RECIPE_SQL = "SELECT Quantity, Reducible, I.Name, I.Tag, I.ItemNameID " +
            "FROM Ingredients JOIN Items I on I.ItemNameID = Ingredients.ItemNameID WHERE RecipeNameID = ?";
    private static final String SELECT_OUTPUTS_BY_RECIPE_SQL = "SELECT Quantity, Reducible, I.Name, I.Tag, I.ItemNameID " +
            "FROM Outputs JOIN Items I on I.ItemNameID = Outputs.ItemNameID WHERE RecipeNameID = ?";


    private DbService() {
    }

    public static List<CraftingTable> getAllCraftingTables() throws SQLException {

        LOGGER.info("Getting all crafting tables");
        List<CraftingTable> tables = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_TABLES_SQL);

            while (rs.next()) {
                CraftingTable upgrade = CraftingTable.builder()
                        .name(rs.getString("Name"))
                        .nameID(rs.getString("NameID"))
                        .upgradeModuleType(rs.getString("UpgradeModule"))
                        .build();
                tables.add(upgrade);
            }
        }

        connection.close();

        return tables;
    }

    public static List<UpgradeModule> getAllUpgradeModules() throws SQLException {
        LOGGER.info("Getting all upgrade modules");
        List<UpgradeModule> upgrades = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_UPGRADES_SQL);

            while (rs.next()) {
                UpgradeModule upgrade = UpgradeModule.builder()
                        .name(rs.getString("Name"))
                        .nameID(rs.getString("TypeNameID"))
                        .modifier(rs.getBigDecimal("Modifier"))
                        .build();
                upgrades.add(upgrade);
            }
        }

        connection.close();

        return upgrades;
    }

    public static List<Recipe> getAllRecipes() throws SQLException {
        LOGGER.info("Getting all recipes");
        List<Recipe> recipes = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_RECIPES_SQL);

            while (rs.next()) {
                String recipeNameID = rs.getString("RecipeNameID");
                Recipe recipe = Recipe.builder()
                        .name(rs.getString("RecipeName"))
                        .nameID(recipeNameID)
                        .skill(Skill.builder()
                                .name(rs.getString("SkillName"))
                                .nameID(rs.getString("SkillNameID"))
                                .basicUpgrade(rs.getBoolean("BasicUpgrade"))
                                .advancedUpgrade(rs.getBoolean("AdvancedUpgrade"))
                                .modernUpgrade(rs.getBoolean("ModernUpgrade"))
                                .lavishWorkspace(rs.getBoolean("LavishWorkspace"))
                                .build())
                        .level(rs.getInt("Level"))
                        .labor(rs.getInt("Labor"))
                        .craftingTable(CraftingTable.builder()
                                .name(rs.getString("Name"))
                                .nameID(rs.getString("NameID"))
                                .upgradeModuleType(rs.getString("UpgradeModule"))
                                .build())
                        .ingredients(getIngredientsForRecipe(recipeNameID))
                        .outputs(getOutputsForRecipe(recipeNameID))
                        .build();
                recipes.add(recipe);
            }
        }

        connection.close();
        return recipes;
    }

    public static List<Ingredient> getIngredientsForRecipe(String recipeNameID) throws SQLException {
        LOGGER.info("Getting ingredients for recipe " + recipeNameID);
        List<Ingredient> ingredients = new ArrayList<>();

        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_INGREDIENTS_BY_RECIPE_SQL)) {
            statement.setString(1, recipeNameID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = Ingredient.builder()
                        .item(Item.builder().name(rs.getString("Name"))
                                .nameID(rs.getString("ItemNameID"))
                                .tag(rs.getBoolean("Tag"))
                                .build())
                        .quantity(rs.getInt("Quantity"))
                        .reducible(rs.getBoolean("Reducible"))
                        .build();
                ingredients.add(ingredient);
            }
        }

        connection.close();
        return ingredients;
    }

    public static List<Output> getOutputsForRecipe(String recipeNameID) throws SQLException {
        LOGGER.info("Getting all outputs");
        List<Output> outputs = new ArrayList<>();

        Connection connection = getConnection();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_OUTPUTS_BY_RECIPE_SQL)) {
            statement.setString(1, recipeNameID);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Output output = Output.builder()
                        .item(Item.builder().name(rs.getString("Name"))
                                .nameID(rs.getString("ItemNameID"))
                                .tag(rs.getBoolean("Tag"))
                                .build())
                        .quantity(rs.getInt("Quantity"))
                        .reducible(rs.getBoolean("Reducible"))
                        .build();
                outputs.add(output);
            }
        }

        connection.close();
        return outputs;
    }

    public static List<Ingredient> getAllIngredients() throws SQLException {
        LOGGER.info("Getting all ingredients");
        List<Ingredient> ingredients = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_INGREDIENTS_SQL);

            while (rs.next()) {
                Ingredient ingredient = Ingredient.builder()
                        .item(Item.builder().name(rs.getString("Name"))
                                .nameID(rs.getString("ItemNameID"))
                                .tag(rs.getBoolean("Tag"))
                                .build())
                        .quantity(rs.getInt("Quantity"))
                        .reducible(rs.getBoolean("Reducible"))
                        .build();
                ingredients.add(ingredient);
            }
        }

        connection.close();
        return ingredients;
    }

    public static List<Output> getAllOutputs() throws SQLException {
        LOGGER.info("Getting all outputs");
        List<Output> outputs = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_OUTPUTS_SQL);

            while (rs.next()) {
                Output output = Output.builder()
                        .item(Item.builder().name(rs.getString("Name"))
                                .nameID(rs.getString("ItemNameID"))
                                .tag(rs.getBoolean("Tag"))
                                .build())
                        .quantity(rs.getInt("Quantity"))
                        .reducible(rs.getBoolean("Reducible"))
                        .build();
                outputs.add(output);
            }
        }

        connection.close();
        return outputs;
    }

    public static List<Item> getAllItems() throws SQLException {
        LOGGER.info("Getting all items");
        List<Item> items = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_ITEMS_SQL);

            while (rs.next()) {
                Item item = Item.builder()
                        .name(rs.getString("Name"))
                        .nameID(rs.getString("ItemNameID"))
                        .tag(rs.getBoolean("Tag"))
                        .build();
                items.add(item);
            }
        }

        connection.close();
        return items;
    }

    public static List<Skill> getAllSkills() throws SQLException {
        LOGGER.info("Getting all skills");
        List<Skill> skills = new ArrayList<>();

        Connection connection = getConnection();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(SELECT_SKILLS_SQL);

            while (rs.next()) {
                Skill skill = Skill.builder()
                        .name(rs.getString("SkillName"))
                        .nameID(rs.getString("SkillNameID"))
                        .basicUpgrade(rs.getBoolean("BasicUpgrade"))
                        .advancedUpgrade(rs.getBoolean("AdvancedUpgrade"))
                        .modernUpgrade(rs.getBoolean("ModernUpgrade"))
                        .lavishWorkspace(rs.getBoolean("LavishWorkspace"))
                        .build();
                skills.add(skill);
            }
        }

        connection.close();
        return skills;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PWD);
    }

}
