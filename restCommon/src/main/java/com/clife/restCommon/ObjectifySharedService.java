package com.clife.restCommon;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Result;

import java.util.List;

public class ObjectifySharedService {

    public Objectify getObjectify() {
        return ObjectifyService.ofy();
    }

    public List list(Class clazz) {
        return getObjectify().load().type(clazz).list();
    }

    public <T> T get(Class<T> clazz, Object id) {
        if (id == null) {
            throw new NullPointerException("ID Can't be null");
        }
        if (id instanceof Long) {
            return getObjectify().load().type(clazz).id((Long)id).now();
        }
        return getObjectify().load().type(clazz).id(id.toString()).now();
    }

    public <T> T save(T entity) {
        getObjectify().save().entity(entity).now();
        return entity;
    }

    public <T> List<T> saveAll(List<T> entity) {
        getObjectify().save().entities(entity).now();
        return entity;
    }

    public void delete(Class clazz, Object id) {
        if (id == null) {
            throw new NullPointerException("ID Can't be null");
        }
        if (id instanceof Long) {
            getObjectify().delete().type(clazz).id((Long)id).now();
        } else {
            getObjectify().delete().type(clazz).id(id.toString()).now();
        }
    }

    public <T> void deleteAll(Class<T> clazz) {
        Objectify objectify = getObjectify();
        List<Key<T>> keys = objectify.load().type(clazz).keys().list();
        objectify.delete().keys(keys).now();
    }
}
