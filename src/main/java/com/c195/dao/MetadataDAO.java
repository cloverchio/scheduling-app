package com.c195.dao;

import com.c195.model.Metadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class MetadataDAO {

    public static Metadata toMetadata(ResultSet resultSet) throws DAOException {
        try {
            return new Metadata.Builder()
                    .withCreatedDate(resultSet.getObject("createDate", Instant.class))
                    .withCreatedBy(resultSet.getString("createdBy"))
                    .withUpdatedDate(resultSet.getObject("lastUpdate", Instant.class))
                    .withUpdatedBy(resultSet.getString("lastUpdateBy"))
                    .build();
        } catch (SQLException e) {
            throw new DAOException("there was an issue creating metadata", e);
        }
    }
}
