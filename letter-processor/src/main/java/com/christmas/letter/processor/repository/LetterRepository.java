package com.christmas.letter.processor.repository;

import com.christmas.letter.processor.model.Letter;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBPagingAndSortingRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;
import org.springframework.stereotype.Repository;

@Repository
@EnableScanCount
@EnableScan
public interface LetterRepository
		extends DynamoDBCrudRepository<Letter, String>,
				DynamoDBPagingAndSortingRepository<Letter, String> {}
