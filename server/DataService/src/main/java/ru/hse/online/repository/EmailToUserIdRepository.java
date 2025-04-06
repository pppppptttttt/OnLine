package ru.hse.online.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.hse.online.storage.EmailToUserIdData;

@Repository
public interface EmailToUserIdRepository extends CassandraRepository<EmailToUserIdData, String> {
}
