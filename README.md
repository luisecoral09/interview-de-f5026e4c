# AirDNA Data Engineering Project

You have been tasked with creating a prototype data ingest and delivery pipeline of property amenities data.  Two files will be delivered daily `properties.json` and `amenities.txt` (descriptions below).  Note that these are just samples and the actual delivery will be on order of millions of records.  Your task will be to write the following two spark jobs:

1) Ingest of properties and amenities into a parquet format (locally)
2) Creation of two data outputs leveraging the parquet data (also locally)

Note that two base jobs have been provided `IngestJob` and `ExportJob`.  Feel free to use these or create your own to accomplish the task.

## Our Objectives

- Can you translate requirements into functional code?
- Do you produce readable, well organized and extensible code?
- Are you familiar with Spark and data processing?
- Can your code be easily tested?
- Is your code performant?
- Can the service scale well?
- Do you handle errors gracefully?

## If You Have Questions

This is meant to simulate a real project that you would be working on at AirDNA. If you have any questions, please do not hesitate to ask them. Furthermore, if you feel something is missing from the requirements, go ahead and add it in and document what you did (code comments are fine).

## Delivery Of Project

Finished code should be zipped up and emailed or shared as a private github repository.  The project should contain a description of how to build and run the spark jobs locally.

# Description

## JOB 1 - INGEST

### Data Descriptions (Input)

`Properties`

Contains a list of properties and attributes about them.

```json
{
    "property_id": 1,                   // unique id for a property
    "active": true,                     // true if active and false otherwise
    "discovered_dt": "<YYYY-mm-dd>"     // date that the property was discovered
}
```

`Amenities`

Contains a record for a property if it has amenities associated.  The amenities are pipe separated.

```text
<property_id1> <amenity_id1>|<amenity_id2>|...
```

### Expected Parquet Tables

`Properties`

| field | type |
| --- | --- |
| property_id | int
| active | boolean
| discovered_dt | date


`Amenities`

| field | type |
| --- | --- |
| property_id | int
| amenity_id | string

<br />

## JOB 2 - DELIVERY

### Data Descriptions (Output)

Two separate deliveries are required using the parquet tables.  Both of these should be generated in the same spark job.

<br />

### **First**: Active properties and their amenities

Should contain a filtered list of ONLY active properties and their associated amenities.

Format: line separated json with the following object keys
- `property_id` - unique property identifier
- `amenities` - a list of all associated amenity ids

<br />

### **Second**: Summarized count of property discovery dates

Should contain a summarized report of the number of properties that have a discovered
date within a particular year and month.  

Format: CSV (with header)
- `year` - An integer representing the year
- `month` - An integer representing the month
- `count` - The count of discovered dts that fell in the associated year and month
