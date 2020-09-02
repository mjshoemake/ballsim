/*
package mjs.hibernate;

import java.io.InputStream;

import mjs.common.utils.FileUtils;
import mjs.common.utils.Loggable;
import mjs.common.utils.XMLBuilder;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;

public class DataConnection extends Loggable {

    SessionFactory sessionFactory = null;
    
    public static DataConnection instance = null;
    
    private DataConnection() {
        super("Hibernate");
    }
    
    public synchronized static DataConnection getInstance() {
        if (instance == null) {
            instance = new DataConnection();
        } 
        return instance;
    }
    
    public boolean isInitialized() {
        return sessionFactory != null;
    }

    public void initialize(String filename) throws Exception {
        InputStream stream = FileUtils.getFileAsStream(filename, true);
        Document xml = parseXml(stream);
        Configuration config = new Configuration().configure(xml);
        String url = System.getenv("JDBC_CONNECTION_STRING");
        if (url != null && ! url.isEmpty()) {
            config.setProperty("hibernate.connection.url", url);
        }
        log.debug("Hibernate conection URL: " + config.getProperty("hibernate.connection.url"));
        sessionFactory = config.buildSessionFactory();
        log.debug("Created sessionFactory: " + sessionFactory.toString());
    }
    
    public static SessionFactory getSessionFactory() throws HibernateException {
        DataConnection conn = getInstance();
        if (conn.isInitialized()) {
            return conn.sessionFactory;
        } else {
            throw new HibernateException("SessionFactory requested but DataConnection has not been initialized yet. Call DataConnection.initialize() first.");
        }
    }

    private Document parseXml(InputStream xml) throws Exception {
        try {
            return XMLBuilder.parse(xml);
        } catch (Exception e) {
            throw e;
        }
    }
}

 */