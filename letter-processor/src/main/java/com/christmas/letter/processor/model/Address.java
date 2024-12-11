package com.christmas.letter.processor.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBDocument
public class Address {
    @DynamoDBAttribute(attributeName = "Street")
    private String street;

    @DynamoDBAttribute(attributeName = "City")
    private String city;

    @DynamoDBAttribute(attributeName = "State")
    private String state;

    @DynamoDBAttribute(attributeName = "ZipCode")
    private String zipCode;
}
