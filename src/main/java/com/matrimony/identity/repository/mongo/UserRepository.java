package com.matrimony.identity.repository.mongo;

import com.matrimony.identity.model.MatrimonyUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("userRepository")
public interface UserRepository extends MongoRepository<MatrimonyUser, Long> {

    @Query(value = "{ 'phoneNumber' : ?0}")
    List<MatrimonyUser> findByPhone(String phoneNumber);

    @Query("{ 'id' : ?0 }")
    Optional<MatrimonyUser> findById(String id);
}
