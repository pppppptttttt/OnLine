package ru.hse.online.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.hse.online.model.EmailToUserId;

@Repository
public interface EmailToUserIdRepository extends CassandraRepository<EmailToUserId, String> {
}
