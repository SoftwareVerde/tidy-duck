package com.softwareverde.tidyduck.most;

import com.softwareverde.json.Json;
import com.softwareverde.json.Jsonable;

public class Company implements Jsonable {
    private Long _id;
    private String _name;

    public Long getId() {
        return this._id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    @Override
    public Json toJson() {
        final Json json = new Json(false);

        json.put("id", _id);
        json.put("name", _name);

        return json;
    }
}
