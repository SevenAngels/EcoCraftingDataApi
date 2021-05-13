package com.apex;

import com.apex.response.EcoDataResponse;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.sql.SQLException;

import static com.apex.db.DbService.*;

public class DataRequestHandler implements HttpFunction {

    private EcoDataResponse getDataResponse() {
        try {
            return EcoDataResponse.builder()
                    .craftingTables(getAllCraftingTables())
                    .upgradeModules(getAllUpgradeModules())
                    .skills(getAllSkills())
                    .items(getAllItems())
                    .ingredients(getAllIngredients())
                    .outputs(getAllOutputs())
                    .recipes(getAllRecipes())
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @SuppressWarnings("squid:S2696")
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        System.out.println("Received request " + request);

        int statusCode = 200;

        EcoDataResponse dataResponse = getDataResponse();

        if (dataResponse == null) {
            statusCode = 500;
        }

        response.setStatusCode(statusCode);
        response.appendHeader("Content-Type", "application/json");
        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().write(new Gson().toJson(dataResponse));
    }
}
