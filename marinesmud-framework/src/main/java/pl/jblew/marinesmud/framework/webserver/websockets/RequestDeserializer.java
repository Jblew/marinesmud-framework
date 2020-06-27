/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.jblew.marinesmud.framework.webserver.websockets;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author teofil
 */
final class RequestDeserializer extends StdDeserializer<Request> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Map<String, Class<? extends RequestObject>> requestClasses;
    
        RequestDeserializer(Map<String, Class<? extends RequestObject>> requestClasses) {
            this(requestClasses, null);
        }

        RequestDeserializer(Map<String, Class<? extends RequestObject>> requestClasses, Class<Request> vc) {
            super(vc);
            this.requestClasses = requestClasses;
        }

        @Override
        public Request deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            String type = node.get("type").asText();
            Class<? extends RequestObject> clazz = requestClasses.get(type);
            RequestObject requestObject = (clazz == null ? null : MAPPER.treeToValue(node.get("object"), clazz));

            Request r = new Request();
            r.type = type;
            r.object = requestObject;

            return r;
        }
    }