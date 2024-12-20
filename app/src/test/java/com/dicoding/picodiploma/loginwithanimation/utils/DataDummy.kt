package com.dicoding.picodiploma.loginwithanimation.utils

import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStories(): List<ListStoryItem> {
        val newListStories: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..50) {
            val story = ListStoryItem(
                    id = "story-FvU4u0Vp${i}S3PMsFg",
                    name = "Dimas$i",
                    description = "Lorem Ipsum $i",
                    photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                    createdAt = "2022-01-08T06:34:18.598Z",
                    lon = 10.212 + i,
                    lat = 16.002 + i
            )
            newListStories.add(story)
        }

        return newListStories
    }
}