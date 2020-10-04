package com.c195.common;

import com.c195.dao.DAOException;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T getWithIO() throws DAOException;
}
