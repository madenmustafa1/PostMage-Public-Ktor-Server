package com.postmage.model.hashtags

data class GetHashtagPostsRequestModel(
    var hashtagId: String? = null,
    var hashtagName: String? = null,
    var postSorted: Int? = HashtagPostsSorted.DESC.index,
)