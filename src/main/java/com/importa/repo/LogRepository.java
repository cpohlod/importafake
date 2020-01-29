package com.importa.repo;

import org.springframework.data.repository.CrudRepository;

import com.importa.bo.Log;

import java.util.List;

public interface LogRepository extends CrudRepository<Log, Long> {

    List<Log> findByName(String name);

}
