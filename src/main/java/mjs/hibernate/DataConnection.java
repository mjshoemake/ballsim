package mjs.hibernate;

import java.io.InputStream;

import mjs.common.utils.FileUtils;
import mjs.common.utils.Loggable;
import mjs.common.utils.XMLBuilder;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.w3c.dom.Document;

/**
 * The singleton connection to a Hibernate database.
 * @author mishoema
 */
public class DataConnection extends Loggable {

    /**
     * The Hibernate SessionFactory used to create new database
     * sessions.
     */
    SessionFactory sessionFactory = null;
    
    /**
     * The Singleton instance.
     */
    public static DataConnection instance = null;
    
    /**
     * Private constructor.
     */
    private DataConnection() {
        super("Hibernate");
    }
    
    /**
     * Retrieve the singleton DataConnection instance.
     * @return DataConnection
     */
    public synchronized static DataConnection getInstance() {
        if (instance == null) {
            instance = new DataConnection();
        } 
        return instance;
    }
    
    /**
     * Is the Hibernate database connection initialized? 
     * @return  boolean
     */
    public boolean isInitialized() {
        return sessionFactory != null;
    }

    /**
     * Initialize Hibernate data access using the specified
     * filename.
     * @param filename String
     * @throws Exception
     */
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
    
    /**
     * Retrieves the current SessionFactory object if the 
     * DataConnection has been initialized already.  If not,
     * it throws a HibernateException.
     * @return SessionFactory
     * @throws HibernateException
     */
    public static SessionFactory getSessionFactory() throws HibernateException {
        DataConnection conn = getInstance();
        if (conn.isInitialized()) {
            return conn.sessionFactory;
        } else {
            throw new HibernateException("SessionFactory requested but DataConnection has not been initialized yet. Call DataConnection.initialize() first.");
        }
    }

    /**
     * Validate the specified XML document against the specified XSD.
     * @param xml InputStream - The XML received from FPG.
     */
    private Document parseXml(InputStream xml) throws Exception {
        try {
            return XMLBuilder.parse(xml);
        } catch (Exception e) {
            throw e;
        }
    }
}