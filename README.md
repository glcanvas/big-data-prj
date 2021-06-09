## Real-time VK posts toxicity analyzer

Our distributed implementation of Vk posts analyzer

TODO add informative description

### Tech details

Under this section will be presented implementation details

For load data from the VK we used [VK-API](https://vk.com/dev/methods)

#### database and it's schema

For storage loaded data we used MongoDB.

Database has structure bellow:

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
    * ** constraints**:
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
    * ** constraints**:
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
    * ** constraints**:
        * id -- unique identifier
        * wallId, postId -- unique tuple

* PostMetaData -- table with last processed post state
    * wallId
        * Wall identifier
        * integer
    * lastTime
        * Last processed comment date in pair wallId&postId (ISO formatted, 2020-01-28T05:31:78.000Z)
        * Instant
    * ** constraints**:
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
    * ** constraints**:
        * wallId -- unique identifier
    

