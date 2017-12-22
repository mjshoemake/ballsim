package mjs.home.services;

import mjs.common.core.BaseService;
import mjs.common.crypto.EncryptionManager;
import mjs.common.crypto.Encryptor;
import mjs.common.exceptions.LoginException;
import mjs.model.User;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsersService extends BaseService {

    public UsersService() {
        super("mjs.model.User", "users", "fname+lname", "user_pk", "mjs.model.User");
    }

    public User login(String username, String password) throws LoginException {
        try {
            Encryptor mgr = EncryptionManager.getInstance(2);
            String encrypted = mgr.encrypt(password);
            List<Object> entities = findByCriteria(Restrictions.eq("username", username));
            if (entities.size() == 1) {
                User user = (User)entities.get(0);
                if (user.getPassword().equals(encrypted)) {
                    return user;
                } else {
                    throw new Exception("The login information provided is invalid. Please try again.");
                }
            } else if (entities.size() > 1) {
                throw new Exception("More than one user returned matching this username but only one is expected.");
            } else if (entities.size() == 0) {
                throw new Exception("The login information provided is invalid. Please try again.");
            } else {
                throw new Exception("Unexpected error. Number of users returned that match the specified username: " + entities.size());
            }
        } catch (LoginException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed. " + e.getMessage(), e);
            throw new LoginException("Login failed. " + e.getMessage());
        }
    }

}
