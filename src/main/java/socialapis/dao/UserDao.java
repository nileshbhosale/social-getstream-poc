package socialapis.dao;


import org.springframework.data.repository.CrudRepository;
import socialapis.domain.User;

import javax.transaction.Transactional;

/**
 * Created by Nilesh Bhosale
 */
@Transactional
public interface UserDao extends CrudRepository<User, Long> {
}
