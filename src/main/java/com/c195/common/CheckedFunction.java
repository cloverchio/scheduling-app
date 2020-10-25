package com.c195.common;

import com.c195.dao.DAOException;

@FunctionalInterface
public interface CheckedFunction<T, R> {
    R applyWithIO(T t) throws DAOException;
}
