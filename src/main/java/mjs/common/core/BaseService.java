package mjs.common.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mjs.common.exceptions.ModelException;
import mjs.common.utils.LogUtils;
import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base class for services which interact with the database through
 * Hibernate.
 */
@Transactional
public class BaseService extends SeerObject {

    /**
     * The log4j logger to use when writing log messages.
     */
    protected static final Logger log = Logger.getLogger("Service");
    protected static final Logger logResultSet = Logger.getLogger("ResultSet");

    @Autowired
    private SessionFactory sessionFactory;
    protected final String entityNameProperty;
    protected final String entityPkProperty;
    protected final String entityType;
    protected final String tableName;

    public BaseService(String entityClass,
                       String entityType,
                       String entityNameProperty,
                       String entityPkProperty,
                       String tableName) {
        super(entityClass);
        this.entityType = entityType;
        this.entityNameProperty = entityNameProperty;
        this.entityPkProperty = entityPkProperty;
        this.tableName = tableName;

        if (! LogUtils.isLoggingConfigured()) {
            LogUtils.initializeLogging();
        }
    }

    public Session openSession() {
        log.debug("Requesting Hibernate session... sessionFactory: " + sessionFactory.toString());
        Session session = sessionFactory.openSession();
        log.debug("Requesting Hibernate session...  Done.  " + session.toString());
        session.setCacheMode(CacheMode.IGNORE);
        return session;
    }

    public List filter(String filter) throws ModelException {
        return filter(filter, null, "asc");
    }

    public List filter(String filter, String sortFields, String sortDirection) throws ModelException {
        // This method uses a filter string with the specified format:
        //    key=value;key=value;key=value
        //    Example:  http://localhost:8080/homeMgr/recipes/filter/recipes_pk=11
        if (! (sortDirection.equals("asc") || sortDirection.equals("desc"))) {
            throw new ModelException("Value for sortDirection is not valid.  Required: one of 'asc' or 'desc'.");
        }

        log.debug("Opening session...");
        Session session = openSession();
        try {
            Map<String, String> filterMap = filterToMap(filter);
            Iterator<String> keys = filterMap.keySet().iterator();
            Criteria criteria = session.createCriteria(entityClass);
            while (keys.hasNext()) {
                String key = keys.next();
                String type = getPropertyType(key);
                log.debug("   key" + key + "  Type: " + type);

                if (type.equals("int")) {
                    String value = filterMap.get(key);
                    try {
                        int intValue = Integer.parseInt(value);
                        criteria.add(Restrictions.eq(key, intValue));
                    } catch(Exception e) {
                        // Skipping this restriction.  Not a valid integer.
                    }
                } else {
                    String value = filterMap.get(key).replace('*', '%');
                    if (value.contains("%")) {
                        criteria.add(Restrictions.like(key, value));
                    } else {
                        criteria.add(Restrictions.eq(key, value));
                    }
                }
            }
            if (sortFields != null) {
                String[] tokens = sortFields.split(",");
                for (String next : tokens) {
                    if (sortDirection.equals("asc")) {
                        criteria.addOrder(Order.asc(next.trim()));
                    } else {
                        criteria.addOrder(Order.desc(next.trim()));
                    }
                }
            }
            List result = criteria.list();
            log.debug("Service: filter()  Type=" + tableName + "  filter: " + filter + "  Result: " + result.size());
            logResultSet.debug("Result Set:");
            LogUtils.debug(logResultSet, result, "   ", true);
            return result;
        } catch (Exception e) {
            log.error("Unable to filter the " + entityType + " data (" + filter + ").", e);
            throw new ModelException("Unable to filter the " + entityType + " (" + filter + "). " + e.getMessage(), e);
        } finally {
            session.close();
        }
    }

    public Map<String, String> filterToMap(String filter) {
        Map<String, String> result = new HashMap<String, String>();
        String[] pairs = filter.split(";");
        for (String next : pairs) {
            String[] item = next.split("=");
            result.put(item[0], item[1]);
        }
        return result;
    }

    public Object getByPK(int id) throws ModelException {
        try {
            List<Object> entities = findByCriteria(Restrictions.eq(entityPkProperty, new Integer(id)));
            if (entities.size() == 1) {
                return entities.get(0);
            } else if (entities.size() > 1) {
                throw new Exception("More than one " + entityType + " returned matching this ID but only one is expected.");
            } else if (entities.size() == 0) {
                throw new Exception("No " + entityType + " found that match the specified ID.");
            } else {
                throw new Exception("Unexpected error. Number of items returned: " + entities.size());
            }
        } catch (ModelException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unable to retrieve this " + entityType + " (" + id + ").", e);
            throw new ModelException("Unable to retrieve this " + entityType + " (" + id + "). " + e.getMessage());
        }
    }

    public List getAll() throws ModelException {
        log.debug("Service: getAll()  START");
        Session session = openSession();
        try {
            List result = session.createQuery("from " + tableName).list();
            log.debug("Service: getAll()  Type=" + tableName + "  Result: " + result.size());
            logResultSet.debug("Result Set:");
            LogUtils.debug(logResultSet, result, "   ", true);
            return result;
        } catch (Exception e) {
            log.error("Service: getAll()  Type=" + tableName + "  Unable to retrieve the " + entityType + " list.", e);

            throw new ModelException("Unable to retrieve the " + entityType + " list. " + e.getMessage());
        } finally {
            session.close();
            log.debug("Service: getAll()  END");
        }
    }

    public int getRowCount() throws ModelException {
        Session session = openSession();
        try {
            Long uniqueResult = (Long)session.createQuery("select count(*) from " + tableName).uniqueResult();
            int count = -1;
            if (uniqueResult != null) {
                count = uniqueResult.intValue();
            }
            log.debug("Service: getRowCount()  Type=" + tableName + "  Result: " + count);
            return count;
        } catch (Exception e) {
            log.error("Unable to retrieve a count of the " + entityType + " list.", e);
            throw new ModelException("Unable to retrieve a count of the " + entityType + " list. " + e.getMessage());
        } finally {
            session.close();
        }
    }

    public List findByCriteria(Criterion criterion) {
        Session session = openSession();
        try {
            Criteria criteria = session.createCriteria(entityClass);
            criteria.add(criterion);
            List result = criteria.list();
            return result;
        } finally {
            session.close();
        }
    }

    public Object findById(String id) {
        Session session = openSession();
        try {
            Object result = session.get(entityClass, id);
            return result;
        } finally {
            session.close();
        }
    }

    public void saveOrUpdate(Object entity) {
        Session session = openSession();
        Transaction tx = null;
        try {
            Object name = getPropertyValue(entity, entityNameProperty);
            log.debug("Saving " + entityType + " " + name + "...");
            tx = session.beginTransaction();
            session.saveOrUpdate(entity);
            tx.commit();
            log.debug("   Save successful.");
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
    }

    public String save(Object entity) throws ModelException {
        try {
            if (entity != null) {
                Session session = openSession();
                Transaction tx = null;
                try {
                    Object name = getPropertyValue(entity, entityNameProperty);
                    log.debug("Saving " + entityType + " " + name + "...");
                    tx = session.beginTransaction();
                    String result = session.save(entity).toString();
                    tx.commit();
                    log.debug("   Saved.  Returning pk: " + result);
                    return result;
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    throw e;
                } finally {
                    session.close();
                }
            } else {
                throw new ModelException("Expected a valid " + entityType + " but received null.");
            }
        } catch (Exception e) {
            log.error("Unable to save the specified " + entityType + ".", e);
            throw new ModelException("Unable to save the specified " + entityType + ". " + e.getMessage());
        }
    }

/*
    public void flush() {
        openSession().flush();
    }
*/

    public void update(Object entity) throws ModelException {
        try {
            if (entity != null) {
                Session session = openSession();
                Transaction tx = null;
                try {
                    Object name = getPropertyValue(entity, entityNameProperty);
                    log.debug("Updating " + entityType + " " + name + "...");
                    tx = session.beginTransaction();
                    session.update(entity);
                    tx.commit();
                    log.debug("   Update successful.");
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    throw e;
                } finally {
                    session.close();
                }
            } else {
                throw new ModelException("Expected a valid " + entityType + " but received null.");
            }
        } catch (Exception e) {
            log.error("Unable to save the specified " + entityType + ".", e);
            throw new ModelException("Unable to save the specified " + entityType + ". " + e.getMessage());
        }
    }

    public void delete(Object entity) throws ModelException {
        try {
            if (entity != null) {
                Session session = openSession();
                Transaction tx = null;
                try {
                    Object name = getPropertyValue(entity, entityNameProperty);
                    log.debug("Deleting " + entityType + " " + name + "...");
                    tx = session.beginTransaction();
                    session.delete(entity);
                    tx.commit();
                    log.debug("   Delete successful.");
                } catch (Exception e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    throw e;
                } finally {
                    session.close();
                }
            } else {
                throw new Exception("Expected a valid " + entityType + " but received null.");
            }
        } catch (Exception e) {
            log.error("Unable to delete this " + entityType + ".", e);
            throw new ModelException("Unable to delete this " + entityType + ". " + e.getMessage());
        }
    }
}
