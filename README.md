[version]: https://shields.io/maven-metadata/v?metadataUrl=https://repo1.maven.org/maven2/io/github/WavJaby/tiny-json/maven-metadata.xml&color=informational&label=Download
[license-shield]: https://img.shields.io/badge/License-Apache%202.0-lightgrey.svg
[download]: #download
[license]: https://github.com/WavJaby/TinyJson/blob/master/LICENSE
[![version][]][download]
[![license-shield][]][license]

# TinyJson
TinyJson is a json data parser library for Java.\
The parser is well tiny, very suitable for temporary use or small projects.

## Summary
It's small and faster than org.json\
Lets get started!
1. [Usage](#Usage)
2. [Download](#Download)
3. [Methods](#Methods)

## Usage
### JsonObject
**Creat a JsonObject**
```java
JsonObject jsonObject = new JsonObject();
```
**Parse JsonObject, and get value**
```java
String rawData = "{\"Hello\":\"World\", \"age\":17}";
JsonObject result = new JsonObject(rawData);
System.out.println(result.getString("Hello"));
System.out.println(result.getInt("age"));
```
**Get ListedJsonObject length**
```java
ListedJsonObject listedJsonObject = new ListedJsonObject("{\"Hello\":\"World\"}");
System.out.println(listedJsonObject.length);
```
### JsonArray
**Creat a JsonArray**
```java
JsonArray jsonArray = new JsonArray();
```
**Parse jsonArray, and get value**
```java
String rawData = "[\"Hello\", \"World\", 17]";
JsonArray result = new JsonArray(rawData);
System.out.println(result.getString(0));
System.out.println(result.getString(1));
System.out.println(result.getInt(2));
```
**Get length**
```java
JsonArray jsonArray = new JsonArray("[\"Hello\", \"World\", 17]");
System.out.println(jsonArray.length);
```
**For loop**
```java
String rawData = "[\"Hello\", \"World\", 17]";
JsonArray result = new JsonArray(rawData);
for (Object i : result) {
    System.out.println(i);
}
```

## Download
[![version][]][download]

**Maven**
```xml
<dependency>
    <groupId>io.github.WavJaby</groupId>
    <artifactId>tiny-json</artifactId>
    <version>0.0.4</version>
</dependency>
```

**Gradle**
```gradle
dependencies {
    //Change 'implementation' to 'compile' in old Gradle versions
    implementation 'io.github.WavJaby:tiny-json:0.0.4'
}
repositories {
    mavenCentral()
}
```

## Methods
### JsonObject
- `toString()` get json string
- `toStringBeauty()` get the json string, with line breaks and tabs
- `addAll(jsonObject)` add all key and value from other jsonObject
- `put(key, value)` put a value into JsonObject
- `remove(key)` remove a value from JsonObject
- `containsKey(key)` return true, if there is a key in JsonObject
- `notNull(key)` return true, if there is a key in JsonObject and the value is not null

- `getJson(key)` get JsonObject
- `getArray(key)` get JsonArray
- `getString(key)` get String
- `getInt(key)` get int value
- `getLong(key)` get long value
- `getBigInteger(key)` get BigInteger value
- `getFloat(key)` get float value
- `getDouble(key)` get double value
- `getBigDecimal(key)` get BigDecimal value
- `getBoolean(key)` get boolean value
- `getObject(key)` get value as Object
- `get(key)` get value
### JsonArray
- `toString()` get json array string
- `toStringBeauty()` get the json array string, with line breaks and tabs
- `add(value)` add a value into JsonArray
- `addAll(jsonArray)` add all value from other jsonArray
- `set(index, value)` set a value in JsonArray
- `remove(index)` remove a value from JsonArray
- `toArray(index)` to Object array
- `content(value)` check if JsonArray content value
- `indexOf(value)` the index of a value in JsonArray 

- `getJson(index)` get JsonObject
- `getArray(index)` get JsonArray
- `getString(index)` get String
- `getInt(index)` get int value
- `getLong(index)` get long value
- `getBigInteger(index)` get BigInteger value
- `getFloat(index)` get float value
- `getDouble(index)` get double value
- `getBigDecimal(index)` get BigDecimal value
- `getBoolean(index)` get boolean value
- `getObject(index)` get value as Object
- `get(index)` get value
