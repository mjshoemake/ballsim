package mjs.common.core;

import mjs.common.exceptions.ModelException;
import mjs.model.PrimaryKey;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

/**
 * REST service used to retrieve, update, and delete user data
 * from the database.
 */
@Controller
public class BaseController extends SeerObject {

    /**
     * The log4j logger to use when writing log messages.
     */
    protected static final Logger log = Logger.getLogger("Controller");

    private final String entityType;
    private String entityNameProperty;
    private String entityPkProperty;
    private final String tableName;

    public BaseController(String entityClass,
                          String entityType,
                          String entityNameProperty,
                          String entityPkProperty,
                          String tableName) {
        super(entityClass);
        log.debug(this.getClass().getName() + "  Constructor()");
        this.entityType = entityType;
        this.tableName = tableName;
        this.entityNameProperty = entityNameProperty;
        this.entityPkProperty = entityPkProperty;
    }


    public ResponseEntity getList(Model model, BaseService service) {
        try {
            log.debug("REST Call: get" + tableName + "List()");
            return createResponse(service.getAll(), HttpStatus.OK);
        } catch (ModelException e) {
            log.error(e.getMessage(), e);
            return createResponseMsg(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createResponseMsg("An error occurred retrieving the " + entityType + " list. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity getByPK(Model model, int pk, BaseService service) {
        try {
            log.debug("REST Call: get" + tableName + "ByPK(pk=" + pk + ")");
            return createResponse(service.getByPK(pk), HttpStatus.OK);
        } catch (ModelException e) {
            log.error(e.getMessage(), e);
            return createResponseMsg(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createResponseMsg("An error occurred retrieving " + entityType + " " + pk + ". " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity filterList(Model model, String filter, BaseService service) {
        try {
            log.debug("REST Call: filter" + tableName + "(filter=" + filter + ")");
            return createResponse(service.filter(filter, "name", "asc"), HttpStatus.OK);
        } catch (ModelException e) {
            log.error(e.getMessage(), e);
            return createResponseMsg(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createResponseMsg("An error occurred filtering the " + entityType + " data. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity filterList(Model model, String filter, String sortFields, String sortDirection, BaseService service) {
        try {
            log.debug("REST Call: filter" + tableName + "(filter=" + filter + ")");
            return createResponse(service.filter(filter, sortFields, sortDirection), HttpStatus.OK);
        } catch (ModelException e) {
            log.error(e.getMessage(), e);
            return createResponseMsg(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createResponseMsg("An error occurred filtering the " + entityType + " data. " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity delete(Model model, String pkList, BaseService service) {
        try {
            log.debug("REST Call: delete" + tableName + "Cookbook(pk=" + pkList + ")");
            String[] pks = pkList.split(",");
            for (String pk : pks) {
                Object entityToDelete = service.getByPK(Integer.parseInt(pk));
                String entityName = getPropertyValue(entityToDelete, entityNameProperty) + "";
                log.debug("   Deleting " + entityType + " " + entityName + "...");
                service.delete(entityToDelete);
                log.debug("   Deleting " + entityType + " " + entityName + "... Done.");
            }
            return createResponseMsg("Successfully deleted the specified entities.", HttpStatus.OK);
        } catch (ModelException e) {
            log.error(e.getMessage(), e);
            return createResponseMsg(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createResponseMsg("An error occurred deleting " + entityType + " " + pkList + ". " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity update(Model model, Object entity, BaseService service) {
        String name = null;
        try {
            name = getPropertyValue(entity, entityNameProperty);
            if (getPropertyValue(entity, entityPkProperty).equals("0")) {
                String newPk = service.save(entity);
                return createResponse(new PrimaryKey(newPk), HttpStatus.OK);
            } else {
                service.update(entity);
                return createResponseMsg("Successfully updated " + entityType + " " + name + ".", HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return createResponseMsg("An error occurred updating " + entityType + " " + name + ". " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected ResponseEntity createResponse(Object obj, HttpStatus status) {
        return new ResponseEntity(obj, status);
    }

    protected ResponseEntity createResponseMsg(String msg, HttpStatus status) {
        return new ResponseEntity(msg, status);
    }

}
