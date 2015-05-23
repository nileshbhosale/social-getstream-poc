package socialapis.dao;

import org.springframework.data.repository.CrudRepository;
import socialapis.domain.Connection;

import javax.transaction.Transactional;

/**
 * Created by Nilesh Bhosale
 */
@Transactional
public interface ConnectionDao extends CrudRepository<Connection, Long> {

    Connection getOneByFollowerIdAndFolloweeId(Long followerId,Long followeeId);
}