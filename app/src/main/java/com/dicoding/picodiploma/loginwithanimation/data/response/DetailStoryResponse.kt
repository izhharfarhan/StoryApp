package com.dicoding.picodiploma.loginwithanimation.data.response

import com.google.gson.annotations.SerializedName

data class DetailStoryResponse(
	@SerializedName("error")
	val error: Boolean,

	@SerializedName("message")
	val message: String,

	@SerializedName("story")
	val story: StoryDetail
)

data class StoryDetail(
	@SerializedName("id")
	val id: String,

	@SerializedName("name")
	val name: String,

	@SerializedName("description")
	val description: String,

	@SerializedName("photoUrl")
	val photoUrl: String,

	@SerializedName("createdAt")
	val createdAt: String,

	@SerializedName("lat")
	val lat: Double,

	@SerializedName("lon")
	val lon: Double
)
