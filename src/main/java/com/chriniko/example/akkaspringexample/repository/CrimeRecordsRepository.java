package com.chriniko.example.akkaspringexample.repository;

import com.chriniko.example.akkaspringexample.domain.CrimeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CrimeRecordsRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(List<CrimeRecord> crimeRecords) {

        //TODO add implementation...

    }

}
