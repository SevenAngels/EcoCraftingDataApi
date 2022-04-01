package com.apex;

import com.apex.model.*;
import com.apex.model.adapters.RecipeAdapter;
import com.google.cloud.translate.v3beta1.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.apex.db.DbService.*;
import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("squid:S1144")
public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final String TRANSLATION_PATH = "C:\\Users\\aritc\\IdeaProjects\\EcoCraftingSupport\\locale\\";

    private static final String PROJECT_ID = "eco-crafting-api";

    private static final List<String> LANGUAGES = Arrays.asList("fr", "es", "de", "pl", "ru", "uk", "ko", "zh", "ja");


    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        printRecipeJson();
    }

    /**
     * Prints the recipe json to put into recipes.ts data file
     *
     * @throws SQLException
     */
    public static void printRecipeJson() throws SQLException {
        List<Recipe> recipes = getAllRecipes();
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(Recipe.class, new RecipeAdapter())
                .create();
        String recipeJson = gson.toJson(recipes);
        String newJson = recipeJson.replaceAll("\"(get[A-z]+ByNameID\\('[A-z]+'\\))\"", "$1");
        System.out.println(newJson);
    }

    /**
     * Use this method when relevant changes are made to the database.
     *
     * @throws SQLException
     * @throws IOException
     */
    public static void createNewTranslations() throws SQLException, IOException {
        createEnglishFilesForTranslation();
        createEnglishReferenceFiles();
        translateAllSupportedLanguages();
        doTranslationsToFile();
    }

    private static void createEnglishFilesForTranslation() throws SQLException, IOException {

        EcoDataResponse ecoData = EcoDataResponse.builder()
                .craftingTables(getAllCraftingTables())
                .upgradeModules(getAllUpgradeModules())
                .skills(getAllSkills())
                .items(getAllItems())
                .ingredients(getAllIngredients())
                .outputs(getAllOutputs())
                .recipes(getAllRecipes())
                .build();


        final StringBuilder stringBuilder = new StringBuilder();

        List<Skill> skills = ecoData.getSkills();
        skills.sort(Skill::compareTo);
        skills.forEach(skill -> stringBuilder.append(skill.getName()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/en/" + "skills.en.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<Item> items = ecoData.getItems();
        items.sort(Item::compareTo);
        items.forEach(item -> stringBuilder.append(item.getName()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/en/" + "items.en.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<CraftingTable> craftingTables = ecoData.getCraftingTables();
        craftingTables.sort(CraftingTable::compareTo);
        craftingTables.forEach(table -> stringBuilder.append(table.getName()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/en/" + "tables.en.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<Recipe> recipes = ecoData.getRecipes();
        recipes.sort(Recipe::compareTo);
        recipes.forEach(recipe -> stringBuilder.append(recipe.getName()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/en/" + "recipes.en.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<UpgradeModule> upgrades = ecoData.getUpgradeModules();
        upgrades.sort(UpgradeModule::compareTo);
        upgrades.forEach(upgrade -> stringBuilder.append(upgrade.getName()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/en/" + "upgrades.en.txt"),
                stringBuilder.toString(), UTF_8, false);
    }

    private static void createEnglishReferenceFiles() throws SQLException, IOException {
        EcoDataResponse ecoData = EcoDataResponse.builder()
                .craftingTables(getAllCraftingTables())
                .upgradeModules(getAllUpgradeModules())
                .skills(getAllSkills())
                .items(getAllItems())
                .ingredients(getAllIngredients())
                .outputs(getAllOutputs())
                .recipes(getAllRecipes())
                .build();

        final StringBuilder stringBuilder = new StringBuilder();

        List<Skill> skills = ecoData.getSkills();
        skills.sort(Skill::compareTo);
        skills.forEach(skill -> stringBuilder.append(skill.getNameID()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/ref/" + "skills.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<Item> items = ecoData.getItems();
        items.sort(Item::compareTo);
        items.forEach(item -> stringBuilder.append(item.getNameID()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/ref/" + "items.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<CraftingTable> craftingTables = ecoData.getCraftingTables();
        craftingTables.sort(CraftingTable::compareTo);
        craftingTables.forEach(table -> stringBuilder.append(table.getNameID()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/ref/" + "tables.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<Recipe> recipes = ecoData.getRecipes();
        recipes.sort(Recipe::compareTo);
        recipes.forEach(recipe -> stringBuilder.append(recipe.getNameID()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/ref/" + "recipes.txt"),
                stringBuilder.toString(), UTF_8, false);

        stringBuilder.delete(0, stringBuilder.length());
        List<UpgradeModule> upgrades = ecoData.getUpgradeModules();
        upgrades.sort(UpgradeModule::compareTo);
        upgrades.forEach(upgrade -> stringBuilder.append(upgrade.getNameID()).append("\n"));
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "/ref/" + "upgrades.txt"),
                stringBuilder.toString(), UTF_8, false);
    }

    private static void translateAllSupportedLanguages() throws SQLException, IOException {
        for (String language : LANGUAGES) {
            translateCraftingDataToLanguage(language);
        }
    }

    private static void doTranslationsToFile() throws IOException {
        List<LocaleData> localeData = createLocaleData();
        FileUtils.writeStringToFile(new File(TRANSLATION_PATH + "locale-data.json"),
                new Gson().toJson(localeData), UTF_8, false);
    }

    private static void translateCraftingDataToLanguage(String languageCode) throws SQLException, IOException {
        EcoDataResponse ecoData = EcoDataResponse.builder()
                .craftingTables(getAllCraftingTables())
                .upgradeModules(getAllUpgradeModules())
                .skills(getAllSkills())
                .items(getAllItems())
                .ingredients(getAllIngredients())
                .outputs(getAllOutputs())
                .recipes(getAllRecipes())
                .build();

        List<String> translationContents = new ArrayList<>();

        StringBuilder stringBuilder = new StringBuilder();
        List<Skill> skills = ecoData.getSkills();
        skills.forEach(skill -> stringBuilder.append(skill.getName()).append("\n"));
        translationContents.add(stringBuilder.toString());

        stringBuilder.delete(0, stringBuilder.length());
        List<Item> items = ecoData.getItems();
        items.forEach(item -> stringBuilder.append(item.getName()).append("\n"));
        translationContents.add(stringBuilder.toString());

        stringBuilder.delete(0, stringBuilder.length());
        List<CraftingTable> craftingTables = ecoData.getCraftingTables();
        craftingTables.forEach(table -> stringBuilder.append(table.getName()).append("\n"));
        translationContents.add(stringBuilder.toString());

        stringBuilder.delete(0, stringBuilder.length());
        List<Recipe> recipes = ecoData.getRecipes();
        recipes.forEach(recipe -> stringBuilder.append(recipe.getName()).append("\n"));
        translationContents.add(stringBuilder.toString());

        stringBuilder.delete(0, stringBuilder.length());
        List<UpgradeModule> upgrades = ecoData.getUpgradeModules();
        upgrades.forEach(upgrade -> stringBuilder.append(upgrade.getName()).append("\n"));
        translationContents.add(stringBuilder.toString());

        try (TranslationServiceClient client = TranslationServiceClient.create()) {
            LocationName parent = LocationName.of(PROJECT_ID, "global");

            TranslateTextRequest request = TranslateTextRequest.newBuilder()
                    .setParent(parent.toString())
                    .setMimeType("text/plain")
                    .setTargetLanguageCode(languageCode)
                    .setSourceLanguageCode("en")
                    .addAllContents(translationContents)
                    .build();

            TranslateTextResponse response = client.translateText(request);

            List<Translation> results = response.getTranslationsList();

            File skillsFile = new File(TRANSLATION_PATH + "/" + languageCode + "/" + "skills." + languageCode + ".txt");
            File itemsFile = new File(TRANSLATION_PATH + "/" + languageCode + "/" + "items." + languageCode + ".txt");
            File tablesFile = new File(TRANSLATION_PATH + "/" + languageCode + "/" + "tables." + languageCode + ".txt");
            File recipesFile = new File(TRANSLATION_PATH + "/" + languageCode + "/" + "recipes." + languageCode + ".txt");
            File upgradesFile = new File(TRANSLATION_PATH + "/" + languageCode + "/" + "upgrades." + languageCode + ".txt");

            FileUtils.writeStringToFile(skillsFile, results.get(0).getTranslatedText(), UTF_8, false);
            FileUtils.writeStringToFile(itemsFile, results.get(1).getTranslatedText(), UTF_8, false);
            FileUtils.writeStringToFile(tablesFile, results.get(2).getTranslatedText(), UTF_8, false);
            FileUtils.writeStringToFile(recipesFile, results.get(3).getTranslatedText(), UTF_8, false);
            FileUtils.writeStringToFile(upgradesFile, results.get(4).getTranslatedText(), UTF_8, false);
        }

    }

    private static List<LocaleData> createLocaleData() throws IOException {
        File idsDir = new File(TRANSLATION_PATH + "ref");

        Collection<File> refFiles = FileUtils.listFiles(idsDir, new String[]{"txt"}, false);
        List<LocaleData> localeDataList = new ArrayList<>();

        //Create the LocaleEntries and add the ids
        for (File file : refFiles) {
            String type = file.getName().substring(0, file.getName().indexOf('.'));
            LocaleData data = LocaleData.builder().type(type).entries(new ArrayList<>()).build();

            List<String> lines = FileUtils.readLines(file, UTF_8);
            for (String line : lines) {
                data.getEntries().add(LocaleEntry.builder().id(line).build());
            }

            localeDataList.add(data);
        }

        Collection<File> localeDirFiles = FileUtils.listFilesAndDirs(new File(TRANSLATION_PATH),
                FalseFileFilter.FALSE, DirectoryFileFilter.DIRECTORY);

        //Get each translated name and add to the locale entries list
        for (File dir : localeDirFiles) {
            String lang = dir.getName();
            Language language;
            try {
                language = Language.valueOf(lang.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Skipping directory " + lang + " since it is not a valid language.");
                continue;
            }

            Collection<File> languageFiles = FileUtils.listFiles(dir, new String[]{"txt"}, false);

            int langFileCount = 0;

            for (File file : languageFiles) {
                String type = file.getName().substring(0, file.getName().indexOf('.'));
                List<String> lines = FileUtils.readLines(file, UTF_8);
                LocaleData data = localeDataList.get(langFileCount);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    data.getEntries().get(i).set(line, language);
                }
                localeDataList.add(data);

                langFileCount++;
            }
        }

        return localeDataList;
    }

    private static List<Message> readMessagesToTranslate() throws IOException {
        File file = new File(TRANSLATION_PATH + "messages\\messages.json");
        String json = FileUtils.readFileToString(file, UTF_8);
        return Arrays.asList(new Gson().fromJson(json, Message[].class));
    }

    private static void translateMessages(Collection<Message> messages) throws IOException {
        String sourceText = "";
        for (Message message : messages) {
            for (LocalizedMessage localizedMessage : message.getLocalizedMessages()) {
                if (localizedMessage.getLang().equals("en")) {
                    sourceText = localizedMessage.getText();
                } else {
                    try (TranslationServiceClient client = TranslationServiceClient.create()) {
                        LocationName parent = LocationName.of(PROJECT_ID, "global");

                        TranslateTextRequest request = TranslateTextRequest.newBuilder()
                                .setParent(parent.toString())
                                .setMimeType("text/plain")
                                .setTargetLanguageCode(localizedMessage.getLang())
                                .setSourceLanguageCode("en")
                                .addContents(sourceText)
                                .build();

                        TranslateTextResponse response = client.translateText(request);

                        List<Translation> results = response.getTranslationsList();
                        localizedMessage.setText(results.get(0).getTranslatedText());
                    }
                }
            }
        }
    }
}
