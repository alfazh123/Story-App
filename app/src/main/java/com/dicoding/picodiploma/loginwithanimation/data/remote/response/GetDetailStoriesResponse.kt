package com.dicoding.picodiploma.loginwithanimation.data.remote.response

data class GetDetailStoriesResponse(
	val error: Boolean,
	val message: String,
	val story: Story
)

data class Story(
	val photoUrl: String,
	val createdAt: String,
	val name: String,
	val description: String,
	val lon: Double? = null,
	val id: String,
	val lat: Double? = null
)

