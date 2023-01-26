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
    <version>0.0.3</version>
</dependency>
```

**Gradle**
```gradle
dependencies {
    //Change 'implementation' to 'compile' in old Gradle versions
    implementation 'io.github.WavJaby:tiny-json:0.0.3'
}
repositories {
    mavenCentral()
}
```

## Methods
### JsonObject
- `toString()` get json string
- `toStringBeauty()` get the json string, but it has line breaks and tabs
- `put(key, value)` put item into JsonObject
- `containsKey(key)` return true, if there is a key in JsonObject
- `notNull(key)` return true, if there is a key in JsonObject and the value is not null

- `getjson(key)` get JsonObject
- `getArray(key)` get JsonArray
- `getString(key)` get String
- `getInt(key)` get int value
- `getLong(key)` get long value
- `getFloat(key)` get float value
- `getDouble(key)` get double value
- `getBoolean(key)` get boolean value
- `getBigInteger(key)` get BigInteger
- `getItem(key)` get Item content with keys and values
- `getObject(key)` get value as Object
- `Items()` get all Item in JsonObject
- `toJsonArray()` if the input is array base type, it can change type from JsonObject to JsonArray
### JsonArray
