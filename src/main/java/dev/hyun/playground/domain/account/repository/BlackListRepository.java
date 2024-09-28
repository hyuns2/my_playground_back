package dev.hyun.playground.domain.account.repository;

import dev.hyun.playground.domain.account.entity.BlackList;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends CrudRepository<BlackList, String> {
}
