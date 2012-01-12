package com.gempukku.lotro.db;

import com.gempukku.lotro.collection.CollectionSerializer;
import com.gempukku.lotro.game.CardCollection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionDAO {
    private DbAccess _dbAccess;
    private CollectionSerializer _collectionSerializer;

    private Map<Integer, Map<String, CardCollection>> _collections = new ConcurrentHashMap<Integer, Map<String, CardCollection>>();

    public CollectionDAO(DbAccess dbAccess, CollectionSerializer collectionSerializer) {
        _dbAccess = dbAccess;
        _collectionSerializer = collectionSerializer;
    }

    public void clearCache() {
        _collections.clear();
    }

    public void deletePlayerCollection(int playerId, String type) {
    }

    public CardCollection getCollectionForPlayer(int playerId, String type) {
        Map<String, CardCollection> playerCollections = _collections.get(playerId);
        if (playerCollections != null) {
            CardCollection collection = playerCollections.get(type);
            if (collection != null)
                return collection;
        }

        CardCollection collection = getCollectionFromDB(playerId, type);
        if (collection != null) {
            Map<String, CardCollection> collectionsByType = _collections.get(playerId);
            if (collectionsByType == null) {
                collectionsByType = new ConcurrentHashMap<String, CardCollection>();
                _collections.put(playerId, collectionsByType);
            }
            collectionsByType.put(type, collection);
            return collection;
        }
        return null;
    }

    public Map<Integer, CardCollection> getPlayerCollectionsByType(String type) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select player_id, collection from collection where type=?");
                try {
                    statement.setString(1, type);
                    ResultSet rs = statement.executeQuery();
                    try {
                        Map<Integer, CardCollection> playerCollections = new HashMap<Integer, CardCollection>();
                        while (rs.next()) {
                            int playerId = rs.getInt(1);
                            Blob blob = rs.getBlob(2);
                            try {
                                InputStream inputStream = blob.getBinaryStream();
                                try {
                                    playerCollections.put(playerId, _collectionSerializer.deserializeCollection(inputStream));
                                } finally {
                                    inputStream.close();
                                }
                            } finally {
                                blob.free();
                            }
                        }
                        return playerCollections;
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player collection from DB", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to get player collection from DB", exp);
        }
    }

    public void setCollectionForPlayer(int playerId, String type, CardCollection collection) {
        if (!type.equals("default")) {
            storeCollectionToDB(playerId, type, collection);
            Map<String, CardCollection> collectionsByType = _collections.get(playerId);
            if (collectionsByType == null) {
                collectionsByType = new ConcurrentHashMap<String, CardCollection>();
                _collections.put(playerId, collectionsByType);
            }
            collectionsByType.put(type, collection);
        }
    }

    private CardCollection getCollectionFromDB(int playerId, String type) {
        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("select collection from collection where player_id=? and type=?");
                try {
                    statement.setInt(1, playerId);
                    statement.setString(2, type);
                    ResultSet rs = statement.executeQuery();
                    try {
                        if (rs.next()) {
                            Blob blob = rs.getBlob(1);
                            try {
                                InputStream inputStream = blob.getBinaryStream();
                                try {
                                    return _collectionSerializer.deserializeCollection(inputStream);
                                } finally {
                                    inputStream.close();
                                }
                            } finally {
                                blob.free();
                            }
                        } else {
                            return null;
                        }
                    } finally {
                        rs.close();
                    }
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player collection from DB", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to get player collection from DB", exp);
        }
    }

    private void storeCollectionToDB(int playerId, String type, CardCollection collection) {
        CardCollection oldCollection = getCollectionFromDB(playerId, type);

        try {
            Connection connection = _dbAccess.getDataSource().getConnection();
            try {
                String sql;
                if (oldCollection == null)
                    sql = "insert into collection (collection, player_id, type) values (?, ?, ?)";
                else
                    sql = "update collection set collection=? where player_id=? and type=?";

                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    _collectionSerializer.serializeCollection(collection, baos);

                    statement.setBlob(1, new ByteArrayInputStream(baos.toByteArray()));
                    statement.setInt(2, playerId);
                    statement.setString(3, type);
                    statement.execute();
                } finally {
                    statement.close();
                }
            } finally {
                connection.close();
            }
        } catch (SQLException exp) {
            throw new RuntimeException("Unable to get player collection from DB", exp);
        } catch (IOException exp) {
            throw new RuntimeException("Unable to get player collection from DB", exp);
        }
    }
}
