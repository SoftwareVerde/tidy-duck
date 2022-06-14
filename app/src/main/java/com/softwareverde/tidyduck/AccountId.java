package com.softwareverde.tidyduck;

import com.softwareverde.util.type.identifier.Identifier;

public class AccountId extends Identifier {
    public static AccountId wrap(final Long accountId) {
        if (accountId == null) { return null; }

        return new AccountId(accountId);
    }

    protected AccountId(final Long value) {
        super(value);
    }
}
