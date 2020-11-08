package com.c195.dao;

import com.c195.model.Metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public final class MetadataDAO {

    public static Metadata toMetadata(ResultSet resultSet) throws DAOException {
        try {
            return new Metadata.Builder()
                    .withCreatedDate(resultSet.getTimestamp("createDate").toInstant())
                    .withCreatedBy(resultSet.getString("createdBy"))
                    .withUpdatedDate(resultSet.getTimestamp("lastUpdate").toInstant())
                    .withUpdatedBy(resultSet.getString("lastUpdateBy"))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("There was an issue creating metadata", e);
        }
    }

    public static Metadata getSaveMetadata(String currentUser, Instant createdDate) {
        return new Metadata.Builder()
                .withCreatedDate(createdDate)
                .withCreatedBy(currentUser)
                .withUpdatedBy(currentUser)
                .build();
    }

    public static Metadata getUpdateMetadata(String currentUser, Instant updatedDate) {
        return new Metadata.Builder()
                .withUpdatedDate(updatedDate)
                .withUpdatedBy(currentUser)
                .build();
    }
}
