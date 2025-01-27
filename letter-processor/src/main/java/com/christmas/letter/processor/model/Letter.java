package com.christmas.letter.processor.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = "ChristmasLetters")
public class Letter {
	@DynamoDBHashKey(attributeName = "Email")
	private String email;

	@DynamoDBAttribute(attributeName = "Name")
	private String name;

	@DynamoDBAttribute(attributeName = "Wishes")
	private String wishes;

	@DynamoDBTypeConvertedJson
	@DynamoDBAttribute(attributeName = "Address")
	private Address address;
}
