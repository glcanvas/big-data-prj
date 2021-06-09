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

* Comment
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
    
* Post
* CommentMetaData
* PostMetaData
* PostMetaToName

