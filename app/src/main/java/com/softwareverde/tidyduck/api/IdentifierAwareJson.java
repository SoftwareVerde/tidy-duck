package com.softwareverde.tidyduck.api;

import com.softwareverde.json.Json;
import com.softwareverde.util.type.identifier.Identifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class IdentifierAwareJson extends Json  {
    public IdentifierAwareJson(final JSONObject jsonObject) {
        super(jsonObject);
    }

    public IdentifierAwareJson(final JSONArray jsonArray) {
        super(jsonArray);
    }

    public IdentifierAwareJson() { }

    public IdentifierAwareJson(final Boolean isArray) {
        super(isArray);
    }

    public <T> IdentifierAwareJson(final Collection<T> c) {
        super(c);
    }

    public <T> IdentifierAwareJson(final Map<String, T> keyValueMap) {
        super(keyValueMap);
    }

    @Override
    public <T> void add(final T value) {
        if (value instanceof Identifier) {
            super.add(((Identifier) value).longValue());
        }
        else {
            super.add(value);
        }
    }

    @Override
    public <T> void put(final String key, final T value) {
        if (value instanceof Identifier) {
            super.put(key, ((Identifier) value).longValue());
        }
        else {
            super.put(key, value);
        }
    }
}
