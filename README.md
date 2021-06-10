## Real-time VK posts toxicity analyzer

Distributed implementation of VK posts analyzer.

### Developed by

* Nikita Duginets  
* Nikita Detkov  
* Nikita Mramorov  
* Rami Al-Naim  

### Description

This program represents streaming pipeline which consists of the following steps:

1) Collects data from VK posts of given groups.
2) Writes data to MongoDB and Kafka.
3) Receives data from Kafka and streams it into Spark.
4) Splits data in two parts:
    * Text data
    * Image data
5) Measures toxicity level of each data peace.
6) Writes results to MongoDB.

### Building

In order to build project, follow the instruction below:

1) Run ``cd publishers/``
2) Set execution permission ``chmod +x build_and_start.sh``
3) Run ``./build_and_start.sh``. If everything works fine, you will see parser logs.
4) Open another terminal window and run ``docker-compose ps`` to check if all services have **Up** state.
5) Add VK group using command ``curl http://localhost:8080/add?add=<group_name>``
6) Check parser logs, you will see that amount of fetched messages increased.
7) Go to ``http://localhost:8889`` to open Jupyter Notebook. If token is required, check
*Spark* logs using ``docker logs --tail 20 <spark_container_name>`` command and copy token.
8) Upload notebooks from **notebooks/** directory.
9) Run notebook **kafka_router.ipynb**, it it will run continuously.
10) Copy commands from **tesseract_installation.sh** file from **ml_text_processing** directory.
11) Run in terminal command ``docker exec -it <spark_container_name> bash``. You will
access container console.
12) Run copied Tesseract installation commands. **WARNING!** This step involves high memory load.
13) Run command ``pip install pymongo``.
14) Run both notebooks **text_consumer.ipynb** and **image_consumer.ipynb** in order
to analyse data.
 
### Implementation details

#### Deployment

Deployment is done by *docker-compose* utility.

#### API

For data loading [VK-API](https://vk.com/dev/methods) was used.

#### Parser

**Kotlin** is used in order to implement **Loader**, **Cron**, **Saver** modules.

#### Kafka

*Kafka* is build from corresponding Docker image. For interaction with *Kafka* the Python
package *kafka-python* is used.

#### Spark

*Spark* is build from corresponding Docker image. For interaction with *Spark* the *pyspark*
Python package is used.

#### MongoDB

Standard *MongoDB* Docker image was used.


#### Database schema

Database structure is shown below:

**name**: "vk"

**tables**:

* Comment -- table with main comment data
    * **with fields**:
        * id
            * Unique identifier
            * string
        * commentId
            * Post's comment identifier
            * integer
        * postId
            * Wall's post identifier
            * integer
        * wallId
            * Wall's identifier (for example -45989129)
            * integer
        * authorId
            * Comment's author
            * integer
        * date
            * Comment's date (ISO formatted, 2020-01-28T05:31:78.000Z)
            * Instant
        * text
            * Comment's text
            * string
        * likes
            * Comment's likes
            * integer
        * images
            * Comment's attached images (["xxx4543...", "aa453543..."])
            * list of bytes
    * **constraints**:
        * id -- unique
        * commentId, wallId, postId -- unique tuple

* Post -- table with main post data
    * **with fields**:
        * id
            * Unique id
            * string
        * postId
            * Wall's post identifier
            * integer
        * wallId
            * Wall's identifier (for example -45989129)
            * integer
        * authorId
            * Post's author
            * integer
        * date
            * Post's date (ISO formatted, 2020-01-28T05:31:78.000Z)
            * Instant
        * text
            * Post's text
            * string
        * likes
            * Post's likes
            * integer
        * reports
            * Post's reposts
            * integer
        * views
            * Post's views
            * integer
        * images
            * Post's attached images (["xxx4543...", "aa453543..."])
            * list of bytes
    * **constraints**:
        * id -- unique identifier
        * wallId, postId -- unique tuple


* CommentMetaData -- table with last processed comment state
    * id
        * Identifier
        * string
    * wallId
        * Wall identifier
        * integer
    * postId
        * Post identifier
        * integer
    * lastTime
        * Last processed comment date in pair wallId&postId (ISO formatted, 2020-01-28T05:31:78.000Z)
        * Instant
    * **constraints**:
        * id -- unique identifier
        * wallId, postId -- unique tuple

* PostMetaData -- table with last processed post state
    * wallId
        * Wall identifier
        * integer
    * lastTime
        * Last processed comment date in pair wallId&postId (ISO formatted, 2020-01-28T05:31:78.000Z)
        * Instant
    * **constraints**:
        * wallId -- unique identifier

* PostMetaToName -- table with wall name and wall title
    * wallId
        * Wall identifier
        * integer
    * wallName
        * Wall link (for example: vk.com/some-group-name)
        * string
    * wallTitle
        * Wall title
        * string
    * **constraints**:
        * wallId -- unique identifier
    

