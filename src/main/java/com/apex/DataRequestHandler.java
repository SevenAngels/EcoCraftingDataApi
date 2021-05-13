package com.apex;

import com.apex.response.EcoDataResponse;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;

import java.sql.SQLException;

import static com.apex.db.DbService.*;
import static java.net.HttpURLConnection.*;

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

        response.appendHeader("Access-Control-Allow-Origin", "*");

        if ("OPTIONS".equals(request.getMethod())) {
            System.out.println("Preflight request, setting access control headers");
            response.appendHeader("Access-Control-Allow-Methods", "GET");
            response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
            response.appendHeader("Access-Control-Max-Age", "3600");
            response.setStatusCode(HTTP_NO_CONTENT);
            return;
        }

        int statusCode = HTTP_OK;

        EcoDataResponse dataResponse = getDataResponse();
        if (dataResponse == null) {
            statusCode = HTTP_INTERNAL_ERROR;
        }

        response.setStatusCode(statusCode);
        response.setContentType("application/json");

        response.getHeaders().forEach((key, value) -> System.out.println(key + " : " + value));
        response.getWriter().write(new Gson().toJson(dataResponse));
    }
}
