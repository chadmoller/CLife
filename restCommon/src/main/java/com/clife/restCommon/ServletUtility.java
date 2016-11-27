package com.clife.restCommon;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServletUtility {
    public static void writeJsonResponse(HttpServletResponse resp, Object obj) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(obj);
        writeJsonStringResponse(resp, json);
    }

    public static void writeJsonStringResponse(HttpServletResponse resp, String json) throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.write(json);
        writer.flush();
        resp.setContentType("application/json");
    }

    public static <T> T readJsonRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        T object = mapper.readValue(request.getReader(), clazz);
        return object;
    }

    public static <T> List<T> readListRequest(HttpServletRequest request, Class<T> clazz) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
        List<T> object = mapper.readValue(request.getReader(), type);
        return object;
    }

    public static <T> ActionBody<T> readActionRequest(HttpServletRequest request, Map<String, ActionConfig<?>> actionConfigMap) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ActionBody.class, new ActionBodyDeserializer(actionConfigMap));
        mapper.registerModule(module);
        return mapper.readValue(request.getReader(), ActionBody.class);
    }

    public static class ActionBodyDeserializer extends StdDeserializer<ActionBody> {
        private Map<String, ActionConfig<?>> actionContentMap;
        public ActionBodyDeserializer(Map<String, ActionConfig<?>> actionContentMap) {
            super(ActionBody.class);
            this.actionContentMap = actionContentMap;
        }

        @Override
        public ActionBody deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            JsonNode node = jp.getCodec().readTree(jp);
            String actionName = node.get("actionName").asText();
            JsonNode actionContentNode = node.get("actionContent");

            ActionConfig<?> actionConfig = actionContentMap.get(actionName);
            Class<?> actionContentClazz = actionConfig.clazz;

            ObjectMapper mapper = new ObjectMapper();
            Object actionContent = mapper.readValue(actionContentNode.toString(), actionContentClazz);

            return new ActionBody(actionName, actionContent);
        }
    }
}
