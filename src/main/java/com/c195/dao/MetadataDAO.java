package com.c195.dao;

import com.c195.model.Metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class MetadataDAO {

    public static Metadata toMetadata(ResultSet resultSet) throws DAOException {
        try {
            return new Metadata.Builder()
                    .withCreatedDate(resultSet.getTimestamp("createDate").toInstant())
                    .withCreatedBy(resultSet.getString("createdBy"))
                    .withUpdatedDate(resultSet.getTimestamp("lastUpdate").toInstant())
                    .withUpdatedBy(resultSet.getString("lastUpdateBy"))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating metadata", e);
        }
    }

    public static Metadata getSaveMetadata(String currentUser) {
        return new Metadata.Builder()
                .withCreatedDate(Instant.now())
                .withCreatedBy(currentUser)
                .withUpdatedBy(currentUser)
                .build();
    }

    public static Metadata getUpdateMetadata(String currentUser) {
        return new Metadata.Builder()
                .withUpdatedDate(Instant.now())
                .withUpdatedBy(currentUser)
                .build();
    }
}
