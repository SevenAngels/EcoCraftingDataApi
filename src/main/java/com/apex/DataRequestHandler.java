package com.apex;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.apex.response.ApiGatewayResponse;
import com.apex.response.EcoDataResponse;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.apex.db.DbService.*;

public class DataRequestHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOGGER = Logger.getLogger(DataRequestHandler.class);

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOGGER.info("Received request " + input);

        int statusCode = 200;

        EcoDataResponse response = null;
        try {
            LOGGER.info("Building eco data response");
            response = EcoDataResponse.builder()
                    .craftingTables(getAllCraftingTables())
                    .upgradeModules(getAllUpgradeModules())
                    .skills(getAllSkills())
                    .items(getAllItems())
                    .ingredients(getAllIngredients())
                    .outputs(getAllOutputs())
                    .recipes(getAllRecipes())
                    .build();
        } catch (SQLException e) {
            LOGGER.error(e);
            statusCode = 500;
        }

        return ApiGatewayResponse.builder()
                .setStatusCode(statusCode)
                .setObjectBody(response)
                .setHeaders(getDefaultHeaders())
                .build();
    }

    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Powered-By", "AWS Lambda & Serverless");
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
