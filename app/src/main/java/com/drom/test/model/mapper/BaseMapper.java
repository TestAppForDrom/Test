package com.drom.test.model.mapper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class BaseMapper<MODEL, ENTITY> {

    @NonNull
    public abstract MODEL mapTo(ENTITY entity);

    @NonNull
    public List<MODEL> mapTo(@Nullable Collection<ENTITY> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        List<MODEL> result = new ArrayList<>(entities.size());
        for (ENTITY e : entities) {
            result.add(mapTo(e));
        }
        return result;
    }
}