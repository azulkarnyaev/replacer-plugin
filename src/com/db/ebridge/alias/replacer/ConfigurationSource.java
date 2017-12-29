package com.db.ebridge.alias.replacer;


import java.util.Optional;

public class ConfigurationSource {

    public Optional<String> mapAlias(String alias) {
        return Optional.ofNullable(alias).filter(a -> a.contains("In")).map(a -> "newOne");
    }

}
