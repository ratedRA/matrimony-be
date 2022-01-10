package com.matrimony.identity.repository.mongo;

import com.matrimony.identity.model.MatrimonyUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends MongoRepository<MatrimonyUser, Long> {

    @Query(value = "phoneNumber:'?0'")
    public MatrimonyUser findByPhone(String phoneNumber);
}
