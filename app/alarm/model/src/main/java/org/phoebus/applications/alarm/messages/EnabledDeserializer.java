package org.phoebus.applications.alarm.messages;

import static org.phoebus.applications.alarm.AlarmSystem.logger;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.util.logging.Level;

import org.phoebus.applications.alarm.model.EnabledState;
import org.phoebus.applications.alarm.model.json.JsonTags;

/**
 * Deserializer used for writing varied date string/boolean enabled values
 *
 * @author Jacqueline Garrahan
 *
 */
public class EnabledDeserializer extends StdDeserializer<EnabledState> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    public EnabledDeserializer() {
        this(null);
    }
  
    public EnabledDeserializer(Class<EnabledState> t) {
        super(t);
    }

    @Override
    public EnabledState deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec oc = jp.getCodec();
        JsonNode jn = oc.readTree(jp);

        // use pattern matching to determine whether boolean or datetime string
        if (jn != null) {
            Pattern pattern = Pattern.compile("true|false", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(jn.asText());
    
            if(matcher.matches()) {
                System.out.println("Matched " + jn.asText());
                return new EnabledState(jn.asBoolean());
             } else {
                 try {
                    LocalDateTime enabled_date = LocalDateTime.parse(jn.asText(), formatter);
                    System.out.println("Enabled date is : " + jn.asText());
                    return new EnabledState(enabled_date);
                 }
                 catch (Exception ex) {
                    System.out.println("Poorly formatted enabled date");
                    logger.log(Level.WARNING, "Bypass date incorrectly formatted." + jn.asText() + "'");
                }
            }
        }
        System.out.println(jn.toString());
        System.out.println("No json node.");
        return new EnabledState(true);
    }
}