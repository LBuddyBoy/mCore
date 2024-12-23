package dev.minechase.core.api.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class MultiScope {

    private final List<String> scopes = new ArrayList<>();

    public MultiScope(String string) {
        this.scopes.addAll(Arrays.asList(string.split(",")));
    }

}
