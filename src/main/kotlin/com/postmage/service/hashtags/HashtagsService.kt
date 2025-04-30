package com.postmage.service.hashtags

import com.mongodb.BasicDBObject
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOneModel
import com.mongodb.client.model.Updates
import com.postmage.enums.StatusCodeUtil
import com.postmage.model.hashtags.*
import com.postmage.model.posts.add_posts.AddPostModel
import com.postmage.model.posts.get_posts.GetUserPostModel
import com.postmage.model.user_config.UserRequestHeaders
import com.postmage.mongo_client.MongoInitialize
import com.postmage.mongo_client.mongo_constants.MongoSort
import com.postmage.repo.sendErrorData
import com.postmage.service.ResponseData
import com.postmage.service.Status
import com.postmage.service.user_posts.UserPostsService
import com.postmage.util.DateUtil
import com.postmage.util.exception.catchException
import com.postmage.util.exception.runCatchableException
import com.postmage.util.extensions.capitalizeFirstWord
import com.postmage.util.strings.AppMessageUtil
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.*
import kotlin.collections.ArrayList

class HashtagsService(
    private val mongoDB: MongoInitialize,
) : HashtagsInterface, KoinComponent {

    private val userPostsService: UserPostsService by inject()

    private fun getHashtagWeek(hashtag: String, week: Int = DateUtil.getCurrentWeekOfYear()) =
        "${hashtag.lowercase().capitalizeFirstWord()}__WEEK:$week"

    override suspend fun savePostHashtag(
        headers: UserRequestHeaders,
        body: AddPostModel,
        postObjectId: String
    ) = runCatchableException {
        if (body.hashtags.isEmpty()) {
            return@runCatchableException false
        }

        var cleanHashtags = ArrayList(body.hashtags.map {
            it.lowercase().capitalizeFirstWord()
        })

        cleanHashtags = ArrayList(cleanHashtags.toHashSet())

        cleanHashtags.forEach { hashtag ->
            val query = BasicDBObject("categoryName", getHashtagWeek(hashtag))

            var hashtagFind = false
            mongoDB.getHashtags.find(query).limit(1).forEach { hashtagModel ->
                hashtagModel.hashtags.add(
                    UserHashtagModel(
                        objectId = ObjectId.get().toString(),
                        userId = headers.tokenData.userId,
                        groupId = body.groupId,
                        postId = postObjectId,
                        creationTime = DateUtil.getTimeNow(),
                        hashtag = hashtag
                    )
                )

                val modifiedList = ArrayList<UpdateOneModel<HashtagModel>>()
                modifiedList.add(
                    UpdateOneModel(
                        Filters.eq("id", hashtagModel.id),
                        Updates.set("hashtags", hashtagModel.hashtags)
                    )
                )

                modifiedList.add(
                    UpdateOneModel(
                        Filters.eq("id", hashtagModel.id),
                        Updates.set("totalCount", hashtagModel.totalCount + 1),
                    )
                )

                mongoDB.getHashtags.bulkWrite(modifiedList)

                hashtagFind = true
            }

            if (!hashtagFind) {
                val model = HashtagModel(
                    id = ObjectId.get().toString(),
                    name = hashtag,
                    creatorUserId = headers.tokenData.userId,
                    editTime = DateUtil.getTimeNow(),
                    categoryName = getHashtagWeek(hashtag),
                    totalCount = 1,
                    week = DateUtil.getCurrentWeekOfYear(),
                    year = DateUtil.getCurrentYear(),
                    hashtags = arrayListOf(
                        UserHashtagModel(
                            objectId = ObjectId.get().toString(),
                            userId = headers.tokenData.userId,
                            groupId = body.groupId,
                            postId = postObjectId,
                            creationTime = DateUtil.getTimeNow(),
                            hashtag = hashtag
                        )
                    ),
                )
                mongoDB.getHashtags.insertOne(model)
            }
        }

        return@runCatchableException true
    }.catchException {
        sendErrorData<Any>(it.localizedMessage)
    }

    override suspend fun getPopularHashtags(headers: UserRequestHeaders): ResponseData<ArrayList<PopularHashtagsModel>> {
        val tenDaysAgo = DateUtil.getTimeNow() - (10 * 24 * 60 * 60 * 1000)
        val tenDayDataQuery = and(
            HashtagModel::creationTime gte tenDaysAgo, // "$gte ->  '>=' "
            HashtagModel::creationTime lt DateUtil.getTimeNow(), // "$lt -> '<' "
        )

        val hashtagModelList = arrayListOf<HashtagModel>()
        val limit = 20

        mongoDB.getHashtags
            .find(tenDayDataQuery)
            .sort(descending(HashtagModel::totalCount))
            .limit(limit)
            .forEach {
                hashtagModelList.add(it)
            }

        if (hashtagModelList.size < limit) {
            val allHashtags = mongoDB.getHashtags
                .find()
                .sort(BasicDBObject("creationTime", MongoSort.DESC))
                .sort(descending(HashtagModel::totalCount))
                .limit(limit - hashtagModelList.size)
                .toList()

            //Daha önceden eklendiyse tekrar eklemiyor lsiteye
            val existingIds = hashtagModelList.map { it.id }.toSet()
            val newHashtags = allHashtags.filterNot { existingIds.contains(it.id) }

            hashtagModelList.addAll(newHashtags)
        }

        val popularHashtagList = hashtagModelList.map {
            PopularHashtagsModel(
                hashtagId = it.id,
                name = it.name,
                creationTime = it.creationTime,
                editTime = it.editTime,
                creatorId = it.creatorUserId,
                totalCount = it.totalCount,
            )
        }

        return ResponseData.success(ArrayList(popularHashtagList))
    }

    override suspend fun getHashtagPosts(
        headers: UserRequestHeaders,
        model: GetHashtagPostsRequestModel
    ): ResponseData<GetHashtagPostsResponseModel> {
        //Hashtag sorgusu
        val query = if (!model.hashtagId.isNullOrEmpty()) {
            BasicDBObject(
                "id",
                model.hashtagId!!
            )
        } else {
            BasicDBObject(
                "name",
                model.hashtagName!!.capitalizeFirstWord()
            )
        }


        var hashtagPostResponse: GetHashtagPostsResponseModel? = null
        val sharedHashtags = arrayListOf<UserHashtagModel>()

        //Hashtagler nasıl sıralanacağını belirliyor
        val hashtagPostSort = HashtagPostsSorted.getValue(model.postSorted ?: -1)
        val sort = when (hashtagPostSort) {
            HashtagPostsSorted.ASC -> BasicDBObject("creationTime", MongoSort.ASC)
            else -> BasicDBObject("creationTime", MongoSort.DESC)
        }

        val hashtagLimit = if (model.hashtagId != null) 1 else 20

        mongoDB.getHashtags
            .find(query)
            .limit(hashtagLimit)
            .sort(sort)
            .forEach { hashtag ->
                hashtagPostResponse = GetHashtagPostsResponseModel(
                    hashtagId = hashtag.id,
                    hashtag = hashtag.name,
                    posts = arrayListOf()
                )
                sharedHashtags.addAll(hashtag.hashtags)
            }

        if (hashtagPostResponse == null) {
            return sendErrorData(
                AppMessageUtil.getAppMessages(headers.language).postNotFound, statusCode = StatusCodeUtil.BAD_REQUEST
            )
        }

        val postList = arrayListOf<GetUserPostModel>()

        //Paylaşılan hashtaglerdeki postları bul ve getir
        sharedHashtags.forEach {
            val response = userPostsService.getPost(headers = headers, postId = it.postId)
            if (response.status == Status.SUCCESS && response.data != null) {
                postList.add(response.data)
            }
        }

        //Postları like sayısına göre de sırala
        val sortedList = when (hashtagPostSort) {
            HashtagPostsSorted.DESC -> postList.sortedByDescending { it.creationTime }
            HashtagPostsSorted.ASC -> postList.sortedBy { it.creationTime }
            HashtagPostsSorted.POPULAR -> postList.sortedByDescending { (it.likeUserId?.size ?: 0) }
            else -> postList.sortedByDescending { it.creationTime }
        }
        /*
        val sortedList = postList.sortedWith(compareByDescending<GetUserPostModel> { it.creationTime }
            .thenByDescending { it.likeUserId?.size })
         */

        hashtagPostResponse!!.posts.addAll(sortedList)

        return ResponseData.success(hashtagPostResponse)
    }

}