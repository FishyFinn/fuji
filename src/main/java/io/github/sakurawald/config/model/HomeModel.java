package io.github.sakurawald.config.model;

import io.github.sakurawald.module.common.structure.Position;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HomeModel {

    public @NotNull Map<String, Map<String, Position>> homes = new HashMap<>();
}
